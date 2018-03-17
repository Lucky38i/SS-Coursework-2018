package client.address.view;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class clientThread implements Runnable
{
    private Socket socket;
    private Resources.Users user;
    private String code;
    clientThread(Resources.Users user, Socket socket, String code)
    {
        this.socket = socket;
        this.user = user;
        this.code = code;
    }

    @Override
    public void run()
    {
        try
        {
            ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());

            outToServer.writeObject(user);

            socket.close();
        }
        catch (IOException e)
        {

        }
    }
}
