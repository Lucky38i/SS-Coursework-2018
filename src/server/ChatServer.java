/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/*
 *
 * @author alexmcbean
 */


import Resources.Pair;
import Resources.Users;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This is the main server that handles all objects going in and out of the server
 * such as registration, log in, friends, music streaming etc.
 * @author alexmcbean
 */
public class ChatServer
{

    //Static variables
    private static final int portNumber = 4444;
    
    //Variables
    private int serverPort;
    private List<serverHandlerThread> clients;
    private List<Users> usersList = new ArrayList<>();

    /**
     * Connects to the identified database and returns the Connections
     * @return the connection object
     */
    private Connection connect()
    {
        String url = "jdbc:sqlite:src/Resources/test.db";
        Connection conn = null;
        try
        {
            conn = DriverManager.getConnection(url);
            logger("Connection to database made");
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    private void logger(String msg)
    {
        LocalDate date = LocalDate.now();
        System.out.println(LocalDate.now()+ " " +LocalTime.now() + " - " +msg);
    }

    /**
     * Connects to the local database
     * and adds the results to a user which then gets added
     * to a list of users
     */
    private void populateUsers()
    {
        String sqlQuery = "SELECT userName, firstName, lastName, birthday, City, userID, loggedIn FROM Users";
        try
        {
            Connection conn = this.connect();

            //Create statements to be executed
            Statement userStatement = conn.createStatement();
            Statement genresStatement = conn.createStatement();
            Statement friendsStatement = conn.createStatement();

            ResultSet resultSet = userStatement.executeQuery(sqlQuery);

            while (resultSet.next())
            {
                //Create a new user and set its properties from SQL results
                Users user = new Users();
                user.setUserID(resultSet.getInt("userID"));
                user.setUserName(resultSet.getString("userName"));
                user.setFirstName(resultSet.getString("firstName"));
                user.setLastName(resultSet.getString("lastName"));
                user.setCity(resultSet.getString("city"));
                user.setBirthday(resultSet.getDate("birthday").toLocalDate());
                user.setLoggedIn(resultSet.getBoolean("loggedIn"));

                String findMusicGenres = "SELECT musicGenre FROM musicGenres a, Users b\n" +
                        "WHERE a.userID = " + resultSet.getInt("userID") + " AND  a.userID = b.userID";

                ResultSet musicResult = genresStatement.executeQuery(findMusicGenres);
                while (musicResult.next())
                {
                    //Set the User's music genres based of SQL results
                    user.musicGenreProperty().get().add(musicResult.getString("musicGenre"));
                }

                String findFriendList = "SELECT userName FROM Users a, Friends b\n" +
                        "WHERE b.userID = " + user.getUserID() + " AND a.userID = b.friendID";

                ResultSet friendResult = friendsStatement.executeQuery(findFriendList);
                while (friendResult.next())
                {
                    //Set the user's friend list based of SQL results
                    user.friendsListProperty().get().add(friendResult.getString("userName"));
                }

                usersList.add(user);
            }
            logger("Database built successfully ");


        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Takes in a user's details and adds it to the database and executes then adds that user
     * to the list of current users
     * @param user received from the client
     */
    private void registerUsers(Users user)
    {

        String addUser = "INSERT INTO Users(userName, firstName, lastName, birthday, City) VALUES(?,?,?,?,?)";
        String findUserID = "SELECT userID FROM Users WHERE userName = '" + user.getUserName() + "'";
        String addMusicGenres = "INSERT INTO musicGenres(userID, musicGenre) VALUES(?,?)";
        try
        {
            //Creates a connection
            Connection conn = this.connect();

            //A statement to update the database
            PreparedStatement preparedStatement = conn.prepareStatement(addUser);

            //Sets each value and executes the update
            preparedStatement.setString(1,user.getUserName());
            preparedStatement.setString(2,user.getFirstName());
            preparedStatement.setString(3,user.getLastName());
            preparedStatement.setDate(4,Date.valueOf(user.getBirthday()));
            preparedStatement.setString(5, user.getCity());
            preparedStatement.executeUpdate();

            //A result set finding the userID
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(findUserID);

            //A new statement to add musicGenres using the newly created user
            PreparedStatement preparedStatement1 = conn.prepareStatement(addMusicGenres);
            while(resultSet.next())
            {
                for (int i=0; i < user.getMusicGenre().size(); i++)
                {
                    preparedStatement1.setInt(1, resultSet.getInt("userID"));
                    preparedStatement1.setString(2, user.getMusicGenre().get(i));
                    preparedStatement1.executeUpdate();
                }
            }

            usersList.add(user);
            logger("User: " + user.getUserName() + " added to database");
        }

        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Find if the user exists in the database
     * @param user finds the recevied user in an SQL query
     * @return true if a user is found otherwise false
     */
    private Pair<Boolean, Users> findUsers(Users user)
    {
        Pair<Boolean, Users> foundUser = new Pair<>();
        foundUser.setFirst(false);
        String findUser = "SELECT * FROM Users WHERE userName = '" + user.getUserName() + "'";
        try
        {
            Connection conn = this.connect();

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(findUser);

            //If column result is empty return empty and set to false or true
            foundUser.setFirst(resultSet.isBeforeFirst());

            if (foundUser.getFirst())
            {
                Users temp = new Users();
                for (Users i : usersList)
                {
                    if (i.getUserName().equals(user.getUserName()))
                    {
                       temp = i;
                    }
                }
                foundUser.setSecond(temp);
                logger("Found user: " + foundUser.getSecond().getFirstName());
            }
            else
                logger("User not found");
        }

        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return foundUser;
    }


    private List<serverHandlerThread> getClients()
    {
        return clients;
    }

    //Starts the server and begins accepting clients
    private void startServer()
    {
        clients = new ArrayList<>();
        ServerSocket serverSocket;
        try
        {
            serverSocket = new ServerSocket(serverPort);
            acceptClients(serverSocket);
        }
        catch (IOException e)
        {
            logger("Could not listen on port: " + serverPort);
            System.exit(1);
        }
    }

    //Continuously accept clients
    private void acceptClients(ServerSocket serverSocket)
    {
        logger("Server starts port = " + serverSocket.getLocalSocketAddress());
        while (true)
        {
            try
            {
                Socket socket = serverSocket.accept();
                //logger("Accepts: " + socket.getRemoteSocketAddress());
                serverHandlerThread client = new serverHandlerThread(this, socket);
                Thread thread = new Thread(client);
                thread.setDaemon(true);
                thread.start();
                clients.add(client);
            }
            catch (IOException e)
            {
                System.err.println("Accept failed on:" + serverPort);
            }
        }
    }

    public ChatServer (int portNumber)
    {
        this.serverPort = portNumber;
    }

    public static void main(String[] args) 
    {
        ChatServer server  = new ChatServer(portNumber);
        server.populateUsers();
        server.startServer();

    }

    /**
     * Task that handles all connections received from the client
     */
    public class serverHandlerThread implements Runnable
    {
        private Socket socket;
        //private BufferedWriter clientOut;
        private ObjectOutputStream toClient;
        private ChatServer server;

        //Constructor
        serverHandlerThread(ChatServer server, Socket socket)
        {
            this.server = server;
            this.socket = socket;
        }

        private ObjectOutputStream getWriter()
        {
            return toClient;
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
                        Users user = (Users) fromClient.readObject();
                        //System.out.println(input);

                        switch (input)
                        {
                            //Logout the user
                            case ".logout":
                                //Set the user to being logged out and print the log
                                for (int i = 0; i < server.usersList.size(); i++)
                                {
                                    if (user.getUserName().equals(server.usersList.get(i).getUserName()))
                                    {
                                        server.usersList.get(i).setLoggedIn(false);
                                        logger(user.getUserName() + " has logged out");
                                    }
                                }

                                server.clients.remove(this);
                                break;

                            //If clients sets .register command then register new user
                            case ".register":
                                server.registerUsers(user);
                                server.clients.remove(this);
                                break;

                            //If clients sends .findUser command then see if user exists in DB
                            case ".findUser":
                                //Create a pair and find the user
                                Pair<Boolean, Users> findUsers;
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
                                        for (int i = 0; i < server.usersList.size(); i++)
                                        {
                                            if (findUsers.getSecond().getUserName().equals(server.usersList.get(i).getUserName()))
                                            {
                                                server.usersList.get(i).setLoggedIn(true);
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
                                server.clients.remove(this);
                                break;

                            //Push message received to other clients
                            default:
                                //System.out.println("Sending message to clients");
                                server.clients.remove(this);
                                for (serverHandlerThread thatClient : server.getClients())
                                {
                                    ObjectOutputStream thatClientOut = thatClient.getWriter();
                                    if (thatClientOut != null)
                                    {
                                        thatClientOut.writeUTF(user.getUserName() + ": " + input + "\r\n");
                                        thatClientOut.flush();
                                    }
                                }
                                break;
                        }
                    }

                }
            }
            catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }
}


