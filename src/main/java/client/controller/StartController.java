package client.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import client.Client;
import client.AIClient;
import server.message.Message;
import server.message.MessageBody;
import server.message.MessageSerializer;

import java.io.IOException;
import java.net.Socket;

public class StartController {

    @FXML
    private Button playAsHumanButton;

    @FXML
    private Button playAsAIButton;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class , new MessageSerializer()).create();

    @FXML
    public void handlePlayAsHuman(ActionEvent event) throws IOException {
        // Start human player setup
        startGame(false);
    }

    @FXML
    public void handlePlayAsAI(ActionEvent event) throws IOException {
        // Start AI player setup
        startGame(true);
    }

    private void startGame(boolean isAI) throws IOException {
        // Close current window
        stage.close();

        // Create socket for the client
        Socket socket = new Socket("localhost", 8888);

        if (isAI) {
            // Create an AIClient if AI is chosen
            AIClient aiClient = new AIClient(socket);
            // Initialize controllers and set AIClient
            initializeControllersAI(aiClient);
        } else {
            // Create a regular Client if human is chosen
            Client client = new Client(socket);
            // Initialize controllers and set Client
            initializeControllersClient(client);
        }
    }

    private void initializeControllersClient(Client client) throws IOException {

        // Assuming both Client and AIClient have the same interface for the controllers
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/client/clientview.fxml"));
        Parent chatRoot = chatLoader.load();
        ClientController chatController = chatLoader.getController();
        chatController.setClient(client);

        FXMLLoader gameBoardLoader = new FXMLLoader(getClass().getResource("/client/DizzyHighway.fxml"));
        Parent gameBoardRoot = gameBoardLoader.load();
        GameBoardController gameBoardController = gameBoardLoader.getController();
        gameBoardController.setClient(client);

        FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("/client/RegisterCard.fxml"));
        Parent registerRoot = registerLoader.load();
        RegisterController registerController = registerLoader.getController();
        registerController.setClient(client);

        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/client/Main.fxml"));
        Parent mainRoot = mainLoader.load();
        MainController mainController = mainLoader.getController();
        mainController.setClient(client);
        mainController.setChatRoot(chatRoot);
        mainController.setGameBoardRoot(gameBoardRoot);
        mainController.setRegisterRoot(registerRoot);

        FXMLLoader robotSelectionLoader = new FXMLLoader(getClass().getResource("/client/chooserobot.fxml"));
        Parent root = robotSelectionLoader.load();
        RobotSelectionController robotSelectionController = robotSelectionLoader.getController();
        robotSelectionController.setClient(client);
        robotSelectionController.setStage(stage);
        robotSelectionController.setChatRoot(mainRoot);
        client.setRobotSelectionController(robotSelectionController);

        // Start the message receiving thread
        client.receiveFromClientHandler();

        // Set up the scene for the main screen
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("RoboRallyChat");
        stage.show();

        Message helloServer = new Message();
        helloServer.setMessageType("HelloServer");
        MessageBody helloServerBody = new MessageBody();
        helloServerBody.setProtocol("Version 0.1");
        helloServerBody.setAI(false);
        helloServerBody.setGroup("Neidische Narwahl");
        helloServer.setMessageBody(helloServerBody);
        client.sendToClientHandler(gson.toJson(helloServer));
    }

    private void initializeControllersAI(AIClient AIclient) throws IOException {
        // Assuming both Client and AIClient have the same interface for the controllers
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/client/clientview.fxml"));
        Parent chatRoot = chatLoader.load();
        ClientController chatController = chatLoader.getController();
        chatController.setClient(AIclient);

        FXMLLoader gameBoardLoader = new FXMLLoader(getClass().getResource("/client/DizzyHighway.fxml"));
        Parent gameBoardRoot = gameBoardLoader.load();
        GameBoardController gameBoardController = gameBoardLoader.getController();
        gameBoardController.setClient(AIclient);

        FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("/client/RegisterCard.fxml"));
        Parent registerRoot = registerLoader.load();
        RegisterController registerController = registerLoader.getController();
        registerController.setClient(AIclient);

        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/client/Main.fxml"));
        Parent mainRoot = mainLoader.load();
        MainController mainController = mainLoader.getController();
        mainController.setClient(AIclient);
        mainController.setChatRoot(chatRoot);
        mainController.setGameBoardRoot(gameBoardRoot);
        mainController.setRegisterRoot(registerRoot);

        FXMLLoader robotSelectionLoader = new FXMLLoader(getClass().getResource("/client/chooserobot.fxml"));
        Parent root = robotSelectionLoader.load();
        RobotSelectionController robotSelectionController = robotSelectionLoader.getController();
        robotSelectionController.setClient(AIclient);
        robotSelectionController.setStage(stage);
        robotSelectionController.setChatRoot(mainRoot);
        AIclient.setRobotSelectionController(robotSelectionController);

        // Start the message receiving thread
        AIclient.receiveFromClientHandler();

        // Set up the scene for the main screen
        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("RoboRallyChat");
        stage.show();

        Message helloServer = new Message();
        helloServer.setMessageType("HelloServer");
        MessageBody helloServerBody = new MessageBody();
        helloServerBody.setProtocol("Version 0.1");
        helloServerBody.setAI(true);
        helloServerBody.setGroup("Neidische Narwahl");
        helloServer.setMessageBody(helloServerBody);
        AIclient.sendToClientHandler(gson.toJson(helloServer));
    }
}
