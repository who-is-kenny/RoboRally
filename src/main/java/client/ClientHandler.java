package client;

import com.google.gson.Gson;
import server.Message;
import server.MessageBody;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private static Map<Integer, ClientHandler> clients;
    private static final Map<Integer, Integer> selectedRobots = new HashMap<>(); // key: robotID, value: clientID

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private static final Gson gson = new Gson();

    // attributes for game.

    private String name;
    private int clientID;
    private int robotID;
    private boolean isReady;


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
            sendCurrentSelections();
            // TODO send player statuses
            // send information about player status


            System.out.println("waiting for client messages");


//            out.println("connection established");
//            out.println("Enter a name: ");
//
//            // check valid name
//            boolean validName = false;
//            while(!validName){
//                String tempName = in.readLine();
//                validName = true;
//                for (ClientHandler ch : clientHandlers){
//                    if (Objects.equals(ch.name, tempName)){
//                        out.println("name already taken , please choose another name: ");
//                        validName = false;
//                    }
//                }
//                name = tempName;
//
//            }
//            out.println("welcome " + name);
//            sendOtherClients(name + " joined the room");



            // receiving messages
            while(socket.isConnected()) {
                String clientInput = in.readLine();
                Message clientMessage = gson.fromJson(clientInput , Message.class);
                MessageBody clientMessageBody = clientMessage.getMessageBody();
                switch (clientMessage.getMessageType()){
                    case "PlayerValue":
                        handlePlayerValue(clientMessageBody);
                        broadcastMessage(createPlayerAddedMessage());
                    case "SetStatus":
                        handleSetStatus(clientMessageBody);
                        broadcastMessage(createPlayerStatusMessage());

                }

//
//                if (message.equals("bye")){
//                    System.out.println(name + " left the server");
//                    sendOtherClients(name + " left the room");
//                    closeEverything();
//                    break;
//                }else{
//                    sendOtherClients(name + "  " +clientID +  ": " + message);
//                }
            }
        }catch (IOException e){
            closeEverything();
            e.printStackTrace();
        }



    }

    private void handleSetStatus(MessageBody messageBody) {
        this.isReady = messageBody.isReady();
        boolean allReady = clientHandlers.stream().allMatch(ch ->ch.isReady);
        if(allReady){
            broadcastMessage(createAllReadyMessage());
        }
    }

    private String createAllReadyMessage(){
        Message allReadyMessage = new Message();
        allReadyMessage.setMessageType("AllReady");
        // try to put clientID name here
        Map<Integer,String> clientIDName = new HashMap<>();
        for (ClientHandler clientHandler : clientHandlers) {
            clientIDName.put(clientHandler.clientID, clientHandler.name); // Ensure `name` is initialized
        }
        MessageBody allReadyMessageBody = new MessageBody();
        allReadyMessageBody.setClientIDName(clientIDName);
        allReadyMessage.setMessageBody(allReadyMessageBody);
        return gson.toJson(allReadyMessage);
    }

    private String createPlayerStatusMessage(){
        Message playerStatusMessage = new Message();
        playerStatusMessage.setMessageType("PlayerStatus");
        MessageBody playerStatusMessageBody = new MessageBody();
        playerStatusMessageBody.setReady(true);
        playerStatusMessageBody.setClientID(clientID);
        playerStatusMessage.setMessageBody(playerStatusMessageBody);
        return gson.toJson(playerStatusMessage);
    }

    private void sendPlayerStatuses(){
        Message spsMessage = new Message();
        spsMessage.setMessageType("CurrentPlayerStatuses");
        MessageBody spsMessageBody = new MessageBody();
        Map<Integer,Boolean> playerStatuses = new HashMap<>();
        for(ClientHandler clientHandler : clientHandlers){
            playerStatuses.put(clientID,isReady);
        }
        spsMessageBody.setCurrentPlayerStatuses(playerStatuses);
        spsMessage.setMessageBody(spsMessageBody);
        out.println(gson.toJson(spsMessage));
    }


    private void sendCurrentSelections() {
        Message currentSelections = new Message();
        currentSelections.setMessageType("CurrentSelections");
        MessageBody messageBody = new MessageBody();
        messageBody.setSelectedRobots(new HashMap<>(selectedRobots)); // Assume MessageBody has a selectedRobots field
        currentSelections.setMessageBody(messageBody);
        out.println(gson.toJson(currentSelections));
    }

    /**
     * send message to all other clients other than themselves
     * @param message message to be sent
     */
    public void sendOtherClients(String message){
        for (ClientHandler ch : clientHandlers){
            if (ch != null && ch.name != null && !ch.name.equals(name)) {
                ch.out.println(message);
            }
        }
    }

    public static void broadcastMessage(String message){
        for (Map.Entry<Integer,ClientHandler> entry : clients.entrySet()){
            entry.getValue().out.println(message);
        }
    }

    // methods for robot selection and player name

    private void handlePlayerValue(MessageBody messageBody){
        this.name = messageBody.getPlayerName();
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
        playerAddedMessageBody.setPlayerName(name);
        playerAddedMessageBody.setFigure(robotID);
        playerAddedMessage.setMessageBody(playerAddedMessageBody);
        return gson.toJson(playerAddedMessage);
    }



    public void sendErrorMessage(){
        Message errorMessage = new Message();
        errorMessage.setMessageType("Error");
        MessageBody errorMessageBody = new MessageBody();
        errorMessageBody.setError("Whoops. That did not work. Try to adjust something.");
        errorMessage.setMessageBody(errorMessageBody);
        out.println(gson.toJson(errorMessage));
    }


    public void closeEverything(){
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

