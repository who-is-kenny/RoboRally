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

        //creating game board loader
        FXMLLoader gameBoardLoader = new FXMLLoader(RRChatApplication.class.getResource("DizzyHighway.fxml"));
        Parent gameBoardRoot = gameBoardLoader.load();
        GameBoardController gameBoardController = gameBoardLoader.getController();
        client.setGameBoardController(gameBoardController);
        gameBoardController.setClient(client);

        //creating mainLoader
        FXMLLoader mainLoader = new FXMLLoader(RRChatApplication.class.getResource("Main.fxml"));
        Parent mainRoot = mainLoader.load();
        MainController mainController = mainLoader.getController();
        mainController.setClient(client);
        mainController.setChatRoot(chatRoot);
        mainController.setGameBoardRoot(gameBoardRoot);



        //creating robotselectionLoader
        FXMLLoader robotSelectionLoader = new FXMLLoader(RRChatApplication.class.getResource("chooserobot.fxml"));
        Parent root = robotSelectionLoader.load();
        RobotSelectionController robotSelectionController = robotSelectionLoader.getController();
        robotSelectionController.setClient(client);
        robotSelectionController.setStage(stage);
        robotSelectionController.setChatRoot(mainRoot);     // TODO was chat root before
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