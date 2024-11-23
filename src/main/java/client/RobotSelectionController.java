package client;

import com.google.gson.Gson;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import server.Message;
import server.MessageBody;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class RobotSelectionController implements Initializable {


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

    private int selectedRobotId;


    private static final Gson gson = new Gson();

    public RobotSelectionController(Client client) {
        this.client = client;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        confirm_robot.disableProperty().bind(Bindings.or(
                robots.selectedToggleProperty().isNull(),
                player_name.textProperty().isEmpty()
        ));

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
        robotSelectionMessageBody.setPlayerName(playerName);
        robotSelectionMessageBody.setFigure(selectedRobotID);
        robotSelectionMessage.setMessageBody(robotSelectionMessageBody);
        //send json to clienthandler via client
        client.sendToClientHandler(gson.toJson(robotSelectionMessage)); // TODO enable function later
        System.out.println(gson.toJson(robotSelectionMessage));
        disableChosenRobot(selectedRobotID); // TODO remove this line later
        // cleat textbox so cannot confirm again
        player_name.clear();
        player_name.setDisable(true);
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



//    public RobotSelectionController(Client client) {
//        this.client = client;
//    }

    public int getSelectedRobotId() {
        return selectedRobotId;
    }

    public void setSelectedRobotId(int selectedRobotId) {
        this.selectedRobotId = selectedRobotId;
    }
}

