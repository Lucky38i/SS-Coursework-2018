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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

//TODO
//Convert this into a task<Void>
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
            DataOutputStream outToClientString = new DataOutputStream(socket.getOutputStream());
            ObjectInputStream inFromClientObject = new ObjectInputStream(socket.getInputStream());
            Scanner in = new Scanner(socket.getInputStream());

            while(!socket.isClosed())
            {
                //TODO implement the following lines
                /*
                if (socket.getInputStream().read() == -1)
					{
						System.out.println("the socket is closing");
						socket.close();
						clientTasks.remove(this);
					}*/
            
                //If server has received a message
                if(in.hasNextLine())
                {
                    //Set received message to string and print
                    String input = in.nextLine();
                    System.out.println(input);

                    //If clients sets .register command then register new user
                    if (input.equals(".register"))
                    {
                        Object obj = inFromClientObject.readObject();
                        Users user = (Users) obj;
                        server.registerUsers(user);
                        in.close();
                    }

                    //If clients sends .findUser command then see if user exists in DB
                    if (input.equals(".findUser"))
                    {
                        Object obj = inFromClientObject.readObject();
                        Users user = (Users) obj;
                        if(server.findUsers(user))
                        {
                            outToClientString.writeUTF("True");
                        }
                        else
                        {
                            outToClientString.writeUTF("False");
                        }

                        in.close();
                    }
                    //Push message received to other clients
                    else
                    {
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
                }

            }
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
