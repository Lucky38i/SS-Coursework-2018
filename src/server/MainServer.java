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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * This is the main server that handles all objects going in and out of the server
 * such as registration, log in, friends, music streaming etc.
 * @author alexmcbean
 */
public class MainServer
{

    //Static variables
    private static final int portNumber = 4444;
    
    //Variables
    private int serverPort;
    //public final List<Users> usersList = new ArrayList<>();
    //private final Map<serverHandlerThread,Socket> testMap = new HashMap<>();
    private final ClientManager clientManagerTemp = new ClientManager();
    private Socket socket;

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

    /**
     * Very basic logger that prints out
     * the current time and date
     * @param msg used when printing the log
     */
    private void logger(String msg)
    {
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

                //usersList.add(user);
                clientManagerTemp.addUser(user);
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


    /**
     * Find if the user exists in the database
     * @param user finds the recevied user in an SQL query
     * @return true if a user is found otherwise false
     */
    public Pair<Boolean, Users> findUsers(Users user)
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
                for (Users i : clientManagerTemp.usersList())
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

    //Starts the server and begins accepting clients
    private void startServer()
    {
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

    serverHandlerThread Client1(Socket socket1)
    {
        serverHandlerThread client2 = new serverHandlerThread(this,socket1,clientManagerTemp);
        clientManagerTemp.addClient(client2);
        return client2;
    }

    //Continuously accept clients
    private void acceptClients(ServerSocket serverSocket)
    {
        logger("Server starts port = " + serverSocket.getLocalSocketAddress());
        ExecutorService executorService = Executors.newCachedThreadPool();
        while (true)
        {
            try
            {
                socket = serverSocket.accept();
                //logger("Accepts: " + socket.getRemoteSocketAddress());
                //serverHandlerThread client = new serverHandlerThread(this, socket,clientManagerTemp);
                /*
                Thread thread = new Thread(client);
                thread.setDaemon(true);
                thread.start();*/


                FutureTask futureTask = new FutureTask(Client1(socket),null);
                executorService.execute(futureTask);

            }
            catch (IOException e)
            {
                System.err.println("Accept failed on:" + serverPort);
            }
        }
    }

    public MainServer(int portNumber)
    {
        this.serverPort = portNumber;
    }

    public static void main(String[] args) 
    {
        MainServer server  = new MainServer(portNumber);
        server.populateUsers();
        server.startServer();

    }
}


