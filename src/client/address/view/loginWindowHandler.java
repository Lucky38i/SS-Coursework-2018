package client.address.view;

import client.address.SceneSwitcher;
import Resources.Users;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Handles for the loginWindow
 * @author alexmcbean 
 */
public class loginWindowHandler implements Initializable
{

    //FXML Variables
    @FXML private TextField textField_Username;

    //Variables
    private static final String mainWindow = "mainWindow.fxml";


    private static final String registerWindow = "view/registerWindow.fxml";
    private Users user = new Users();

    private Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
    private Alert alertError = new Alert(Alert.AlertType.ERROR);


    /**
     * This method checks if the user entered a name
     * if not then an error dialog appears
     * <p>
     * otherwise the scene changes
     *
     * @param actionEvent
     */
    @FXML private void open_MainMenu(ActionEvent actionEvent)
    {
        if (textField_Username.getText().equals(""))
        {
            alertError.setTitle("");
            alertError.setHeaderText(null);
            alertError.setContentText("Please enter a username");
            alertError.showAndWait();


        } else
        {
            user.setUserName(textField_Username.getText());

            Task<Users> task = new javaFXWorker(user, ".findUser");

            task.setOnSucceeded(event ->
                    Platform.runLater(() ->
                    {
                        switch (task.getMessage())
                        {
                            case "Failed":
                                alertError.setTitle("");
                                alertError.setHeaderText(null);
                                alertError.setContentText("Login Failure");
                                alertError.showAndWait();
                                break;
                            case "False":
                                alertError.setTitle("");
                                alertError.setHeaderText(null);
                                alertError.setContentText("User does not exist");
                                alertError.showAndWait();
                                break;
                            case "True":
                                //Setup the alert details and show it
                                alertInfo.setTitle("");
                                alertInfo.setHeaderText(null);
                                alertInfo.setContentText("Login successful");
                                alertInfo.showAndWait();

                                //Receive the object returned by the task and switch the scene
                                Users taskUser = task.getValue();
                                switchToMainMenu(actionEvent, taskUser);
                                break;
                        }
                    }));

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();



        }

    }

    /**
     * Opens the register windows
     * @param actionEvent
     */
    @FXML private void open_RegisterWindow(ActionEvent actionEvent)
    {
        SceneSwitcher sceneSwitcher = new SceneSwitcher(registerWindow, actionEvent);
        sceneSwitcher.switchScene();
    }

    /**
     * This switches the scene to the main menu. This was made because
     * the SceneSwitcher class wouldn't be modular if it sent an object to the main window specifically making it's
     * use obsolete.
     * @param actionEvent
     * @param user used to send to the main window controller
     */
    private void switchToMainMenu(ActionEvent actionEvent, Users user)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            //Create a loader and set it's location to mainWindow
            loader.setLocation(getClass().getResource(mainWindow));

            //Set the scene to the loader's location
            Parent windowParent = loader.load();
            Scene windowScene = new Scene(windowParent);

            //Access the controller and init data
            mainWindowController controller = loader.getController();
            controller.initData(user);

            //Find the stage information
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            window.setScene(windowScene);
            window.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
    }


}

