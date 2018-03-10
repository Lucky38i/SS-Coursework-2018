package client;

import java.util.Scanner;
import javafx.application.Application;
import javafx.concurrent.Task;

public class Main
{
    //Static variables
    private static final String host = "localhost";
    private static final int portNumber = 4444;

    public static void main (String args[])
    {

        //Requests user to enter name
        String readName = null;
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter username");

        //Loop to make sure username is not empty
        while (readName == null || readName.trim().equals(""))
        {
            readName = scan.nextLine();
            if (readName.trim().equals(""))
            {
                System.out.println("Invalid, please try again");
            }
        }

        //Client CLI thread
        Runnable clientCLI = new backEnd(readName, scan, host, portNumber);
        Thread clientThread = new Thread(clientCLI);

        clientThread.start();
        Application.launch(clientFX.class);
    }

}
class backEnd implements Runnable
{
    private String userName;
    private Scanner scan;
    private String host;
    private int portNumber;

    backEnd(String userName, Scanner scan, String host, int portNumber)
    {
        this.userName = userName;
        this.scan = scan;
        this.host = host;
        this.portNumber = portNumber;
    }
    public void run()
    {
        Client client = new Client(userName,host,portNumber);
        client.startClient(client, scan);
    }
}
