package server;

import com.google.gson.Gson;
import server.message.Message;
import server.message.MessageBody;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
    private static final Gson gson = new Gson();

    // attributes for game.

    private String name;
    private final int clientID;
    private int robotID = -1;
    private boolean isReady;
    static List<String> availableMaps = List.of("DizzyHighway");
    private String selectedMap;
    private int startingPointX;
    private int startingPointY;



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
                    case "PlayerValue":
                        handlePlayerValue(clientMessageBody);
                        broadcastMessage(createPlayerAddedMessage());
                        break;
                    case "SetStatus":
                        handleSetStatus(clientMessageBody);
                        break;
                    case "MapSelected":
                        selectedMap = clientMessageBody.getMap();
                        System.out.println(selectedMap);
                        // sends client message to other clients
                        broadcastMessage(clientInput);
//                        broadcastMessage(readJsonFile()); // TODO enable when fixed function
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
                                broadcastMessage(createRotationMessage(clientID, "clockwise"));
                                break;
                            case "Left":
                                broadcastMessage(createRotationMessage(clientID, "counterclockwise"));
                                break;
                            case "Up":
                                break;
                            case "Down":
                                broadcastMessage(createRotationMessage(clientID, "clockwise"));
                                broadcastMessage(createRotationMessage(clientID, "clockwise"));
                                break;
                            default:
                                System.out.println("direction invalid for reboot");
                        }
                        // TODO send robot move message so that robot moves to reboot square
                        // TODO send rotate message based on direction of choice sent by user in this switch case. ( access through client message body)
                        break;

                }
            }
        }catch (IOException e){
            closeEverything();
            e.printStackTrace();
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
        }else{
            broadcastMessage(createActivePhaseMessage(2));
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
            broadcastMessage(createMovementMessage(1,6,6)); // TODO remove this test later
//            broadcastMessage(createRotationMessage(2 , "clockwise")); // TODO remove this test later
//            broadcastMessage(createRebootMessage(2));
            broadcastMessage(createCheckPointMessage(2,1));


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
    private String readTextFile(String fileName) {
        // Adjust the file path to point to your .txt directory
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("client/json/" + fileName + ".json");
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

    // (8) //TODO use these methods in game logic
    //movement
    private String createMovementMessage(int ClientID , int x, int y){
        Message MovementMessage = new Message();
        MovementMessage.setMessageType("Movement");
        MessageBody MovementMessageBody = new MessageBody();
        MovementMessageBody.setClientID(ClientID);
        MovementMessageBody.setX(x);
        MovementMessageBody.setY(y);
        MovementMessage.setMessageBody(MovementMessageBody);
        return gson.toJson(MovementMessage);
    }

    //rotation
    private String createRotationMessage(int ClientID , String rotation){
        Message RotationMessage = new Message();
        RotationMessage.setMessageType("PlayerTurning");
        MessageBody RotationMessageBody = new MessageBody();
        RotationMessageBody.setClientID(ClientID);
        RotationMessageBody.setRotation(rotation);
        RotationMessage.setMessageBody(RotationMessageBody);
        return gson.toJson(RotationMessage);
    }
    // animation
    private String createAnimationMessage (String animationType){
        Message animationMessage = new Message();
        animationMessage.setMessageType("Animation");
        MessageBody animationMessageBody = new MessageBody();
        animationMessageBody.setType(animationType);
        animationMessage.setMessageBody(animationMessageBody);
        return gson.toJson(animationMessage);
    }
    // reboot
    private String createRebootMessage (int ClientID){
        Message rebootMessage = new Message();
        rebootMessage.setMessageType("Reboot");
        MessageBody rebootMessageBody = new MessageBody();
        rebootMessageBody.setClientID(ClientID);
        rebootMessage.setMessageBody(rebootMessageBody);
        return gson.toJson(rebootMessage);
    }
    // energy
    private String createEnergyMessage (int ClientID, int count, String source){
        Message energyMessage = new Message();
        energyMessage.setMessageType("Energy");
        MessageBody energyMessageBody = new MessageBody();
        energyMessageBody.setClientID(ClientID);
        energyMessageBody.setCount(count);
        energyMessageBody.setSource(source);
        energyMessage.setMessageBody(energyMessageBody);
        return gson.toJson(energyMessage);
    }

    // checkpoint reached
    private String createCheckPointMessage(int ClientID, int number){
        Message checkPointMessage = new Message();
        checkPointMessage.setMessageType("CheckPointReached");
        MessageBody checkPointMessageBody = new MessageBody();
        checkPointMessageBody.setClientID(ClientID);
        checkPointMessageBody.setNumber(number);
        checkPointMessage.setMessageBody(checkPointMessageBody);
        return gson.toJson(checkPointMessage);
    }

    // game over
    private String createGameOverMessage (int ClientID){
        Message gameOverMessage = new Message();
        gameOverMessage.setMessageType("GameFinished");
        MessageBody gameOverMessageBody = new MessageBody();
        gameOverMessageBody.setClientID(ClientID);
        gameOverMessage.setMessageBody(gameOverMessageBody);
        return gson.toJson(gameOverMessage);
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

//    public static void main(String[] args) {
//        Gson gson = new Gson();
//
//        MessageBody body = new MessageBody();
//        body.setProtocol("Version 0.1");
//
//        Message message = new Message();
//        message.setMessageType("HelloClient");
//        message.setMessageBody(body);
//        System.out.println(gson.toJson(message));
//    }
}

