package client.address.model;


import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
* Model class for for users
*
* @author Alex McBean
*/

public class Users
{

    //Variables
    private final StringProperty userName;
    private final StringProperty city;
    private final ObjectProperty<LocalDate> birthday;
    private final StringProperty musicGenre;

    /**
     * Default constructor.
     */
    public Users()
    {
        this(null);
    }

    /**
     * Constructor with some initial data.
     * Takes in a {@code String} and sets a reference
     *
     * @param userName
     *
     */
    public Users(String userName)
    {
        this.userName = new SimpleStringProperty(userName);

        // Some initial dummy data, just for convenient testing.
        this.city = new SimpleStringProperty("Nottingham");
        this.birthday = new SimpleObjectProperty<LocalDate>(LocalDate.of(1998, 2, 19));
        this.musicGenre = new SimpleStringProperty("Hip-Hop");
    }

    //Setters and Getters
    public String getUserName()
    {
        return userName.get();
    }

    public void setUserName(String userName)
    {
        this.userName.set(userName);
    }

    public StringProperty firstUserProperty()
    {
        return userName;
    }

    public String getMusicGenre()
    {
        return musicGenre.get();
    }

    public void setMusicGenre(String musicGenre)
    {
        this.musicGenre.set(musicGenre);
    }

    public StringProperty musicGenreProperty()
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

    public void setBirthday(LocalDate birthday)
    {
        this.birthday.set(birthday);
    }

    public ObjectProperty<LocalDate> birthdayProperty()
    {
        return birthday;
    }

}
