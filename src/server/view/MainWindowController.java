package server.view;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import server.MainServer;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable
{
    @FXML private Text lbl_Welcome;
    @FXML private TextArea txt_MainServerLogs;

    private int mainPort;
    private int chatPort;
    private Thread mainThread;

    /**
     * Initializes the data for the servers to start on
     * @param mainPort the port for the main server
     */
    void initData(int mainPort)
    {
        this.mainPort = mainPort;
        //this.chatPort = chatPort;
        lbl_Welcome.setText("Main Server started on port: " + this.mainPort + "\nChat Server started on port: ");

        //Start Main server
        Task<Void> task = new MainServer(this.mainPort, txt_MainServerLogs);
        mainThread = new Thread(task);
        mainThread.setDaemon(true);
        mainThread.start();
    }

    @FXML private void shutdownServers()
    {
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }
}
