package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.message.ActiveCard;
import server.message.Message;
import server.message.MessageBody;
import server.message.MessageSerializer;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientHandler implements Runnable{

    //connection
    private volatile boolean running = true;
    private static final long PING_INTERVAL = 5000; // 5 seconds
    private static final long PING_TIMEOUT = 5000; // 5 seconds to wait for response

    // To store the time of the last "Alive" response from each client
    private Map<Integer, Long> lastAliveResponseTime = new HashMap<>();



    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private static Map<Integer, ClientHandler> clients;
    private static final Map<Integer, Integer> selectedRobots = new HashMap<>(); // key: robotID, value: clientID

    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class , new MessageSerializer()).create();

    // attributes for game.

    private String name;
    private final int clientID;
    private int robotID = -1;
    private boolean isReady;
    static List<String> availableMaps = List.of("DizzyHighway" , "LostBearings" , "ExtraCrispy" , "DeathTrap");
    private String selectedMap;
    private int startingPointX;
    private int startingPointY;

    ArrayList<String> cardsInHand = new ArrayList<>(Collections.nCopies(5, "Null"));


    static List<String> sampleCards = new ArrayList<>(List.of("MoveI", "TurnLeft", "UTurn", "BackUp", "PowerUp",
            "Again", "TurnLeft", "TurnLeft", "TurnRight"));

    public ClientHandler(Socket socket , int clientID , Map<Integer, ClientHandler> clients ){

        this.socket = socket;
        this.clientID = clientID;
        this.clients = clients;
        clientHandlers.add(this);

    }

    // thread waits for message from client
    @Override
    public void run() {



        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //establish connection with client
            Message helloClient = new Message();
            helloClient.setMessageType("HelloClient");
            MessageBody helloClientBody = new MessageBody();
            helloClientBody.setProtocol("Version 0.1");
            helloClient.setMessageBody(helloClientBody);
            out.println(gson.toJson(helloClient));
            System.out.println(gson.toJson(helloClient));

            // wait for client response to establish connection
            boolean connectionEstablished = false;
            while (!connectionEstablished){
                String clientInput = in.readLine();
                Message clientMessage = gson.fromJson(clientInput, Message.class);
                if(clientMessage.getMessageType().equals("HelloServer") && clientMessage.getMessageBody().getProtocol().equals("Version 0.1")){
                    connectionEstablished = true;
                }else{
                    sendErrorMessage();
                }
            }
            System.out.println("connection established sending welcome message");

            // send welcome message
            Message welcomeMessage = new Message();
            welcomeMessage.setMessageType("Welcome");
            MessageBody welcomeMessageBody = new MessageBody();
            welcomeMessageBody.setClientID(clientID);
            welcomeMessage.setMessageBody(welcomeMessageBody);
            out.println(gson.toJson(welcomeMessage));

            // send information about selected robots
//            sendCurrentSelections();
            // loops through clienthandlers and sends information about name and selected robot
            for (ClientHandler ch : clientHandlers){
                if (ch.robotID != -1){
                    out.println(ch.createPlayerAddedMessage());
                }
            }
            // loops through clienthandlers and sends isready status of clients that are ready
            for (ClientHandler ch : clientHandlers){
                if(ch.isReady){
                    out.println(ch.createPlayerStatusMessage(true));
                }
            }
            // TODO send player statuses
            // send information about player status


            System.out.println("waiting for client messages");

            startAlivePingThread();

            // receiving messages
            while(socket.isConnected()) {
                String clientInput = in.readLine();
                Message clientMessage = gson.fromJson(clientInput , Message.class);
                MessageBody clientMessageBody = clientMessage.getMessageBody();
                switch (clientMessage.getMessageType()){
                    case "Alive":
                        lastAliveResponseTime.replace(clientID, System.currentTimeMillis());
                        System.out.println("Received 'Alive' response from client " + clientID);
                        break;
                    case "PlayerValues":
                        handlePlayerValue(clientMessageBody);
                        broadcastMessage(createPlayerAddedMessage());
                        break;
                    case "SetStatus":
                        handleSetStatus(clientMessageBody);
                        break;
                    case "MapSelected":
                        for (ClientHandler clientHandler : clientHandlers){
                            clientHandler.selectedMap = clientMessageBody.getMap();
                        }
                        System.out.println(selectedMap);
                        // sends client message to other clients
                        broadcastMessage(clientInput);
//                        broadcastMessage(DizzyHighway);
                        //broadcastMessage(readJsonFile(selectedMap)); // TODO enable when fixed function
                        // initiates first phase
                        broadcastMessage(createActivePhaseMessage(0));
                        // sends currentplayer message to start selection
                        broadcastMessage(createCurrentPlayerMessage());
                        break;
                    case "SendChat":
                        handleSendChat(clientMessageBody);
                        break;
                    case "SetStartingPoint":
                        handleSetStartingPoint(clientMessageBody);
                        break;
                    case "RebootDirection":
                        String direction = clientMessageBody.getDirection();
                        switch (direction){
                            case "Right":
                                sendRotationMessage(clientID, "clockwise");
                                break;
                            case "Left":
                                sendRotationMessage(clientID, "counterclockwise");
                                break;
                            case "Up":
                                break;
                            case "Down":
                                sendRotationMessage(clientID, "clockwise");
                                sendRotationMessage(clientID, "clockwise");
                                break;
                            default:
                                System.out.println("direction invalid for reboot");
                        }
                        // TODO send robot move message so that robot moves to reboot square
                        // TODO send rotate message based on direction of choice sent by user in this switch case. ( access through client message body)
                        break;
                    case "SelectedCard":
                        System.out.println("Handling SelectedCard..."); // todo remove
                        handleSelectedCard(clientMessageBody);
                        break;
                    case "SelectionFinished":
                        //handleSelectionFinished();
                        broadcastMessage(createTimerStartMessage());
                        startServerTimer();
                        break;
                    case "PlayCard":
                        sendCardPlayed(clientMessageBody);
                        break;


                }
            }
        }catch (IOException e){
            closeEverything();
            e.printStackTrace();
        }



    }



    private void handleSelectedCard(MessageBody clientMessageBody) {
        int register = clientMessageBody.getRegister();
        String card = clientMessageBody.getCard();

        if (register < 0 || register >= cardsInHand.size()) {
            System.out.println("Invalid register index.");
        }else{
            cardsInHand.set(register,card);
            sendCardSelected(register, !card.equals("Null"));
            System.out.println(cardsInHand);
        }
    }

    private void handleSetStartingPoint(MessageBody clientMessageBody) {


        startingPointX = clientMessageBody.getX();
        startingPointY = clientMessageBody.getY();
        // create  starting point taken message
        Message startingPointTakenMessage = new Message();
        startingPointTakenMessage.setMessageType("StartingPointTaken");
        MessageBody startingPointTakenMessageBody = new MessageBody();
        startingPointTakenMessageBody.setX(clientMessageBody.getX());
        startingPointTakenMessageBody.setY(clientMessageBody.getY());
        startingPointTakenMessageBody.setDirection("right");
        startingPointTakenMessageBody.setClientID(clientID);
        startingPointTakenMessage.setMessageBody(startingPointTakenMessageBody);
        broadcastMessage(gson.toJson(startingPointTakenMessage));

        // update current player to the next player
        if (counter < clientHandlers.size()){
            broadcastMessage(createCurrentPlayerMessage());
            sendYourCards(sampleCards);
        }else{
            // swtich to next phase
            // TODO GAME LOGIC create instance of game in server class
            // TODO GAME LOGIC maybe game loop starts here
            // TODO GAME LOGIC loop through clienthandlers, create a player instance for each client handler and add to game.
            switchToProgrammingPhase();
            sendYourCards(sampleCards);

        }


    }

    //chat
    private void handleSendChat(MessageBody clientMessageBody) {
        String message = clientMessageBody.getMessage();
        int to = clientMessageBody.getTo();
        int from = clientMessageBody.getFrom();
        if(to == -1){
            //create public message from server
            Message pm = new Message();
            pm.setMessageType("ReceivedChat");
            MessageBody pmb = new MessageBody();
            pmb.setMessage(message);
            pmb.setFrom(from);
            pmb.setTo(to);
            pmb.setPrivate(false);
            pm.setMessageBody(pmb);
            broadcastMessage(gson.toJson(pm));
            sendMovementMessage(1,6,6);// TODO remove this test later
//            broadcastMessage(createRotationMessage(2 , "clockwise")); // TODO remove this test later
//            broadcastMessage(createRebootMessage(2));
            sendCheckPointMessage(2,1);


        }else{
            //create private message from server
            Message dm = new Message();
            dm.setMessageType("ReceivedChat");
            MessageBody dmb = new MessageBody();
            dmb.setMessage(message);
            dmb.setFrom(from);
            dmb.setTo(to);
            dmb.setPrivate(true);
            dm.setMessageBody(dmb);
            sendMessageToClient(to,gson.toJson(dm));
        }
    }

    // methods for player ready status

    private void handleSetStatus(MessageBody messageBody) {
        this.isReady = messageBody.isReady();
        broadcastMessage(createPlayerStatusMessage(isReady));
        out.println(createSelectMapMessage());
    }

    private String createPlayerStatusMessage(boolean isReady){
        Message playerStatusMessage = new Message();
        playerStatusMessage.setMessageType("PlayerStatus");
        MessageBody playerStatusMessageBody = new MessageBody();
        playerStatusMessageBody.setReady(isReady);
        playerStatusMessageBody.setClientID(clientID);
        playerStatusMessage.setMessageBody(playerStatusMessageBody);
        return gson.toJson(playerStatusMessage);
    }

    // methods for map selection

    private String createSelectMapMessage(){
        Message selectMapMessage = new Message();
        selectMapMessage.setMessageType("SelectMap");
        MessageBody selectMapMessageBody = new MessageBody();
        selectMapMessageBody.setAvailableMaps(availableMaps);
        selectMapMessage.setMessageBody(selectMapMessageBody);
        return gson.toJson(selectMapMessage);
    }


    //todo fix this
    private String readJsonFile(String fileName) {
        URL fileUrl = getClass().getClassLoader().getResource("client/json/" + fileName + ".json");
        if (fileUrl == null) {
            System.out.println("File not found at: client/json/" + fileName + ".json");
            return null;
        }

        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("client/json/" + fileName + ".json");
        if (inputStream == null) {
            System.out.println("File not found: " + fileName);
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
            return fileContent.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    // methods for robot selection and player name

    private void handlePlayerValue(MessageBody messageBody){
        this.name = messageBody.getName();
        this.robotID = messageBody.getFigure();
        selectedRobots.put(robotID , clientID);
        System.out.println(name);
        System.out.println(robotID);
    }

    private String createPlayerAddedMessage (){
        Message playerAddedMessage = new Message();
        playerAddedMessage.setMessageType("PlayerAdded");
        MessageBody playerAddedMessageBody = new MessageBody();
        playerAddedMessageBody.setClientID(clientID);
        playerAddedMessageBody.setName(name);
        playerAddedMessageBody.setFigure(robotID);
        playerAddedMessage.setMessageBody(playerAddedMessageBody);
        return gson.toJson(playerAddedMessage);
    }

    //(6)
    public void sendCardPlayed (MessageBody messageBody){
        String card = messageBody.getCard();
        Message cardPlayed = new Message();
        cardPlayed.setMessageType("CardPlayed");
        MessageBody cardPlayedBody = new MessageBody();
        cardPlayedBody.setClientID(clientID);
        cardPlayedBody.setCard(card);
        cardPlayed.setMessageBody(cardPlayedBody);
        broadcastMessage(gson.toJson(cardPlayed));
    }

    //(7)
    // current player message
    private static int counter = 0;
    private String createCurrentPlayerMessage(){
        int currentID = clientHandlers.get(counter).clientID;
        counter ++ ;

        Message currentPlayerMessage = new Message();
        currentPlayerMessage.setMessageType("CurrentPlayer");
        MessageBody currentPlayerMessageBody = new MessageBody();
        currentPlayerMessageBody.setClientID(currentID);
        currentPlayerMessage.setMessageBody(currentPlayerMessageBody);
        return gson.toJson(currentPlayerMessage);
    }


    private String createActivePhaseMessage (int phase){
        Message ActivePhaseMessage = new Message();
        ActivePhaseMessage.setMessageType("ActivePhase");
        MessageBody ActivePhaseMessageBody = new MessageBody();
        ActivePhaseMessageBody.setPhase(phase);
        ActivePhaseMessage.setMessageBody(ActivePhaseMessageBody);
        return gson.toJson(ActivePhaseMessage);

    }

    public void switchToProgrammingPhase(){
        broadcastMessage(createActivePhaseMessage(2));
    }

    public void switchToAktivierungPhase(){
        System.out.println("switching to aktivierungsphase");
        broadcastMessage(createActivePhaseMessage(3));
    }

    public void resetCardsInHand() {
        for (int i = 0; i < cardsInHand.size(); i++) {
            cardsInHand.set(i, "Null");
        }
    }

    public void sendYourCards (List<String> cards){
        System.out.println("sending cards to players");
        Message yourCards = new Message();
        yourCards.setMessageType("YourCards");
        MessageBody yourCardsBody = new MessageBody();
        yourCardsBody.setCardsInHand(cards);
        yourCards.setMessageBody(yourCardsBody);
        out.println(gson.toJson(yourCards));

    }

    public void sendNotYourCards (int clientID){
        Message notYourCards = new Message();
        notYourCards.setMessageType("NotYourCards");
        MessageBody notYourCardsBody = new MessageBody();
        notYourCardsBody.setClientID(clientID);
        notYourCardsBody.setCardsInHand(9);
        notYourCards.setMessageBody(notYourCardsBody);
        broadcastMessage(gson.toJson(notYourCards));
    }

    public void sendShuffleCoding (){
        Message shuffleCoding = new Message();
        shuffleCoding.setMessageType("ShuffleCoding");
        MessageBody shuffleCodingBody = new MessageBody();
        shuffleCodingBody.setClientID(clientID);
        shuffleCoding.setMessageBody(shuffleCodingBody);
        out.println(gson.toJson(shuffleCoding));
    }

    public void sendCardSelected(int register , boolean filled){
        Message cardSelected = new Message();
        cardSelected.setMessageType("CardSelected");
        MessageBody cardSelectedBody = new MessageBody();
        cardSelectedBody.setClientID(clientID);
        cardSelectedBody.setRegister(register);
        cardSelectedBody.setFilled(filled);
        cardSelected.setMessageBody(cardSelectedBody);
        broadcastMessage(gson.toJson(cardSelected));
        System.out.println("sending card selected message to other clients");
    }

    private String createTimerStartMessage (){
        Message timerStart = new Message();
        timerStart.setMessageType("TimerStarted");
        timerStart.setMessageBody(new MessageBody());
        return gson.toJson(timerStart);
    }

    public void sendTimerEnded (List<Integer> clientsWithNullCards){
        Message timerEnded = new Message();
        timerEnded.setMessageType("TimerEnded");
        MessageBody timerEndedMB = new MessageBody();
        timerEndedMB.setClientIDs(clientsWithNullCards);
        timerEnded.setMessageBody(timerEndedMB);
        out.println(gson.toJson(timerEnded));
    }

    public void sendCardsYouGotNow (List<String> cards){
        Message cardsYouGotNow = new Message();
        cardsYouGotNow.setMessageType("CardsYouGotNow");
        MessageBody cardsYouGotNowBody = new MessageBody();
        cardsYouGotNowBody.setCards(cards);
        cardsYouGotNow.setMessageBody(cardsYouGotNowBody);
        out.println(gson.toJson(cardsYouGotNow));
    }

    public void sendCurrentCards (List<ActiveCard> activeCards){
        Message currentCards = new Message();
        currentCards.setMessageType("CurrentCards");
        MessageBody currentCardsBody = new MessageBody();
        currentCardsBody.setActiveCards(activeCards);
        currentCards.setMessageBody(currentCardsBody);
        broadcastMessage(gson.toJson(currentCards));

    }

    public void sendReplaceCard(int register , String newCard , int clientID){
        Message replaceCard = new Message();
        replaceCard.setMessageType("ReplaceCard");
        MessageBody replaceCardBody = new MessageBody();
        replaceCardBody.setRegister(register);
        replaceCardBody.setNewCard(newCard);
        replaceCardBody.setClientID(clientID);
        replaceCard.setMessageBody(replaceCardBody);
        broadcastMessage(gson.toJson(replaceCard));
    }

    // (8) //TODO use these methods in game logic
    //movement
    public void sendMovementMessage(int ClientID , int x, int y){
        Message MovementMessage = new Message();
        MovementMessage.setMessageType("Movement");
        MessageBody MovementMessageBody = new MessageBody();
        MovementMessageBody.setClientID(ClientID);
        MovementMessageBody.setX(x);
        MovementMessageBody.setY(y);
        MovementMessage.setMessageBody(MovementMessageBody);
        broadcastMessage(gson.toJson(MovementMessage));
    }

    //rotation
    public void sendRotationMessage(int ClientID , String rotation){
        Message RotationMessage = new Message();
        RotationMessage.setMessageType("PlayerTurning");
        MessageBody RotationMessageBody = new MessageBody();
        RotationMessageBody.setClientID(ClientID);
        RotationMessageBody.setRotation(rotation);
        RotationMessage.setMessageBody(RotationMessageBody);
        broadcastMessage(gson.toJson(RotationMessage));
    }
    // animation
    public void sendAnimationMessage (String animationType){
        Message animationMessage = new Message();
        animationMessage.setMessageType("Animation");
        MessageBody animationMessageBody = new MessageBody();
        animationMessageBody.setType(animationType);
        animationMessage.setMessageBody(animationMessageBody);
        broadcastMessage(gson.toJson(animationMessage));
    }
    // reboot
    public void sendRebootMessage (int ClientID){
        Message rebootMessage = new Message();
        rebootMessage.setMessageType("Reboot");
        MessageBody rebootMessageBody = new MessageBody();
        rebootMessageBody.setClientID(ClientID);
        rebootMessage.setMessageBody(rebootMessageBody);
        broadcastMessage(gson.toJson(rebootMessage));
    }
    // energy
    public void sendEnergyMessage (int ClientID, int count, String source){
        Message energyMessage = new Message();
        energyMessage.setMessageType("Energy");
        MessageBody energyMessageBody = new MessageBody();
        energyMessageBody.setClientID(ClientID);
        energyMessageBody.setCount(count);
        energyMessageBody.setSource(source);
        energyMessage.setMessageBody(energyMessageBody);
        broadcastMessage(gson.toJson(energyMessage));
    }

    // checkpoint reached
    public void sendCheckPointMessage(int ClientID, int number){
        Message checkPointMessage = new Message();
        checkPointMessage.setMessageType("CheckPointReached");
        MessageBody checkPointMessageBody = new MessageBody();
        checkPointMessageBody.setClientID(ClientID);
        checkPointMessageBody.setNumber(number);
        checkPointMessage.setMessageBody(checkPointMessageBody);
        broadcastMessage(gson.toJson(checkPointMessage));
    }

    // game over
    public void sendGameOverMessage (int ClientID){
        Message gameOverMessage = new Message();
        gameOverMessage.setMessageType("GameFinished");
        MessageBody gameOverMessageBody = new MessageBody();
        gameOverMessageBody.setClientID(ClientID);
        gameOverMessage.setMessageBody(gameOverMessageBody);
        broadcastMessage(gson.toJson(gameOverMessage));

    }



    // broadcasting messages
    public static void broadcastMessage(String message){
        for (ClientHandler ch : clientHandlers){
            ch.out.println(message);
        }
    }

    //send message to a specific target
    public static void sendMessageToClient(int targetClientId, String message) {
        for(ClientHandler ch : clientHandlers){
            if (ch.clientID == targetClientId){
                ch.out.println(message);
            }
        }
    }


    //error message
    public void sendErrorMessage(){
        Message errorMessage = new Message();
        errorMessage.setMessageType("Error");
        MessageBody errorMessageBody = new MessageBody();
        errorMessageBody.setError("Whoops. That did not work. Try to adjust something.");
        errorMessage.setMessageBody(errorMessageBody);
        out.println(gson.toJson(errorMessage));
    }

    private void startAlivePingThread() {
        // This thread will send an "Alive" message every 5 seconds
        new Thread(() -> {
            while (running && socket.isConnected()) {
                try {
                    sendAlivePing();
                    checkForClientTimeout();
                    Thread.sleep(PING_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    closeEverything();
                }
            }
            System.out.println("Ping thread stopped for client " + clientID);
        }).start();
    }

    private void sendAlivePing() {
        // Send an "Alive" message to the client
        Message aliveMessage = new Message();
        aliveMessage.setMessageType("Alive");
        aliveMessage.setMessageBody(new MessageBody());
        out.println(gson.toJson(aliveMessage));
        System.out.println("Sent 'Alive' message to client " + clientID);
    }

    private void checkForClientTimeout() {
        // Check if the client has failed to respond to the last "Alive" message within the timeout period
        if (lastAliveResponseTime.containsKey(clientID)) {
            long lastResponseTime = lastAliveResponseTime.get(clientID);
            if (System.currentTimeMillis() - lastResponseTime > PING_TIMEOUT) {
                System.out.println("Client " + clientID + " timed out. Disconnecting.");
                closeEverything();
            }
        }
    }

    private void startServerTimer(){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            // Action to perform after 30 seconds
            List<Integer> clientsWithNullCards = new ArrayList<>();
            for (ClientHandler clientHandler : clientHandlers){
                if (clientHandler.cardsInHand.contains("Null")){
                    clientsWithNullCards.add(clientID);
                }
            }
            sendTimerEnded(clientsWithNullCards);

            String[] moves = {"MoveI", "MoveII"};

            for (ClientHandler clientHandler : clientHandlers){
                if (clientHandler.cardsInHand.contains("Null")){
                    Message cardyougot = new Message();
                    cardyougot.setMessageType("CardsYouGotNow");
                    MessageBody aasefase = new MessageBody();
                    aasefase.setCards(List.of(moves));
                    cardyougot.setMessageBody(aasefase);
                    clientHandler.out.println(gson.toJson(cardyougot));
                    System.out.println(gson.toJson(cardyougot));
                }
            }
        }, 30, TimeUnit.SECONDS);
        scheduler.schedule(() -> {
            switchToAktivierungPhase();
            switchToProgrammingPhase();
            resetCardsInHand();
            Collections.shuffle(sampleCards);
            sendYourCards(sampleCards);
        }, 35, TimeUnit.SECONDS);
    }

    public void closeEverything(){
        running = false;
        out.println("closing client handler");
        clientHandlers.remove(this);
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Gson gson1 = new Gson();
//
//        MessageBody body = new MessageBody();
//        body.setProtocol("Version 0.1");
//
//        Message message = new Message();
//        message.setMessageType("HelloClient");
//        message.setMessageBody(body);
//        System.out.println(gson.toJson(message));

//        Collections.shuffle(sampleCards);
//        System.out.println(sampleCards);
//        Collections.shuffle(sampleCards);
//        System.out.println(sampleCards);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Message.class, new MessageSerializer())
                .create();

        Message helloClient = new Message();
        helloClient.setMessageType("HelloClient");
        MessageBody helloClientBody = new MessageBody();
        helloClientBody.setProtocol("Version 0.1");
        helloClient.setMessageBody(helloClientBody);
        System.out.println(gson.toJson(helloClient));

        System.out.println(gson1.toJson(helloClient));



    }
    String DizzyHighway = """
            {"messageType": "GameStarted", "messageBody": {"gameMap": [
              [
                [{"type":"Empty","isOnBoard":"Start A"}],
                [{"type":"Empty","isOnBoard":"Start A"}],
                [{"type":"Empty","isOnBoard":"Start A"}],
                [{"type":"StartPoint","isOnBoard":"Start A"}],
                [{"orientations":["right"],"type":"Antenna","isOnBoard":"Start A"}],
                [{"type":"Empty","isOnBoard":"Start A"}],
                [{"type":"StartPoint","isOnBoard":"Start A"}],
                [{"type":"Empty","isOnBoard":"Start A"}],
                [{"type":"Empty","isOnBoard":"Start A"}],
                [{"type":"Empty","isOnBoard":"Start A"}]
              ],
              [
                [{"type":"Empty","isOnBoard":"Start A"}],
                [{"type":"StartPoint","isOnBoard":"Start A"}],
                [{"orientations":["top"],"type":"Wall","isOnBoard":"Start A"}],
                [{"type":"Empty","isOnBoard":"Start A"}],[{"type":"StartPoint","isOnBoard":"Start A"}],
                [{"type":"StartPoint","isOnBoard":"Start A"}],
                [{"type":"Empty","isOnBoard":"Start A"}],
                [{"orientations":["bottom"],"type":"Wall","isOnBoard":"Start A"}],
                [{"type":"StartPoint","isOnBoard":"Start A"}],
                [{"type":"Empty","isOnBoard":"Start A"}]
              ],
              [
                [{"speed":1,"orientations":["right","left"],"type":"ConveyorBelt","isOnBoard":"Start A"}],
                [{"type":"Empty","isOnBoard":"Start A"}],
                [{"type":"Empty","isOnBoard":"Start A"}],
                [{"type":"Empty","isOnBoard":"Start A"}],
                [{"orientations":["right"],"type":"Wall","isOnBoard":"Start A"}],
                [{"orientations":["right"],"type":"Wall","isOnBoard":"Start A"}],
                [{"type":"Empty","isOnBoard":"Start A"}],
                [{"type":"Empty","isOnBoard":"Start A"}],
                [{"type":"Empty","isOnBoard":"Start A"}],
                [{"speed":1,"orientations":["right","left"],"type":"ConveyorBelt","isOnBoard":"Start A"}]
              ],
              [
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["right","left"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["right","left"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"count":1,"type":"EnergySpace","isOnBoard":"5B"}]
              ],
              [
                [{"speed":2,"orientations":["bottom","top"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["bottom","top","right"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["bottom","top"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["bottom","top"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["bottom","top"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["bottom","top"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["bottom","top"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["bottom","left","top"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["right","left","top"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}]
              ],
              [
                [{"speed":2,"orientations":["bottom","top"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["left","top","right"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"count":1,"type":"EnergySpace","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["right","left"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}]
              ],
              [
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["left","right"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"orientations":["top"],"type":"Wall","isOnBoard":"5B"}],
                [{"count":1,"orientations":["top"],"type":"Laser","isOnBoard":"5B"},{"orientations":["bottom"],"type":"Wall","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"orientations":["left"],"type":"Wall","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["right","left"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}]
              ],
              [
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["left","right"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"},{"orientations":["bottom"],"type":"RestartPoint","isOnBoard":"DizzyHighway"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"count":1,"type":"EnergySpace","isOnBoard":"5B"}],
                [{"count":1,"orientations":["left"],"type":"Laser","isOnBoard":"5B"},{"orientations":["right"],"type":"Wall","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["right","left"],"type":"ConveyorBelt","isOnBoard":"5B"}],[{"type":"Empty","isOnBoard":"5B"}]
              ],
              [
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["left","right"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"count":1,"orientations":["right"],"type":"Laser","isOnBoard":"5B"},{"orientations":["left"],"type":"Wall","isOnBoard":"5B"}],
                [{"count":1,"type":"EnergySpace","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["right","left"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}]
              ],
              [
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["left","right"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"orientations":["right"],"type":"Wall","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"count":1,"orientations":["bottom"],"type":"Laser","isOnBoard":"5B"},{"orientations":["top"],"type":"Wall","isOnBoard":"5B"}],
                [{"orientations":["bottom"],"type":"Wall","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["right","left"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}]
              ],
              [
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["left","right"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"count":1,"type":"EnergySpace","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["right","bottom","left"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["top","bottom"],"type":"ConveyorBelt","isOnBoard":"5B"}]
              ],
              [
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["left","right","bottom"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["top","right","bottom"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["top","bottom"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["top","bottom"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["top","bottom"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["top","bottom"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["top","bottom"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["top","bottom","left"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["top","bottom"],"type":"ConveyorBelt","isOnBoard":"5B"}]
              ],
              [
                [{"count":1,"type":"EnergySpace","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["left","right"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"speed":2,"orientations":["left","right"],"type":"ConveyorBelt","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"},{"count":1,"type":"CheckPoint","isOnBoard":"DizzyHighway"}],
                [{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],[{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}],[{"type":"Empty","isOnBoard":"5B"}],
                [{"type":"Empty","isOnBoard":"5B"}]
              ]
            ]}}""";
}



