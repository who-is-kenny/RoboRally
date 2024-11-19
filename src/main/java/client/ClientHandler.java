package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String name;


    public ClientHandler(Socket socket){

        this.socket = socket;
        clientHandlers.add(this);

    }

    // thread waits for message from client
    @Override
    public void run() {



        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
}

