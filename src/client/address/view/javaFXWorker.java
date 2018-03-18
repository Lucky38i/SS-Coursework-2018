package client.address.view;

import Resources.Users;
import javafx.concurrent.Task;
import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * A class creates a seperates task along side the JAVAFX Thread
 * that creates a connection with the server and sends the user object to be registered with the
 * SQL database
 */
public class javaFXWorker extends Task<Void>
{
    Users user;
    private static String code;
    private static final String host = "localhost";
    private static final int portNumber = 4444;


    public javaFXWorker(Users user, String code)
    {
        this.user = user;
        this.code = code;
    }

    @Override
    protected Void call() throws Exception
    {
        if (code.equals(".register"))
        {
            try
            {
                Socket socket = new Socket(host, portNumber);
                Thread.sleep(1000);

                //Setup I/O
                ObjectOutputStream outToServerObject = new ObjectOutputStream(socket.getOutputStream());
                PrintWriter serverOutString = new PrintWriter(socket.getOutputStream(), false);

                serverOutString.println(code);
                serverOutString.flush();

                outToServerObject.writeObject(user);
                outToServerObject.flush();

                socket.close();
                updateMessage("True");

            } catch (IOException e)
            {
                System.err.println("Fatal Connection error!");
                e.printStackTrace();
                updateMessage("Failed");
            }
        }

        if (code.equals(".findUser"))
        {
            try
            {
                Socket socket = new Socket(host, portNumber);
                Thread.sleep(1000);

                //Setup I/O
                ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());
                PrintWriter serverOutString = new PrintWriter(socket.getOutputStream(), false);
                DataInputStream in = new DataInputStream(socket.getInputStream());

                serverOutString.println(code);
                serverOutString.flush();

                outToServer.writeObject(user);
                outToServer.flush();


                String input = in.readUTF();
                if (input.equals("True"))
                {
                    updateMessage("True");
                }
                else if (input.equals("False"))
                {
                    updateMessage("False");
                }

                socket.close();

            }

            catch (IOException e)
            {
                updateMessage("Failed");
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void succeeded()
    {
        super.done();
    }
}
