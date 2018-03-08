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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;
import java.util.*;

public class Client extends JFrame{
    
    //Static variables
    private static final String host = "localhost";
    private static final int portNumber = 4444;
    
    //Variables
    private String userName;
    private String serverHost;
    private int serverPort;
    private String userChat;
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
        textField_Send.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("Text is: " + textField_Send.getText());
                //userChat = textField_Send.getText();
                userChat = textField_Send.getText().trim();
                textField_Send.setText("");
            }
        });

        textArea_Receive.setText("Test");
    }

    //Constructor
    private Client(String userName, String host, int portNumber)
    {
        this.userName = userName;
        this.serverHost = host;
        this.serverPort = portNumber;
    }

    public static void main (String[] args)
    {
        //Initialize JFrame
        JFrame windowClient = new JFrame("Spotlike");
        windowClient.setContentPane(new Client().contentPanel);
        windowClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        windowClient.setLocationRelativeTo(null);
        windowClient.pack();
        windowClient.setVisible(true);

        //Variables
        //public String testingText;

        //Requests user to enter name
        String readName = null;
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter username");

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
        client.startClient(client, scan);

    }

    private void startClient(Client client, Scanner scan){
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
