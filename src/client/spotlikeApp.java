package client;

import java.util.Scanner;

public class spotlikeApp
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

        Client client = new Client(readName,host,portNumber);
        client.startClient(client, scan);
    }
}
