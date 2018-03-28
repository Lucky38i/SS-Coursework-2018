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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
            System.out.println("User: " + user.getUserName() + " added to database");
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

            //If column result is empty return empty
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
            }
        }

        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return foundUser;
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
        clients = new ArrayList<>();
        ServerSocket serverSocket;
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

    /**
     * Task that handles all connections received from the client
     */
    public class serverHandlerThread implements Runnable
    {
        private Socket socket;
        private PrintWriter clientOut;
        private ChatServer server;

        //Constructor
        serverHandlerThread(ChatServer server, Socket socket)
        {
            this.server = server;
            this.socket = socket;
        }

        private PrintWriter getWriter()
        {
            return clientOut;
        }

        @Override
        public void run ()
        {
            try(InputStream fromClient = socket.getInputStream(); OutputStream toClient = socket.getOutputStream())
            {
                //Setup I/O
                this.clientOut = new PrintWriter(toClient, false);
                Scanner in = new Scanner(fromClient);
                ObjectInputStream fromClientObject = new ObjectInputStream(fromClient);

                while(!socket.isClosed())
                {
                    //If server has received a message
                    if(in.hasNextLine())
                    {
                        //Set received message to string and print
                        String input = in.nextLine();
                        System.out.println(input);

                        //If clients sets .register command then register new user
                        //TODO Fix this!
                        if (input.equals(".register"))
                        {
                            Object obj = fromClientObject.readObject();
                            Users user = (Users) obj;
                            server.registerUsers(user);

                            System.out.println("Closing socket: " + socket.getRemoteSocketAddress());
                            socket.close();
                            clients.remove(this);
                            in.close();
                        }

                        //If clients sends .findUser command then see if user exists in DB
                        if (input.equals(".findUser"))
                        {
                            //Read object from client
                            Object obj = fromClientObject.readObject();
                            Users user = (Users) obj;

                            //Create a pair and find the user
                            Pair<Boolean,Users> findUsers;
                            findUsers = server.findUsers(user);

                            if(findUsers.getFirst())
                            {
                                this.clientOut.write("True");
                                this.clientOut.flush();
                            }
                            else
                            {
                                this.clientOut.write("false");
                                this.clientOut.flush();
                            }

                            System.out.println("Closing socket: " + socket.getRemoteSocketAddress());
                            socket.close();
                            clients.remove(this);

                            in.close();
                        }
                        //Push message received to other clients
                        /*
                        else
                        {
                            for (serverHandlerThread thatClient : server.getClients())
                            {
                                PrintWriter thatClientOut = thatClient.getWriter();
                                if (thatClientOut != null)
                                {
                                    thatClientOut.write(input + "\r\n");
                                    thatClientOut.flush();
                                }
                            }
                        }*/
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


