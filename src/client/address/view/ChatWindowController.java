package client.address.view;

import Resources.Users;
import client.address.SceneSwitcher;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import java.util.ArrayList;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class ChatWindowController
{
    @FXML private TextArea txtArea_Receive;
    @FXML private TextField chat_Message;
    private String text;

    private Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);

    public void initData(String text)
    {
        this.text = text;
        //txtArea_Receive.appendText(text);
    }

    @FXML private void send_Chat(ActionEvent actionEvent)
    {
        if (!chat_Message.getText().equals(""));
        {
            chat_Message.getText();
            txtArea_Receive.appendText(chat_Message.getText()+"\n");
        }

    }
}
