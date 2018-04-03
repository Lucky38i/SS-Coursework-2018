package client.address.view;

import Resources.Users;
import client.address.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

    //Variables
    private Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
    private Users user = new Users();

    /**
     * This sets the received user to <code>this.user</code> to be used by this controller
     * @param user receives a user object from the registerWindowController
     */
    public void initData(Users user)
    {
        this.user = user;
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


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //Initialize the user details
        txt_UserName.setText(txt_UserName.getText() + " " + user.getUserName());
        txt_FirstName.setText(user.getFirstName());
        txt_LastName.setText(user.getLastName());
        txt_City.setText(user.getCity());
        txt_Birthday.setText(String.valueOf(user.getBirthday()));
        txt_Age.setText(String.valueOf(user.getAge()));

        column_Genres.setCellValueFactory(new PropertyValueFactory<>("musicGenre"));
        column_Friends.setCellValueFactory(new PropertyValueFactory<>("friendsList"));

    }
}
