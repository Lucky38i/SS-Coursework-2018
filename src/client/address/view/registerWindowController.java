package client.address.view;


import Resources.Users;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Controller for registerWindow.fxml
 */
public class registerWindowController implements Initializable
{

    //FXML Controller Variables
    @FXML private DatePicker date_Birthday;
    @FXML private TextField txt_FirstName;
    @FXML private TextField txt_Username;
    @FXML private TextField txt_LastName;
    @FXML private TextField txt_City;
    @FXML private ComboBox choice_Genres;
    @FXML private TextArea txtArea_MuscList;
    @FXML private Hyperlink HL_Cancel;

    //Variables
    private Alert alertError = new Alert(Alert.AlertType.ERROR);
    private Alert alert = new Alert(Alert.AlertType.INFORMATION);
    private String loginWindow = "loginWindow.fxml";
    private Users user = new Users();
    private SceneSwitcher sceneSwitcher;


    public void addChoice_Genres(String genre)
    {
        choice_GenresProperty().getItems().add(genre);
    }

    private ComboBox choice_GenresProperty()
    {
        return choice_Genres;
    }

    /**
     * Opens the login window scene
     * @param actionEvent
     * @throws IOException
     */
    public void open_LoginWindow(ActionEvent actionEvent) throws IOException
    {
        sceneSwitcher = new SceneSwitcher(loginWindow,actionEvent);
        sceneSwitcher.switchScene();
    }

    /**
     * Registers the user then if successful opens back the login window
     * @param actionEvent
     * @throws IOException
     */
    public void btn_registerUser(ActionEvent actionEvent) throws IOException
    {
        //Check that no fields are blank
        if (txt_FirstName.getText().equals("") || txt_Username.getText().equals("") || txt_LastName.getText().equals("") || txt_City.getText().equals(""))
        {
            alertError.setTitle("");
            alertError.setHeaderText(null);
            alertError.setContentText("One of the fields is blank");
            alertError.showAndWait();
        }
        //Check if at least one genre is selcted
        else if (user.musicGenreProperty().size() < 1)
        {
            alertError.setTitle("");
            alertError.setHeaderText(null);
            alertError.setContentText("Please select at least one music interest");
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

            //Receives a message from the task and either show a failure or success
            task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                    new EventHandler<WorkerStateEvent>()
                    {
                        @Override
                        public void handle(WorkerStateEvent event)
                        {
                            if (task.getMessage().equals("Failed"))
                            {
                                alertError.setTitle("");
                                alertError.setHeaderText(null);
                                alertError.setContentText("Registration Failure");
                                alertError.showAndWait();
                            }
                            else
                            {
                                alert.setTitle("");
                                alert.setHeaderText(null);
                                alert.setContentText("Registration Successful");
                                alert.showAndWait();

                                System.out.println(task.getMessage());

                                sceneSwitcher = new SceneSwitcher(loginWindow, actionEvent);
                                try
                                {
                                    sceneSwitcher.switchScene();
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();


        }
    }


    /**
     * Drops the selected item in the menu into the
     * txtArea_MusicList
     * @param actionEvent
     */
    public void add_Genre(ActionEvent actionEvent)
    {
        String selection = choice_GenresProperty().getSelectionModel().getSelectedItem().toString();
        user.musicGenreProperty().get().add(selection);
        txtArea_MuscList.appendText(selection + "\n");
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
                PrintWriter serverOutString = new PrintWriter(socket.getOutputStream(), false);

                serverOutString.println(code);
                serverOutString.flush();

                outToServerObject.writeObject(user);
                outToServerObject.flush();

                socket.close();

            }
            catch (IOException e)
            {
                System.err.println("Fatal Connection error!");
                e.printStackTrace();
                updateMessage("Failed");
            }
            return null;
        }

        @Override
        protected void succeeded()
        {
            super.done();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        addChoice_Genres("African");
        addChoice_Genres("Asian");
        addChoice_Genres("Avant-Garde");
        addChoice_Genres("Blues");
        addChoice_Genres("Caribbean");
        addChoice_Genres("Comedy");
        addChoice_Genres("Country");
        addChoice_Genres("Easy Listening");
        addChoice_Genres("Electronic");
        addChoice_Genres("Folk");
        addChoice_Genres("Hip Hop");
        addChoice_Genres("Jazz");
        addChoice_Genres("Latin");
        addChoice_Genres("Pop");
        addChoice_Genres("R&B and Soul");
        addChoice_Genres("Rock");
        choice_GenresProperty().setTooltip(new Tooltip("Pick your music interests"));
    }
}
