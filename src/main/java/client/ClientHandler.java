package client;

import com.google.gson.Gson;
import server.message.Message;
import server.message.MessageBody;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientHandler implements Runnable{

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
    static List<String> availableMaps = List.of("Dizzy Highway");


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

            // receiving messages
            while(socket.isConnected()) {
                String clientInput = in.readLine();
                Message clientMessage = gson.fromJson(clientInput , Message.class);
                MessageBody clientMessageBody = clientMessage.getMessageBody();
                switch (clientMessage.getMessageType()){
                    case "PlayerValue":
                        handlePlayerValue(clientMessageBody);
                        broadcastMessage(createPlayerAddedMessage());
                        break;
                    case "SetStatus":
                        handleSetStatus(clientMessageBody);
                        break;
                    case "MapSelected":
                        broadcastMessage(clientInput);
                        break;
                    case "SendChat":
                        handleSendChat(clientMessageBody);
                        break;



                }
            }
        }catch (IOException e){
            closeEverything();
            e.printStackTrace();
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

