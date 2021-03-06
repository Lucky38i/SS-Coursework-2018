package client.address.view;


import Resources.Users;
import client.address.SceneSwitcher;
import client.address.javaFXWorker;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for registerWindow.fxml
 */
public class registerWindowController implements Initializable
{

    //FXML Controller Variables
    @FXML private DatePicker date_Birthday;
    @FXML private TextField txt_FirstName, txt_Username, txt_LastName, txt_City;
    @FXML private ComboBox choice_Genres;
    @FXML private TextArea txtArea_MuscList;

    //Variables
    private Alert alertError = new Alert(Alert.AlertType.ERROR);
    private Alert alert = new Alert(Alert.AlertType.INFORMATION);
    private Users user = new Users();
    private static String host = "localhost";
    private static int portNumber = 4444;
    private final String loginWindow = "view/loginWindow.fxml";


    private void addChoice_Genres(String genre)
    {
        choice_GenresProperty().getItems().add(genre);
    }

    private ComboBox choice_GenresProperty()
    {
        return choice_Genres;
    }

    /**
     * Opens the login window scene
     * @param actionEvent This is used to switch the scene
     */
    public void open_LoginWindow(ActionEvent actionEvent)
    {
        String loginWindow = "view/loginWindow.fxml";
        SceneSwitcher sceneSwitcher = new SceneSwitcher(loginWindow, actionEvent);
        sceneSwitcher.switchScene();
    }

    /**
     * Registers the user then if successful opens back the login window
     */
    public void btn_registerUser(ActionEvent actionEvent)
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



            Task<Users> task = new javaFXWorker(user, ".register",host,portNumber);


            //Receives a message from the task and either show a failure or success
            task.setOnSucceeded(event ->
                    Platform.runLater(() ->
                    {
                        switch (task.getMessage())
                        {
                            case "Failed":
                            {
                                alertError.setTitle("");
                                alertError.setHeaderText(null);
                                alertError.setContentText("Registration Failure");
                                alertError.showAndWait();
                                break;
                            }
                            case "True":
                            {
                                alert.setTitle("");
                                alert.setHeaderText(null);
                                alert.setContentText("Registration Successful");
                                alert.showAndWait();

                                //TODO fix this so that it doesn't close
                                //System.exit(0);
                                SceneSwitcher sceneSwitcher = new SceneSwitcher(loginWindow,actionEvent);
                                sceneSwitcher.switchScene();
                                break;
                            }
                        }
                    }));


            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }


    /**
     * Drops the selected item in the menu into the
     * txtArea_MusicList
     */
    public void add_Genre()
    {
        String selection = choice_GenresProperty().getSelectionModel().getSelectedItem().toString();
        user.musicGenreProperty().get().add(selection);
        txtArea_MuscList.appendText(selection + "\n");
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
