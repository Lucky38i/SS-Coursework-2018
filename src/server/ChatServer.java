package server;

import javafx.concurrent.Task;

public class ChatServer extends Task<Void>
{
    //Static variables
    private static final int portNumber = 4444;

    //Variables
    private int serverPort;
    private ClientManager clientManagerTemp;

    public ChatServer(int serverPort)
    {
        this.serverPort = serverPort;
    }
    @Override
    protected Void call() throws Exception
    {

        return null;
    }
}
