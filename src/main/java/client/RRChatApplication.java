package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class RRChatApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // create instance of client
        Socket socket = new Socket("localhost" , 8888);
        Client client = new Client(socket);
        System.out.println("connected to server");

        // creating chatloader
        FXMLLoader chatLoader = new FXMLLoader(RRChatApplication.class.getResource("clientview.fxml"));
        Parent chatRoot = chatLoader.load();
        ClientController chatController = chatLoader.getController();
        client.setClientController(chatController);
        chatController.setClient(client);


        //creating robotselectionLoader
        FXMLLoader robotSelectionLoader = new FXMLLoader(RRChatApplication.class.getResource("chooserobot.fxml"));

        Parent root = robotSelectionLoader.load();

// Retrieve the controller instance created by FXMLLoader
        RobotSelectionController robotSelectionController = robotSelectionLoader.getController();

// Pass the client to the controller
        robotSelectionController.setClient(client); // Add a `setClient` method to RobotSelectionController
        robotSelectionController.setStage(stage);
        //pass chat root to chat controller to be called later when switching scenes
        robotSelectionController.setChatRoot(chatRoot);
// Save the controller in the client for later use
        client.setRobotSelectionController(robotSelectionController);


        // start listen thread for messages
        client.receiveFromClientHandler();

        // setting scene
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("RoboRallyChat");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}