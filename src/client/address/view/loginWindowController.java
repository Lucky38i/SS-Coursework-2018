package client.address.view;

import Resources.Users;
import client.address.SceneSwitcher;
import client.address.javaFXWorker;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Handles for the loginWindow
 * @author alexmcbean 
 */
public class loginWindowController implements Initializable
{
    //FXML Variables
    @FXML private TextField textField_Username, txt_MainPort, txt_ChatPort, txt_IPAddress;
    @FXML private Label txt_Version;
    @FXML private ToggleButton btn_Config;
    @FXML private AnchorPane pane_Config, pane_Login;

    //Variables
    private static final String mainWindow = "mainWindow.fxml";
    private static final String registerWindow = "view/registerWindow.fxml";
    private Users user = new Users();
    private Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
    private Alert alertError = new Alert(Alert.AlertType.ERROR);
    private static final String version = "V0.45";
    private static String host = "localhost";
    private static int mainPortNumber = 4444;
    private static int chatPortNumber = 4445;

    @FXML private void open_Config(ActionEvent actionEvent)
    {
        if (btn_Config.selectedProperty().get())
        {
            pane_Login.setVisible(false);
            pane_Config.setVisible(true);
        }
        else
        {
            pane_Login.setVisible(true);
            pane_Config.setVisible(false);
        }
    }


    /**
     * This method checks if the user entered a name
     * if not then an error dialog appears
     * <p>
     * otherwise the scene changes
     *
     * @param actionEvent not being used atm
     */
    @FXML private void open_MainMenu(ActionEvent actionEvent)
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
            //Checks if any of the config settings have been changed
            if (!txt_MainPort.getText().equals(""))
                mainPortNumber = Integer.parseInt(txt_MainPort.getText());

            if (!txt_ChatPort.getText().equals(""))
                chatPortNumber = Integer.parseInt(txt_ChatPort.getText());

            if (!txt_IPAddress.getText().equals(""))
            {
                host = txt_IPAddress.getText();
            }

            user.setUserName(textField_Username.getText());

            Task<Users> task = new javaFXWorker(user, ".findUser",host, mainPortNumber);

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
                                switchToMainMenu(actionEvent, taskUser, host, mainPortNumber, chatPortNumber);
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
     * @param actionEvent used to switch the scene
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
     * @param actionEvent used to switch the scene
     * @param user used to send to the main window controller
     */
    private void switchToMainMenu(ActionEvent actionEvent, Users user, String host, int mainPortNumber, int chatPortNumber)
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
            controller.initData(user, host, mainPortNumber, chatPortNumber);

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
        Image configImage = new Image(getClass().getResourceAsStream("../../../Resources/Cogwheel-512.png"),btn_Config.getPrefWidth(),btn_Config.getPrefHeight(),true,false);
        btn_Config.setGraphic(new ImageView(configImage));
        txt_MainPort.setText(String.valueOf(mainPortNumber));
        txt_ChatPort.setText(String.valueOf(mainPortNumber));
        txt_IPAddress.setText(host);
        pane_Config.setVisible(false);
        txt_Version.setText(version);
    }
}

