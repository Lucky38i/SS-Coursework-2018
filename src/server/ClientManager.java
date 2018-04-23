package server;


import Resources.Pair;
import Resources.SharedSongs;
import Resources.Users;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A client manager that synchronizes all data between threads
 * @author alexmcbean
 */
public class ClientManager
{
    private final List<serverHandlerThread> clients;
    private final List<Users> users;
    private TextArea maintextArea, chatTextArea;

    /**
     * Constructor
     */
    public ClientManager(TextArea maintextArea, TextArea chatTextArea)
    {
        this.clients = new ArrayList<>();
        this.users = new ArrayList<>();
        this.maintextArea = maintextArea;
        this.chatTextArea = chatTextArea;
    }

    synchronized void addClient(serverHandlerThread client)
    {
        this.clients.add(client);
    }

    synchronized void removeClient(serverHandlerThread client)
    {
        this.clients.remove(client);
    }

    synchronized List<serverHandlerThread> clientlist()
    {
        return new ArrayList<>(this.clients);
    }

    private synchronized void addUser(Users user)
    {
        this.users.add(user);
    }

    //TODO find out if this really isn't of any use
   synchronized void removeUser(Users user)
    {
        this.users.remove(user);
    }

    synchronized List<Users> usersList()
    {
        return new ArrayList<>(this.users);
    }

    /**
     * This method adds a new friend if the user accepts a friend request
     * by updating the database as-well as the userlist
     * @param input The string input containing the accept code and the username
     * @param user The current thread's user object
     */
    synchronized void addNewFriend(String input, Users user)
    {
        String[] names = input.split("[.]");
        String findUser = "SELECT userID, userName FROM Users WHERE username = "+"'"+names[2]+"';";
        Integer tempUser = 0;

        try(Connection conn = this.connect();
            Statement statement= conn.createStatement();
            ResultSet resultSet = statement.executeQuery(findUser))
        {
            //Find the userID of the new friend
            while(resultSet.next())
            {
                tempUser = resultSet.getInt("userID");
            }

            //Add the new friend to the user's friend list
            for (int i = 0; i < usersList().size(); i++)
            {
                if (usersList().get(i).getUserName().contains(user.getUserName()))
                {
                    usersList().get(i).getFriendsList().add(names[2]);
                }

                else if(usersList().get(i).getUserName().contains(names[2]))
                {
                    usersList().get(i).getFriendsList().add(user.getUserName());
                }
            }


            String addUser = "INSERT INTO Friends(userID, friendID) VALUES(" + user.getUserID() + "," + tempUser +");";
            String addUser1 = "INSERT INTO Friends(userID, friendID) VALUES(" + tempUser + "," + user.getUserID() +");";

            try(PreparedStatement preparedStatement = conn.prepareStatement(addUser);
                PreparedStatement preparedStatement1 = conn.prepareStatement(addUser1))
            {
                //Execute the queries
                preparedStatement.executeUpdate();
                preparedStatement1.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Very basic logger that prints out
     * the current time and date
     * @param msg used when printing the log
     */
    synchronized void logger(String msg, String type)
    {
        String logMessage = LocalDate.now()+ " " +LocalTime.now() + " - " +msg +"\n";
        //System.out.println(logMessage);
        if (type.contains("Main"))
        {
            Platform.runLater(() -> maintextArea.appendText(logMessage));
        }
        else if (type.contains("Chat"))
        {
            Platform.runLater(()-> chatTextArea.appendText(logMessage));
        }
        else
        {
            Platform.runLater(()->
            {
                maintextArea.appendText(logMessage);
                chatTextArea.appendText(logMessage);
            });
        }

    }

    /**
     * Connects to the identified database and returns the Connections
     * @return the connection object
     */
    private synchronized Connection connect()
    {
        String url = "jdbc:sqlite:src/Resources/test.db";
        Connection conn = null;
        try
        {
            conn = DriverManager.getConnection(url);
            logger("Connection to database made", "Both");
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
    public synchronized void populateUsers()
    {
        String sqlQuery = "SELECT userName, firstName, lastName, birthday, City, userID, loggedIn FROM Users";

        try(Connection conn = this.connect();
            Statement userStatement = conn.createStatement();
            Statement genresStatement = conn.createStatement();
            Statement friendsStatement = conn.createStatement();
            Statement friendShareStatement = conn.createStatement();
            Statement songShareStatement = conn.createStatement();
            ResultSet resultSet = userStatement.executeQuery(sqlQuery))
        {
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

                //Add the music genre interests
                try(ResultSet musicResult = genresStatement.executeQuery(findMusicGenres))
                {
                    while (musicResult.next())
                    {
                        //Set the User's music genres based of SQL results
                        user.musicGenreProperty().get().add(musicResult.getString("musicGenre"));
                    }

                    String findFriendList = "SELECT userName FROM Users a, Friends b\n" +
                            "WHERE b.userID = " + user.getUserID() + " AND a.userID = b.friendID";

                    //Add the friends list
                    try(ResultSet friendResult = friendsStatement.executeQuery(findFriendList))
                    {
                        while (friendResult.next())
                        {
                            //Set the user's friend list based of SQL results
                            user.friendsListProperty().get().add(friendResult.getString("userName"));
                        }

                        /**
                         * The following two try statement populates the sharedSongs list
                         */
                        String findFriendShare = "SELECT userName FROM Users a, Shared_Songs b WHERE b.userID =" + user.getUserID() + " AND a.userID = b.friendID";
                        try (ResultSet friendShareResult = friendShareStatement.executeQuery(findFriendShare))
                        {
                            SharedSongs temp = new SharedSongs();
                            while (friendShareResult.next())
                            {
                                temp.setFriend(friendShareResult.getString("userName"));
                            }

                            String findSharedSongs = "SELECT FriendID, Song FROM Shared_Songs a WHERE a.UserID=" + user.getUserID();
                            try(ResultSet sharedSongsResult = songShareStatement.executeQuery(findSharedSongs))
                            {
                                while (sharedSongsResult.next())
                                {
                                    temp.sharedSongsProperty().get().add(sharedSongsResult.getString("Song"));
                                }
                            }

                            user.sharedSongsListProperty().get().add(temp);

                        }
                    }
                }
                addUser(user);
            }
            logger("Database built successfully ","Both");


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
    synchronized void registerUsers(Users user)
    {

        String addUser = "INSERT INTO Users(userName, firstName, lastName, birthday, City) VALUES(?,?,?,?,?)";
        String findUserID = "SELECT userID FROM Users WHERE userName = '" + user.getUserName() + "'";
        String addMusicGenres = "INSERT INTO musicGenres(userID, musicGenre) VALUES(?,?)";

        try(Connection conn = this.connect();
            PreparedStatement preparedStatement = conn.prepareStatement(addUser);
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(findUserID);
            PreparedStatement preparedStatement1 = conn.prepareStatement(addMusicGenres))
        {
            //Sets each value and executes the update
            preparedStatement.setString(1,user.getUserName());
            preparedStatement.setString(2,user.getFirstName());
            preparedStatement.setString(3,user.getLastName());
            preparedStatement.setDate(4,Date.valueOf(user.getBirthday()));
            preparedStatement.setString(5, user.getCity());
            preparedStatement.executeUpdate();

            while(resultSet.next())
            {
                for (int i=0; i < user.getMusicGenre().size(); i++)
                {
                    preparedStatement1.setInt(1, resultSet.getInt("userID"));
                    preparedStatement1.setString(2, user.getMusicGenre().get(i));
                    preparedStatement1.executeUpdate();
                }
            }

            addUser(user);
            logger("User: " + user.getUserName() + " added to database", "Both");
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
    synchronized Pair<Boolean, Users> findUsers(Users user)
    {
        Pair<Boolean, Users> foundUser = new Pair<>();
        foundUser.setFirst(false);
        String findUser = "SELECT * FROM Users WHERE userName = '" + user.getUserName() + "'";

        try(Connection conn = this.connect();
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(findUser))
        {
            //If column result is empty return empty and set to false or true
            foundUser.setFirst(resultSet.isBeforeFirst());
            foundUser.setSecond(new Users());

            if (foundUser.getFirst())
            {
                Users temp = new Users();
                for (Users i : usersList())
                {
                    if (i.getUserName().equals(user.getUserName()))
                    {
                        temp = i;
                    }
                }
                foundUser.setSecond(temp);
                logger("Found user: " + foundUser.getSecond().getFirstName(),"Main");
            }
            else
                logger("User not found","Main");
        }

        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return foundUser;
    }

}
