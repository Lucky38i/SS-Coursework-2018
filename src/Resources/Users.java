package Resources;



import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
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
    private IntegerProperty userID = new SimpleIntegerProperty(this, "id");

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


    private StringProperty userName = new SimpleStringProperty(this,"userName");

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


    private StringProperty firstName = new SimpleStringProperty(this, "firstName");

    public String getFirstName()
    {
        return firstName.get();
    }

    public void setFirstName(String firstName)
    {
        this.firstName.set(firstName);
    }

    public StringProperty firstNameProperty()
    {
        return firstName;
    }


    private StringProperty lastName = new SimpleStringProperty(this, "lastName");

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


    private List<String> musicGenres = new ArrayList<>();
    ObservableList<String> observableList = FXCollections.observableArrayList();
    private ListProperty<String> musicGenre = new SimpleListProperty<String>(this, "musicGenre", observableList);

    public ObservableList<String> getMusicGenre()
    {
        return musicGenre.get();
    }

    public ListProperty<String> musicGenreProperty()
    {
        return musicGenre;
    }

    public void setMusicGenre(ObservableList<String> musicGenre)
    {
        this.musicGenre.set(musicGenre);
    }

    public void addToMusicGenre(String type)
    {

    }


    private StringProperty city = new SimpleStringProperty(this, "city");

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


    private ObjectProperty<LocalDate> birthday = new SimpleObjectProperty<>(this, "birthday");

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

    public void setBirthdayFromString(String birthday)
    {
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MMM-dd");
        LocalDate localDate= LocalDate.parse(birthday,formatter);
        setBirthday(localDate);
    }

    public ObjectProperty<LocalDate> birthdayProperty()
    {
        return birthday;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(getUserName());
        out.writeObject(getFirstName());
        out.writeObject(getLastName());
        out.writeObject(getCity());
        out.writeObject(getBirthday());
        out.writeObject(getMusicGenre());
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
        setMusicGenre((ObservableList<String>) in.readObject());
    }



}
