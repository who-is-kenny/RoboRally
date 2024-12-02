package client;


import client.controller.ClientController;
import client.controller.GameBoardController;
import client.controller.RegisterController;
import client.controller.RobotSelectionController;
import com.google.gson.Gson;
import javafx.scene.paint.Color;
import server.message.Message;
import server.message.MessageBody;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final Gson gson = new Gson();

    private int totalClients = 0;
    private List<Integer> readyClientIDs = new ArrayList<>();
    private boolean mapSelected;
    private String selectedMap;

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

            while (clientID == 0){
                String connectionMessage = in.readLine();
                Message handlerMessage = gson.fromJson(connectionMessage, Message.class);
                if (handlerMessage.getMessageType().equals("HelloClient")){
                    // creating message and sending it back to the handler to establish connection.
                    Message helloServer = new Message();
                    helloServer.setMessageType("HelloServer");
                    MessageBody helloServerBody = new MessageBody();
                    helloServerBody.setProtocol("Version 0.1");
                    helloServerBody.setAI(false);
                    helloServerBody.setGroup("Neidische Narwahl");
                    helloServer.setMessageBody(helloServerBody);
                    out.println(gson.toJson(helloServer));
                    // handle welcome message
                } else if (handlerMessage.getMessageType().equals("Welcome")) {
                    setClientID(handlerMessage.getMessageBody().getClientID());
                }
            }
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
                        MessageBody messageFromHandlerBody = messageFromHandler.getMessageBody();
                        switch (messageFromHandler.getMessageType()){
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
                                if (messageFromHandlerBody.isReady()){
                                    if(!readyClientIDs.contains(messageFromHandlerBody.getClientID())){
                                        readyClientIDs.add(messageFromHandlerBody.getClientID());
                                        if(readyClientIDs.getFirst() == clientID){
                                            robotSelectionController.setDisableMap(false);
                                        }
                                    }
                                    if (mapSelected && readyClientIDs.size() == totalClients){
                                        robotSelectionController.switchToChatScene();
                                        clientController.updateClientList();
                                    }
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
                                break;
                            case "SelectMap":
                                robotSelectionController.handleSelectMap(messageFromHandlerBody);
                                break;
                            case "MapSelected":
                                mapSelected = true;
                                selectedMap = messageFromHandlerBody.getMap();
                                if (mapSelected && readyClientIDs.size() == totalClients) {
                                    robotSelectionController.switchToChatScene();
                                    clientController.updateClientList();
                                }
                                break;
                            case "ReceivedChat":
                                if(messageFromHandlerBody.getFrom() != clientID){
                                    clientController.addMessage(messageFromHandlerBody);
                                }
                                break;
                            case "ActivePhase":
                                activePhase = messageFromHandlerBody.getPhase();
                                if(messageFromHandlerBody.getPhase() == 2){
                                    gameBoardController.handleactivephase2();
                                }
                                System.out.println("active phase: " + activePhase);  //TODO remove print
                                break;
                            case "CurrentPlayer":
                                currentPlayerID = messageFromHandlerBody.getClientID();
                                System.out.println("currentplayer : " + currentPlayerID);  //TODO remove print
                                break;
                            case "StartingPointTaken":
                                gameBoardController.handleStartingPointTaken(messageFromHandlerBody);
                                break;
                            case "Movement":
                                gameBoardController.handleRobotMovement(messageFromHandlerBody);
                                break;
                            case "PlayerTurning":
                                gameBoardController.handleRobotTurn(messageFromHandlerBody);
                                break;
                            case "Reboot":
                                gameBoardController.handleReboot(messageFromHandlerBody);
                                if(clientID == messageFromHandlerBody.getClientID()){
                                    gameBoardController.sendRebootPopup();
                                }
                                break;
                            case "Energy":
                                gameBoardController.applyGlowToRobot(messageFromHandlerBody.getClientID(), Color.ORANGE,1.0);
                                break;
                            case "CheckPointReached":
                                gameBoardController.applyGlowToRobot(messageFromHandlerBody.getClientID(), Color.YELLOW,1.0);
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

}
