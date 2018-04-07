package server;

import Resources.Pair;
import Resources.Users;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Task that handles all connections received from the client
 */
public class serverHandlerThread implements Runnable
{
    private Socket socket;
    private ObjectOutputStream toClient;
    private ClientManager clientManagerTemp;
    //private MainServer server;
    private Users user;
    private volatile boolean isRunning;

    //Constructor
    serverHandlerThread(MainServer server, Socket socket, ClientManager clientManagerTemp)
    {
        this.server = server;
        this.socket = socket;
        this.clientManagerTemp = clientManagerTemp;
        isRunning = false;
    }

    private ObjectOutputStream getWriter()
    {
        return toClient;
    }

    private void logger(String msg)
    {
        System.out.println(LocalDate.now()+ " " +LocalTime.now() + " - " +msg);
    }


    private Users getUser()
    {
        return user;
    }

    @Override
    public void run ()
    {
        try
        {
            isRunning = true;
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
                    Object obj = fromClient.readObject();

                    //logger(input);

                    switch (input)
                    {
                        case ".request":

                            break;
                        //Logout the user
                        case ".logout":
                            //Set the user to being logged out and print the log
                            user = (Users) obj;

                            clientManagerTemp.removeClient(this);

                            for (int i = 0; i < clientManagerTemp.usersList().size(); i++)
                            {
                                if (user.getUserName().equals(clientManagerTemp.usersList().get(i).getUserName()))
                                {
                                    clientManagerTemp.usersList().get(i).setLoggedIn(false);
                                    logger(user.getUserName() + " has logged out");
                                }
                            }

                            break;

                        //If clients sets .register command then register new user
                        case ".register":

                            clientManagerTemp.removeClient(this);

                            user = (Users) obj;
                            server.registerUsers(user);

                            break;

                        //Sends out all the current online users
                        case ".online":
                            user = (Users) obj;

                            clientManagerTemp.removeClient(this);

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
                            break;

                        //If clients sends .findUser command then see if user exists in DB
                        case ".findUser":
                            user = (Users) obj;
                            //Create a pair and find the user
                            Pair<Boolean, Users> findUsers;

                            clientManagerTemp.removeClient(this);

                            findUsers = server.findUsers(user);

                            if (findUsers.getSecond().getLoggedIn().equals(true))
                            {
                                toClient.writeUTF("false");
                                toClient.flush();

                                toClient.writeObject(findUsers.getSecond());
                                toClient.flush();

                                logger("IP: " + socket.getRemoteSocketAddress() + " tried to access an already logged in account");
                            }

                            else
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
                                            logger(findUsers.getSecond().getUserName() + " has logged in");
                                        }
                                    }
                                }

                                else
                                {
                                    toClient.writeUTF("false");
                                    toClient.flush();

                                    toClient.writeObject(findUsers.getSecond());
                                    toClient.flush();
                                }
                            }
                            break;

                        //Push message received to other clients
                        default:
                            logger("Sending message to clients");
                            user = (Users) obj;

                            clientManagerTemp.removeClient(this);
                            logger("Clientmanager temp is:" + String.valueOf(clientManagerTemp.clientlist().size()));

                            for (serverHandlerThread thatClient : clientManagerTemp.clientlist())
                            {
                                ObjectOutputStream thatClientOut = thatClient.getWriter();
                                if (thatClient.isRunning)
                                {
                                    if (thatClientOut != null)
                                    {
                                        thatClientOut.writeUTF(user.getUserName() + ": " + input + "\r\n");
                                        thatClientOut.flush();
                                    }
                                }
                            }

                            break;
                    }
                }

            }
        } catch (Exception e)
        {
            e.printStackTrace();
            isRunning=false;
        }
    }
}
