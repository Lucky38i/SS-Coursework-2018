package client.address.model;


import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
* Model class for for users
*
* @author Alex McBean
*/

public class Users
{

    //Variables
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
        this.firstName = new SimpleStringProperty("Alex");
        this.lastName = new SimpleStringProperty("McBean");
        this.city = new SimpleStringProperty("Nottingham");
        this.birthday = new SimpleObjectProperty<LocalDate>(LocalDate.of(1998, 2, 19));
        this.musicGenre = new SimpleListProperty<>(FXCollections.observableArrayList());

        this.musicGenre.add("Hip-Hop");

    }

    /* Setters and Getters */
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
