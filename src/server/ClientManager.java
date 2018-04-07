package server;


import Resources.Pair;
import Resources.Users;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A client manager that synchronizes all data between threads
 * @author alexmcbean
 */
class ClientManager
{
    private final List<serverHandlerThread> clients;
    private final List<Users> users;

    /**
     * Constructor
     */
    ClientManager()
    {
        this.clients = new ArrayList<>();
        this.users = new ArrayList<>();
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

    synchronized void removeUser(Users user)
    {
        this.users.remove(user);
    }

    synchronized List<Users> usersList()
    {
        return new ArrayList<>(this.users);
    }

    /**
     * Very basic logger that prints out
     * the current time and date
     * @param msg used when printing the log
     */
    synchronized void logger(String msg)
    {
        System.out.println(LocalDate.now()+ " " +LocalTime.now() + " - " +msg);
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
            logger("Connection to database made");
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
    synchronized void populateUsers()
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
                addUser(user);
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
    synchronized void registerUsers(Users user)
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

            //A new statement to addClient musicGenres using the newly created user
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

            addUser(user);
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
    synchronized Pair<Boolean, Users> findUsers(Users user)
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

}
