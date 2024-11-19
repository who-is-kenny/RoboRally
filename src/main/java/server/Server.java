package server;

import client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 8888;

    private ServerSocket serversocket;
    private int numClients ;

    public Server(ServerSocket serversocket) {
        this.serversocket = serversocket;
        this.numClients = 0;
    }

    /**
     * starts a new server and counts number of clients
     */
    public void startServer(){

        while(!serversocket.isClosed()){
            try {
                Socket clientSocket = serversocket.accept();
                numClients ++;
                System.out.println(" client " + numClients + " connected to server");
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread serverThread = new Thread(clientHandler);
                serverThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void closeServer(){
        // TODO if needed

    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
