package client.address;

import client.address.model.Users;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
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

    private Stage primaryStage;
    private BorderPane rootLayout;

    /**
     * Data as an observable list of Users
     */
    private ObservableList<Users> usersData = FXCollections.observableArrayList();

    /**
     * Constructor
     */
    public MainApp()
    {
        //Sample data
        usersData.add(new Users("alex"));
        usersData.add(new Users("Jack"));
    }

    /**
     * Returns the data as an observable list of Users
     * @return usersData
     */

    public ObservableList<Users> getUsersData()
    {
        return usersData;
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
            this.primaryStage = primaryStage;
            this.primaryStage.setTitle("SpotLike!");

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
