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


import client.address.model.Users;

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
    private List<server.Users> usersList;

    /**
     * Connects to the identified database and returns the Connections
     * @return the connection object
     */
    private Connection connect()
    {
        String url = "jdbc:sqlite:src/server/Resources/test.db";
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
                server.Users user = new server.Users();
                user.setUserID(resultSet.getInt("userID"));
                user.setUserName(resultSet.getString("userName"));
                user.setFirstName(resultSet.getString("firstName"));
                user.setLastName(resultSet.getString("lastName"));
                user.setCity(resultSet.getString("City"));
                user.setBirthday(resultSet.getDate("birthday").toLocalDate());

                String findMusicGenres = "SELECT musicGenre FROM musicGenres a, Users b\n" +
                        "WHeRE a.userID = " + resultSet.getInt("userID") + " AND  a.userID = b.userID";

                ResultSet musicResult = statement2.executeQuery(findMusicGenres);
                while (musicResult.next())
                {
                    user.addMusicGenre(musicResult.getString("musicGenre"));
                }
                usersList.add(user);
            }

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


