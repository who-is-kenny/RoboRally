package client;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import server.Message;
import server.MessageBody;

import java.io.*;
import java.net.Socket;
import java.util.Map;


public class Client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final Gson gson = new Gson();
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
                            case "CurrentSelections":
                                Map<Integer, Integer> currentSelections = messageFromHandlerBody.getSelectedRobots();
                                currentSelections.keySet().forEach(robotSelectionController::disableChosenRobot);
                                break;
                            case "PlayerAdded":
                                robotSelectionController.handlePlayerAdded(messageFromHandlerBody , clientID);
                                break;
                            case "AllReady":
                                clientController.setClientIdName(messageFromHandlerBody.getClientIDName());
                                robotSelectionController.switchToChatScene();
                                clientController.updateClientList();
                                break;

                        }

//                        clientController.addMessage(messageFromHandler);
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
