package ch.makery.address;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application
{

    private Stage primaryStage;
    private String loginScreenFXML = "resources/FXML/loginScreen.fxml";


    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("SpotLike!");

        showLoginScreen();

    }

    public void showLoginScreen()
    {
        try {
            // Load root layout from fxml file.
            Parent root = FXMLLoader.load(getClass().getResource(loginScreenFXML));

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
