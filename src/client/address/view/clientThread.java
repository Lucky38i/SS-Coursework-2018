package client.address.view;

import server.Users;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class clientThread implements Runnable
{
    private Socket socket;
    private client.address.model.Users user;
    private String code;
    clientThread(client.address.model.Users user, Socket socket, String code)
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
