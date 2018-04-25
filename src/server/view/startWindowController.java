package server.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class startWindowController implements Initializable
{
    // FXML Variables
    @FXML private TextField txt_MainPort, txt_ChatPort;

    // Variables
    private Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);

    @FXML private void openMainWindow(ActionEvent actionEvent)
    {

        if (txt_ChatPort.getText().equals(txt_MainPort.getText()) || txt_MainPort.getText().equals(txt_ChatPort.getText()))
        {
            alertInfo.setTitle("");
            alertInfo.setHeaderText(null);
            alertInfo.setContentText("The port numbers can't be the same");
            alertInfo.showAndWait();
        }
        else
            switchToMainMenu(actionEvent, Integer.parseInt(txt_MainPort.getText()), Integer.parseInt(txt_ChatPort.getText()));
    }

    /**
     * This switches the scene to the main menu. This was made because
     * the SceneSwitcher class wouldn't be modular if it sent an object to the main window specifically making it's
     * use obsolete.
     * @param actionEvent used to switch the scene
     * @param mainPort The main port for the main Server
     */
    private void switchToMainMenu(ActionEvent actionEvent, int mainPort, int chatPort)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();

            //Create a loader and set it's location to mainWindow
            String mainWindow = "mainWindow.fxml";
            loader.setLocation(getClass().getResource(mainWindow));

            //Set the scene to the loader's location
            Parent windowParent = loader.load();
            Scene windowScene = new Scene(windowParent);

            //Access the controller and init data
            MainWindowController controller = loader.getController();
            controller.initData(mainPort, chatPort);

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
    public void initialize(URL location, ResourceBundle resources)
    {
        txt_MainPort.setText("4444");
        txt_ChatPort.setText("4445");
    }

}
