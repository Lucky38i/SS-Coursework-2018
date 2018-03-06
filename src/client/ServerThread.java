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

import java.net.*;
import java.io.*;
import java.util.*;

public class ServerThread implements Runnable
{
    
    private Socket socket;
    private String userName;
    
    private boolean isAlived;
    private final LinkedList<String> messagesToSend;
    private boolean hasMessages = false;
    
    
    //Constructor
    ServerThread(Socket socket,  String userName)
    {
        this.socket = socket;
        this.userName = userName;
        messagesToSend = new LinkedList<String>();
    }
    
    //Pushes messages sent by the client into the list
    public void addNextMessage(String message)
    {
        synchronized (messagesToSend)
        {
            hasMessages = true;
            messagesToSend.push(message);
        }
    }
    
    public void welcomeMessage()
    {
        System.out.println("Welcome : " + userName);
        System.out.println("Local Port : " + socket.getLocalPort());
        System.out.println("Server = " + socket.getRemoteSocketAddress() + ":" + socket.getPort());
    }
    
    @Override
    public void run()
    {
        welcomeMessage();
        
        try
        {
            //Setup I/O
            PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), false);   //Output to server
            InputStream serverInStream = socket.getInputStream();                       //Input from server
            Scanner serverIn = new Scanner(serverInStream);                             

            while(!socket.isClosed())
            {
                //Print messages from server
                if(serverInStream.available() > 0)
                {
                    //Print out messages from the server
                    if(serverIn.hasNextLine())
                    {
                        System.out.println(serverIn.nextLine());
                    }
                }
                
                
                if(hasMessages)
                {
                    String nextSend = "";
                    
                    //Set string to first message in list
                    synchronized(messagesToSend)
                    {
                        nextSend = messagesToSend.pop();
                        hasMessages = !messagesToSend.isEmpty();
                    }
                    //Send username and message from client to server 
                    serverOut.println(userName + " > " + nextSend);
                    serverOut.flush();
                }
            }
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
