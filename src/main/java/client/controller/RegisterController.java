package client.controller;

import client.Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;
import server.message.Message;
import server.message.MessageBody;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import server.message.MessageSerializer;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    public RegisterController() {}

    @FXML private ImageView card1Image, card2Image, card3Image, card4Image, card5Image, card6Image, card7Image, card8Image, card9Image;
    @FXML private Button card1, card2, card3, card4, card5, card6, card7, card8, card9;
    @FXML private Button selectedCard1, selectedCard2, selectedCard3, selectedCard4, selectedCard5;
    @FXML private Button resetButton;
    @FXML
    private Label timer;

    @FXML
    private Timeline timerAnimation;
    private final boolean[] cardsSelected = new boolean[9];
    private final boolean[] registersFilled = new boolean[5];
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class , new MessageSerializer()).create();
//    private static final Gson gson = new Gson();
    private boolean isTimerRunning = false;
    private int timeRemaining = 30;

    // For connection to client
    private Client client;

    public void setClient(Client client) {
        this.client = client;
    }

    private Button[] getCardButtons() {
        return new Button[]{card1, card2, card3, card4, card5, card6, card7, card8, card9};
    }

    private Button[] getRegisterButtons() {
        return new Button[]{selectedCard1, selectedCard2, selectedCard3, selectedCard4, selectedCard5};
    }

    private Button getFirstEmptyRegister() {
        Button[] registers = getRegisterButtons();
        for (int i = 0; i < registers.length; i++) {
            if (!registersFilled[i]) return registers[i];
        }
        return null;
    }

    private int getRegisterIndex(Button register) {
        Button[] registers = getRegisterButtons();
        for (int i = 0; i < registers.length; i++) {
            if (registers[i] == register) return i;
        }
        return -1;
    }

    private boolean areAllRegistersFilled() {
        for (boolean filled : registersFilled) {
            if (!filled) {
                return false;
            }
        }
        return true;
    }

    @FXML
    private void handleReset() {
        // Prevent resetting if all registers are filled
        if (areAllRegistersFilled()) {
            return;
        }

        // Reset registers
        Button[] registers = getRegisterButtons();
        for (Button register : registers) {
            register.setGraphic(null);
            register.getStyleClass().remove("selected-card");
        }

        // Reset card buttons
        for (Button cardButton : getCardButtons()) {
            cardButton.setDisable(false);
            cardButton.setOpacity(1.0);
            cardButton.getStyleClass().remove("disabled-card");
            cardButton.getStyleClass().add("available-card");
        }

        Arrays.fill(cardsSelected, false);
        Arrays.fill(registersFilled, false);
    }

    // According to the protocol, player can't change anything after the 5th card is placed
    // TODO Review if this button is necessary
    @FXML
    private void handleSubmit() {
        resetButton.setDisable(true);
        for (Button register : getRegisterButtons()) {
            register.setDisable(true);
            register.setOpacity(0.75);
        }
    }

    @FXML
    public void handleCardSelection(javafx.event.ActionEvent actionEvent) {
        Button cardButton = (Button) actionEvent.getSource();
        int cardIndex = Integer.parseInt(cardButton.getId().replace("card", "")) - 1;
        if (cardsSelected[cardIndex]) return;

        Button emptyRegister = getFirstEmptyRegister();
        if (emptyRegister != null) {
            // Assign card images to register
            ImageView cardImageView = (ImageView) cardButton.getGraphic();
            ImageView registerImageView = new ImageView(cardImageView.getImage());
            registerImageView.setFitWidth(cardImageView.getFitWidth() * 1.25);
            registerImageView.setFitHeight(cardImageView.getFitHeight() * 1.25);
            registerImageView.setPreserveRatio(true);
            emptyRegister.setGraphic(registerImageView);
//            emptyRegister.setText(cardButton.getText());
            emptyRegister.getStyleClass().add("selected-card");

            // Update card state
            String cardName = cardButton.getText();
            int registerIndex = getRegisterIndex(emptyRegister);
            cardsSelected[cardIndex] = true;
            registersFilled[registerIndex] = true;
            cardButton.setDisable(true);
            cardButton.setOpacity(0.5);
            cardButton.getStyleClass().remove("available-card");
            cardButton.getStyleClass().add("disabled-card");

            // TODO "CardSelected" message to the server
            Message selectedCardMessage = new Message();
            selectedCardMessage.setMessageType("SelectedCard");
            MessageBody body = new MessageBody();
            body.setCard(cardName);// needs to be fixed
            body.setRegister(registerIndex);
            selectedCardMessage.setMessageBody(body);
            System.out.println(gson.toJson(selectedCardMessage)); // Todo remove kenny
            client.sendToClientHandler(gson.toJson(selectedCardMessage));

            // If all registers are filled, notify the server and disable UI
            if (areAllRegistersFilled()) {
                resetButton.setDisable(true);
                for (Button register : getRegisterButtons()) {
                    register.setDisable(true);
                    register.setOpacity(0.75);
                }
                System.out.println("All registers are filled.");

                // TODO "SelectionFinished" message to the server
                Message selectionFinishedMessage = new Message();
                selectionFinishedMessage.setMessageType("SelectionFinished");
                body.setClientID(client.getClientID());
                selectionFinishedMessage.setMessageBody(body);
                client.sendToClientHandler(gson.toJson(selectionFinishedMessage));
            }
        } else {
            System.out.println("No empty register available!");
        }
    }

    @FXML
    public void handleRegisterClick(javafx.scene.input.MouseEvent mouseEvent) {
            Button register = (Button) mouseEvent.getSource();
            int registerIndex = getRegisterIndex(register);

            if (registersFilled[registerIndex]) {
                // Clear the register
                ImageView registerImageView = (ImageView) register.getGraphic();
                if (registerImageView != null) {
                    Image cardImage = registerImageView.getImage();

                    // Re-enable the corresponding card button
                    for (Button cardButton : getCardButtons()) {
                        ImageView cardImageView = (ImageView) cardButton.getGraphic();
                        if (cardImageView != null && cardImageView.getImage().equals(cardImage)) {
                            cardButton.setDisable(false);
                            cardButton.setOpacity(1.0);
                            cardButton.getStyleClass().remove("disabled-card");
                            cardButton.getStyleClass().add("available-card");

                            int cardIndex = Integer.parseInt(cardButton.getId().replace("card", "")) - 1;
                            cardsSelected[cardIndex] = false;
                            break;
                        }
                    }
                }

                // Clear the register UI
                register.setGraphic(null);
                register.getStyleClass().remove("selected-card");
                registersFilled[registerIndex] = false;

                // TODO set card to Null message
                // this was wrong b4
                Message cardRemovedMessage = new Message();
                cardRemovedMessage.setMessageType("SelectedCard");
                MessageBody body = new MessageBody();
                body.setRegister(registerIndex);
                body.setCard("Null");
                cardRemovedMessage.setMessageBody(body);
                System.out.println(gson.toJson(cardRemovedMessage));
                client.sendToClientHandler(gson.toJson(cardRemovedMessage));
            }
        }

    @FXML
    public void handleYourCards(List<String> cardsInHand) {
        ImageView[] cardImages = {card1Image, card2Image, card3Image, card4Image, card5Image, card6Image, card7Image, card8Image, card9Image};
        Button[] cardButtons = {card1, card2, card3, card4, card5, card6, card7, card8, card9};

        Platform.runLater(()->{
            for (int i = 0; i < cardsInHand.size(); i++) {
                String cardName = cardsInHand.get(i);
                String imagePath = "/client/images/" + cardName + ".png";
                URL imageURL = getClass().getResource(imagePath);

                if (imageURL != null) {
                    try {
                        Image cardImage = new Image(imageURL.toExternalForm());
                        cardImages[i].setImage(cardImage);
                        cardImages[i].setFitHeight(80);
                        cardImages[i].setFitWidth(70);
                        cardImages[i].setPreserveRatio(true);
                        cardButtons[i].setDisable(false);
                        cardButtons[i].setOpacity(1.0);
                        cardButtons[i].setGraphic(cardImages[i]);
                        //added this
                        cardButtons[i].setText(cardName);
                    } catch (Exception e) {
                        System.err.println("Error loading image for card: " + cardName);
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Image not found for card: " + cardName + " at " + imagePath);
                }
            }

            for (int i = cardsInHand.size(); i < cardImages.length; i++) {
                cardImages[i].setImage(null);
                cardButtons[i].setDisable(true);
                cardButtons[i].setOpacity(0.5);
                cardButtons[i].setText("");
                cardButtons[i].setGraphic(null);
            }

            System.out.println("Handling cards: " + cardsInHand);
        });


    }

    public void startTimer(int seconds) {
        timeRemaining = seconds;
        timerAnimation = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
            if (timeRemaining > 0) {
                timeRemaining--;
                timer.setText(String.format("00:%02d", timeRemaining));
            } else {
                timerAnimation.stop();
                handleTimerEnded();
            }
        }));

        timerAnimation.setCycleCount(seconds);
        timerAnimation.play();
    }

    private void handleTimerEnded() {
        // TODO 'TimerEnded' message ???
    }


    public void fillEmptyRegistersFromMessage(MessageBody messageBody) {
        Platform.runLater(() -> {
        System.out.println("filliing registers");
        // Extract cards from the message body
        List<String> cardsToPlace = messageBody.getCards();
        Button[] registers = getRegisterButtons();

        // Loop through cards and fill empty registers
        for (String cardName : cardsToPlace) {
            Button emptyRegister = getFirstEmptyRegister(); // Find the first empty register
            if (emptyRegister != null) {
                // Create an image for the card and place it in the register
                String imagePath = "/client/images/" + cardName + ".png";
                URL imageURL = getClass().getResource(imagePath);

                if (imageURL != null) {
                    try {
                        Image cardImage = new Image(imageURL.toExternalForm());
                        ImageView registerImageView = new ImageView(cardImage);
                        registerImageView.setFitWidth(80); // Adjust as needed
                        registerImageView.setFitHeight(100);
                        registerImageView.setPreserveRatio(true);

                        // Place the image in the register

                        emptyRegister.setGraphic(registerImageView);
                        emptyRegister.getStyleClass().add("selected-card");

                        // Mark the register as filled
                        int registerIndex = getRegisterIndex(emptyRegister);
                        registersFilled[registerIndex] = true;

                        System.out.println("Filled register " + registerIndex + " with card: " + cardName);

                        if (areAllRegistersFilled()) {
                            resetButton.setDisable(true);
                            for (Button register : getRegisterButtons()) {
                                register.setDisable(true);
                                register.setOpacity(0.75);
                            }
                            System.out.println("All registers are filled.");
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading image for card: " + cardName);
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Image not found for card: " + cardName + " at " + imagePath);
                }
            } else {
                System.out.println("No empty register available for card: " + cardName);
                break; // Stop if no more empty registers
            }
        }});
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Simulate receiving a card list from the server
//        List<String> sampleCards = List.of(
//                "MoveI", "TurnLeft", "UTurn", "BackUp", "PowerUp",
//                "Again", "TurnLeft", "TurnLeft", "TurnRight"
//        );
//
//        // Call the handleYourCards method with the simulated card list
//        handleYourCards(sampleCards);
    }

    public void handleactivephase2() {
        // Reset registers
        Button[] registers = getRegisterButtons();
        Platform.runLater(() -> {
            resetButton.setDisable(false);

            for (Button register : registers) {
                register.setGraphic(null);
                register.setDisable(false);
                register.setOpacity(1.0);
                register.getStyleClass().remove("selected-card");
            }

            // Reset card buttons
            for (Button cardButton : getCardButtons()) {
                cardButton.setDisable(false);
                cardButton.setOpacity(1.0);
                cardButton.getStyleClass().remove("disabled-card");
                cardButton.getStyleClass().add("available-card");
            }

            Arrays.fill(cardsSelected, false);
            Arrays.fill(registersFilled, false);
        });

    }

    
    // use for temporary cards or something??
    public void sendPlayCard (String card){
        Message playCard = new Message();
        playCard.setMessageType("PlayCard");
        MessageBody playCardBody = new MessageBody();
        playCardBody.setCard(card);
        playCard.setMessageBody(playCardBody);
        client.sendToClientHandler(gson.toJson(playCard));
    }

    public void handleReplaceCard(MessageBody messageFromHandlerBody) {
        Platform.runLater(() -> {
            // Get the register index and new card name from the message body
            int registerIndex = messageFromHandlerBody.getRegister();
            String newCardName = messageFromHandlerBody.getNewCard();

            // Find the corresponding register button
            Button[] registers = getRegisterButtons();
            if (registerIndex >= 0 && registerIndex < registers.length) {
                Button targetRegister = registers[registerIndex];

                // Load the new card image
                String imagePath = "/client/images/" + newCardName + ".png";
                URL imageURL = getClass().getResource(imagePath);

                if (imageURL != null) {
                    try {
                        Image newCardImage = new Image(imageURL.toExternalForm());
                        ImageView newCardImageView = new ImageView(newCardImage);
                        newCardImageView.setFitWidth(80); // Adjust as needed
                        newCardImageView.setFitHeight(100);
                        newCardImageView.setPreserveRatio(true);

                        // Update the register with the new card image
                        targetRegister.setGraphic(newCardImageView);
                        targetRegister.getStyleClass().add("selected-card");

                        // Ensure the register is marked as filled
                        registersFilled[registerIndex] = true;

                        System.out.println("Replaced register " + registerIndex + " with card: " + newCardName);

                    } catch (Exception e) {
                        System.err.println("Error loading image for card: " + newCardName);
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Image not found for card: " + newCardName + " at " + imagePath);
                }
            } else {
                System.err.println("Invalid register index: " + registerIndex);
            }
        });
    }


    /** --------------------------------------------------------------------------------------------------------------- **/
    // AI methods:

    public void AIFillRegister(String cardName){
        Platform.runLater(() -> {
            System.out.println("filliing register for AI");


            // Loop through cards and fill empty registers
                Button emptyRegister = getFirstEmptyRegister(); // Find the first empty register
                if (emptyRegister != null) {
                    // Create an image for the card and place it in the register
                    String imagePath = "/client/images/" + cardName + ".png";
                    URL imageURL = getClass().getResource(imagePath);

                    if (imageURL != null) {
                        try {
                            Image cardImage = new Image(imageURL.toExternalForm());
                            ImageView registerImageView = new ImageView(cardImage);
                            registerImageView.setFitWidth(80); // Adjust as needed
                            registerImageView.setFitHeight(100);
                            registerImageView.setPreserveRatio(true);

                            // Place the image in the register

                            emptyRegister.setGraphic(registerImageView);
                            emptyRegister.getStyleClass().add("selected-card");

                            // Mark the register as filled
                            int registerIndex = getRegisterIndex(emptyRegister);
                            registersFilled[registerIndex] = true;

                            System.out.println("Filled register " + registerIndex + " with card: " + cardName);

                            if (areAllRegistersFilled()) {
                                resetButton.setDisable(true);
                                for (Button register : getRegisterButtons()) {
                                    register.setDisable(true);
                                    register.setOpacity(0.75);
                                }
                                System.out.println("All registers are filled.");
                            }
                        } catch (Exception e) {
                            System.err.println("Error loading image for card: " + cardName);
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("Image not found for card: " + cardName + " at " + imagePath);
                    }
                } else {
                    System.out.println("No empty register available for card: " + cardName);
                }
            });
    }


}
