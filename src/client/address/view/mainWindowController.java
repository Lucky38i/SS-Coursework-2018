package client.address.view;

import Resources.Pair;
import Resources.Users;
import client.address.SceneSwitcher;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Controller for the mainWindow FXML file
 * @author alexmcbean
 */
public class mainWindowController implements Initializable
{
    //FXML Controller Variables
    @FXML private Text txt_UserName;
    @FXML private TextField txt_FirstName, txt_LastName, txt_City, txt_Birthday, txt_Age, txt_SendMessage, txt_Search;
    @FXML private ListView<String> lst_Genres, lst_Friends, lst_OnlineUsers, lst_Requests, lst_SearchedUsers, lst_SearchedMusic, lst_SharedSongs;
    @FXML private TextArea txt_Messages;
    @FXML private ContextMenu friends_Menu, users_Menu, requests_Menu;
    @FXML private ToggleButton toggle_PMWindow;
    Stage PMWindowStage = new Stage();
    ChatWindowController chatWindowController;

    //Variables
    private Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
    private static Users user;
    private String host = "localhost", song;
    private int mainPortNumber = 4444;
    private int chatPortNumber = 4445;
    private Timeline theLittleTimerThatCould;
    private backgroundThread bgThread;
    private Task<Void> task;
    private ObservableList<Pair<String,ObservableList<String>>> newList = FXCollections.observableArrayList();
    private MediaPlayer mediaPlayer;

    /**
     * Instantiates all required data for this controller and starts the required timers
     * as-well as starting the private message window
     * @param user receives a user object from the registerWindowController
     */
    void initData(Users user, String host, int mainPortNumber, int chatPortNumber)
    {
        mainWindowController.user = user;
        this.host = host;
        this.mainPortNumber = mainPortNumber;
        this.chatPortNumber = chatPortNumber;

        txt_UserName.setText(txt_UserName.getText() + " " + user.getFirstName() + "!");
        txt_FirstName.setText(user.getFirstName());
        txt_LastName.setText(user.getLastName());
        txt_City.setText(user.getCity());
        txt_Birthday.setText(String.valueOf(user.getBirthday()));
        txt_Age.setText(String.valueOf(user.getAge()));

        lst_Genres.setItems(user.musicGenreProperty().get());
        lst_Friends.setItems(user.friendsListProperty().get());


        //Connect to the server to receive messages
        bgThread = new backgroundThread(this, host, mainPortNumber, mainWindowController.user);
        task = bgThread;
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        //A periodic timer that checks the list of online users and friend requests
        theLittleTimerThatCould = new Timeline(new KeyFrame(Duration.seconds(5), event -> Platform.runLater(()->
        {
            lst_OnlineUsers.setItems(bgThread.getOnlineUserList());
            lst_Requests.setItems(bgThread.getFriendRequests());
        })));

        theLittleTimerThatCould.setCycleCount(Timeline.INDEFINITE);
        theLittleTimerThatCould.playFrom(Duration.seconds(4.5));

        StartPMWindow();
    }

    /**
     * Sets the scene up for the private message stage
     */
    private void StartPMWindow()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatWindow.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            chatWindowController = loader.getController();
            chatWindowController.initData(host, chatPortNumber, user);

            //PMWindowStage.initStyle(StageStyle.UNDECORATED);

            PMWindowStage.setScene(scene);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private synchronized void addToNewList(Pair<String, ObservableList<String>> pair)
    {
        newList.add(pair);
    }

    private synchronized ObservableList<Pair<String, ObservableList<String>>> getNewList()
    {
        return newList;
    }


    @FXML private void play_Music()
    {
        //TODO
        String location = "test.mp3";
        Media hit = new Media(new File(location).toURI().toString());
        mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.play();
    }

    @FXML private void stop_Music()
    {
        mediaPlayer.stop();
        String location = "test.mp3";
        File tempFile = new File(location);
       //TODO Delete the temporary song
    }

    /**
     * This handles music (incomplete javaDoc)
     */
    @FXML private void handleMusic()
    {

        //TODO move this to the bgThread
        Task<Void> task = new Task<Void>()
        {
            String tempSong = "test.mp3";
            int current;

            @Override
            protected Void call()
            {
                if (lst_SharedSongs.getSelectionModel().getSelectedItems().equals(""))
                {
                    alertInfo.setTitle("");
                    alertInfo.setHeaderText(null);
                    //TODO change the message
                    alertInfo.setContentText("You need to pick a song");
                    alertInfo.showAndWait();
                }
                else
                {
                    song = lst_SharedSongs.getSelectionModel().getSelectedItem();
                    try (Socket socket = new Socket(host, mainPortNumber);
                         ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
                         ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream()))
                    {
                        toServer.writeUTF(".Music." + song);
                        toServer.flush();


                        File file = new File(tempSong);
                        file.createNewFile();

                        byte[] buffer = new byte[4096];
                        int fileLength = fromServer.readInt();

                        try(BufferedOutputStream bOS = new BufferedOutputStream(new FileOutputStream(file)))
                        {
                            while (fileLength > 0 && (current = fromServer.read(buffer, 0, Math.min(4096, fileLength))) > 0)
                            {
                                bOS.write(buffer, 0, current);
                                fileLength -= current;
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        task.setOnSucceeded(event ->
        Platform.runLater(this::play_Music));
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Method that changes the scene back to the login screen and logs the user out
     * @param actionEvent Takes the local action event to find the source and switch scene
     */
    @FXML private void open_LoginScreen(ActionEvent actionEvent)
    {
        bgThread.addNextMessage(".logout");
        task.setOnSucceeded(event ->
                Platform.runLater(() ->
                {
                    theLittleTimerThatCould.stop();
                    chatWindowController.getTheTimer().stop();
                    chatWindowController.bgThread.addNextMessage(".LogoffChat");
                    PMWindowStage.close();


                    alertInfo.setTitle("");
                    alertInfo.setHeaderText(null);
                    alertInfo.setContentText("Successfully logged out");
                    alertInfo.showAndWait();

                    String loginWindow = "view/loginWindow.fxml";
                    SceneSwitcher sceneSwitcher = new SceneSwitcher(loginWindow,actionEvent);
                    sceneSwitcher.switchScene();
                }));
    }

    /**
     * Sends a user message to the server board
     */
    @FXML private void send_message()
    {
        if (!txt_SendMessage.getText().equals(""))
        {
            bgThread.addNextMessage(txt_SendMessage.getText());
            txt_SendMessage.setText("");
        }
        else
        {
            alertInfo.setTitle("");
            alertInfo.setHeaderText(null);
            alertInfo.setContentText("your message can't be empty");
            alertInfo.showAndWait();
        }
    }

    /**
     * This sends a friend request to the user specified
     * as-well as checking if an inappropriate user is not selected
     */
    @FXML private void send_FriendRequest()
    {
        String selectedName = lst_OnlineUsers.getSelectionModel().getSelectedItem();
        if (selectedName.contains(("(Friend)")) || selectedName.equals(user.getUserName()))
        {
            alertInfo.setTitle("");
            alertInfo.setHeaderText(null);
            //TODO change the message
            alertInfo.setContentText("Why would you do that?");
            alertInfo.showAndWait();
        }
        else bgThread.addNextMessage(".Request." + lst_OnlineUsers.getSelectionModel().getSelectedItem());
    }

    /**
     * accept the selected friend request
     */
    @FXML private void accept_FriendRequest()
    {
        String selectedName = lst_Requests.getSelectionModel().getSelectedItem();
        bgThread.addNextMessage(".Accept." + selectedName);
        for (int i = 0; i < bgThread.getFriendRequests().size(); ++i)
            if (bgThread.getFriendRequests().get(i).contains(selectedName))
                bgThread.getFriendRequests().remove(i);

    }

    /**
     * declines a friend request
     */
    @FXML private void decline_FriendRequest()
    {
        //Retrieve the selected name and send it to the server then delete it from the list
        String selectedName = lst_Requests.getSelectionModel().getSelectedItem();
        bgThread.addNextMessage(".Decline."+ selectedName);
        for (int i = 0; i < bgThread.getFriendRequests().size(); ++i)
            if (bgThread.getFriendRequests().get(i).contains(selectedName))
                bgThread.getFriendRequests().remove(i);
    }

    /**
     * This searches for the users who have a specific music interest
     */
    public void searchMusicInterests()
    {
        if (txt_Search.getText().equals(""))
        {
            alertInfo.setTitle("");
            alertInfo.setHeaderText(null);
            //TODO change the message
            alertInfo.setContentText("This box can't be empty");
            alertInfo.showAndWait();
        }
        else
            newList.clear();
            bgThread.addNextMessage(".Search."+txt_Search.getText());
    }

    /**
     * This get the music genres for the selected user and
     * places it in the list
     */
    @FXML private void getMusicInterests()
    {
        Boolean done = false;
        for (int i = 0; i < newList.size() && !done; ++i)
        {
            if (newList.get(i).getFirst().equals(lst_SearchedUsers.getSelectionModel().getSelectedItem()))
            {
                lst_SearchedMusic.setItems(null);
                lst_SearchedMusic.setItems(newList.get(i).getSecond());
                done = true;
            }
        }
    }

    /**
     * Retrieve the songs shared with the selected friend
     */
    @FXML private void getSharedSongs()
    {
        boolean done = false;
        for (int i = 0; i < user.sharedSongsListProperty().get().size() && !done; ++i)
        {
            if (user.getSharedSongsList().get(i).getFriend().equals(lst_Friends.getSelectionModel().getSelectedItem()))
            {
                lst_SharedSongs.setItems(null);
                lst_SharedSongs.setItems(user.getSharedSongsList().get(i).getSharedSongs());
                done = true;
            }
            else
                lst_SharedSongs.setItems(null);
        }
    }


    /**
     * Opens the context menu for friends list view
     * @param contextMenuEvent needed to get menu's X Y coordinate
     */
    @FXML private void open_contextMenuFriends(ContextMenuEvent contextMenuEvent)
    {
        friends_Menu.show(lst_Friends, contextMenuEvent.getScreenX(),contextMenuEvent.getScreenY());
    }

    /**
     * Opens the context menu for the online users list view
     * @param contextMenuEvent needed to get menu's X & Y coordinate
     */
    @FXML private void open_ContextMenuUsers(ContextMenuEvent contextMenuEvent)
    {
        users_Menu.show(lst_OnlineUsers,contextMenuEvent.getScreenX(),contextMenuEvent.getScreenY());
    }

    /**
     * opens the context menu for the requests list view
     * @param contextMenuEvent needed to get the menu's X & y coordinates
     */
    @FXML private void open_ContextMenuRequests(ContextMenuEvent contextMenuEvent)
    {
        requests_Menu.show(lst_Requests,contextMenuEvent.getScreenX(),contextMenuEvent.getScreenY());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //TODO
        /*
        PMWindowStage.setOnCloseRequest(event ->
        {
            chatWindowController.bgThread.addNextMessage(".LogoffChat");
            System.out.println("Closing PM Window");
        }); */
    }

    /**
     * Toggles the private message window to either hide or show
     */
    public void toggle_PMWindow()
    {
        if (toggle_PMWindow.selectedProperty().get())
        {
            PMWindowStage.show();
        }
        else
        {
            PMWindowStage.hide();
        }

    }


    /**
     * This is a Task that connects to the server and receives messages to
     * be appended to the text area
     */
    public class backgroundThread extends Task<Void>
    {
        private String host;
        private int portNumber;
        private mainWindowController controller;
        private Users user;
        private final LinkedBlockingQueue<String> messagesToSend;
        private ObservableList<String> friendRequest = FXCollections.observableArrayList();
        private ObservableList<String> searchedUsers = FXCollections.observableArrayList();
        private ObservableList<String> onlineUserList = FXCollections.observableArrayList();
        private boolean hasMessages = false;


        backgroundThread(mainWindowController controller, String host, int portNumber, Users user)
        {
            this.controller = controller;
            this.host = host;
            this.portNumber = portNumber;
            this.user = user;
            messagesToSend = new LinkedBlockingQueue<>();
        }

        /**
         * Add a new message to the messages to be
         * sent to the server
         * @param msg The message to the sent to the server
         */
        synchronized void addNextMessage(String msg)
        {
            try
            {
                hasMessages = true;
                messagesToSend.put(msg);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        synchronized ObservableList<String> getFriendRequests()
        {
            return friendRequest;
        }

        synchronized  ObservableList<String> getOnlineUserList()
        {
            return onlineUserList;
        }

        private void setOnlineUserList(ArrayList<String> list)
        {
            Platform.runLater(()->this.onlineUserList.setAll(list));
        }

        /**
         * Handles friend requests
         * @param input
         */
        void handleFriendRequest(String input)
        {
            String[] names = input.split("[.]");
            friendRequest.add(names[2]);
        }
        //Writes to the UI's text area
        private void writeToUI(String input)
        {
            Platform.runLater(()-> controller.txt_Messages.appendText(input+"\r"));
        }

        @Override
        protected Void call()
        {
            try(Socket socket = new Socket(host,portNumber);
                ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream()))
            {
                toServer.writeUTF(".Viewer");
                toServer.flush();

                toServer.writeObject(user);
                toServer.flush();

                while (!socket.isClosed())
                {
                    if (fromServer.available() > 0)
                    {
                        //Print messages from server
                        String input = fromServer.readUTF();


                        if (input.equals("Done"))
                        {
                            socket.close();
                        }

                        else if (input.contains(".online"))
                        {
                            Object obj = fromServer.readObject();
                            ArrayList<String> templist = (ArrayList<String>) obj;

                            //Checks if any of the online users are friends
                            for (int i = 0; i < templist.size(); ++i)
                            {
                                for (int x = 0; x < user.getFriendsList().size(); ++x)
                                {
                                    if (templist.get(i).equals(user.getFriendsList().get(x)))
                                    {
                                        templist.set(i, templist.get(i) + "(Friend)");
                                    }
                                }
                            }
                            setOnlineUserList(templist);
                        } else if (input.contains(".Search"))
                        {
                            //Read the received list and set it as a temporary list
                            ObservableList<Users> tempList = FXCollections.observableArrayList();
                            tempList.setAll((ArrayList<Users>) fromServer.readObject());
                            ArrayList<String> tempListForUsers = new ArrayList<>();

                            for (Users i : tempList)
                            {
                                Pair<String, ObservableList<String>> newPair = new Pair<>();
                                newPair.setFirst(i.getUserName());
                                newPair.setSecond(i.musicGenreProperty().get());
                                addToNewList(newPair);
                            }

                            for (int i = 0; i < getNewList().size(); ++i)
                            {
                                tempListForUsers.add(getNewList().get(i).getFirst());
                            }

                            Platform.runLater(() ->
                            {
                                lst_SearchedUsers.refresh();
                                searchedUsers.setAll(tempListForUsers);
                                lst_SearchedUsers.setItems(searchedUsers);
                            });

                        } else if (input.contains(".Request"))
                        {
                            handleFriendRequest(input);
                        } else if (input.contains(".Get"))
                        {
                            Users updateUser = (Users) fromServer.readObject();

                            Platform.runLater(() ->
                            {
                                mainWindowController.user = updateUser;
                                lst_Friends.setItems(updateUser.friendsListProperty().get());
                            });
                        } else if (input.contains(".Accept"))
                        {
                            String[] names = input.split("[.]");
                            writeToUI("[ADMIN]: " + names[2] + " has accepted your friend request" + "\n");

                        } else if (input.contains(".Decline"))
                        {
                            String[] names = input.split("[.]");
                            writeToUI("[ADMIN]: " + names[2] + " has declined your friend request" + "\n");

                            for (int i = 0; i < friendRequest.size(); ++i)
                                if (friendRequest.get(i).contains(names[2]))
                                    friendRequest.remove(i);
                        } else
                        {
                            writeToUI(input);
                        }
                    }

                    if (hasMessages)
                    {
                        String nextSend;
                        synchronized (messagesToSend)
                        {

                            nextSend = messagesToSend.take();
                            hasMessages = !messagesToSend.isEmpty();
                        }

                        toServer.writeUTF(nextSend);
                        toServer.flush();
                    }
                }



            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
}
