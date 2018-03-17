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


import Resources.Users;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            System.out.println("Connection to database established");
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    /**
     * Connects to the local database
     * and adds the results to a user which then gets added
     * to a list of users
     */
    private void populateUsers()
    {
        String sqlQuery = "SELECT userName, firstName, lastName, birthday, City, userID FROM Users";


        try
        {
            Connection conn = this.connect();

            Statement statement1 = conn.createStatement();
            Statement statement2 = conn.createStatement();

            ResultSet resultSet = statement1.executeQuery(sqlQuery);

            while (resultSet.next())
            {
                Users user = new Users();
                user.setUserID(resultSet.getInt("userID"));
                user.setUserName(resultSet.getString("userName"));
                user.setFirstName(resultSet.getString("firstName"));
                user.setLastName(resultSet.getString("lastName"));
                user.setCity(resultSet.getString("City"));
                user.setBirthday(resultSet.getDate("birthday").toLocalDate());

                String findMusicGenres = "SELECT musicGenre FROM musicGenres a, Users b\n" +
                        "WHERE a.userID = " + resultSet.getInt("userID") + " AND  a.userID = b.userID";

                ResultSet musicResult = statement2.executeQuery(findMusicGenres);
                while (musicResult.next())
                {
                    user.addMusicGenre(musicResult.getString("musicGenre"));
                }
                usersList.add(user);
            }
            System.out.println("Database built successfully ");

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
    public void registerUsers(Users user)
    {

        String addUser = "INSERT INTO User(userName, firstName, lastName, birthday, City) VALUES(?,?,?,?,?)";
        String findUserID = "SELECT userID FROM Users WHERE userName = " + user.getUserName();
        String addMusicGenres = "INSERT INTO musicGenres(userID, musicGenre) VALUES(?,?)";
        try
        {
            //Creates a connection
            Connection conn = this.connect();

            //A statement to update the database
            PreparedStatement preparedStatement = conn.prepareStatement(addUser);

            //A result set finding the userID
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(findUserID);

            //Sets each value and executes the update
            preparedStatement.setString(1,user.getUserName());
            preparedStatement.setString(2,user.getFirstName());
            preparedStatement.setString(3,user.getLastName());
            preparedStatement.setDate(4,Date.valueOf(user.getBirthday()));
            preparedStatement.setString(5, user.getCity());
            preparedStatement.executeUpdate();

            //A new statement to add musicGenres using the newly created user
            PreparedStatement preparedStatement1 = conn.prepareStatement(addMusicGenres);
            while(resultSet.next())
            {
                for (int i=0; i < user.getMusicGenre().size(); i++)
                {
                    preparedStatement1.setInt(1, resultSet.getInt("userID"));
                    preparedStatement.setString(2, user.getMusicGenre().get(i));
                    preparedStatement.executeUpdate();
                }
            }

            usersList.add(user);
            System.out.println("User: " + user.getUserName() + " added to databse");
        }

        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }

    }
    
    
    public static void main(String[] args) 
    {
        ChatServer server  = new ChatServer(portNumber);
        server.populateUsers();
        server.startServer();

    }
    
    public ChatServer (int portNumber)
    {
        this.serverPort = portNumber;
    }
    
    public List<serverHandlerThread> getClients()
    {
        return clients;
    }
    
    private void startServer()
    {
        clients = new ArrayList<serverHandlerThread>();
        ServerSocket serverSocket = null;
        try
        {
            serverSocket = new ServerSocket(serverPort);
            acceptClients(serverSocket);
        }
        catch (IOException e)
        {
            System.err.println("Could not listen on port: " + serverPort);
            System.exit(1);
        }
    }
    
    
    //Continuously accept clients
    private void acceptClients(ServerSocket serverSocket)
    {
        System.out.println("Server starts port = " + serverSocket.getLocalSocketAddress());
        while (true)
        {
            try 
            {
                Socket socket = serverSocket.accept();
                System.out.println("Accepts: " + socket.getRemoteSocketAddress());
                serverHandlerThread client = new serverHandlerThread(this, socket);
                Thread thread = new Thread(client);
                thread.start();
                clients.add(client);
            }
            catch (IOException e)
            {
                System.err.println("Accept failed on:" + serverPort);
            }
        }   
    }   
}


