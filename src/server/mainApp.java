package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main method to start the UI for the servers
 * @author almcb
 */
public class mainApp extends Application
{
    private static final String startScreenFXML = "view/startWindow.fxml";

    @Override
    public void start(Stage primaryStage)
    {
        try
        {
            primaryStage.setTitle("SpotLike Server!");
            primaryStage.getIcons().add(new Image("Resources/Music-icon.png"));

            Parent root = FXMLLoader.load(mainApp.class.getResource(startScreenFXML));


            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
