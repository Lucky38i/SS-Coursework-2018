/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/**
 *
 * @author alexmcbean
 */

import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    
    //Static variables
    private static final String host = "localhost";
    private static final int portNumber = 4444;
    
    //Variables
    private String userName;
    private String serverHost;
    private int serverPort;
    private JTabbedPane tabbedPanel;
    private JPanel mainPanel;
    private JPanel clientSocial;
    private JPanel clientChat;
    private JButton Enter;

    public static void main (String[] args)
    {
        //Initialize JFrame
        JFrame windowClient = new JFrame("Spotlike");
        windowClient.setContentPane(new Client().mainPanel);
        windowClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        windowClient.pack();
        windowClient.setVisible(true);

        //Requests user to enter name
        String readName = null;
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter usename");
        
        //Loop to make sure username is not empty
        while (readName == null || readName.trim().equals(""))
        {
            readName = scan.nextLine();
            if (readName.trim().equals(""))
            {
                System.out.println("Invalid, please try again");
            }
        }
        
        //Start client
        Client client = new Client(readName, host, portNumber);
        client.startClient(scan);
        
    }

    // Default Constructor
    private Client()
    {

    }

    //Constructor
    private Client(String userName, String host, int portNumber)
    {
        this.userName = userName;
        this.serverHost = host;
        this.serverPort = portNumber;
    }
    
    private void startClient(Scanner scan){
        try
        {
            //Create new socket and wait for network communication
            Socket socket = new Socket(serverHost, serverPort);
            Thread.sleep(1000);
            
            //Create thread and start it
            ServerThread serverThread = new ServerThread(socket, userName);
            Thread serverAccessThread = new Thread(serverThread);
            serverAccessThread.start();
            
            
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
