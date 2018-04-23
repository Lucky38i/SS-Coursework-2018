package server;

import javafx.concurrent.Task;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class ChatServer extends Task<Void>
{
    //Static variables
    private static final int portNumber = 4444;

    //Variables
    private int serverPort;
    private ClientManager clientManagerTemp;

    public ChatServer(int serverPort, ClientManager clientManagerTemp)
    {
        this.serverPort = serverPort;
        this.clientManagerTemp = clientManagerTemp;
    }


    /**
     * Starts the server and begins accepting clients
     */
    private void startServer()
    {
        ServerSocket serverSocket;
        try
        {
            serverSocket = new ServerSocket(serverPort);
            acceptClients(serverSocket);
        }
        catch (IOException e)
        {
            clientManagerTemp.logger("Could not listen on port: " + serverPort, "Chat");
            System.exit(1);
        }
    }

    /**
     * Creates a new client and adds it to the list of clients
     * @param acceptedSocket The socket that has been accepted
     * @return returns a new client that has been added to the client list
     */
    private serverHandlerThread newClient(Socket acceptedSocket)
    {
        serverHandlerThread client = new serverHandlerThread(acceptedSocket,clientManagerTemp);
        clientManagerTemp.addClient(client);
        return client;
    }

    /**
     * Continuously accepts clients
     * @param serverSocket The server socket
     */
    private void acceptClients(ServerSocket serverSocket)
    {
        clientManagerTemp.logger("Server starts port = " + serverSocket.getLocalSocketAddress(), "Chat");
        ExecutorService executorService = Executors.newCachedThreadPool();
        while (true)
        {
            try
            {
                Socket socket = serverSocket.accept();
                FutureTask futureTask = new FutureTask(newClient(socket),null);
                executorService.execute(futureTask);
            }
            catch (IOException e)
            {
                System.err.println("Accept failed on:" + serverPort);
            }
        }
    }


    @Override
    protected Void call() throws Exception
    {
        startServer();
        return null;
    }
}
