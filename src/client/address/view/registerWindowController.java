package client.address.view;


import Resources.Users;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class registerWindowController implements Initializable
{

    @FXML private DatePicker date_Birthday;
    @FXML private TextField txt_FirstName;
    @FXML private TextField txt_Username;
    @FXML private TextField txt_LastName;
    @FXML private TextField txt_City;
    @FXML private ChoiceBox choice_Genres;
    @FXML private TextArea txtArea_MuscList;
    @FXML private Hyperlink HL_Cancel;

    private Alert alertError = new Alert(Alert.AlertType.ERROR);
    private String loginWindow = "loginWindow.fxml";
    private Users user = new Users();


    public ChoiceBox getChoice_Genres()
    {
        return choice_Genres;
    }

    public void open_LoginWindow(ActionEvent actionEvent) throws IOException
    {

        //Create a loader and set it's location to mainWindow
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(loginWindow));

        //Set the scene to the loader's location
        Parent loginWindowParent = loader.load();
        Scene loginWindowScene = new Scene(loginWindowParent);

        //Find the stage information
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        window.setScene(loginWindowScene);
        window.show();

    }

    public void btn_registerUser(ActionEvent actionEvent) throws IOException
    {
        if (txt_FirstName.getText().equals("") || txt_Username.getText().equals("") || txt_LastName.getText().equals("") || txt_City.getText().equals(""))
        {
            alertError.setTitle("");
            alertError.setHeaderText(null);
            alertError.setContentText("One of the fields is blank");
            alertError.showAndWait();
        }
        else
        {
            user.setUserName(txt_Username.getText());
            user.setFirstName(txt_FirstName.getText());
            user.setLastName(txt_LastName.getText());
            user.setCity(txt_City.getText());
            user.setBirthday(date_Birthday.getValue());


            Task<Void> task = new registerUser(user);
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * A class creates a seperates task along side the JAVAFX Thread
     * that creates a connection with the server and sends the user object to be registered with the
     * SQL database
     */
    private class registerUser extends Task<Void>
    {
        Users user;
        private static final String code = ".register";
        private static final String host = "localhost";
        private static final int portNumber = 4444;
        private clientThread clientThread;


        public registerUser(Users user)
        {
            this.user = user;
        }

        @Override
        protected Void call() throws Exception
        {
            try
            {
                Socket socket = new Socket(host, portNumber);
                Thread.sleep(1000);

                //Setup I/O
                ObjectOutputStream outToServerObject = new ObjectOutputStream(socket.getOutputStream());
                //PrintWriter serverOutString = new PrintWriter(socket.getOutputStream(), false);
                InputStream serverInString = socket.getInputStream();

                //serverOutString.println(code);
               // serverOutString.flush();

                outToServerObject.writeObject(user);
                outToServerObject.flush();
            }
            catch (IOException e)
            {
                System.err.println("Fatal Connection error!");
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        getChoice_Genres().getItems().add("African");
        getChoice_Genres().getItems().add("Asian");
        getChoice_Genres().getItems().add("Avant-Garde");
        getChoice_Genres().getItems().add("Blues");
        getChoice_Genres().getItems().add("Caribbean");
        getChoice_Genres().getItems().add("Comedy");
        getChoice_Genres().getItems().add("Country");
        getChoice_Genres().getItems().add("Easy Listening");
        getChoice_Genres().getItems().add("Electronic");
        getChoice_Genres().getItems().add("Folk");
        getChoice_Genres().getItems().add("Hip Hop");
        getChoice_Genres().getItems().add("Jazz");
        getChoice_Genres().getItems().add("Latin");
        getChoice_Genres().getItems().add("Pop");
        getChoice_Genres().getItems().add("R&B and Soul");
        getChoice_Genres().getItems().add("Rock");
    }
}
