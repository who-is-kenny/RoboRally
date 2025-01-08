package client.controller;

import client.Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.Blend;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import server.message.Message;
import server.message.MessageBody;
import server.message.MessageSerializer;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class GameBoardController implements Initializable {
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class , new MessageSerializer()).create();

//    private static final Gson gson = new Gson();

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
    private String selectedMap;

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
        if (selectedMap.equals("DeathTrap")){
            robotImage.setRotate(270);
        }else{
            robotImage.setRotate(90);
        }


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

    public void applyGlowToRobot(int clientID, Color glowColor, double durationInSeconds) {
        Platform.runLater(() -> {
            // Find the robot image by its ID
            ImageView robotImage = (ImageView) game_grid.getChildren().stream().filter(node -> node instanceof ImageView && clientID == Integer.parseInt(node.getId())).findFirst().orElse(null);

            if (robotImage != null) {
                // Create a DropShadow for the glow effect
                DropShadow glowEffect = new DropShadow();
                glowEffect.setColor(glowColor);    // Set the glow color
                glowEffect.setRadius(20);         // Set the glow radius
                glowEffect.setSpread(0.6);        // Set intensity of the glow
                robotImage.setEffect(glowEffect); // Apply the effect to the robot

                // Use PauseTransition to remove the glow after the specified duration
                PauseTransition pause = new PauseTransition(Duration.seconds(durationInSeconds));
                pause.setOnFinished(event -> robotImage.setEffect(null)); // Remove glow
                pause.play();
            } else {
                System.err.println("Robot for client ID " + clientID + " not found on the game board.");
            }
        });
    }

    public void changeBackgroundImage(String imageName) {
        Platform.runLater(() -> {
            try {
                //change the background image
                selectedMap = imageName;
                String imagePath = Objects.requireNonNull(getClass().getResource("/client/images/" + imageName + ".png")).toExternalForm();
                DizzyHighway.setImage(new javafx.scene.image.Image(imagePath));

                // move gears for death trap
                if (imageName.equals("DeathTrap")){
                    GridPane.setRowIndex(gear1, 1);
                    GridPane.setColumnIndex(gear1, 11);

                    GridPane.setRowIndex(gear2, 3);
                    GridPane.setColumnIndex(gear2, 12);

                    GridPane.setRowIndex(gear3, 4);
                    GridPane.setColumnIndex(gear3, 11);

                    GridPane.setRowIndex(gear4, 5);
                    GridPane.setColumnIndex(gear4, 11);

                    GridPane.setRowIndex(gear5, 6);
                    GridPane.setColumnIndex(gear5, 12);

                    GridPane.setRowIndex(gear6, 8);
                    GridPane.setColumnIndex(gear6, 11);

                    System.out.println("Gears repositioned for map: " + imageName);
                }

                System.out.println("Background image changed to: " + imageName);
            } catch (NullPointerException e) {
                System.err.println("Image not found: " + imageName);
            }
        });
    }

    public void sendDamageSelectionPopup(MessageBody messageBody) {


        Platform.runLater(() -> {
            int requiredCount = (messageBody.getCount());
            List<String> availablePiles = messageBody.getAvailablePiles();
            // Example: This count and availablePiles will come from the message

            // Show the dialog for card selection
            Optional<List<String>> result = showCardSelectionDialog(availablePiles, requiredCount);

            // Handle the user's choice
            result.ifPresent(selectedCards -> {
                if (selectedCards.size() == requiredCount) {
                    // User selected the correct number of cards
                    System.out.println("User selected the following cards: " + selectedCards);

                    // Send the chosen cards back to the server
                    Message selectedDamageMessage = new Message();
                    selectedDamageMessage.setMessageType("SelectedDamage");

                    MessageBody selectedDamageMessageBody = new MessageBody();
                    selectedDamageMessageBody.setCards(selectedCards);

                    selectedDamageMessage.setMessageBody(selectedDamageMessageBody);

                    // Send the message to the client handler
                    client.sendToClientHandler(gson.toJson(selectedDamageMessage));
                } else {
                    // Show a warning if the user didn't select the required number of cards
                    showErrorPopup("Invalid Selection", "Please select exactly " + requiredCount + " cards.");
                }
            });
        });
    }

    public void showGameWinnerPopup(MessageBody messageBody) {
        Platform.runLater(() -> {
            // Extract the winner's client ID from the message body
            int winnerClientID = messageBody.getClientID();

            // Create an information alert to display the winner
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("We Have a Winner!");
            alert.setContentText("Congratulations Player " + winnerClientID + " , YOU WIN!");

            // Show the alert and wait for user to close it
            alert.showAndWait();
        });
    }

    public void makeRobotVibrate(int clientID, double durationInSeconds) {
        Platform.runLater(() -> {
            // Find the robot image by its ID
            ImageView robotImage = (ImageView) game_grid.getChildren().stream().filter(node -> node instanceof ImageView && Integer.toString(clientID).equals(node.getId())).findFirst().orElse(null);

            if (robotImage != null) {
                double originalRotate = robotImage.getRotate();

                Timeline rotationTimeline = new Timeline(
                        new KeyFrame(Duration.millis(50), e -> robotImage.setRotate(originalRotate - 15)),
                        new KeyFrame(Duration.millis(100), e -> robotImage.setRotate(originalRotate + 15)),
                        new KeyFrame(Duration.millis(150), e -> robotImage.setRotate(originalRotate))
                );

                rotationTimeline.setCycleCount((int) (durationInSeconds * 1000 / 150));
                rotationTimeline.setOnFinished(e -> {
                    robotImage.setRotate(originalRotate);
                    System.out.println("Rotation completed for robot of Client ID: " + clientID);
                });
                rotationTimeline.play();
            } else {
                System.err.println("Robot for client ID " + clientID + " not found on the game board.");
            }
        });
    }

    public void makeAllRobotsShootLaser(double durationInSeconds) {
        Platform.runLater(() -> {
            // Iterate over all nodes in the game grid
            for (javafx.scene.Node node : game_grid.getChildren()) {
                if (node instanceof ImageView) {
                    ImageView robotImage = (ImageView) node;

                    // Create a red tint effect at the tip
                    DropShadow laserGlow = new DropShadow();
                    laserGlow.setColor(Color.WHITE);
                    laserGlow.setRadius(10); // Intensity of the glow
                    laserGlow.setSpread(0.8); // Concentrate the glow effect

                    // Offset to the "tip" of the robot (assume the top side as the tip)
                    laserGlow.setOffsetX(0);
                    laserGlow.setOffsetY(-5); // Adjust for the tip; tune as per robot image size

                    // Apply the effect to the robot image
                    robotImage.setEffect(laserGlow);
                }
            }

            // Create a Timeline to remove the effect from all robots after the duration
            Timeline laserTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(durationInSeconds), e -> {
                        for (javafx.scene.Node node : game_grid.getChildren()) {
                            if (node instanceof ImageView) {
                                ((ImageView) node).setEffect(null); // Remove the effect
                            }
                        }
                        System.out.println("Laser effect completed for all robots.");
                    })
            );

            laserTimeline.play();
        });
    }

    private Optional<List<String>> showCardSelectionDialog(List<String> availablePiles, int requiredCount) {
        // Create a dialog to allow the user to select cards from available piles
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Select Damage Cards");
        alert.setHeaderText("Please choose " + requiredCount + " damage cards");

        // Use a VBox to hold the ComboBoxes for each selection
        VBox vbox = new VBox();
        List<ComboBox<String>> comboBoxes = new ArrayList<>();

        // Create a ComboBox for each required card selection
        for (int i = 0; i < requiredCount; i++) {
            // Create a horizontal box (HBox) to hold the label and combo box side by side
            HBox hbox = new HBox();

            // Label for "Card X"
            Label label = new Label("Card " + (i + 1) + ": ");

            // ComboBox for selecting damage cards
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.getItems().addAll(availablePiles); // Add available piles to the dropdown
            comboBox.getSelectionModel().selectFirst(); // Set default selection to the first pile

            comboBoxes.add(comboBox);

            // Add the label and ComboBox to the HBox
            hbox.getChildren().addAll(label, comboBox);

            // Add the HBox to the VBox
            vbox.getChildren().add(hbox);
        }

        alert.getDialogPane().setContent(vbox);

        // Show the dialog and wait for user input
        alert.showAndWait();


        // Collect the selected piles (cards) from each ComboBox
        List<String> selectedCards = comboBoxes.stream()
                .map(ComboBox::getValue) // Get selected value from each ComboBox
                .collect(Collectors.toList());

        // Ensure the number of selected cards matches the required count
        if (selectedCards.size() != requiredCount) {
            // If the number of selected cards is incorrect, return empty
            return Optional.empty();
        }

        // Return the list of selected cards (from the ComboBox selections)
        return Optional.of(selectedCards);
    }

    private void showErrorPopup(String title, String message) {
        // Display an error popup to inform the user that they selected an incorrect number of cards
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public void handleConnectionUpdate(MessageBody messageFromHandlerBody) {
        int clientID = messageFromHandlerBody.getClientID();

        Platform.runLater(() -> {
            // Find the robot image by its ID
            ImageView robotImage = null;
            for (javafx.scene.Node node : game_grid.getChildren()) {
                if (node instanceof ImageView && Integer.toString(clientID).equals(node.getId())) {
                    robotImage = (ImageView) node;
                    break;
                }
            }

            if (robotImage != null) {
                game_grid.getChildren().remove(robotImage); // Remove the robot from the grid
                System.out.println("Robot with client ID " + clientID + " has been removed from the game board.");
            } else {
                System.err.println("Robot with client ID " + clientID + " not found on the game board.");
            }
        });

    }


    public void addClientIDRobotID(int ClientID , int RobotID){
        ClientIDRobotID.put(ClientID , RobotID);
    }

    /** --------------------------------------------------------------------------------------------------------------- **/
    // AI methods:

    public void AISelectStartingPoint() {
        Platform.runLater(() -> {
            // Collect all available gears on the grid
            List<ImageView> availableGears = new ArrayList<>();
            for (ImageView gear : new ImageView[]{gear1, gear2, gear3, gear4, gear5, gear6}) {
                if (game_grid.getChildren().contains(gear)) {
                    availableGears.add(gear);
                }
            }

            // If no gears are available, return
            if (availableGears.isEmpty()) {
                System.err.println("No available gears for AI to select as a starting point.");
                return;
            }

            // Randomly pick one of the available gears
            Random random = new Random();
            ImageView selectedGear = availableGears.get(random.nextInt(availableGears.size()));

            // Get the coordinates of the selected gear
            Integer row = GridPane.getRowIndex(selectedGear);
            Integer col = GridPane.getColumnIndex(selectedGear);

            // Handle null cases (default to 0 if no position is explicitly set)
            row = (row == null) ? 0 : row;
            col = (col == null) ? 0 : col;

            System.out.println("AI selected starting point at Row: " + row + ", Column: " + col);

            // Simulate the AI selecting the starting point
            Message setStartingPointMessage = new Message();
            setStartingPointMessage.setMessageType("SetStartingPoint");
            MessageBody setStartingPointMessageBody = new MessageBody();
            setStartingPointMessageBody.setX(col);
            setStartingPointMessageBody.setY(row);
            setStartingPointMessage.setMessageBody(setStartingPointMessageBody);
            System.out.println("AI sending coordinates to client handler");
            client.sendToClientHandler(gson.toJson(setStartingPointMessage));

            // Send the message to the client handler
//            if (client.getClientID() == client.getCurrentPlayerID() && client.getActivePhase() == 0) {
//                System.out.println("AI sending coordinates to client handler");
//                client.sendToClientHandler(gson.toJson(setStartingPointMessage));
//            }
        });
    }

}
