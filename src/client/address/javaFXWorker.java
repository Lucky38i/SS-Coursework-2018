package client.address;

import Resources.Users;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private static String host;
    private static int portNumber;

    public javaFXWorker(Users user, String code, String host, int portNumber)
    {
        this.user = user;
        javaFXWorker.code = code;
        javaFXWorker.host = host;
        javaFXWorker.portNumber = portNumber;
    }


    @Override
    protected Users call() throws Exception
    {
        try(Socket socket = new Socket(host, portNumber);
            ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream()))
        {
            Thread.sleep(100);

            toServer.writeUTF(code);
            toServer.flush();

            toServer.writeObject(user);
            toServer.flush();

            switch (code)
            {
                case ".findUser":
                {
                    String input = fromServer.readUTF();
                    readUser = (Users) fromServer.readObject();

                    if (input.equals("True"))
                    {
                        updateMessage("True");
                        updateValue(readUser);
                    } else if (input.equals("false"))
                    {
                        updateMessage("False");
                    }
                    break;
                }
                case ".logout":
                {
                    String input = fromServer.readUTF();
                    System.out.println(input);
                    break;
                }
                default:
                    updateMessage("True");
                    break;
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
