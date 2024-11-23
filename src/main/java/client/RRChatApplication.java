package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
        ClientController chatController = new ClientController(client);
        chatLoader.setController(chatController);
        client.setClientController(chatController);
        client.receiveFromClientHandler();

        // setting scene

        Scene scene = new Scene(chatLoader.load(), 600, 400);
        stage.setTitle("RoboRallyChat");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}