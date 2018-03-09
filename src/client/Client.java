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

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;
import java.util.*;

public class Client extends JFrame{

    
    //Variables
    private String userName;
    private String serverHost;
    private int serverPort;
    private ServerThread serverThread;

    //JFrame Variables
    private JPanel contentPanel;
    private JTabbedPane panel_Social;
    private JPanel jpanel_Social;
    private JPanel jpanel_Chat;
    private JTextArea textArea_Receive;
    private JTextField textField_Send;
    private JTextArea textArea_ClientList;
    private JButton btn_Enter;


    public JTextArea getTextArea_Receive()
    {
        return this.textArea_Receive;
    }

    public void setTextArea_Receive(String value)
    {
        this.textArea_Receive.append(value);
    }


    // Default Constructor
    public Client()
    {
        //textArea_Receive.setText("Test");
    }

    //Constructor
    public Client(String userName, String host, int portNumber)
    {
        this.userName = userName;
        this.serverHost = host;
        this.serverPort = portNumber;

        textArea_Receive.setText("Test");

    }


    public void startClient(Client client, Scanner scan)
    {
        //Initialize JFrame
        JFrame windowClient = new JFrame("Spotlike");
        windowClient.setContentPane(new Client().contentPanel);
        windowClient.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        windowClient.setLocationRelativeTo(null);
        windowClient.pack();
        windowClient.setVisible(true);

        try
        {
            //Create new socket and wait for network communication
            Socket socket = new Socket(serverHost, serverPort);
            Thread.sleep(1000);
            
            //Create thread and start it
            serverThread = new ServerThread(client, socket, userName);
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
