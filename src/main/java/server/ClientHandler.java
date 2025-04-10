package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.game.Game;
import server.game.Robot;
import server.message.*;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

    //timer handle to stop the timer should all players have passed their cards
    private ScheduledExecutorService timerScheduler;

    // attributes for game.

    private final int minPlayer = 2;

    private String name;
    private final int clientID;
    private int robotID = -1;
    private boolean isReady;
    static List<String> availableMaps = List.of("DizzyHighway" , "LostBearings" , "ExtraCrispy" , "DeathTrap" , "Twister");
    private String selectedMap;
    private int startingPointX;
    private int startingPointY;
    private boolean isAI = false;
    private boolean timerStarted = false;
    private boolean finishedProgramming = false;
    private boolean timerEnded = true;
    private final String facingDirection = "right";

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
                    this.isAI = clientMessage.getMessageBody().getAI();
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


            // loops through clienthandlers and sends information about name and selected robot
            for (ClientHandler ch : clientHandlers){
                if (ch.robotID != -1){
                    out.println(ch.createPlayerAddedMessage());
                }
            }
            // loops through clienthandlers and sends isready status of clients that are ready
            for (ClientHandler ch : clientHandlers){
                if(ch.isReady && !ch.isAI){
                    out.println(ch.createPlayerStatusMessage(true));
                }
            }

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
                        //System.out.println("Received 'Alive' response from client " + clientID);
                        break;
                    case "PlayerValues":
                        handlePlayerValue(clientMessageBody);
                        broadcastMessage(createPlayerAddedMessage());
                        break;
                    case "SetStatus":
                        handleSetStatus(clientMessageBody);
                        if (!isAI) {
                            if(areAllClientsReadyAndMapSelected()){
                                MapMessages mapMessages = new MapMessages();
                                if (selectedMap.equals("DizzyHighway")){
                                    broadcastMessage(mapMessages.createDizzyHighway());
                                } else if (selectedMap.equals("LostBearings")) {
                                    broadcastMessage(mapMessages.createLostBearingsMap());
                                }else if (selectedMap.equals("DeathTrap")) {
                                    broadcastMessage(mapMessages.createDeathTrap());
                                }else if (selectedMap.equals("ExtraCrispy")) {
                                    broadcastMessage(mapMessages.createExtraCrispy());
                                }
                                // initiates first phase
                                broadcastMessage(createActivePhaseMessage(0));
                                // sends currentplayer message to start selection
                                broadcastMessage(createCurrentPlayerMessage());
                            }
                        } else {
                            // Check if all clients are AIs and minimum players are connected
                            boolean allAI = true;
                            int aiCount = 0;

                            synchronized (clientHandlers) {
                                for (ClientHandler clientHandler : clientHandlers) {
                                    if (!clientHandler.isAI) {
                                        allAI = false;
                                        break;
                                    }
                                    if (clientHandler.isAI) {
                                        aiCount++;
                                    }
                                }
                            }

                            if (allAI && aiCount >= minPlayer) {
                                // Select a random map from availableMaps
                                Random random = new Random();
                                selectedMap = availableMaps.get(random.nextInt(availableMaps.size()));

                                // Assign the selected map to all clients
                                for (ClientHandler clientHandler : clientHandlers) {
                                    clientHandler.selectedMap = selectedMap;
                                }

                                System.out.println("Selected Map: " + selectedMap);

                                // Broadcast the selected map to all clients
                                Message mapSelectedMessage = new Message();
                                mapSelectedMessage.setMessageType("MapSelected");
                                MessageBody mapSelectedBody = new MessageBody();
                                mapSelectedBody.setMap(selectedMap);
                                mapSelectedMessage.setMessageBody(mapSelectedBody);
                                broadcastMessage(gson.toJson(mapSelectedMessage));

                                // Start the game if all clients are ready and a map is selected
                                if (areAllClientsReadyAndMapSelected()) {
                                    MapMessages mapMessages = new MapMessages();
                                    if (selectedMap.equals("DizzyHighway")) {
                                        broadcastMessage(mapMessages.createDizzyHighway());
                                    } else if (selectedMap.equals("LostBearings")) {
                                        broadcastMessage(mapMessages.createLostBearingsMap());
                                    } else if (selectedMap.equals("DeathTrap")) {
                                        broadcastMessage(mapMessages.createDeathTrap());
                                    } else if (selectedMap.equals("ExtraCrispy")) {
                                        broadcastMessage(mapMessages.createExtraCrispy());
                                    }

                                    // Initiate the first phase
                                    broadcastMessage(createActivePhaseMessage(0));
                                    // Send current player message to start selection
                                    broadcastMessage(createCurrentPlayerMessage());
                                }
                            }
                        }
                        break;
                    case "MapSelected":
                        for (ClientHandler clientHandler : clientHandlers){
                            clientHandler.selectedMap = clientMessageBody.getMap();
                        }
                        System.out.println(selectedMap);
                        // sends client message to other clients
                        broadcastMessage(clientInput);
//                        broadcastMessage(DizzyHighway);
                        if(areAllClientsReadyAndMapSelected()){
                            MapMessages mapMessages = new MapMessages();
                            if (selectedMap.equals("DizzyHighway")){
                                broadcastMessage(mapMessages.createDizzyHighway());
                            } else if (selectedMap.equals("LostBearings")) {
                                broadcastMessage(mapMessages.createLostBearingsMap());
                            }else if (selectedMap.equals("DeathTrap")) {
                                broadcastMessage(mapMessages.createDeathTrap());
                            }else if (selectedMap.equals("ExtraCrispy")) {
                                broadcastMessage(mapMessages.createExtraCrispy());
                            }
                            // initiates first phase
                            broadcastMessage(createActivePhaseMessage(0));
                            // sends currentplayer message to start selection
                            broadcastMessage(createCurrentPlayerMessage());
                        }
                        break;
                    case "SendChat":
                        handleSendChat(clientMessageBody);
                        break;
                    case "SetStartingPoint":
                        handleSetStartingPoint(clientMessageBody);
                        break;
                    case "RebootDirection":
                        String direction = clientMessageBody.getDirection();
                        Game.getInstance().passRebootDirection(clientID , direction);
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
                        // TODO GAME LOGIC send robot move message so that robot moves to reboot square
                        // TODO GAME LOGIC send rotate message based on direction of choice sent by user in this switch case. ( access through client message body)
                        break;
                    case "SelectedCard":
                        handleSelectedCard(clientMessageBody);
                        break;
                    case "SelectionFinished":
                        if (!finishedProgramming){
                            boolean allAI = true;

                            synchronized (clientHandlers) {
                                for (ClientHandler clientHandler : clientHandlers) {
                                    if (!clientHandler.isAI) {
                                        allAI = false;
                                        break;
                                    }
                                }
                            }

                            if (allAI) {
                                // Handle Selection Finished for AIs
//                                out.println(createTimerStartMessage());
//                                startServerTimer();
                                this.finishedProgramming = true;
                                this.timerEnded = true;
                                System.out.println("All AI clients: Starting timer and proceeding.");
                            } else {
                                // If not all clients are AIs, ignore this message from AIs
                                if (!isAI) {
                                    if(!timerStarted){
                                        broadcastMessage(createTimerStartMessage());
                                        for (ClientHandler clientHandler : clientHandlers){
                                            clientHandler.timerStarted = true;
                                        }

                                    }
//                                    startServerTimer();
                                    this.finishedProgramming = true;
                                    this.timerEnded = true;
                                    System.out.println("Real player detected: Processing 'SelectionFinished' normally.");
                                } else {
//                                    System.out.println("Ignoring 'SelectionFinished' message from AI client.");
                                    this.finishedProgramming = true;
                                }
                            }
                        }
                        // Check if all clients are AIs

                        break;
                    case "PlayCard":
                        sendCardPlayed(clientMessageBody);
                        break;
                    case "SelectedDamage":
                        Game.getInstance().givePlayerSelectedDamage(clientMessageBody,clientID);
                        break;


                }
            }
        }catch (IOException e){
            closeEverything();
            e.printStackTrace();
        }
    }



    private boolean areAllClientsReadyAndMapSelected() {
        // Check if the selected map is set
        if (selectedMap == null || selectedMap.isEmpty()) {
            return false;
        }
        // Check if all clientHandlers are ready
        synchronized (clientHandlers) {
            for (ClientHandler client : clientHandlers) {
                if (!client.isReady) {
                    return false;
                }
            }
        }
        return true;
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
            //[timo, 6.12., 18:42] sendYourCards(sampleCards); // TODO GAME LOGIC remove
        }else{
            // DONE GAME LOGIC create instance of game in server class
            // DONE GAME LOGIC loop through clienthandlers, create a player instance for each client handler and add to game.
            Game game = Game.getInstance();
            game.initialize(clientHandlers);
            // DONE GAME LOGIC maybe game loop starts here
            new Thread(game).start();


            // DONE GAME LOGIC use send Your Cards method to send cards to client and remove sendyourcards(samplecards)
            //switchToProgrammingPhase();[timo, 8.12.] implemented in Game.programmingPhase() //needs to be called from game
            //sendYourCards(sampleCards); -"-

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
        if (!isAI){
            broadcastMessage(createPlayerStatusMessage(isReady));
            out.println(createSelectMapMessage());
        }

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

    //(7) //TODO GAME LOGIC use these methods in game logic
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
        System.out.println("=====================> Programming Phase <==================================");
//        broadcastMessage(createActivePhaseMessage(2));
        out.println(createActivePhaseMessage(2));
    }

    public void switchToAktivierungPhase(){
        System.out.println("======================> switching to aktivierungsphase <====================");
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
        //System.out.println("sending card selected message to other clients");
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

    // (8) //TODO GAME LOGIC use these methods in game logic
    //movement
    public void sendMovementMessage(int ClientID , int x, int y){
        System.out.println("CLIENTHANDLER.sendMovementMessage(" + ClientID + ", " + x + ", " + y + ")");
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

    // damage
    public void sendDamageMessage(int clientID, List<String> cards){
        Message damageMessage = new Message();
        damageMessage.setMessageType("DrawDamage");
        MessageBody damageMessageBody = new MessageBody();
        damageMessageBody.setClientID(clientID);
        damageMessageBody.setCards(cards);
        damageMessage.setMessageBody(damageMessageBody);
        broadcastMessage(gson.toJson(damageMessage));
    }

    // pick damage
    public void sendPickDamageMessage (int count , List<String> availablePiles){
        Message pickDamageMessage = new Message();
        pickDamageMessage.setMessageType("PickDamage");
        MessageBody pickDamageMessageBody = new MessageBody();
        pickDamageMessageBody.setCount(count);
        pickDamageMessageBody.setAvailablePiles(availablePiles);
        pickDamageMessage.setMessageBody(pickDamageMessageBody);
        broadcastMessage(gson.toJson(pickDamageMessage));
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

    public void sendTwisterAnimationMessage (int checkpoint , int x, int y){
        Message animationMessage = new Message();
        animationMessage.setMessageType("AnimationT");
        MessageBody animationMessageBody = new MessageBody();
        animationMessageBody.setNumber(checkpoint);
        animationMessageBody.setX(x);
        animationMessageBody.setY(y);
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
    // connection update
    public void sendConnectionUpdateMessage(){
        Message connectionUpdateMessage = new Message();
        connectionUpdateMessage.setMessageType("ConnectionUpdate");
        MessageBody connectionUpdateMessageBody = new MessageBody();
        connectionUpdateMessageBody.setClientID(clientID);
        connectionUpdateMessageBody.setConnected(false);
        connectionUpdateMessageBody.setAction("Remove");
        connectionUpdateMessage.setMessageBody(connectionUpdateMessageBody);
        broadcastMessage(gson.toJson(connectionUpdateMessage));
        System.out.println("sending connection update message.");
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
            sendConnectionUpdateMessage();
            System.out.println("Ping thread stopped for client " + clientID);
        }).start();
    }

    private void sendAlivePing() {
        // Send an "Alive" message to the client
        Message aliveMessage = new Message();
        aliveMessage.setMessageType("Alive");
        aliveMessage.setMessageBody(new MessageBody());
        out.println(gson.toJson(aliveMessage));
        //System.out.println("Sent 'Alive' message to client " + clientID);
    }

    private void checkForClientTimeout() {
        // Check if the client has failed to respond to the last "Alive" message within the timeout period
        if (lastAliveResponseTime.containsKey(clientID)) {
            long lastResponseTime = lastAliveResponseTime.get(clientID);
            if (System.currentTimeMillis() - lastResponseTime > PING_TIMEOUT) {
                System.out.println("Client " + clientID + " timed out. Disconnecting.");
                sendConnectionUpdateMessage();
                closeEverything();
            }
        }
    }

    private void startServerTimer(){

        this.timerScheduler = Executors.newScheduledThreadPool(1);
        this.timerScheduler.schedule(() -> {
            // Action to perform after 30 seconds
            List<Integer> clientsWithNullCards = new ArrayList<>();
            if (!this.timerEnded) {
                for (ClientHandler clientHandler : clientHandlers) {
                    clientHandler.setTimerStatus(true);
                    if (clientHandler.cardsInHand.contains("Null")) {
                        clientsWithNullCards.add(clientID);
                    }
                }
            }
            sendTimerEnded(clientsWithNullCards);
        }, 5, TimeUnit.SECONDS);
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

    }
    //[timo, 6.12.] getter for game initialization
    public String getName(){
        return this.name;
    }
    public int getRobotID(){
        return this.robotID;
    }
    public int[] getStartingPosition(){
        return new int[]{this.startingPointX, this.startingPointY};
    }
    public String getSelectedMap(){
        return this.selectedMap;
    }

    public boolean isFinishedProgramming(){
        return this.finishedProgramming;
    }

    /**
     * sets finishedProgramming = false
     */
    public void setProgrammingFlag(){
        this.finishedProgramming = false;
        //System.out.println("CLIENTHANDLER.setProgrammingFlag() " + this.finishedProgramming);
    }

    public ArrayList<String> getCardsInHand(){
        return this.cardsInHand;
    }

    public boolean getTimerStatus(){
        return this.timerEnded;
    }

    /**
     * sets the boolean timerEnded to the provided boolean
     * @param timerEnded
     */
    public void setTimerStatus(boolean timerEnded){
        this.timerEnded = timerEnded;
        //System.out.println("CLIENTHANDLER.setTimerStatus("+ this.timerEnded + ")");
    }

    public void shutdownTimer(){
        if (this.timerScheduler != null){
            timerScheduler.shutdown();
        }
    }

    public String getFacingDirection(){
        return this.facingDirection;
    }

    public int getClientID(){
        return this.clientID;
    }
    // -------------------------------------------------------------------------------------------------------------------
    //Ai methods
    private String chooseRandomMap(){
        Random random = new Random();
        int randomIndex = random.nextInt(availableMaps.size());
        return availableMaps.get(randomIndex);
    }


}



