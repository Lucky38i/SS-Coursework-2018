package client.address.view;

import Resources.Users;
import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;

/**
 * A class creates a seperates task along side the JAVAFX Thread
 * that creates a connection with the server and sends the user object to be registered with the
 * SQL database
 * @author alexmcbean
 */
public class javaFXWorker extends Task<Users>
{
    private Users user;
    private Users readUser;
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
        try(Socket socket = new Socket(host, portNumber);
            ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream()))
        {
            Thread.sleep(1000);

            //Setup I/O
            toServer.writeUTF(code);
            toServer.flush();

            toServer.writeObject(user);
            toServer.flush();


            if (code.equals(".findUser"))
            {
                String input = fromServer.readUTF();
                readUser = (Users) fromServer.readObject();

                if (input.equals("True"))
                {
                    updateMessage("True");
                    updateValue(readUser);
                }

                else if (input.equals("false"))
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
        return readUser;
    }

    @Override
    protected void succeeded()
    {
        super.done();
    }
}
