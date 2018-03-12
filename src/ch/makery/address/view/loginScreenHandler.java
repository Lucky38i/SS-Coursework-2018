package ch.makery.address.view;

import ch.makery.address.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class loginScreenHandler
{

    @FXML private Button btn_Login;
    @FXML private TextField textField_Username;

    private Alert alert = new Alert(Alert.AlertType.CONFIRMATION);


    public void open_MainMenu(ActionEvent actionEvent)
    {
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("I have a great message for you!");

        alert.showAndWait();
    }
}
