package Resources;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SharedSongs implements Serializable
{
    private transient StringProperty friend;
    public StringProperty friendProperty()
    {
        return friend;
    }

    private transient ListProperty<String> sharedSongs;
    public ListProperty<String> sharedSongsProperty()
    {
        return sharedSongs;
    }

    public SharedSongs()
    {
        initInstance();
    }

    private void initInstance()
    {
        friend = new SimpleStringProperty();
        sharedSongs = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    private void writeObject(ObjectOutputStream out) throws IOException
    {
        WriteObjectsHelper.writeAllProp(out,friend, sharedSongs);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        initInstance();
        ReadObjectsHelper.readAllProp(in,friend, sharedSongs);
    }

    public ObservableList<String> getSharedSongs()
    {
        return sharedSongs.get();
    }

    public void setSharedSongs(ObservableList<String> sharedSongs)
    {
        this.sharedSongs.set(sharedSongs);
    }


    public String getFriend()
    {
        return friend.get();
    }

    public void setFriend(String friend)
    {
        this.friend.set(friend);
    }
}
