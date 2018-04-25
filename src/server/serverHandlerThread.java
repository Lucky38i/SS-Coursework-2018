package server;

import Resources.AudioUtil;
import Resources.Pair;
import Resources.Users;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Task that handles all connections received from the client
 * @author alexmcbean
 */
public class serverHandlerThread extends Task<Void>
{
    //Variables
    private Socket socket;
    private ObjectOutputStream toClient;
    private ClientManager clientManagerTemp;
    private Users user;
    private Timeline updaterTimer;
    private Timeline chatTimer;

    /**
     * The main constructor
     * @param socket The accepted socket
     * @param clientManagerTemp The client Manager to handle all data between threads
     */
    serverHandlerThread(Socket socket, ClientManager clientManagerTemp)
    {
        this.socket = socket;
        this.clientManagerTemp = clientManagerTemp;
    }

    private ObjectOutputStream getWriter()
    {
        return toClient;
    }

    /**
     * This sets the client's viewer that is used to receive messages
     * from the server and assigns it to the client list
     * @param user This is the user that has been read from the client
     */
    private void setViewer(Users user)
    {
        int testInt = clientManagerTemp.clientlist().indexOf(this);
        clientManagerTemp.clientlist().get(testInt).user = user;
        clientManagerTemp.clientlist().get(testInt).user.setLoggedIn(true);

        for (int i = 0; i < clientManagerTemp.usersList().size(); ++i)
            if (clientManagerTemp.usersList().get(i).getUserName().contains(user.getUserName()))
                clientManagerTemp.usersList().get(i).setLoggedIn(true);
    }

    /**
     * This sets the client's private message viewer that is used to receive
     * private message from the user
     * @param user The user that has been read from the client
     */
    private void setChatViewer(Users user)
    {
        int testInt = clientManagerTemp.clientlist().indexOf(this);
        clientManagerTemp.clientlist().get(testInt).user = user;
    }

    /**
     * This accepts an input from a client with the specific code
     * whether it be friend request, acceptance of friend request or denial and sends
     * the appropriate message to the receiving client
     * @param input This is the code with the accompanying user who's going to receive a request delimited by
     */
    private void writeMessageToUser(String input)
    {
        try
        {
            String[] names = input.split("[.]");

            //Find the receiving user's socket
            serverHandlerThread findUser = null;
            for (int i = 0; i < clientManagerTemp.clientlist().size(); i++)
            {
                if (clientManagerTemp.clientlist().get(i).user.getUserName().equals(names[2]))
                {
                    findUser = clientManagerTemp.clientlist().get(i);
                }
            }

            //This sends the message to the appropriate user
            assert findUser != null;
            ObjectOutputStream toFindUser = findUser.getWriter();
            toFindUser.writeUTF("." +names[1] + "." + user.getUserName());
            toFindUser.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Handles friend requests
     * @param input the request received
     */
    private void handleRequest(String input)
    {
        if (input.contains(".Accept"))
        {
            clientManagerTemp.addNewFriend(input,user);
            writeMessageToUser(input);
        }
        else if (input.contains(".Decline"))
        {
            writeMessageToUser(input);
        }
    }

    /**
     * Handles private messages and finds out who to send it to.
     * @param input Input contains the message and username to send the message to.
     * @param user Used to retrieve who the message is coming from.
     */
    private void handlePrivateMessage(String input, Users user)
    {
        String[] names = input.split("[.]");
        String message = names[3];
        String userName = names[2];

        if (userName.contains("(Friend)"))
        {
            userName = userName.substring(0,userName.indexOf("(F"));
        }

        String[] usernameToSend = user.getUserName().split("[.]");
        clientManagerTemp.logger(usernameToSend[0] + " is sending a message to " + userName, "Chat");

        try
        {
            serverHandlerThread findUser = null;
            for (int i = 0; i < clientManagerTemp.clientlist().size(); ++i)
            {
                if (clientManagerTemp.clientlist().get(i).user.getUserName().equals(userName + ".ChatViewer"))
                {
                    findUser = clientManagerTemp.clientlist().get(i);
                }
            }
            assert findUser != null;
            ObjectOutputStream toFindUser = findUser.getWriter();

            toFindUser.writeUTF(".Message." + usernameToSend[0] +"."+message);
            toFindUser.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * this registers the current user into the user list as-well as executes it into the database
     * @param user The user model for which to register to the database
     */
    private void registerUser(Users user)
    {
        logoff();
        clientManagerTemp.registerUsers(user);
    }

    /**
     * This acquires the list of currently online users and sends
     * it back to the asking socket
     * @param toClient the output stream to send the retrieved clients to
     */
    private void getOnlineUsers(ObjectOutputStream toClient)
    {
        try
        {
            //Gathers a list of all the users that are online
            ArrayList<String> tempList = new ArrayList<>();
            for (Users i : clientManagerTemp.usersList())
            {
                if (i.getLoggedIn())
                {
                    tempList.add(i.getUserName());
                }
            }
            toClient.writeUTF(".online");
            toClient.flush();

            toClient.writeObject(tempList);
            toClient.flush();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a user and sends it to the client, basically updating it, in case of
     * any changes such as a new friend
     * @param toClient The output stream
     * @param input The get message code
     */
    private void getUpdatedUser(ObjectOutputStream toClient, String input)
    {
        try
        {
            String[] names = input.split("[.]");
            for (int i=0;i< clientManagerTemp.usersList().size(); ++i)
            {
                if (clientManagerTemp.usersList().get(i).getUserName().contains(names[2]))
                {
                    toClient.reset();
                    toClient.writeUTF(input);
                    toClient.flush();

                    toClient.reset();
                    toClient.writeObject(clientManagerTemp.usersList().get(i));
                    toClient.flush();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This finds out if the request user exists or is already logged in
     * @param toClient the output stream from which to send the response to
     * @param user the user to be looking for
     */
    private void findUser(ObjectOutputStream toClient, Users user)
    {
        try
        {
            Pair<Boolean, Users> findUsers;

            findUsers = clientManagerTemp.findUsers(user);

            //If the user is already logged in then return false to prevent duplicate log in
            if (findUsers.getSecond().getLoggedIn().equals(true))
            {
                toClient.writeUTF("false");
                toClient.flush();

                toClient.writeObject(findUsers.getSecond());
                toClient.flush();

                clientManagerTemp.logger("IP: " + socket.getRemoteSocketAddress() + " tried to access an already logged in account","Main");
            } else
            {
                //If user is found then send the user object to the client
                if (findUsers.getFirst())
                {
                    toClient.writeUTF("True");
                    toClient.flush();

                    toClient.writeObject(findUsers.getSecond());
                    toClient.flush();

                    //Set the user to being logged in and print the log
                    for (int i = 0; i < clientManagerTemp.usersList().size(); i++)
                    {
                        if (findUsers.getSecond().getUserName().equals(clientManagerTemp.usersList().get(i).getUserName()))
                        {
                            clientManagerTemp.usersList().get(i).setLoggedIn(true);
                            clientManagerTemp.logger(findUsers.getSecond().getUserName() + " has logged in","Main");
                        }
                    }
                } else if (!findUsers.getFirst())
                {
                    toClient.writeUTF("false");
                    toClient.flush();

                    toClient.writeObject(findUsers.getSecond());
                    toClient.flush();
                }
            }

            logoff();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Finds the requested music and sends the file to the user
     * @param input  the message containing the requested song
     * @param toClient The object output stream used to send the file
     */
    private void findMusic(String input, ObjectOutputStream toClient)
    {
        logoff();
        String[] names = input.split("[.]");
        clientManagerTemp.logger("Someone request song: " + names[2] + ".mp3","Main");

        File musicFile = AudioUtil.getSoundFile("src/Resources/Songs/" + names[2]+ ".mp3");
        Integer fileLength = (int) musicFile.length();
        byte[] buffer = new byte[fileLength];

        try(FileInputStream fileReader = new FileInputStream(musicFile);
            BufferedInputStream bis = new BufferedInputStream(fileReader))
        {
            bis.read(buffer, 0, buffer.length);

            clientManagerTemp.logger("Sending " + "src/Resources/Songs/" + names[2]+ ".mp3" + "(" + buffer.length + " bytes)","Main");

            toClient.writeInt(fileLength);
            toClient.write(buffer,0, buffer.length);
            toClient.flush();

            clientManagerTemp.logger("Finished sending","Main");
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * This creates a new song file in the Resources folder based on the song
     * the user uploaded the notifies the receiving user that a song was shared
     * with them.
     * @param input The message to notify the receiving user that that a song
     *              was shared with them
     * @param fromClient The Object input stream to read the received file
     */
    private void createNewSong(String input, ObjectInputStream fromClient)
    {
        try
        {
            String[] names = input.split("[.]");
            String userToTell = names[1];
            String songName = names[2];

            File file = new File("src/Resources/Songs/" + songName);
            file.createNewFile();

            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file)))
            {
                byte[] buffer = new byte[4096];
                int fileLength = fromClient.readInt();
                int current;

                while (fileLength > 0 && (current = fromClient.read(buffer, 0, Math.min(4096, fileLength))) > 0)
                {
                    bos.write(buffer, 0, current);
                    fileLength -= current;
                }
                handlePrivateMessage(".Message." + userToTell + "." + "I shared '"+ songName+"' with you!",user);
                clientManagerTemp.logger(userToTell + " I shared " + songName, "Chat");

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * This method searches for users who have the searched music interests
     * then compiles a list of all these users and sends it back to the client
     * @param toClient The object output stream for which to write to
     * @param input the received message from the server
     */
    private void searchMusicInterests(ObjectOutputStream toClient, String input)
    {
        String[] names = input.split("[.]");
        ArrayList<Users> searchedUsers = new ArrayList<>();
        try
        {
            for (int i = 0; i < clientManagerTemp.usersList().size(); ++i)
            {
                for (int x = 0; x < clientManagerTemp.usersList().get(i).getMusicGenre().size(); ++x)
                {
                    if (clientManagerTemp.usersList().get(i).getMusicGenre().get(x).contains(names[2]))
                    {
                        searchedUsers.add(clientManagerTemp.usersList().get(i));
                    }
                }
            }
            //Removes duplicates
            Set<Users> hashSet = new HashSet<>(searchedUsers);
            searchedUsers.clear();
            searchedUsers.addAll(hashSet);

            toClient.writeUTF(input);
            toClient.flush();

            toClient.writeObject(searchedUsers);
            toClient.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Writes the received message to all clients' viewers
     * @param input The received message
     */
    private void writeMessageToAll(String input)
    {
        clientManagerTemp.logger(user.getUserName() + " Said: " + input,"Main" );
        try
        {
            //Write the client input to all clients
            for (serverHandlerThread thatClient : clientManagerTemp.clientlist())
            {
                ObjectOutputStream thatClientOut = thatClient.getWriter();
                if (thatClientOut != null)
                {
                    thatClientOut.writeUTF(user.getUserName() + ": " + input + "\r\n");
                    thatClientOut.flush();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * This removes the current thread from the client list
     * and sets the logged in state of the user to false
     * and printing out that said user has logged out
     * @param toClient The output stream to let the client aware that this action
     *                 has completed. (This is not needed anymore)
     */
    private void logout(ObjectOutputStream toClient)
    {
        try
        {
            updaterTimer.stop();
            logoff();

            //Set the user's log in state to false
            for (int i = 0; i < clientManagerTemp.usersList().size(); i++)
            {
                if (user.getUserName().equals(clientManagerTemp.usersList().get(i).getUserName()))
                {
                    clientManagerTemp.usersList().get(i).setLoggedIn(false);
                    clientManagerTemp.logger(user.getUserName() + " has logged out","Main");
                }
            }

            toClient.writeUTF("Done");
            toClient.flush();

            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void logoffChat()
    {
        try
        {
            chatTimer.stop();
            logoff();

            socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This removes the current thread from the list of clients
     */
    private void logoff()
    {
        clientManagerTemp.removeClient(this);
    }

    @Override
    public Void call ()
    {
        try
        {
            //Setup I/O
            toClient = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());

            while(!socket.isClosed())
            {
                Thread.sleep(100);

                if (fromClient.available() > 0)
                {
                    //Reads message and objects from client
                    String input = fromClient.readUTF();

                    //Send a request to the user
                    if (input.contains(".Viewer"))
                    {
                        setViewer((Users) fromClient.readObject());

                        //A periodic updaterTimer that sends online users periodically
                        updaterTimer = new Timeline(new KeyFrame(Duration.seconds(5), event ->
                        {
                            getOnlineUsers(toClient);
                            getUpdatedUser(toClient, ".Get." + user.getUserName());

                        }));
                        updaterTimer.setCycleCount(Timeline.INDEFINITE);
                        updaterTimer.playFrom(Duration.seconds(4));
                    }
                    else if (input.contains(".ChatViewer"))
                    {
                        setChatViewer((Users) fromClient.readObject());
                        getOnlineUsers(toClient);

                        chatTimer = new Timeline(new KeyFrame(Duration.seconds(4), event ->
                        {

                            getOnlineUsers(toClient);

                        }));
                        chatTimer.setCycleCount(Timeline.INDEFINITE);
                        chatTimer.play();
                    }

                    else if (input.contains(".LogoffChat"))
                    {
                        logoffChat();
                    }

                    else if (input.contains(".Message"))
                    {
                        handlePrivateMessage(input, user);
                    }

                    else if (input.contains(".Music"))
                    {
                        findMusic(input, toClient);
                    }

                    else if (input.contains(".NewSong"))
                    {
                        createNewSong(input, fromClient);
                    }

                    else if (input.contains(".Search"))
                    {
                        searchMusicInterests(toClient, input);
                    }

                    else if (input.contains(".Accept") || input.contains(".Decline"))
                    {
                        handleRequest(input);
                    }

                    else if (input.contains(".Request"))
                    {
                        writeMessageToUser(input);
                    }
                    //Logout the user
                    else if (".logout".equals(input))
                    {
                        logout(toClient);
                    }

                    //If clients sets .register command then register new user
                    else if (".register".equals(input))
                    {
                        registerUser((Users) fromClient.readObject());
                    }

                    //If clients sends .findUser command then see if user exists in DB
                    else if (".findUser".equals(input))
                    {
                        Users tempUser = (Users) fromClient.readObject();
                        findUser(toClient, tempUser);
                    }

                    else
                    {
                        writeMessageToAll(input);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
