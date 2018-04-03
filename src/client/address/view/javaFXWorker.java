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
 * @author alexmcbean
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
        try
        {
            Socket socket = new Socket(host, portNumber);
            InputStream fromServer = socket.getInputStream();
            OutputStream toServer = socket.getOutputStream();
            Thread.sleep(1000);

            //Setup I/O
            BufferedWriter serverOutString = new BufferedWriter(new OutputStreamWriter(toServer));
            BufferedReader in = new BufferedReader(new InputStreamReader(fromServer));
            ObjectInputStream fromServerObject = new ObjectInputStream(fromServer);
            ObjectOutputStream toServerObject  = new ObjectOutputStream(toServer);



            serverOutString.write(code);
            serverOutString.flush();

            toServerObject.writeObject(user);
            toServerObject.flush();


            if (code.equals(".findUser"))
            {
                Users readUser = (Users) fromServerObject.readObject();
                String input = in.readLine();

                if (input.equals("True"))
                {
                    updateMessage("True");
                    updateValue(readUser);
                } else if (input.equals("false"))
                {
                    updateMessage("False");
                }
            } else
            {
                updateMessage("True");
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
