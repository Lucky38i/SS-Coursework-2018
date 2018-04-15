package client.address.view;

import Resources.Users;
import client.address.SceneSwitcher;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;


/**
 * Controller for the mainWindow FXML file
 * @author alexmcbean
 */
public class mainWindowController implements Initializable
{
    //FXML Controller Variables
    @FXML private Text txt_UserName;
    @FXML private TextField txt_FirstName, txt_LastName, txt_City, txt_Birthday, txt_Age, txt_SendMessage, txt_Search;
    @FXML private ListView<String> lst_Genres, lst_Friends, lst_OnlineUsers, lst_Requests, lst_SimiliarMusicInterests;
    @FXML private TextArea txt_Messages;
    @FXML private ContextMenu friends_Menu, users_Menu, requests_Menu;

    //Variables
    private Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
    private static Users user;
    private static final String host = "localhost";
    private static final int portNumber = 4444;
    private Timeline theLittleTimerThatCould;
    private backgroundThread bgThread;
    private Task<Void> task;
    private Timeline theLittleTimerThatCouldBrother;


    /**
     * This sets the received user to <code>this.user</code> to be used by this controller
     * @param user receives a user object from the registerWindowController
     */
    void initData(Users user)
    {
        mainWindowController.user = user;

        txt_UserName.setText(txt_UserName.getText() + " " + user.getFirstName() + "!");
        txt_FirstName.setText(user.getFirstName());
        txt_LastName.setText(user.getLastName());
        txt_City.setText(user.getCity());
        txt_Birthday.setText(String.valueOf(user.getBirthday()));
        txt_Age.setText(String.valueOf(user.getAge()));

        lst_Genres.setItems(user.musicGenreProperty().get());
        lst_Friends.setItems(user.friendsListProperty().get());

        //Connect to the server to receive messages
        bgThread = new backgroundThread(this, host, portNumber, mainWindowController.user);
        task = bgThread;
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

    }

    private void setUser(Users user)
    {
        this.user = user;
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
                    theLittleTimerThatCouldBrother.stop();

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
     * @param actionEvent not being used currently
     */
    @FXML private void send_message(ActionEvent actionEvent)
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
     * @param actionEvent Not currently being used
     */
    @FXML private void send_FriendRequest(ActionEvent actionEvent)
    {
        String selectedName = lst_OnlineUsers.getSelectionModel().getSelectedItem();
        //TODO change the message
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
     * @param actionEvent
     */
    @FXML private void accept_FriendRequest(ActionEvent actionEvent)
    {
        String selectedName = lst_Requests.getSelectionModel().getSelectedItem();
        bgThread.addNextMessage(".Accept." + selectedName);
        for (int i = 0; i < bgThread.getFriendRequests().size(); ++i)
            if (bgThread.getFriendRequests().get(i).contains(selectedName))
                bgThread.getFriendRequests().remove(i);

    }

    /**
     * declines a friend request
     * @param actionEvent not currently being used
     */
    @FXML private void decline_FriendRequest(ActionEvent actionEvent)
    {
        //Retrieve the selected name and send it to the server then delete it from the list
        String selectedName = lst_Requests.getSelectionModel().getSelectedItem();
        bgThread.addNextMessage(".Decline."+ selectedName);
        for (int i = 0; i < bgThread.getFriendRequests().size(); ++i)
            if (bgThread.getFriendRequests().get(i).contains(selectedName))
                bgThread.getFriendRequests().remove(i);
    }

    public void searchMusicInterests(ActionEvent actionEvent)
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
            bgThread.addNextMessage(".Search."+txt_Search.getText());
    }

    /**
     * This method retrieves the online users every 5 seconds and outputs it to the list in the
     * social tab
     * @param event currently not being used
     */
    @FXML private void getOnlineUsers_and_Requests(Event event)
    {
        //A periodic timer that finds new users every 5 seconds
        theLittleTimerThatCould = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                //Task to find all the current online users
                //Kind of sloppy but it works
                Task<Void> task = new Task<Void>()
                {
                    private static final String host = "localhost";
                    private static final int portNumber = 4444;
                    private static final String code = ".online";
                    private ObservableList<String> onlineUserList = FXCollections.observableArrayList();
                    private Users dummyUser = new Users();

                    @Override protected Void call() throws Exception
                    {
                        try(Socket socket = new Socket(host,portNumber))
                        {
                            Thread.sleep(100);

                            ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
                            ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());

                            toServer.writeUTF(code);
                            toServer.flush();

                            toServer.writeObject(dummyUser);
                            toServer.flush();

                            Object obj = fromServer.readObject();
                            ArrayList<String> templist = (ArrayList<String>) obj;
                            onlineUserList.addAll(templist);

                            //Checks if any of the online users are friends
                            for (int i = 0; i < onlineUserList.size(); ++i)
                            {
                                for (int x = 0; x < user.getFriendsList().size(); ++x)
                                {
                                    if (onlineUserList.get(i).equals(user.getFriendsList().get(x)))
                                        onlineUserList.set(i,onlineUserList.get(i) + "(Friend)");
                                }
                            }
                            Platform.runLater(()-> lst_OnlineUsers.setItems(onlineUserList));
                        }
                        return null;
                    }
                };

                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
            }
        }));

        //Time that grabs the set of friend requests
        theLittleTimerThatCouldBrother = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                Platform.runLater(()-> lst_Requests.setItems(bgThread.getFriendRequests()));
            }
        }));


        theLittleTimerThatCould.setCycleCount(Timeline.INDEFINITE);
        theLittleTimerThatCould.playFrom(Duration.seconds(4.5));
        theLittleTimerThatCouldBrother.setCycleCount(Timeline.INDEFINITE);
        theLittleTimerThatCouldBrother.playFrom(Duration.seconds(4.5));

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
    public void open_ContextMenuRequests(ContextMenuEvent contextMenuEvent)
    {
        requests_Menu.show(lst_Requests,contextMenuEvent.getScreenX(),contextMenuEvent.getScreenY());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //TODO

    }

    /**
     * This is a Task that connects to the server and receives messages to
     * be appended to the text area
     */
    private class backgroundThread extends Task<Void>
    {
        private String host;
        private int portNumber;
        private mainWindowController controller;
        //private Users userViewer;
        private Users user;
        private final LinkedList<String> messagesToSend;
        private ObservableList<String> friendRequest = FXCollections.observableArrayList();
        private boolean hasMessages = false;


        backgroundThread(mainWindowController controller, String host, int portNumber, Users user)
        {
            this.controller = controller;
            this.host = host;
            this.portNumber = portNumber;
            this.user = user;
            messagesToSend = new LinkedList<>();
        }

        synchronized void addNextMessage(String msg)
        {
            hasMessages = true;
            messagesToSend.push(msg);
        }

        synchronized ObservableList<String> getFriendRequests()
        {
            return friendRequest;
        }

        void handleFriendRequest(String input)
        {
            String[] names = input.split("[.]");
            System.out.println(names.length + ": " + names[2]);
            friendRequest.add(names[2]);
        }
        //Writes to the UI's text area
        private void writeToUI(String input)
        {
            Platform.runLater(()-> controller.txt_Messages.appendText(input+"\r"));
        }

        @Override
        protected Void call() throws Exception
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
                    Thread.sleep(100);
                    //Print messages from server
                    if (fromServer.available() > 0)
                    {
                        //Print out messages from the server and appends the text area
                        String input = fromServer.readUTF();
                        if (input.equals("Done"))
                        {
                            socket.close();
                        }
                        else if (input.contains(".Search"))
                        {
                            ObservableList<String> templist = FXCollections.observableArrayList();
                            templist.setAll((ArrayList<String>) fromServer.readObject());
                            Platform.runLater(()-> lst_SimiliarMusicInterests.setItems(templist));

                        }
                        else if (input.contains(".Request"))
                        {
                            handleFriendRequest(input);
                        }
                        else if (input.contains(".Get"))
                        {
                            Users updateUser = (Users) fromServer.readObject();
                            Platform.runLater(()->
                            {
                                mainWindowController.user = updateUser;
                                lst_Friends.setItems(updateUser.friendsListProperty().get());
                            });
                        }
                        else if (input.contains(".Accept"))
                        {
                            String[] names = input.split("[.]");
                            writeToUI("[ADMIN]: " + names[2] + " has accepted your friend request"+"\n");
                            //Gets the updated user
                            addNextMessage(".Get."+user.getUserName());
                        }
                        else if (input.contains(".Decline"))
                        {
                            String[] names = input.split("[.]");
                            writeToUI("[ADMIN]: " + names[2] + " has declined your friend request"+"\n");

                            for (int i = 0; i < friendRequest.size(); ++i)
                                if (friendRequest.get(i).contains(names[2]))
                                    friendRequest.remove(i);
                        }
                        else
                        {
                            writeToUI(input);
                        }
                    }

                    if (hasMessages)
                    {
                        //System.out.println("I have a message to send!");
                        String nextSend;
                        synchronized (messagesToSend)
                        {
                            nextSend = messagesToSend.pop();
                            hasMessages = !messagesToSend.isEmpty();
                        }

                        toServer.writeUTF(nextSend);
                        toServer.flush();
                    }
                }
            }
            return null;
        }
    }
}
