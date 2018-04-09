package server;

import Resources.Pair;
import Resources.Users;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Task that handles all connections received from the client
 */
public class serverHandlerThread implements Runnable
{
    //Variables
    private Socket socket;
    private ObjectOutputStream toClient;
    private ClientManager clientManagerTemp;
    private Users user;

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
    }

    /**
     * This acknowledges a friend request and
     * finds the user it is being sent to then sends
     * the request code to the client with the requestor's username
     * @param input This is the request code with the accompanying user who's going to receive a request delimited by
     *              a <code>[.]</code>
     *              for example <code>input = "Request.userName"</code>
     */
    private void friendRequest(String input)
    {
        try
        {
            String[] names = input.split("[.]");
            System.out.println(names.length + ": " + names[2]);

            serverHandlerThread findUser = null;
            for (int i = 0; i < clientManagerTemp.clientlist().size(); i++)
            {
                if (clientManagerTemp.clientlist().get(i).user.getUserName().equals(names[2]))
                {
                    findUser = clientManagerTemp.clientlist().get(i);
                }
            }

            //TODO create a code for the client to interpret
            //This sends the request to the appropriate user
            assert findUser != null;
            System.out.println(findUser.socket.getRemoteSocketAddress());
            ObjectOutputStream toFindUser = findUser.getWriter();
            toFindUser.writeUTF("A request has been sent!" + "\r\n");
            toFindUser.flush();
        }
        catch (IOException e)
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

            logoff();

            //Gathers a list of all the users that are online
            ArrayList<String> tempList = new ArrayList<>();
            for (Users i : clientManagerTemp.usersList())
            {
                if (i.getLoggedIn())
                {
                    tempList.add(i.getUserName());
                }
            }

            toClient.writeObject(tempList);
            toClient.flush();

            socket.close();
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

                clientManagerTemp.logger("IP: " + socket.getRemoteSocketAddress() + " tried to access an already logged in account");
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
                            clientManagerTemp.logger(findUsers.getSecond().getUserName() + " has logged in");
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
     * Writes the received message to all clients' viewers
     * @param input The received message
     */
    private void writeMessage(String input)
    {
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
            logoff();

            //Set the user's log in state to false
            for (int i = 0; i < clientManagerTemp.usersList().size(); i++)
            {
                if (user.getUserName().equals(clientManagerTemp.usersList().get(i).getUserName()))
                {
                    clientManagerTemp.usersList().get(i).setLoggedIn(false);
                    clientManagerTemp.logger(user.getUserName() + " has logged out");
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

    /**
     * This removes the current thread from the list of clients
     */
    private void logoff()
    {
        clientManagerTemp.removeClient(this);
    }

    @Override
    public void run ()
    {
        try
        {
            //Setup I/O
            toClient = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());

            while(!socket.isClosed())
            {
                //If server has received a message
                if(fromClient.available() > 0)
                {
                    //Reads message and objects from client
                    String input = fromClient.readUTF();


                    //Send a request to the user
                    if (input.contains(".Viewer"))
                    {
                        setViewer((Users) fromClient.readObject());
                    }

                    else if (input.contains(".Request"))
                    {
                        friendRequest(input);
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

                    //Sends out all the current online users
                    else if (".online".equals(input))
                    {
                        getOnlineUsers(toClient);
                    }

                    //If clients sends .findUser command then see if user exists in DB
                    else if (".findUser".equals(input))
                    {
                        findUser(toClient, (Users) fromClient.readObject());
                    }
                    else
                    {
                       writeMessage(input);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
