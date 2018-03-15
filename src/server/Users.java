package server;


import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
* Model class for for users
*
* @author Alex McBean
*/

public class Users
{

    //Variables
    private final IntegerProperty userID;
    private final StringProperty userName;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty city;
    private final ObjectProperty<LocalDate> birthday;
    private final ListProperty<String> musicGenre;
    /**
     * Default constructor.
     */
    public Users()
    {
        this(null,0,null,null,null,null, null);
    }

    /**
     * Constructor with some initial data.
     * Takes in a {@code String} and sets a reference
     *
     * @param userName
     *
     */
    public Users(String userName, int userID, String firstName, String lastName, String city, LocalDate birthday, List<String> musicGenres)
    {
        this.userName = new SimpleStringProperty(userName);

        // Some initial dummy data, just for convenient testing.
        this.userID = new SimpleIntegerProperty(userID);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.city = new SimpleStringProperty(city);
        this.birthday = new SimpleObjectProperty<LocalDate>(birthday);
        ObservableList<String> observableList = FXCollections.observableArrayList(musicGenres);
        this.musicGenre = new SimpleListProperty<String>(observableList);

    }

    /* Setters and Getters */

    public int getUserID()
    {
        return userID.get();
    }

    public void setUserID(int userID)
    {
        this.userID.set(userID);
    }

    public IntegerProperty userIDProperty()
    {
        return userID;
    }

    public String getUserName()
    {
        return userName.get();
    }

    public void setUserName(String userName)
    {
        this.userName.set(userName);
    }

    public StringProperty userNameProperty()
    {
        return userName;
    }

    public String getFirstName()
    {
        return firstName.get();
    }

    public void setFirstName(String firstName)
    {
        this.firstName.set(firstName);
    }

    public String getLastName()
    {
        return lastName.get();
    }

    public void setLastName(String lastName)
    {
       this.lastName.set(lastName);
    }

    public StringProperty lastNameProperty()
    {
        return lastName;
    }

    public StringProperty firstNameProperty()
    {
        return firstName;
    }

    public List<String> getMusicGenre()
    {
        return musicGenre.get();
    }

    public void addMusicGenre(String musicGenre)
    {
        this.musicGenre.add(musicGenre);
    }

    public ListProperty<String> musicGenreProperty()
    {
        return musicGenre;
    }

    public String getCity()
    {
        return city.get();
    }

    public void setCity(String city)
    {
        this.city.set(city);
    }

    public StringProperty cityProperty()
    {
        return city;
    }

    public LocalDate getBirthday()
    {
        return birthday.get();
    }

    public int getAge()
    {
         return Period.between(birthday.get(), LocalDate.now()).getYears();
    }

    public void setBirthday(LocalDate birthday)
    {
        this.birthday.set(birthday);
    }

    public ObjectProperty<LocalDate> birthdayProperty()
    {
        return birthday;
    }



}
