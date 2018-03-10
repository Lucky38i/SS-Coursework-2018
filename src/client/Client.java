/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/*
 *
 * @author alexmcbean
 */


import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client{

    
    //Variables
    private String userName;
    private String serverHost;
    private int serverPort;
    private ServerThread serverThread;

    // Default Constructor
    public Client()
    {

    }

    //Constructor
    public Client(String userName, String host, int portNumber)
    {
        this.userName = userName;
        this.serverHost = host;
        this.serverPort = portNumber;
    }

    public void startClient(Client client, Scanner scan)
    {
        try
        {
            //Create new socket and wait for network communication
            Socket socket = new Socket(serverHost, serverPort);
            Thread.sleep(1000);
            System.out.println("creating socket..");

            //Create thread and start it
            serverThread = new ServerThread(socket, userName);
            Thread serverAccessThread = new Thread(serverThread);
            serverAccessThread.start();
            System.out.println("Created thread");

            while(serverAccessThread.isAlive())
            {
                //Checks if there is another token
                if(scan.hasNextLine())
                {
                    serverThread.addNextMessage(scan.nextLine());
                }

            }
        }
        catch(IOException ex)
        {
            System.err.println("Fatal Connection error!");
            ex.printStackTrace();
        }
        catch(InterruptedException ex)
        {
            System.out.println("Interrupted");
        }
    }
}
