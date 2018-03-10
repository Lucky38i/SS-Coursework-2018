package client;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class clientFXHandler
{

    @FXML
    public TextField textArea_Send;

    @FXML
    public TextArea textArea_Receive;

    @FXML
    public void printOut()
    {
        textArea_Receive.setText(textArea_Send.getText());
    }
    @FXML
    public void appendTextArea_Receive(String message)
    {
        textArea_Receive.appendText(message);
    }

}
