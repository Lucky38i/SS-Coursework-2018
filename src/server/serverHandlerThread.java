package server;

import Resources.Pair;
import Resources.Users;

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
     * This removes the current client from the list of clients
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
                        user = (Users) fromClient.readObject();
                        int testint = clientManagerTemp.clientlist().indexOf(this);
                        clientManagerTemp.clientlist().get(testint).user = user;

                    }
                    //TODO
                    else if (input.contains(".Request"))
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

                        //TODO Send the message to the below socket
                        System.out.println(findUser.socket.getRemoteSocketAddress());


                    }
                    //Logout the user
                    else if (".logout".equals(input))
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

                    //If clients sets .register command then register new user
                    else if (".register".equals(input))
                    {
                        logoff();

                        user = (Users) fromClient.readObject();

                        clientManagerTemp.registerUsers(user);



                    }

                    //Sends out all the current online users
                    else if (".online".equals(input))
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


                        //If clients sends .findUser command then see if user exists in DB
                    }

                    else if (".findUser".equals(input))
                    {//Create a pair and find the user

                        user = (Users) fromClient.readObject();

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
                                        clientManagerTemp.logger(findUsers.getSecond().getUserName() + " has logged in");
                                    }
                                }
                            }

                            else if (!findUsers.getFirst())
                            {
                                toClient.writeUTF("false");
                                toClient.flush();

                                toClient.writeObject(findUsers.getSecond());
                                toClient.flush();
                            }
                        }

                        logoff();


                        //Push message received to other clients
                    }
                    else
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
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
