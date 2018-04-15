/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/*
 *
 * @author alexmcbean
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * This is the main server that handles all objects going in and out of the server
 * such as registration, log in, friends, music streaming etc.
 * @author alexmcbean
 */
public class MainServer
{

    //Static variables
    private static final int portNumber = 4444;
    
    //Variables
    private int serverPort;
    private final ClientManager clientManagerTemp = new ClientManager();

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
            clientManagerTemp.logger("Could not listen on port: " + serverPort);
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
        clientManagerTemp.logger("Server starts port = " + serverSocket.getLocalSocketAddress());
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

    /**
     * Main constructor that takes in a port Number
     * @param portNumber the port number by which to start the server
     */
    public MainServer(int portNumber)
    {
        this.serverPort = portNumber;
    }

    /**
     * The main method, could be seperated into a main program
     * @param args not being currently used
     */
    public static void main(String[] args) 
    {
        MainServer server  = new MainServer(portNumber);
        server.clientManagerTemp.populateUsers();
        server.startServer();

    }
}


