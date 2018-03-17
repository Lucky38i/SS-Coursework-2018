package client.address.view;

import Resources.Users;
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

public class loginWindowHandler implements Initializable
{

    //FXML Variables
    @FXML private AnchorPane anchorPane;
    @FXML private Button btn_Login;
    @FXML private TextField textField_Username;

    //Variables
    private static final String mainWindow = "mainWindow.fxml";
    private static final String registerWindow = "registerWindow.fxml";
    private Users user = new Users();
    private SceneSwitcher sceneSwitcher;

    private Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
    private Alert alertError = new Alert(Alert.AlertType.ERROR);


    /**
     * This method checks if the user entered a name
     * if not then an error dialog appears
     *
     * otherwise the scene changes
     *
     * @param actionEvent
     * @throws IOException
     */
    public void open_MainMenu(ActionEvent actionEvent) throws IOException
    {
        if (textField_Username.getText().equals(""))
        {
            alertError.setTitle("");
            alertError.setHeaderText(null);
            alertError.setContentText("Please enter a username");
            alertError.showAndWait();


        }
        else
        {
            user.setUserName(textField_Username.getText());

            //Setup the alert details and show it
            alertInfo.setTitle("");
            alertInfo.setHeaderText(null);
            alertInfo.setContentText("Login successful");
            alertInfo.showAndWait();

            sceneSwitcher = new SceneSwitcher(mainWindow, actionEvent);
            sceneSwitcher.switchScene();

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
