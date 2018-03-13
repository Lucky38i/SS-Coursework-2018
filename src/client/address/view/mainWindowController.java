package client.address.view;

import client.address.model.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Controller for the mainWindow FXML file
 * @author alexmcbean
 */
public class mainWindowController
{

    //FXML Variables
    @FXML private Text txt_UserName;

    //Variables
    private Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
    private String loginWindow = "loginWindow.fxml";

    /**
     * This method initializes the data for the user model
     * @param user
     */
    public void initData(Users user)
    {
        txt_UserName.setText(txt_UserName.getText() + ": " + user.getFirstName() + "!");
    }

    /**
     * Method that changes the scene back to the login screen
     * @param actionEvent
     */
    public void open_LoginScreen(ActionEvent actionEvent) throws IOException
    {
        alertInfo.setTitle("");
        alertInfo.setHeaderText(null);
        alertInfo.setContentText("Successfully logged out");
        alertInfo.showAndWait();

        Parent mainWindowParent = FXMLLoader.load(getClass().getResource(loginWindow));
        Scene mainWindowScene = new Scene(mainWindowParent);

        //Find the stage information
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        window.setScene(mainWindowScene);

        window.show();
    }
}
