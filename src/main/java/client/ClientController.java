package client;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import server.Message;
import server.MessageBody;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    private Button button_send;
    @FXML
    private TextField text_input;
    @FXML
    private VBox vbox_message;
    @FXML
    private ScrollPane sp_message;
    @FXML
    private ChoiceBox name_dropdown;

    private Client client;

    private static final Gson gson = new Gson();

    public ClientController(Client client) {
        this.client = client;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        try{
//            Socket socket = new Socket("localhost" , 8888);
//            client = new Client(socket);
//            System.out.println("connected to server");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        vbox_message.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                sp_message.setVvalue((Double) t1);
            }
        });

        client.receiveFromClientHandler(vbox_message);
    }

    /**
     * creates a new text box and adds it to the chat GUI
     * @param messageFromHandler
     * @param vBox
     */
    public static void addMessage(String messageFromHandler, VBox vBox) {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10, 5, 5, 10));
        Text text = new Text(messageFromHandler);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setPadding(new Insets(10, 5, 5, 10));
        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBox.getChildren().add(hBox);
            }
        });

    }

    /**
     * method linked to send button (chat app GUI).
     * adds text to own chat and sends text to client handler to be broadcasted
     * @param e
     */
    @FXML
    private void sendMessage(ActionEvent e) {
        String message = text_input.getText();
        System.out.println(message);
        if (!message.isEmpty()) {
            HBox hBox = new HBox();
            hBox.setPadding(new Insets(5, 5, 5, 10));
            Text text = new Text(message);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setPadding(new Insets(5, 5, 5, 10));
            hBox.getChildren().add(textFlow);
            vbox_message.getChildren().add(hBox);
            client.sendToClientHandler(message);
            text_input.clear();
        }
    }

    public String createPrivateMessage(String message , int targetClientID){
        Message privateMessage = new Message();
        privateMessage.setMessageType("SendChat");
        MessageBody privateMessageBody = new MessageBody();
        privateMessageBody.setMessage(message);
        privateMessageBody.setTo(targetClientID);
        privateMessage.setMessageBody(privateMessageBody);
        return gson.toJson(privateMessage);
    }

    public String createBroadcastMessage(String message){
        Message broadcastMessage = new Message();
        broadcastMessage.setMessageType("SendChat");
        MessageBody broadcastMessageBody = new MessageBody();
        broadcastMessageBody.setMessage(message);
        broadcastMessageBody.setTo(-1);
        broadcastMessageBody.setPrivate(true);
        broadcastMessage.setMessageBody(broadcastMessageBody);
        return gson.toJson(broadcastMessage);
    }


}


