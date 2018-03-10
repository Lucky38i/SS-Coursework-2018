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


import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientThread implements Runnable
{
    private Socket socket;
    private PrintWriter clientOut;
    private ChatServer server;
    
    //Constructer
    public ClientThread(ChatServer server, Socket socket)
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
            Scanner in = new Scanner(socket.getInputStream());

            // start communicating
            while(!socket.isClosed())
            {
                //If server has received a message
                if(in.hasNextLine())
                {
                    //Set received message to string and print
                    String input = in.nextLine();
                    System.out.println(input);
                    
                    //Push message received to other clients
                    for(ClientThread thatClient : server.getClients())
                    {
                        PrintWriter thatClientOut = thatClient.getWriter();
                        if(thatClientOut != null){
                            thatClientOut.write(input + "\r\n");
                            thatClientOut.flush();
                        }
                    }
                }
            }
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    

}
