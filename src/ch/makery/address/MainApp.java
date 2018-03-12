package ch.makery.address;

import ch.makery.address.model.Users;
import ch.makery.address.view.loginScreenHandler;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import javax.jws.soap.SOAPBinding;
import java.io.IOException;

/**
 * The main class of the client program
 *
 * @author alexmcbean
 * @version 0.1
 */
public class MainApp extends Application
{
    private static final String loginScreenFXML = "view/loginScreen.fxml";
    private static final String mainWindowFXML = "view/mainWindow.fxml";
    private static final String rootLayoutFXML = "view/rootLayout.fxml";

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
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("SpotLike!");
        //showLoginScreen();
        initRootLayout();
        showMenuScreen();

    }

    /**
     * Sets the scene as the login screen
     */
    public void showLoginScreen()
    {
        try {
            // Load root layout from fxml file.
            Parent root = FXMLLoader.load(MainApp.class.getResource(loginScreenFXML));

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * shows the main screen in the root layout
     */
    public void showMenuScreen()
    {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(mainWindowFXML));
            AnchorPane mainWindow = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(mainWindow);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the root layout
     */
    public void initRootLayout()
    {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(rootLayoutFXML));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
