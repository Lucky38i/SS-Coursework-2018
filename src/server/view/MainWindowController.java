package server.view;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import server.ChatServer;
import server.ClientManager;
import server.MainServer;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable
{
    @FXML private Text lbl_Welcome;
    @FXML private TextArea txt_MainServerLogs, txt_ChatServerLogs;

    private int mainPort;
    private int chatPort;
    private Thread mainThread;
    private Thread chatThread;

    /**
     * Initializes the data for the servers to start on
     * @param mainPort the port for the main server
     */
    void initData(int mainPort)
    {
        this.mainPort = mainPort;
        //this.chatPort = chatPort;
        lbl_Welcome.setText("Main Server started on port: " + this.mainPort + "\nChat Server started on port: ");
        ClientManager clientManager = new ClientManager(txt_MainServerLogs, txt_ChatServerLogs);
        clientManager.populateUsers();

        //Start Main server
        Task<Void> mainTask = new MainServer(this.mainPort, clientManager);
        Task<Void> chatTask = new ChatServer(this.chatPort);

        mainThread = new Thread(mainTask);
        chatThread = new Thread(chatTask);

        mainThread.setDaemon(true);
        chatThread.setDaemon(true);

        mainThread.start();
        chatThread.start();
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
