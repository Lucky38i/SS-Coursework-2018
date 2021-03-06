package Resources;



import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
* Model class for for users
*
* @author alexmcbean
*/

public class Users implements Serializable
{
    private transient IntegerProperty userID;
    private IntegerProperty userIDProperty()
    {
        return userID;
    }

    private transient StringProperty userName;
    private StringProperty userNameProperty()
    {
        return userName;
    }

    private transient StringProperty firstName;
    private StringProperty firstNameProperty()
    {
        return firstName;
    }

    private transient StringProperty lastName;
    private StringProperty lastNameProperty()
    {
        return lastName;
    }

    private transient StringProperty city;
    private StringProperty cityProperty()
    {
        return city;
    }

    private transient ObjectProperty<LocalDate> birthday;
    private ObjectProperty<LocalDate> birthdayProperty()
    {
        return birthday;
    }

    private transient ListProperty<String> musicGenre;
    public ListProperty<String> musicGenreProperty()
    {
        return musicGenre;
    }

    private transient ListProperty<String> friendsList;
    public ListProperty<String> friendsListProperty() { return friendsList; }

    private transient BooleanProperty loggedIn;
    private BooleanProperty loggedInProperty()
    {
        return loggedIn;
    }

    private transient ListProperty<SharedSongs> sharedSongsList;
    public ListProperty<SharedSongs> sharedSongsListProperty()
    {
        return sharedSongsList;
    }


    /**
     * Default constructor.
     */
    public Users()
    {
        initInstance();
    }

    private void initInstance()
    {
        userID = new SimpleIntegerProperty();
        userName = new SimpleStringProperty();
        firstName = new SimpleStringProperty();
        lastName = new SimpleStringProperty();
        city = new SimpleStringProperty();
        birthday = new SimpleObjectProperty<>();
        musicGenre = new SimpleListProperty<>(FXCollections.observableArrayList());
        friendsList = new SimpleListProperty<>(FXCollections.observableArrayList());
        sharedSongsList = new SimpleListProperty<>(FXCollections.observableArrayList());
        loggedIn = new SimpleBooleanProperty();
        loggedIn.set(false);
    }

    private void writeObject(ObjectOutputStream out) throws IOException
    {
        WriteObjectsHelper.writeAllProp(out, userID,userName,firstName,lastName,city,birthday,musicGenre, friendsList, sharedSongsList);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        initInstance();
        ReadObjectsHelper.readAllProp(in, userID,userName,firstName,lastName,city,birthday,musicGenre, friendsList, sharedSongsList);
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


    public String getUserName()
    {
        return userName.get();
    }
    public void setUserName(String userName)
    {
        this.userName.set(userName);
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


    public String getCity()
    {
        return city.get();
    }
    public void setCity(String city)
    {
        this.city.set(city);
    }


    public LocalDate getBirthday()
    {
        return birthday.get();
    }
    public void setBirthday(LocalDate birthday)
    {
        this.birthday.set(birthday);
    }
    public int getAge()
    {
        return Period.between(birthday.get(), LocalDate.now()).getYears();
    }


    public List<String> getMusicGenre()
    {
        return musicGenre.get();
    }
    public void setMusicGenre(ObservableList musicGenre)
    {
        this.musicGenre.set(musicGenre);
    }

    public List<String> getFriendsList()
    {
        return friendsList.get();
    }
    public void setFriendsList(ObservableList friendsList)
    {
        this.friendsList.set(friendsList);
    }

    public List<SharedSongs> getSharedSongsList()
    {
        return sharedSongsList.get();
    }
    public void setSharedSongsList(ObservableList sharedSongsList)
    {
        this.sharedSongsList.set(sharedSongsList);
    }

    public Boolean getLoggedIn()
    {
        return loggedIn.get();
    }
    public void setLoggedIn(Boolean state)
    {
        this.loggedIn.set(state);
    }
}

