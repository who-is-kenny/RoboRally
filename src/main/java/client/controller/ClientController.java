package client.controller;

import client.Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import server.message.Message;
import server.message.MessageBody;
import server.message.MessageSerializer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
    private ChoiceBox <String> name_dropdown;

    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }
    private Client client;

    private Map<Integer, String> clientIdName = new HashMap<>();
    public Map<Integer, String> getClientIdName() {
        return clientIdName;
    }
    public void setClientIdName(Map<Integer, String> clientIdName) {
        this.clientIdName = clientIdName;
    }

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class , new MessageSerializer()).create();

//    private static final Gson gson = new Gson();

    public ClientController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        vbox_message.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                sp_message.setVvalue((Double) t1);
            }
        });

    }

    /**
     * creates a new text box and adds it to the chat GUI
     *
     * @param messageBody
     */
    public void addMessage(MessageBody messageBody) {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10, 5, 5, 10));
        Text text = new Text(messageBody.getMessage());
        TextFlow textFlow = new TextFlow(text);
        textFlow.setPadding(new Insets(10, 5, 5, 10));
        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox_message.getChildren().add(hBox);
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
            // check if we send to everyone or someone specific
            String dropdownSelected = name_dropdown.getValue();
            // set to everyone if nothing selected
            if (dropdownSelected ==null){
                dropdownSelected = "everyone";
            }
            String chatMessage;
            if(!dropdownSelected.equals("everyone")){
                int targetID = Integer.parseInt(dropdownSelected.substring(dropdownSelected.lastIndexOf('(') + 1,dropdownSelected.lastIndexOf(')') ));
                chatMessage = createPrivateMessage(message , targetID );
            }else{
                chatMessage = createBroadcastMessage(message);
            }
            client.sendToClientHandler(chatMessage);
            text_input.clear();
        }
    }

    public String createPrivateMessage(String message , int targetClientID){
        Message privateMessage = new Message();
        privateMessage.setMessageType("SendChat");
        MessageBody privateMessageBody = new MessageBody();
        privateMessageBody.setMessage(message);
        privateMessageBody.setTo(targetClientID);
        privateMessageBody.setFrom(client.getClientID());
        privateMessage.setMessageBody(privateMessageBody);
        return gson.toJson(privateMessage);
    }

    public String createBroadcastMessage(String message){
        Message broadcastMessage = new Message();
        broadcastMessage.setMessageType("SendChat");
        MessageBody broadcastMessageBody = new MessageBody();
        broadcastMessageBody.setMessage(message);
        broadcastMessageBody.setTo(-1);
        broadcastMessageBody.setFrom(client.getClientID());
        broadcastMessageBody.setPrivate(true);
        broadcastMessage.setMessageBody(broadcastMessageBody);
        return gson.toJson(broadcastMessage);
    }

    // updates list of clients in chat dropdown
    public void updateClientList (){
        name_dropdown.getItems().add("everyone");
        for(Map.Entry<Integer,String> entry : clientIdName.entrySet()){
            String dropdownName = entry.getValue() + " (" + entry.getKey() + ")";
            name_dropdown.getItems().add(dropdownName);
        }
    }
    public void handleConnectionUpdate(MessageBody messageFromHandlerBody) {
        int clientID = messageFromHandlerBody.getClientID();
        if(clientIdName.containsKey(clientID)){
            String clientName = clientIdName.remove(clientID);
            String dropdownName = clientName + " (" + clientID + ")";
            Platform.runLater(() -> name_dropdown.getItems().remove(dropdownName));
        }
    }
    // adds clientid and name to map
    public void addClientIDName (String playerName , int clientID) {
        clientIdName.put(clientID,playerName);
    }


}


