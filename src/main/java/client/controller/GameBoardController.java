package client.controller;

import client.Client;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import server.message.Message;
import server.message.MessageBody;

import java.net.URL;
import java.util.*;

public class GameBoardController implements Initializable {

    private static final Gson gson = new Gson();

    @FXML
    private ImageView DizzyHighway;

    @FXML
    private GridPane game_grid;

    @FXML
    private ImageView gear1;

    @FXML
    private ImageView gear2;

    @FXML
    private ImageView gear3;

    @FXML
    private ImageView gear4;

    @FXML
    private ImageView gear5;

    @FXML
    private ImageView gear6;

    @FXML
    private Pane mainPane;

    private Client client;
    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }

    // stores client id and robot id for starting point selection.
    private Map<Integer , Integer> ClientIDRobotID = new HashMap<>();

    public GameBoardController(){}


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        gear1.setOnMouseClicked(event -> handleGearClick(gear1));
        gear2.setOnMouseClicked(event -> handleGearClick(gear2));
        gear3.setOnMouseClicked(event -> handleGearClick(gear3));
        gear4.setOnMouseClicked(event -> handleGearClick(gear4));
        gear5.setOnMouseClicked(event -> handleGearClick(gear5));
        gear6.setOnMouseClicked(event -> handleGearClick(gear6));
    }

    private void handleGearClick(ImageView gear) {
        Integer row = GridPane.getRowIndex(gear);
        Integer col = GridPane.getColumnIndex(gear);
        // Handle null cases (default to 0 if no position is explicitly set)
        row = (row == null) ? 0 : row;
        col = (col == null) ? 0 : col;

        System.out.println("Gear clicked at Row: " + row + ", Column: " + col);

        // create set starting point message
        Message setStartingPointMessage = new Message();
        setStartingPointMessage.setMessageType("SetStartingPoint");
        MessageBody setStartingPointMessageBody = new MessageBody();
        setStartingPointMessageBody.setX(col);
        setStartingPointMessageBody.setY(row);
        setStartingPointMessage.setMessageBody(setStartingPointMessageBody);
        // send message to client handler
        if (client.getClientID() == client.getCurrentPlayerID() && client.getActivePhase() == 0){
            System.out.println("sending coordinates to clienthandler");
            client.sendToClientHandler(gson.toJson(setStartingPointMessage));
        }
    }


    public void handleStartingPointTaken(MessageBody messageFromHandlerBody) {
        // Extract coordinates and client info from the message
        int x = messageFromHandlerBody.getX();
        int y = messageFromHandlerBody.getY();
        int clientID = messageFromHandlerBody.getClientID();
        String direction = messageFromHandlerBody.getDirection();

        // Retrieve the robot ID for this client
        Integer robotID = ClientIDRobotID.get(clientID);
//        if (robotID == null) {
//            System.err.println("No robot ID found for client ID: " + clientID);
//        }

        String resourcePath = "/client/images/robot" + robotID + ".PNG";
        URL resourceUrl = getClass().getResource(resourcePath);

        if (resourceUrl == null) {
            System.err.println("Resource not found: " + resourcePath);
            return;
        }
        System.out.println("Looking for resource: " + resourcePath);
        System.out.println("Robot ID: " + robotID);
        System.out.println("Client ID: " + clientID);

        String robotImageFile = resourceUrl.toString();
        ImageView robotImage = new ImageView(robotImageFile);




        // Load the corresponding robot image


//        String robotImageFile = Objects.requireNonNull(getClass().getResource("/client/images/robot" + robotID + ".PNG")).toString(); // Update with your file path
//        ImageView robotImage = new ImageView(robotImageFile);

        // Set the ID of the robot image to the client ID for future use
        robotImage.setId(String.valueOf(clientID));

        // Add any desired image properties
        robotImage.setFitWidth(40); // Set width
        robotImage.setFitHeight(40); // Set height
        robotImage.setPreserveRatio(true);

        //  TODO rotate the image based on direction if needed
//        switch (direction.toLowerCase()) {
//            case "right":
//                robotImage.setRotate(90);
//                break;
//            case "left":
//                robotImage.setRotate(270);
//                break;
//            case "down":
//                robotImage.setRotate(180);
//                break;
//            default:
//                // No rotation needed for "up"
//                break;
//        }
        //TODO change this to else if when there are more maps
        robotImage.setRotate(90);

        Platform.runLater(() -> {
            // Update the grid with the robot image
            for (ImageView gear : new ImageView[]{gear1, gear2, gear3, gear4, gear5, gear6}) {
                if (GridPane.getRowIndex(gear) == y && GridPane.getColumnIndex(gear) == x) {
                    game_grid.getChildren().remove(gear); // Remove the gear
                    game_grid.add(robotImage, x, y); // Add robot image to the grid
                    break;
                }
            }
        });


    }
    //remove remaining gears when active phase = 2
    public void handleactivephase2() {
        Platform.runLater(() -> {
            // Iterate through the grid and remove all the gear images
            for (ImageView gear : new ImageView[]{gear1, gear2, gear3, gear4, gear5, gear6}) {
                if (game_grid.getChildren().contains(gear)) {
                    game_grid.getChildren().remove(gear); // Remove the gear
                }
            }
            System.out.println("All remaining gears have been removed from the game board.");
        });
    }

    //move robot function
    public void handleRobotMovement(MessageBody messageBody){
        int new_x = messageBody.getX();
        int new_y = messageBody.getY();
        int clientID = messageBody.getClientID();

        Platform.runLater(() -> {
            // Find the robot image by its ID
            ImageView robotImage = null;
            for (javafx.scene.Node node : game_grid.getChildren()) {
                if (node instanceof ImageView && clientID == Integer.parseInt(node.getId())) {
                    robotImage = (ImageView) node;
                    break;
                }
            }

            if (robotImage != null) {
                // Update the robot's position by setting new GridPane row and column constraints
                GridPane.setColumnIndex(robotImage, new_x);
                GridPane.setRowIndex(robotImage, new_y);
                System.out.println("Robot for client ID " + clientID + " moved to (" + new_x + ", " + new_y + ")");
            } else {
                System.err.println("Robot for client ID " + clientID + " not found on the game board.");
            }
        });

    }

    // robot turn function
    public void handleRobotTurn(MessageBody messageBody){
        int clientID = messageBody.getClientID();
        String rotation = messageBody.getRotation();

        Platform.runLater(() -> {
            // Find the robot image by its ID
            ImageView robotImage = null;
            for (javafx.scene.Node node : game_grid.getChildren()) {
                if (node instanceof ImageView && clientID == Integer.parseInt(node.getId())) {
                    robotImage = (ImageView) node;
                    break;
                }
            }

            if (robotImage != null) {
                // Adjust the rotation based on the message
                double currentRotation = robotImage.getRotate(); // Get the current rotation angle
                double newRotation;

                if ("clockwise".equalsIgnoreCase(rotation)) {
                    newRotation = currentRotation + 90; // Rotate 90 degrees clockwise
                } else if ("counterclockwise".equalsIgnoreCase(rotation)) {
                    newRotation = currentRotation - 90; // Rotate 90 degrees counterclockwise
                } else {
                    System.err.println("Invalid rotation direction: " + rotation);
                    return;
                }

                // Set the new rotation value
                robotImage.setRotate(newRotation);
                System.out.println("Robot for client ID " + clientID + " turned " + rotation + " to angle: " + newRotation);
            } else {
                System.err.println("Robot for client ID " + clientID + " not found on the game board.");
            }
        });
    }

    // robot reboot
    public void handleReboot(MessageBody messageFromHandlerBody){
        Platform.runLater(() -> {

            // reset robot direction back to default
            ImageView robotImage = null;
            for (javafx.scene.Node node : game_grid.getChildren()) {
                if (node instanceof ImageView && messageFromHandlerBody.getClientID() == Integer.parseInt(node.getId())) {
                    robotImage = (ImageView) node;
                    break;
                }
            }
            assert robotImage != null;
            robotImage.setRotate(0);
        });
    }
    // pop up for rebooting client
    public void sendRebootPopup() {
        Platform.runLater(() -> {
            // Define the reboot direction options
            String[] directions = {"Up", "Down", "Left", "Right"};

            // Create a ChoiceDialog with the options
            ChoiceDialog<String> dialog = new ChoiceDialog<>(directions[0], directions);
            dialog.setTitle("Reboot Direction");
            dialog.setHeaderText("Reboot Required!");
            dialog.setContentText("Choose a direction to reboot:");

            // Show the dialog and wait for user input
            Optional<String> result = dialog.showAndWait();

            // Handle the user's choice
            result.ifPresent(chosenDirection -> {
                System.out.println("User chose to reboot in the direction: " + chosenDirection);

                // Send the chosen direction back to the server
                Message rebootMessage = new Message();
                rebootMessage.setMessageType("RebootDirection");
                MessageBody rebootMessageBody = new MessageBody();
                rebootMessageBody.setDirection(chosenDirection);
                rebootMessage.setMessageBody(rebootMessageBody);

                client.sendToClientHandler(gson.toJson(rebootMessage));
            });
        });
    }



    public void addClientIDRobotID(int ClientID , int RobotID){
        ClientIDRobotID.put(ClientID , RobotID);
    }


}
