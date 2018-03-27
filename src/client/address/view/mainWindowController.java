package client.address.view;

import client.address.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;

import java.io.IOException;


/**
 * Controller for the mainWindow FXML file
 * @author alexmcbean
 */
public class mainWindowController
{
    //Variables
    private Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);

    /**
     * Method that changes the scene back to the login screen
     * @param actionEvent Takes the local action event to find the source and switch scene
     */
    public void open_LoginScreen(ActionEvent actionEvent) throws IOException
    {
        alertInfo.setTitle("");
        alertInfo.setHeaderText(null);
        alertInfo.setContentText("Successfully logged out");
        alertInfo.showAndWait();

        String loginWindow = "view/loginWindow.fxml";
        SceneSwitcher sceneSwitcher = new SceneSwitcher(loginWindow,actionEvent);
        sceneSwitcher.switchScene();
    }
}
