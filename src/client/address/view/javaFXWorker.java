package client.address.view;

import Resources.Users;
import javafx.concurrent.Task;

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
    private Users user;
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
        try(Socket socket = new Socket(host, portNumber);ObjectOutputStream outToServerObject = new ObjectOutputStream(socket.getOutputStream()))
        {

            Thread.sleep(1000);

            //Setup I/O
            PrintWriter serverOutString = new PrintWriter(socket.getOutputStream(), false);
            Scanner in = new Scanner(socket.getInputStream());

            while (!socket.isClosed())
            {
                serverOutString.println(code);
                serverOutString.flush();

                outToServerObject.writeObject(user);
                outToServerObject.flush();

                if (code.equals(".findUser"))
                {
                    String input = in.nextLine();

                    if (input.equals("True"))
                    {
                        updateMessage("True");
                    } else if (input.equals("false"))
                    {
                        updateMessage("False");
                    }
                } else
                {
                    updateMessage("True");
                }
                socket.close();
            }
        }
        catch (IOException e)
        {
            System.err.println("Fatal Connection error!");
            e.printStackTrace();
            updateMessage("Failed");
        }
        return null;
    }

    @Override
    protected void succeeded()
    {
        super.done();
    }
}
