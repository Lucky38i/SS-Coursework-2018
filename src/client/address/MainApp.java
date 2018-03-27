package client.address;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

    /**
     * Constructor
     */
    public MainApp()
    {
        //Sample data
    }

    /**
     * The main entry point for JavaFX applications
     * @param primaryStage
     *
     */
    @Override
    public void start(Stage primaryStage)
    {
        try
        {
            primaryStage.setTitle("SpotLike!");

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
