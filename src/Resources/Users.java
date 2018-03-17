package Resources;



import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.SimpleTimeZone;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
* Model class for for users
*
* @author Alex McBean
*/

public class Users implements Externalizable
{
    /**
     * Default constructor.
     */
    public Users()
    {
        this(null,null,null,null,null);
    }

    /**
     * Constructor with some initial data.
     * Takes in a {@code String} and sets a reference
     *
     * @param userName
     *
     */
    public Users(String userName,  String firstName, String lastName, String city, LocalDate birthday)
    {
        setUserName(userName);
        setFirstName(firstName);
        setLastName(lastName);
        setCity(city);
        setBirthday(birthday);
    }

    /* Setters and Getters */
    private transient IntegerProperty userID = new SimpleIntegerProperty(this, "id");

    public int getUserID()
    {
        return userIDProperty().get();
    }

    public void setUserID(int userID)
    {
        userIDProperty().set(userID);
    }

    public IntegerProperty userIDProperty()
    {
        return userID;
    }


    private transient StringProperty userName = new SimpleStringProperty(this,"userName");

    public String getUserName()
    {
        return userNameProperty().get();
    }

    public void setUserName(String userName)
    {
        userNameProperty().set(userName);
    }

    public StringProperty userNameProperty()
    {
        return userName;
    }


    private transient StringProperty firstName = new SimpleStringProperty(this, "firstName");

    public String getFirstName()
    {
        return firstNameProperty().get();
    }

    public void setFirstName(String firstName)
    {
        firstNameProperty().set(firstName);
    }

    public StringProperty firstNameProperty()
    {
        return firstName;
    }


    private transient StringProperty lastName = new SimpleStringProperty(this, "lastName");

    public String getLastName()
    {
        return lastNameProperty().get();
    }

    public void setLastName(String lastName)
    {
       lastNameProperty().set(lastName);
    }

    public StringProperty lastNameProperty()
    {
        return lastName;
    }


    private transient StringProperty city = new SimpleStringProperty(this, "city");

    public String getCity()
    {
        return cityProperty().get();
    }

    public void setCity(String city)
    {
        cityProperty().set(city);
    }

    public StringProperty cityProperty()
    {
        return city;
    }


    private transient ObjectProperty<LocalDate> birthday = new SimpleObjectProperty<>(this, "birthday");

    public LocalDate getBirthday()
    {
        return birthdayProperty().get();
    }

    public int getAge()
    {
        return Period.between(birthdayProperty().get(), LocalDate.now()).getYears();
    }

    public void setBirthday(LocalDate birthday)
    {
        birthdayProperty().set(birthday);
    }

    public ObjectProperty<LocalDate> birthdayProperty()
    {
        return birthday;
    }


    private transient ObservableList<String> observableList = FXCollections.observableArrayList();
    private transient ListProperty<String> musicGenre = new SimpleListProperty<>(this, "musicGenre", observableList);

    public List<String> getMusicGenre()
    {
        return musicGenreProperty().get();
    }

    public void setMusicGenre(ObservableList musicGenre)
    {
        musicGenreProperty().set(musicGenre);
    }

    public ListProperty<String> musicGenreProperty()
    {
        return musicGenre;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(getUserName());
        out.writeObject(getFirstName());
        out.writeObject(getLastName());
        out.writeObject(getCity());
        out.writeObject(getBirthday());
        //TODO Fix the musicGenre
        //out.writeObject(getMusicGenre());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        //Todo
        setUserName((String) in.readObject());
        setFirstName((String) in.readObject());
        setLastName((String) in.readObject());
        setCity((String) in.readObject());
        setBirthday((LocalDate) in.readObject());
        //TODO fix reading the musicGenre
        //setMusicGenre((ObservableList) in.readObject());
    }



}
