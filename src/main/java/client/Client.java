package client;


import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;


public class Client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;


    public Client(Socket socket){

        try {
            this.socket = socket;
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
     * @param vBox text box for incoming message
     */
    public void receiveFromClientHandler(VBox vBox){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromHandler;
                while( socket.isConnected()){
                    try {

                        messageFromHandler = in.readLine(); // IO Error after we close everything the first time -> jump to catch -> then break to end thread
                        ClientController.addMessage(messageFromHandler , vBox);
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
