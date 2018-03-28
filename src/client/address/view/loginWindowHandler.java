package client.address.view;

import client.address.SceneSwitcher;
import Resources.Users;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class loginWindowHandler implements Initializable
{

    //FXML Variables
    @FXML private AnchorPane anchorPane;
    @FXML private Button btn_Login;
    @FXML private TextField textField_Username;

    //Variables
    private static final String mainWindow = "view/mainWindow.fxml";


    private static final String registerWindow = "view/registerWindow.fxml";
    private Users user = new Users();
    private SceneSwitcher sceneSwitcher;

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
    public void open_MainMenu(ActionEvent actionEvent)
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
                                Users taskUser = task.getValue();

                                sceneSwitcher = new SceneSwitcher(mainWindow, actionEvent);
                                try
                                {
                                    sceneSwitcher.switchScene();
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }));

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();



        }

    }

    public void open_RegisterWindow(ActionEvent actionEvent) throws IOException
    {
        sceneSwitcher = new SceneSwitcher(registerWindow, actionEvent);
        sceneSwitcher.switchScene();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
    }


}

