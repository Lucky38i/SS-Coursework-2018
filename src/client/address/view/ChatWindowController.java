package client.address.view;

import Resources.Pair;
import Resources.Users;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The chatWindowController that handles all private messages between the client and selected users
 * @author alexmcbean, Alex Pona
 */
public class ChatWindowController
{
    @FXML private TextArea txt_Messages;
    @FXML private TextField chat_Message;
    @FXML private ListView<String> lst_Users;

    private List<Pair<String,List<String>>> privateMessages;
    private String selectedUser;
    private backgroundThread bgThread;
    private String host;
    private int portNumber;
    private Users user;
    private Timeline theTimer;

    private Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);

    void initData(String host, int portNumber, Users user)
    {
        this.host = host;
        this.portNumber = portNumber;
        this.user = user;
        privateMessages = new ArrayList<>();

        bgThread = new backgroundThread(this.host,this.portNumber,this.user);
        Task task = bgThread;
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        //A periodic timer that checks the list of online users
        theTimer = new Timeline(new KeyFrame(Duration.seconds(5), event -> Platform.runLater(()-> lst_Users.setItems(bgThread.getOnlineUserList()))));
        theTimer.setCycleCount(Timeline.INDEFINITE);
        theTimer.playFrom(Duration.seconds(4.5));

    }

    public Timeline getTheTimer()
    {
        return theTimer;
    }

    private synchronized List<Pair<String, List<String>>> getPrivateMessages()
    {
        return privateMessages;
    }

    @FXML private void send_Chat()
    {
        if (chat_Message.getText().equals(""))
        {
            alertInfo.setTitle("");
            alertInfo.setHeaderText(null);
            alertInfo.setContentText("Your message can't be empty");
            alertInfo.showAndWait();
        }
        else
        {
            bgThread.addNextMessage(".Message." + selectedUser + "." + chat_Message.getText());
            chat_Message.setText("");
        }
    }

    /**
     * gets the messages being discussed with a user
     */
    public void get_Messages()
    {
        selectedUser = lst_Users.getSelectionModel().getSelectedItem();
        txt_Messages.setText("");
        if (privateMessages.size() > 0)
        {
            for (Pair<String, List<String>> privateMessage : privateMessages)
            {
                if (privateMessage.getFirst().equals(selectedUser) || privateMessage.getFirst().equals(selectedUser.substring(0,selectedUser.indexOf("(F"))))
                {
                    for (int x = 0; x < privateMessage.getSecond().size(); ++x)
                        txt_Messages.appendText(privateMessage.getSecond().get(x) + "\n");
                }
            }
        }
        else
            txt_Messages.setText("");
    }

    /**
     * Retrieves the file selected and sends it to the server
     * (Currently not working)
     * @param actionEvent not working
     */
    @FXML private void Open_File(ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(txt_Messages.getScene().getWindow());
        String fileName = file.getName();
        bgThread.addNextSong(file, fileName);
    }

    class backgroundThread extends Task<Void>
    {

        private String host;
        private int portNumber;
        private Users user;
        private boolean hasMessages = false;
        private final LinkedBlockingQueue<String> messagesToSend;
        private boolean hasMusic = false;
        private final LinkedBlockingQueue<Pair<File,String>> musicToSend;
        private ObservableList<String> onlineUserList = FXCollections.observableArrayList();

        backgroundThread(String host, int portNumber, Users user)
        {
            this.host = host;
            this.portNumber = portNumber;
            this.user = user;
            messagesToSend = new LinkedBlockingQueue<>();
            musicToSend = new LinkedBlockingQueue<>();
        }

        synchronized ObservableList<String> getOnlineUserList()
        {
            return onlineUserList;
        }

        private void setOnlineUserList(ArrayList<String> list)
        {
            Platform.runLater(()->this.onlineUserList.setAll(list));
        }

        /**
         * Add a new message to the messages to be
         * sent to the server
         * @param msg The message to the sent to the server
         */
        synchronized void addNextMessage(String msg)
        {
            try
            {
                hasMessages = true;
                messagesToSend.put(msg);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        synchronized void addNextSong(File file, String fileName)
        {
            try
            {
                Pair<File, String> newSong = new Pair<>();
                newSong.setFirst(file);
                newSong.setSecond(fileName);
                hasMusic = true;
                musicToSend.put(newSong);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
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
            String userName = names[2];
            String message = names[3];
            boolean userChatExists = false;
            for (int i= 0; i < getPrivateMessages().size(); i++)
            {
                if (getPrivateMessages().get(i).getFirst().equals(userName))
                {
                    userChatExists = true;
                }
            }

            if (userChatExists)
            {
                for (int i = 0; i < getPrivateMessages().size(); i++)
                {
                    if (getPrivateMessages().get(i).getFirst().equals(userName))
                    {
                        getPrivateMessages().get(i).getSecond().add(message);
                    }
                }
            }
            else
            {
                Pair<String,List<String>> newUserChat = new Pair<>();
                List<String> tempList = new ArrayList<>();
                newUserChat.setFirst(userName);
                newUserChat.setSecond(tempList);
                newUserChat.getSecond().add(userName + ": " + message);
                getPrivateMessages().add(newUserChat);
            }
        }

        @Override
        protected Void call()
        {
            user.setUserName(user.getUserName() + ".ChatViewer");
            try(Socket socket = new Socket(host,portNumber);
                ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream()))
            {

                toServer.writeUTF(".ChatViewer");
                toServer.flush();

                toServer.writeObject(user);
                toServer.flush();

                while (!socket.isClosed())
                {
                    if (fromServer.available() > 0)
                    {
                        String input = fromServer.readUTF();

                        if (input.contains(".online"))
                        {
                            Object obj = fromServer.readObject();
                            ArrayList<String> templist = (ArrayList<String>) obj;

                            //Checks if any of the online users are friends
                            for (int i = 0; i < templist.size(); ++i)
                            {
                                for (int x = 0; x < user.getFriendsList().size(); ++x)
                                {
                                    if (templist.get(i).equals(user.getFriendsList().get(x)))
                                    {
                                        templist.set(i, templist.get(i) + "(Friend)");
                                    }
                                }
                            }
                            setOnlineUserList(templist);
                        } else if (input.contains(".Message"))
                        {
                            handlePrivateMessages(input);
                        }
                    }

                    if (hasMessages)
                    {
                        String nextSend;
                        synchronized (messagesToSend)
                        {
                            nextSend = messagesToSend.take();
                            hasMessages = !messagesToSend.isEmpty();
                        }

                        toServer.writeUTF(nextSend);
                        toServer.flush();
                    }

                    if (hasMusic)
                    {
                        Pair<File, String> nextSong;
                        synchronized (musicToSend)
                        {
                            nextSong = musicToSend.take();
                            hasMusic = !musicToSend.isEmpty();
                        }

                        Integer fileLength = (int) nextSong.getFirst().length();
                        byte[] buffer = new byte[fileLength];

                        addNextMessage(".NewSong."+lst_Users.getSelectionModel().getSelectedItem() +"."+nextSong.getSecond());


                        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(nextSong.getFirst())))
                        {
                            bis.read(buffer,0,buffer.length);

                            toServer.writeInt(fileLength);
                            toServer.flush();
                            toServer.write(buffer,0, buffer.length);
                            toServer.flush();
                        }

                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
}
