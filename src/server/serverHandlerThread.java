/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 *
 * @author alexmcbean
 */
package server;


import Resources.Users;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class serverHandlerThread implements Runnable
{
    private Socket socket;
    private PrintWriter clientOut;
    private ChatServer server;
    //Constructor
    public serverHandlerThread(ChatServer server, Socket socket)
    {
        this.server = server;
        this.socket = socket;
    }
    
    private PrintWriter getWriter()
    {
        return clientOut;
    }
    
    @Override
    public void run ()
    {
        try
        {
            //Setup I/O
            this.clientOut = new PrintWriter(socket.getOutputStream(), false);
            ObjectInputStream inFromClientObject = new ObjectInputStream(socket.getInputStream());


           // Scanner in = new Scanner(socket.getInputStream());
            Users user = (Users) inFromClientObject.readObject();
            server.registerUsers(user);
            // start communicating
                /*
            while(!socket.isClosed())
            {
                //If server has received a message
                if(in.hasNextLine())
                {
                    //Set received message to string and print
                    String input = in.nextLine();
                    System.out.println(input);

                    if (input.equals(".register"))
                    {
                        Object obj = inFromClientObject.readObject();
                        Users user = (Users) obj;
                        System.out.println("Username is: " + user);
                        server.registerUsers(user);
                    }
                    //Push message received to other clients
                    for (serverHandlerThread thatClient : server.getClients())
                    {
                        PrintWriter thatClientOut = thatClient.getWriter();
                        if (thatClientOut != null)
                        {
                            thatClientOut.write(input + "\r\n");
                            thatClientOut.flush();
                        }
                    }
                }

            }*/
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    

}
