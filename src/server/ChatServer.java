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
import java.util.ArrayList;
import java.util.List;

public class ChatServer
{

    //Static variables
    private static final int portNumber = 4444;
    
    //Variables
    private int serverPort;
    private List<ClientThread> clients;
    
    
    public static void main(String[] args) 
    {
        ChatServer server  = new ChatServer(portNumber);
        server.startServer();
    }
    
    public ChatServer (int __portNumber)
    {
        serverPort = __portNumber;
    }
    
    public List<ClientThread> getClients()
    {
        return clients;
    }
    
    private void startServer()
    {
        clients = new ArrayList<ClientThread>();
        ServerSocket serverSocket = null;
        try
        {
            serverSocket = new ServerSocket(serverPort);
            acceptClients(serverSocket);
        }
        catch (IOException e)
        {
            System.err.println("Could not listen on port: " + serverPort);
            System.exit(1);
        }
    }
    
    
    //Continuously accpt clients
    private void acceptClients(ServerSocket serverSocket)
    {
        System.out.println("Server starts port = " + serverSocket.getLocalSocketAddress());
        while (true)
        {
            try 
            {
                Socket socket = serverSocket.accept();
                System.out.println("Accepts: " + socket.getRemoteSocketAddress());
                ClientThread client = new ClientThread(this, socket);
                Thread thread = new Thread(client);
                thread.start();
                clients.add(client);
            }
            catch (IOException e)
            {
                System.err.println("Accept failed on:" + serverPort);
            }
        }   
    }   
}


