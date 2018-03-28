package client.address.view;

import Resources.Users;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * A class creates a seperates task along side the JAVAFX Thread
 * that creates a connection with the server and sends the user object to be registered with the
 * SQL database
 */
public class javaFXWorker extends Task<Users>
{
    private Users user;
    private static String code;
    private static final String host = "localhost";
    private static final int portNumber = 4444;


    javaFXWorker(Users user, String code)
    {
        this.user = user;
        javaFXWorker.code = code;
    }

    @Override
    protected Users call() throws Exception
    {
        try(Socket socket = new Socket(host, portNumber);InputStream fromServer = socket.getInputStream(); OutputStream toServer = socket.getOutputStream())
        {

            Thread.sleep(1000);

            //Setup I/O
            PrintWriter serverOutString = new PrintWriter(toServer, false);
            ObjectOutputStream toServerObject  = new ObjectOutputStream(toServer);
            Scanner in = new Scanner(fromServer);



            while (!socket.isClosed())
            {
                serverOutString.println(code);
                serverOutString.flush();

                toServerObject.writeObject(user);
                toServerObject.flush();

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
