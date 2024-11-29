package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    private static final int PORT = 8888;
    private ServerSocket serversocket;
    private static Map<Integer, ClientHandler> clients = new ConcurrentHashMap<>();
    private static AtomicInteger clientIdCounter = new AtomicInteger(1);


    public Server(ServerSocket serversocket) {
        this.serversocket = serversocket;

    }

    /**
     * starts a new server and counts number of clients
     */
    public void startServer(){

        while(!serversocket.isClosed()){
            try {
                Socket clientSocket = serversocket.accept();
                int clientId = clientIdCounter.getAndIncrement();
                System.out.println(" client " + clientId + " connected to server");
                ClientHandler clientHandler = new ClientHandler(clientSocket , clientId , clients);
                clients.put(clientId, clientHandler);
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
