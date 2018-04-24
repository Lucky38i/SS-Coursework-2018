package client.address.view;

import Resources.Pair;
import Resources.Users;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The controller that handles all private messages between the client and selected users
 * @author alexmcbean, Alex Pona
 */
public class ChatWindowController
{
    @FXML private TextArea txt_Messages;
    @FXML private TextField chat_Message;
    @FXML private ListView<String> lst_Users;

    private ArrayList<Pair<String,List<String>>> privateMessages;
    private backgroundThread bgThead;
    private String host;
    private int portNumber;
    private Users user;

    private Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);

    void initData(String host, int portNumber, Users user)
    {
        this.host = host;
        this.portNumber = portNumber;
        this.user = user;

        bgThead = new backgroundThread(host,portNumber,user);

    }

    private synchronized ArrayList<Pair<String,List<String>>> getPrivateMessages()
    {
        return privateMessages;
    }

    @FXML private void send_Chat(ActionEvent actionEvent)
    {
        if (chat_Message.getText().equals(""))
        {
            alertInfo.setTitle("");
            alertInfo.setHeaderText(null);
            alertInfo.setContentText("Your message can't be empty");
            alertInfo.showAndWait();
        }
        else
            bgThead.addNextMessage(".Message." + lst_Users.getSelectionModel().getSelectedItem()+"."+chat_Message.getText());
    }

    /**
     * gets the messages being discussed with a user
     * @param mouseEvent //TODO
     */
    public void get_Messages(MouseEvent mouseEvent)
    {
        String selectUser = lst_Users.getSelectionModel().getSelectedItem();
        txt_Messages.setText("");
        for (int i=0; i < privateMessages.size(); ++i)
        {
            if (privateMessages.get(i).getFirst().equals(selectUser))
            {
                for (int x = 0; x < privateMessages.get(i).getSecond().size(); ++x)
                {
                    txt_Messages.appendText(privateMessages.get(i).getSecond().get(x)+"\n");
                }
            }
        }
    }

    class backgroundThread extends Task<Void>
    {

        private String host;
        private int portNumber;
        private Users user;
        private boolean hasMessages = false;
        private final LinkedList<String> messagesToSend;
        private final ArrayList<String> messageList;

        backgroundThread(String host, int portNumber, Users user)
        {
            this.host = host;
            this.portNumber = portNumber;
            this.user = user;
            messagesToSend = new LinkedList<>();
            messageList = new ArrayList<>();
        }

        /**
         * Add a new message to the messages to be
         * sent to the server
         * @param msg The message to the sent to the server
         */
        synchronized void addNextMessage(String msg)
        {
            hasMessages = true;
            messagesToSend.push(msg);
        }

        /**
         * This receives a message and parses it between
         * code, user who it came from and the message received
         * then addes it to the private message list or creates a new one
         * @param input the received message
         */
        void handlePrivateMessages(String input)
        {
            String[] names = input.split("[.]");
            boolean userChatExists = false;
            for (int i= 0; i < getPrivateMessages().size(); i++)
            {
                if (getPrivateMessages().get(i).getFirst().equals(names[2]))
                {
                    userChatExists = true;
                }
            }

            if (userChatExists)
            {
                for (int i = 0; i < getPrivateMessages().size(); i++)
                {
                    if (getPrivateMessages().get(i).getFirst().equals(names[2]))
                    {
                        getPrivateMessages().get(i).getSecond().add(names[3]);
                    }
                }
            }
            else
            {
                Pair<String,List<String>> newUserChat = new Pair<>();
                newUserChat.setFirst(names[2]);
                newUserChat.getSecond().add(names[3]);
                getPrivateMessages().add(newUserChat);
            }
        }

        @Override
        protected Void call() throws Exception
        {
            user.setUserName(user.getUserName() + ".ChatViewer");
            try(Socket socket = new Socket(host,portNumber);
                ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream()))
            {

                toServer.writeUTF(".ChatViewer");
                toServer.flush();

                toServer.writeObject(user);

                while (!socket.isClosed())
                {
                    Thread.sleep(100);

                    if (fromServer.available() >0)
                    {
                        String input = fromServer.readUTF();

                        if (input.contains(".Message"))
                        {
                            handlePrivateMessages(input);
                        }

                        else if (input.contains(".online"))
                        {
                            //TODO
                            break;
                        }
                    }

                    if (hasMessages)
                    {
                        String nextSend;
                        synchronized (messagesToSend)
                        {
                            nextSend = messagesToSend.pop();
                            hasMessages = !messagesToSend.isEmpty();
                        }

                        toServer.writeUTF(nextSend);
                        toServer.flush();
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
}
