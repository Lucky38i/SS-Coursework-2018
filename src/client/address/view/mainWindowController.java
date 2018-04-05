package client.address.view;

import Resources.Users;
import client.address.SceneSwitcher;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
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
    @FXML private TableView<Users> tbl_Profile;
    @FXML private TableColumn<Users, ObservableList<String>> column_Genres;
    @FXML private TableColumn<Users, ObservableList<String>> column_Friends;
    @FXML private TextArea txt_Messages;
    @FXML private TextField txt_SendMessage;

    //Variables
    private Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
    private Users user;


    /**
     * This sets the received user to <code>this.user</code> to be used by this controller
     * @param user receives a user object from the registerWindowController
     */
    void initData(Users user)
    {
        this.user = user;
        //Initialize the user details
        init();

    }

    private void init()
    {
        txt_UserName.setText(txt_UserName.getText() + " " + user.getFirstName() + "!");
        txt_FirstName.setText(user.getFirstName());
        txt_LastName.setText(user.getLastName());
        txt_City.setText(user.getCity());
        txt_Birthday.setText(String.valueOf(user.getBirthday()));
        txt_Age.setText(String.valueOf(user.getAge()));

        //TODO fix this
        column_Genres.setCellValueFactory(new PropertyValueFactory<>("musicGenre"));
        column_Friends.setCellValueFactory(new PropertyValueFactory<>("friendsList"));
    }


    /**
     * Method that changes the scene back to the login screen
     * @param actionEvent Takes the local action event to find the source and switch scene
     */
    @FXML private void open_LoginScreen(ActionEvent actionEvent)
    {
        alertInfo.setTitle("");
        alertInfo.setHeaderText(null);
        alertInfo.setContentText("Successfully logged out");
        alertInfo.showAndWait();

        String loginWindow = "view/loginWindow.fxml";
        SceneSwitcher sceneSwitcher = new SceneSwitcher(loginWindow,actionEvent);
        sceneSwitcher.switchScene();
    }

    @FXML private void send_message(ActionEvent actionEvent)
    {
        //Starts a new task and connects to the server and sends the message
        //Not proud of this implementation but it works
        Task<Users> task = new javaFXWorker(user, txt_SendMessage.getText());
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //Connect to the server to receive messages
        Task<Void> task = new backgroundThread(this);
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
        private static final String host = "localhost";
        private static final int portNumber = 4444;
        private mainWindowController controller;

        backgroundThread(mainWindowController controller)
        {
            this.controller = controller;
        }

        @Override
        protected Void call() throws Exception
        {
            try(Socket socket = new Socket(host,portNumber);
                ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream()))
            {
                while (!socket.isClosed())
                {
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
