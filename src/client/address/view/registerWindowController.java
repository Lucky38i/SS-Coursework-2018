package client.address.view;

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
import java.net.URL;
import java.util.ResourceBundle;

public class registerWindowController implements Initializable
{

    @FXML private DatePicker Date_Birthday;
    @FXML private TextField txt_FirstName;
    @FXML private TextField txt_Username;
    @FXML private TextField txt_LastName;
    @FXML private TextField txt_City;
    @FXML private ChoiceBox choice_Genres;
    @FXML private TextArea txtArea_MuscList;
    @FXML private Hyperlink HL_Cancel;

    private String loginWindow = "loginWindow.fxml";

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
