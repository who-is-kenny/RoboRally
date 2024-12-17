package client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.message.Message;
import server.message.MessageBody;
import server.message.MessageSerializer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class AIClient extends Client{
    private final Random random;
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class , new MessageSerializer()).create();
//    private Socket socket;
//    private BufferedReader in;
//    private PrintWriter out;

    public AIClient(Socket socket) {
        super(socket);
        random = new Random();
//        try {
//            this.socket = socket;
//            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void receiveFromClientHandler() {
        new Thread(() -> {
            String inputFromHandler;
            while (getSocket().isConnected()) {
                try {
//                    Thread.sleep(1000);
                    inputFromHandler = getIn().readLine();
                    Message messageFromHandler = gson.fromJson(inputFromHandler, Message.class);

                    MessageBody messageFromHandlerBody = messageFromHandler.getMessageBody();
                    switch (messageFromHandler.getMessageType()) {
                        case "Welcome":
                            System.out.println("processing ai welcome message");
                            setClientID(messageFromHandlerBody.getClientID());
                            getRobotSelectionController().AIChooseRandomRobotAndReady();
                            break;
                        case "PlayerStatus":
                            System.out.println("AI doesnt update when player status called");
                            break;
                        case "ActivePhase":
                            setActivePhase(messageFromHandlerBody.getPhase());
                            System.out.println("ai active phase: " + getActivePhase());  //TODO remove print
                            if (messageFromHandlerBody.getPhase() == 0){
                                getRobotSelectionController().switchToChatScene();
                                getClientController().updateClientList();
                            }
                            if(messageFromHandlerBody.getPhase() == 2){
                                getGameBoardController().handleactivephase2();
                                getRegisterController().handleactivephase2();
                                AIprocessCards();
                            }
                            break;
                        case "CurrentPlayer":
                            setCurrentPlayerID(messageFromHandlerBody.getClientID());
                            System.out.println("currentplayer : " + getCurrentPlayerID());  //TODO remove print
                            System.out.println("asdfasdfasdf");
                            if (this.getClientID() == getCurrentPlayerID()){
                                getGameBoardController().AISelectStartingPoint();
                            }
                            break;
                        case "MapSelected":
                            setMapSelected(true);
                            setSelectedMap(messageFromHandlerBody.getMap());
//                                if (mapSelected && readyClientIDs.size() == totalClients) {
//                                    robotSelectionController.switchToChatScene();
//                                    clientController.updateClientList();
//                                }
                            getGameBoardController().changeBackgroundImage(getSelectedMap());
                            break;
                        case "StartingPointTaken":
                            getGameBoardController().handleStartingPointTaken(messageFromHandlerBody);
                            break;
                        default:
                            super.receiveFromClientHandler(); // Handle other messages in the usual way
                            break;
                    }
                } catch (Exception e) {
                    System.out.println("Error in AI client handler communication");
                    closeClient();
                    break;
                }
            }
        }).start();
    }



    private void AIprocessCards(){

        List <String> AICards;
        AICards = AISelectRandomCards(getCardsInHand());
        System.out.println("AI cards:" + AICards);
        for (int i = 0; i< AICards.size(); i++){
            //fill the card in the register
            getRegisterController().AIFillRegister(AICards.get(i));
            // send message to server
            Message cardSelectionMessage = new Message();
            cardSelectionMessage.setMessageType("SelectedCard");
            MessageBody cardSelectionBody = new MessageBody();
            cardSelectionBody.setCard(AICards.get(i));
            cardSelectionBody.setRegister(i);
            cardSelectionMessage.setMessageBody(cardSelectionBody);
            sendToClientHandler(gson.toJson(cardSelectionMessage));

        }
    }

    private List<String> AISelectRandomCards(List<String> cardsInHand) {
        List<String> selectedCards = new ArrayList<>();
        int cardsToSelect = Math.min(5, cardsInHand.size()); // Limit to the number of available cards

        for (int i = 0; i < cardsToSelect; i++) {
            int randomIndex = random.nextInt(cardsInHand.size());
            selectedCards.add(cardsInHand.get(randomIndex));
            cardsInHand.remove(randomIndex);
        }

        return selectedCards;
    }

    private void handleYourCards(MessageBody messageBody) {
        List<String> cardsInHand = messageBody.getCardsInHandAsList();
        List<String> selectedCards = selectRandomCards(cardsInHand);

        for (int i = 0; i< selectedCards.size(); i++){
            Message cardSelectionMessage = new Message();
            cardSelectionMessage.setMessageType("SelectedCards");
            MessageBody cardSelectionBody = new MessageBody();
            cardSelectionBody.setCard(selectedCards.get(i));
            cardSelectionBody.setRegister(i);
            cardSelectionMessage.setMessageBody(cardSelectionBody);

            sendToClientHandler(gson.toJson(cardSelectionMessage));
        }
    }

    private List<String> selectRandomCards(List<String> cardsInHand) {
        List<String> selectedCards = new ArrayList<>();
        int cardsToSelect = 5;

        for (int i = 0; i < cardsToSelect; i++) {
            int randomIndex = random.nextInt(cardsInHand.size());
            selectedCards.add(cardsInHand.get(randomIndex));
            cardsInHand.remove(randomIndex);
        }

        return selectedCards;
    }

    private void handleTimerStarted() {
        System.out.println("AI timer started, waiting for automatic card submission.");
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345); // Replace with your server's IP and port
            AIClient aiClient = new AIClient(socket);
            aiClient.receiveFromClientHandler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



