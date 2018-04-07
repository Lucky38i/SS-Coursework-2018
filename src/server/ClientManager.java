package server;


import java.util.ArrayList;
import java.util.List;

public class ClientManager
{
    private final List<MainServer.serverHandlerThread> clients;

    ClientManager()
    {
        this.clients = new ArrayList<>();
    }

    synchronized void add(MainServer.serverHandlerThread client)
    {
        this.clients.add(client);
    }

    synchronized void remove(MainServer.serverHandlerThread client)
    {
        this.clients.remove(client);
    }

    public synchronized List<MainServer.serverHandlerThread> list()
    {
        return new ArrayList<>(this.clients);
    }
}
