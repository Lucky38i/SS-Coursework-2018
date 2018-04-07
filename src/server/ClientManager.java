package server;


import Resources.Users;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class ClientManager
{
    private final List<serverHandlerThread> clients;
    private final List<Users> users;

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

    synchronized void addUser(Users user)
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

}
