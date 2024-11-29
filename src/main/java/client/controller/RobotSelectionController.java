package client.controller;

import client.Client;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import server.message.Message;
import server.message.MessageBody;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RobotSelectionController implements Initializable {

    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Parent chatRoot;
    public Parent getChatRoot() {
        return chatRoot;
    }
    public void setChatRoot(Parent chatRoot) {
        this.chatRoot = chatRoot;
    }


    @FXML
    private Button select_map_button;
    @FXML
    private ChoiceBox<String> available_maps;
    @FXML
    private ToggleGroup ready;
    @FXML
    private ToggleButton not_ready_button;
    @FXML
    private ToggleButton ready_button;
    @FXML
    private RadioButton robot_1;
    @FXML
    private RadioButton robot_2;
    @FXML
    private RadioButton robot_3;
    @FXML
    private RadioButton robot_4;
    @FXML
    private RadioButton robot_6;
    @FXML
    private RadioButton robot_5;

    @FXML
    private ToggleGroup robots;
    @FXML
    private Button confirm_robot;
    @FXML
    private TextField player_name;

    private Client client;
    public void setClient(Client client) {
        this.client = client;
    }

    private int selectedRobotId;

    private static final Gson gson = new Gson();

    public RobotSelectionController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        confirm_robot.disableProperty().bind(Bindings.or(
                robots.selectedToggleProperty().isNull(),
                player_name.textProperty().isEmpty()
        ));

        select_map_button.disableProperty().bind(
                available_maps.valueProperty().isNull()
        );

    }

    @FXML
    private void confirmRobot (ActionEvent e){
        RadioButton selectedRobot = (RadioButton) robots.getSelectedToggle();
        // get Id of selected robot
        int selectedRobotID = Integer.parseInt(selectedRobot.getId());
        // get player name
        String playerName = player_name.getText();
        //create Json message
        Message robotSelectionMessage = new Message();
        robotSelectionMessage.setMessageType("PlayerValue");
        MessageBody robotSelectionMessageBody = new MessageBody();
        robotSelectionMessageBody.setName(playerName);
        robotSelectionMessageBody.setFigure(selectedRobotID);
        robotSelectionMessage.setMessageBody(robotSelectionMessageBody);
        //send json to clienthandler via client
        client.sendToClientHandler(gson.toJson(robotSelectionMessage)); // TODO enable function later
        System.out.println(gson.toJson(robotSelectionMessage));
        disableChosenRobot(selectedRobotID); // TODO remove this line later
        // cleat textbox so cannot confirm again
        player_name.clear();
        player_name.setDisable(true);
        // enable ready and unready button after confirmation of robot.
        ready_button.setDisable(false);
        not_ready_button.setDisable(false);
    }
    @FXML
    private void confirmReady(ActionEvent e){
        //create ready message
        Message readyMessage = new Message();
        readyMessage.setMessageType("SetStatus");
        MessageBody readyMessageBody = new MessageBody();
        readyMessageBody.setReady(true);
        readyMessage.setMessageBody(readyMessageBody);
        client.sendToClientHandler(gson.toJson(readyMessage));
        System.out.println(gson.toJson(readyMessage));
        System.out.println("ready");
    }
    @FXML
    private void confirmUnready(ActionEvent e){
        //create unready message
        Message unreadyMessage = new Message();
        unreadyMessage.setMessageType("SetStatus");
        MessageBody unreadyMessageBody = new MessageBody();
        unreadyMessageBody.setReady(false);
        unreadyMessage.setMessageBody(unreadyMessageBody);
        client.sendToClientHandler(gson.toJson(unreadyMessage));
        System.out.println(gson.toJson(unreadyMessage));
        System.out.println("unready");
    }

    @FXML
    private void selectMap(ActionEvent e){
        Message mapSelectedMessage = new Message();
        mapSelectedMessage.setMessageType("MapSelected");
        MessageBody mapSelectedMessageBody = new MessageBody();
        mapSelectedMessageBody.setMap(available_maps.getValue());
        mapSelectedMessage.setMessageBody(mapSelectedMessageBody);
        client.sendToClientHandler(gson.toJson(mapSelectedMessage));
    }

    public void handlePlayerAdded(MessageBody ms , int clientID){
        int robotID = ms.getFigure();
        if (ms.getClientID() != clientID){
            disableChosenRobot(robotID);
        }
    }

    public void disableChosenRobot(int robotID){
        if (Integer.parseInt(robot_1.getId()) == robotID) robot_1.setDisable(true);
        if (Integer.parseInt(robot_2.getId()) == robotID) robot_2.setDisable(true);
        if (Integer.parseInt(robot_3.getId()) == robotID) robot_3.setDisable(true);
        if (Integer.parseInt(robot_4.getId()) == robotID) robot_4.setDisable(true);
        if (Integer.parseInt(robot_5.getId()) == robotID) robot_5.setDisable(true);
        if (Integer.parseInt(robot_6.getId()) == robotID) robot_6.setDisable(true);
    }

    public void switchToChatScene(){
        Platform.runLater(()->{
            Scene chatScene = new Scene(chatRoot, 850, 600);
            stage.setScene(chatScene);

        });
    }


    public int getSelectedRobotId() {
        return selectedRobotId;
    }

    public void setSelectedRobotId(int selectedRobotId) {
        this.selectedRobotId = selectedRobotId;
    }


    public void handleSelectMap(MessageBody messageFromHandlerBody) {
        List<String> availableMaps = messageFromHandlerBody.getAvailableMaps();
        // Add maps to the ChoiceBox if it's not already populated
        if (available_maps.getItems().isEmpty()){
            for (String map : availableMaps){
                // prevent adding duplicate items
                if(!available_maps.getItems().contains(map)){
                    available_maps.getItems().add(map);
                }
            }
        }
    }

    public void setDisableMap(boolean b){
        available_maps.setDisable(b);
    }
}

