package client.address;

import Resources.Users;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;

/**
 * Class that handles the switching of scenes on a stage
 */
public class SceneSwitcher
{
    //Variables
    public FXMLLoader loader;
    private String sceneLoc;
    private ActionEvent actionEvent;
    /**
     * Constructor
     * @param sceneLoc
     * @param actionEvent
     */
    public SceneSwitcher(String sceneLoc,ActionEvent actionEvent)
    {
        this.loader = new FXMLLoader();
        this.sceneLoc = sceneLoc;
        this.actionEvent = actionEvent;

    }

    /**
     * Handles the switching of scenes
     */
    public void switchScene()
    {
        try
        {
            //Create a loader and set it's location to mainWindow
            loader.setLocation(getClass().getResource(sceneLoc));

            //Set the scene to the loader's location
            Parent windowParent = loader.load();
            Scene windowScene = new Scene(windowParent);

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
}
