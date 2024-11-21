package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import server.Message;
import server.MessageBody;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private int clientID;
    private Map<Integer, ClientHandler> clients;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String name;
    private static final Gson gson = new Gson();


    public ClientHandler(Socket socket , int clientID , Map<Integer, ClientHandler> clients ){

        this.socket = socket;
        this.clientID =clientID;
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
                }
            }
            // send welcome message
            Message welcomeMessage = new Message();
            welcomeMessage.setMessageType("Welcome");
            MessageBody welcomeMessageBody = new MessageBody();
            welcomeMessageBody.setClientID(clientID);
            welcomeMessage.setMessageBody(welcomeMessageBody);
            out.println(gson.toJson(welcomeMessage));

            out.println("connection established");


            out.println("Enter a name: ");

            // check valid name
            boolean validName = false;
            while(!validName){
                String tempName = in.readLine();
                validName = true;
                for (ClientHandler ch : clientHandlers){
                    if (Objects.equals(ch.name, tempName)){
                        out.println("name already taken , please choose another name: ");
                        validName = false;
                    }
                }
                name = tempName;

            }
            out.println("welcome " + name);
            sendOtherClients(name + " joined the room");



            // receiving messages
            while(socket.isConnected()) {
                String message;
                message = in.readLine();
                if (message.equals("bye")){
                    System.out.println(name + " left the server");
                    sendOtherClients(name + " left the room");
                    closeEverything();
                    break;
                }else{
                    sendOtherClients(name + ": " + message);
                }
            }
        }catch (IOException e){
            closeEverything();
            e.printStackTrace();
        }



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

