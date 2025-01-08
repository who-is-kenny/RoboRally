package client;


import client.controller.ClientController;
import client.controller.GameBoardController;
import client.controller.RegisterController;
import client.controller.RobotSelectionController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.paint.Color;
import server.message.Message;
import server.message.MessageBody;
import server.message.MessageSerializer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Client {

    private Socket socket;
    private final Random random = new Random();

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    private BufferedReader in;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    private PrintWriter out;
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class , new MessageSerializer()).create();

    private int totalClients = 0;
    private List<Integer> readyClientIDs = new ArrayList<>();

    public String getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(String selectedMap) {
        this.selectedMap = selectedMap;
    }

    public boolean isMapSelected() {
        return mapSelected;
    }

    public void setMapSelected(boolean mapSelected) {
        this.mapSelected = mapSelected;
    }

    private boolean mapSelected;
    private String selectedMap;
    private boolean isAI = false;

    private List<String> cardsInHand = new ArrayList<>();
    public List<String> getCardsInHand() {
        return cardsInHand;
    }
    public void setCardsInHand(List<String> cardsInHand) {
        this.cardsInHand = cardsInHand;
    }


    private int activePhase;
    public int getActivePhase() {
        return activePhase;
    }
    public void setActivePhase(int activePhase) {
        this.activePhase = activePhase;
    }

    private int currentPlayerID;
    public int getCurrentPlayerID() {
        return currentPlayerID;
    }
    public void setCurrentPlayerID(int currentPlayerID) {
        this.currentPlayerID = currentPlayerID;
    }

    //clientID getter and setter
    private int clientID = 0;
    public int getClientID() {
        return clientID;
    }
    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    // chat controller getter and setter
    private ClientController clientController;
    public ClientController getClientController() {
        return clientController;
    }
    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }

    // robot selection controller getter and setter
    private RobotSelectionController robotSelectionController;
    public RobotSelectionController getRobotSelectionController() {
        return robotSelectionController;
    }
    public void setRobotSelectionController(RobotSelectionController robotSelectionController) {
        this.robotSelectionController = robotSelectionController;
    }

    //game board controller getter and setter
    private GameBoardController gameBoardController;
    public GameBoardController getGameBoardController() {
        return gameBoardController;
    }
    public void setGameBoardController(GameBoardController gameBoardController) {
        this.gameBoardController = gameBoardController;
    }

    // register controller getter and setter
    private RegisterController registerController;
    public RegisterController getRegisterController() {
        return registerController;
    }
    public void setRegisterController(RegisterController registerController) {
        this.registerController = registerController;
    }

    public Client(Socket socket){

        try {
            this.socket = socket;
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // establish connection with server/client handler

//            while (clientID == 0){
//                String connectionMessage = in.readLine();
//                Message handlerMessage = gson.fromJson(connectionMessage, Message.class);
//                if (handlerMessage.getMessageType().equals("HelloClient")){
//                    // creating message and sending it back to the handler to establish connection.
//                    Message helloServer = new Message();
//                    helloServer.setMessageType("HelloServer");
//                    MessageBody helloServerBody = new MessageBody();
//                    helloServerBody.setProtocol("Version 0.1");
//                    helloServerBody.setAI(false);
//                    helloServerBody.setGroup("Neidische Narwahl");
//                    helloServer.setMessageBody(helloServerBody);
//                    out.println(gson.toJson(helloServer));
//                    // handle welcome message
//                } else if (handlerMessage.getMessageType().equals("Welcome")) {
//                    setClientID(handlerMessage.getMessageBody().getClientID());
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * send message to client handler
     * @param messageToClientHandler
     */
    public void sendToClientHandler(String messageToClientHandler){
        try {
            out.println(messageToClientHandler);
            System.out.println("sending message to clienthandler");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error sending message to client handler");}
    }

    /**
     * receives messages from client handler and adds the messages to the chat GUI
     */
    public void receiveFromClientHandler(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String inputFromHandler;
                while( socket.isConnected()){
                    try {
                        inputFromHandler = in.readLine(); // IO Error after we close everything the first time -> jump to catch -> then break to end thread
                        Message messageFromHandler = gson.fromJson(inputFromHandler , Message.class);
                        if ("GameStarted".equals(messageFromHandler.getMessageType())) {
                            continue; // Skip this message and go to the next iteration
                        }
                        MessageBody messageFromHandlerBody = messageFromHandler.getMessageBody();
                        switch (messageFromHandler.getMessageType()){
                            case "HelloClient":
                                break;
                            case "Welcome":
                                setClientID(messageFromHandlerBody.getClientID());
                                if (isAI){
                                    robotSelectionController.AIChooseRandomRobotAndReady();
                                }
                                break;
                            case "Alive":
                                sendAliveResponse();
                                break;
                            case "PlayerAdded":
                                robotSelectionController.handlePlayerAdded(messageFromHandlerBody , clientID);
                                String playerName = messageFromHandlerBody.getName();
                                int ClientID = messageFromHandlerBody.getClientID();
                                int RobotID =messageFromHandlerBody.getFigure();
                                gameBoardController.addClientIDRobotID(ClientID,RobotID);
                                clientController.addClientIDName(playerName,ClientID);
                                // counts the number of clients to be compared later with number of ready players
                                totalClients++;
                                break;
                            case "PlayerStatus": // TODO remove prints from this case when not needed
                                if (!isAI){
                                    if (messageFromHandlerBody.isReady()){
                                        if(!readyClientIDs.contains(messageFromHandlerBody.getClientID())){
                                            readyClientIDs.add(messageFromHandlerBody.getClientID());
                                            if(readyClientIDs.getFirst() == clientID){
                                                // allows the first client to select map
                                                robotSelectionController.setDisableMap(false);
                                            }
                                        }
//                                    if (mapSelected && readyClientIDs.size() == totalClients){
//                                        robotSelectionController.switchToChatScene();
//                                        clientController.updateClientList();
//                                    }
                                    }else{
                                        if(readyClientIDs.contains(messageFromHandlerBody.getClientID())){
                                            if (messageFromHandlerBody.getClientID() == clientID){
                                                robotSelectionController.setDisableMap(true);
                                            }
                                            readyClientIDs.remove((Integer) messageFromHandlerBody.getClientID());
                                            if(!readyClientIDs.isEmpty() && readyClientIDs.getFirst() == clientID){
                                                robotSelectionController.setDisableMap(false);
                                            }
                                        }
                                    }
                                }else{
                                    System.out.println("ai not handling player status");
                                    break;
                                }

                                break;
                            case "SelectMap":
                                robotSelectionController.handleSelectMap(messageFromHandlerBody);
                                break;
                            case "MapSelected":
                                mapSelected = true;
                                selectedMap = messageFromHandlerBody.getMap();
//                                if (mapSelected && readyClientIDs.size() == totalClients) {
//                                    robotSelectionController.switchToChatScene();
//                                    clientController.updateClientList();
//                                }
                                gameBoardController.changeBackgroundImage(selectedMap);

                                break;
                            case "ReceivedChat":
                                if(messageFromHandlerBody.getFrom() != clientID){
                                    clientController.addMessage(messageFromHandlerBody);
                                }
                                break;
                            case "YourCards":
                                cardsInHand = messageFromHandlerBody.getCardsInHandAsList();
                                registerController.handleYourCards(cardsInHand);
                                break;
                            case "TimerStarted":
                                registerController.setGameState(messageFromHandler.getMessageType(), messageFromHandlerBody, clientID, currentPlayerID);
                                if (registerController != null) {
                                    registerController.startTimer(30);
                                }
                                break;
                            case "ActivePhase":
                                activePhase = messageFromHandlerBody.getPhase();
                                System.out.println("active phase: " + activePhase);  //TODO remove print
                                registerController.setGamePhase(activePhase);
                                if (messageFromHandlerBody.getPhase() == 0){
                                    robotSelectionController.switchToChatScene();
                                    clientController.updateClientList();
                                }
                                if(messageFromHandlerBody.getPhase() == 2){
                                    gameBoardController.handleactivephase2();
                                    registerController.handleactivephase2();

                                    if (isAI){
                                        AIprocessCards();
                                    }
                                }
                                break;
                            case "CurrentPlayer":
                                currentPlayerID = messageFromHandlerBody.getClientID();
                                registerController.setGameState(messageFromHandler.getMessageType(), messageFromHandlerBody, clientID, currentPlayerID);
                                System.out.println("currentplayer : " + currentPlayerID);  //TODO remove print
                                if(isAI){
                                    if (currentPlayerID == clientID){
                                        gameBoardController.AISelectStartingPoint();
                                    }
                                }
                                break;
                            case "StartingPointTaken":
                                registerController.setGameState(messageFromHandler.getMessageType(), messageFromHandlerBody, clientID, currentPlayerID);

                                gameBoardController.handleStartingPointTaken(messageFromHandlerBody);
                                break;
                            case "Movement":
                                gameBoardController.handleRobotMovement(messageFromHandlerBody);
                                break;
                            case "PlayerTurning":
                                gameBoardController.handleRobotTurn(messageFromHandlerBody);
                                break;
                            case "PickDamage":
                                gameBoardController.sendDamageSelectionPopup(messageFromHandlerBody);
                                break;
                            case "Reboot":
                                gameBoardController.handleReboot(messageFromHandlerBody);
                                registerController.setGameState(messageFromHandler.getMessageType(), messageFromHandlerBody, clientID, currentPlayerID);
                                if(clientID == messageFromHandlerBody.getClientID()){
                                    gameBoardController.sendRebootPopup();
                                }
                                gameBoardController.applyGlowToRobot(messageFromHandlerBody.getClientID(), Color.GREENYELLOW,1.0);
                                break;
                            case "Energy":
                                gameBoardController.applyGlowToRobot(messageFromHandlerBody.getClientID(), Color.ORANGE,1.0);
                                break;
                            case "CheckPointReached":
                                registerController.setGameState(messageFromHandler.getMessageType(), messageFromHandlerBody, clientID, currentPlayerID);
                                gameBoardController.applyGlowToRobot(messageFromHandlerBody.getClientID(), Color.YELLOW,1.0);
                                break;
                            case "CardsYouGotNow":
                                registerController.fillEmptyRegistersFromMessage(messageFromHandlerBody);
                                break;
                            case "ConnectionUpdate":
                                clientController.handleConnectionUpdate(messageFromHandlerBody);
                                gameBoardController.handleConnectionUpdate(messageFromHandlerBody);
                                break;
                            case "ReplaceCard":
                                registerController.handleReplaceCard(messageFromHandlerBody);
                                break;
                            case "DrawDamage":
                                gameBoardController.applyGlowToRobot(messageFromHandlerBody.getClientID(), Color.RED,1.0);
                                break;
                            case "GameFinished":
                                gameBoardController.showGameWinnerPopup(messageFromHandlerBody);
                                break;
                            case "Animation":
                                if (messageFromHandlerBody.getType().equals("laser")){
                                    gameBoardController.makeAllRobotsShootLaser(1.0);
                                }
                                break;
                        }
                    } catch (IOException e) {
                        System.out.println("error when receiving clienthandler message");
                        closeClient();
                        System.out.println("ending client listener thread");
                        break;
                    }
                }
            }
        }).start();
    }

    private void sendAliveResponse() {
        Message aliveMessage = new Message();
        aliveMessage.setMessageType("Alive");
        aliveMessage.setMessageBody(new MessageBody());
        out.println(gson.toJson(aliveMessage));
        System.out.println("Sent 'Alive' response to handler.");    // TODO remove print
    }

    public void closeClient(){   //Socket socket, PrintWriter out, BufferedReader in
        System.out.println("closing client");
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** --------------------------------------------------------------------------------------------------------------- **/
    // AI methods:




//    private void AIHandleYourCards(MessageBody messageBody) {
//        List<String> cardsInHand = messageBody.getCardsInHandAsList();
//        System.out.println("cards in hand" + cardsInHand);
//        AICards = AISelectRandomCards(cardsInHand);
//        System.out.println("AI cards:" + AICards);
//    }

    private void AIprocessCards(){

        List <String> AICards;
        AICards = AISelectRandomCards(cardsInHand);
        System.out.println("AI cards:" + AICards);
        for (int i = 0; i< AICards.size(); i++){
            //fill the card in the register
            registerController.AIFillRegister(AICards.get(i));
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

    public boolean isAI() {
        return isAI;
    }

    public void setAI(boolean AI) {
        isAI = AI;
    }
}
