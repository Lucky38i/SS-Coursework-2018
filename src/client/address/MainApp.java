package client.address;

import client.address.view.mainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import server.view.MainWindowController;

import java.io.IOException;

/**
 * The main class of the client program
 *
 * @author alexmcbean
 * @version 0.1
 */
public class MainApp extends Application
{
    private static final String loginScreenFXML = "view/loginWindow.fxml";
    private static final String mainWindowFXML = "view/mainWindow.fxml";
     /**
     * The main entry point for JavaFX applications
     * @param primaryStage used as the.. primary stage
     *
     */
    @Override
    public void start(Stage primaryStage)
    {
        try
        {
            primaryStage.setTitle("SpotLike!");
            primaryStage.getIcons().add(new Image("Resources/Music-icon.png"));

            Parent root = FXMLLoader.load(MainApp.class.getResource(loginScreenFXML));


            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }

}
