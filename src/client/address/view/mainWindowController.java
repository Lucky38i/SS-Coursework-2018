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
import java.util.ArrayList;
import java.util.ResourceBundle;


/**
 * Controller for the mainWindow FXML file
 * @author alexmcbean
 */
public class mainWindowController implements Initializable
{
    //FXML Controller Variables
    @FXML private Text txt_UserName;
    @FXML private TextField txt_FirstName;
    @FXML private TextField txt_LastName;
    @FXML private TextField txt_City;
    @FXML private TextField txt_Birthday;
    @FXML private TextField txt_Age;
    @FXML private ListView<String> lst_Genres;
    @FXML private ListView<String> lst_Friends;
    @FXML private ListView<String> lst_OnlineUsers;
    @FXML private TextArea txt_Messages;
    @FXML private TextField txt_SendMessage;
    @FXML private ContextMenu friends_Menu;
    @FXML private ContextMenu users_Menu;

    //Variables
    private Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
    private Users user;
    private static final String host = "localhost";
    private static final int portNumber = 4444;
    Timeline theLittleTimerThatCould;


    /**
     * This sets the received user to <code>this.user</code> to be used by this controller
     * @param user receives a user object from the registerWindowController
     */
    void initData(Users user)
    {
        this.user = user;
        init();

    }

    /**
     * and extension of initData() that sets all the data in the profile view
     */
    private void init()
    {
        txt_UserName.setText(txt_UserName.getText() + " " + user.getFirstName() + "!");
        txt_FirstName.setText(user.getFirstName());
        txt_LastName.setText(user.getLastName());
        txt_City.setText(user.getCity());
        txt_Birthday.setText(String.valueOf(user.getBirthday()));
        txt_Age.setText(String.valueOf(user.getAge()));

        //TODO fix this
        lst_Genres.setItems(user.musicGenreProperty().get());
        lst_Friends.setItems(user.friendsListProperty().get());
    }


    /**
     * Method that changes the scene back to the login screen and logs the user out
     * @param actionEvent Takes the local action event to find the source and switch scene
     */
    @FXML private void open_LoginScreen(ActionEvent actionEvent)
    {
        //Starts a new task and connects to the server and logs the user out
        //Not proud of this implementation but it works
        javaFXWorker task = new javaFXWorker(user, ".logout","localhost",4444);
        task.setOnSucceeded(event ->
        Platform.runLater(() ->
        {
            theLittleTimerThatCould.stop();

            alertInfo.setTitle("");
            alertInfo.setHeaderText(null);
            alertInfo.setContentText("Successfully logged out");
            alertInfo.showAndWait();


            String loginWindow = "view/loginWindow.fxml";
            SceneSwitcher sceneSwitcher = new SceneSwitcher(loginWindow,actionEvent);
            sceneSwitcher.switchScene();
        }));
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

    }

    /**
     * Sends a user message to the server board
     * @param actionEvent not being used currently
     */
    @FXML private void send_message(ActionEvent actionEvent)
    {
        //Starts a new task and connects to the server and sends the message
        //Not proud of this implementation but it works
        Task<Users> task = new javaFXWorker(user, txt_SendMessage.getText(),"localhost",4444);
        txt_SendMessage.setText("");
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
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
     * This method retrieves the online users every 5 seconds and outputs it to the list in the
     * social tab
     * @param event currently not being used
     */
    @FXML private void getOnlineUsers(Event event)
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

        theLittleTimerThatCould.setCycleCount(Timeline.INDEFINITE);
        theLittleTimerThatCould.play();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //Connect to the server to receive messages
        Task<Void> task = new backgroundThread(this,host,portNumber);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
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

        backgroundThread(mainWindowController controller, String host, int portNumber)
        {
            this.controller = controller;
            this.host = host;
            this.portNumber = portNumber;
        }

        @Override
        protected Void call() throws Exception
        {
            try(Socket socket = new Socket(host,portNumber);
                ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream ToServer = new ObjectOutputStream(socket.getOutputStream()))
            {
                while (!socket.isClosed())
                {
                    Thread.sleep(100);
                    //Print messages from server
                    if (fromServer.available() > 0)
                    {
                        //Print out messages from the server and appends the text area
                        String input = fromServer.readUTF();
                        Platform.runLater(() -> controller.txt_Messages.appendText(input));
                    }
                }
            }
            return null;
        }
    }
}
