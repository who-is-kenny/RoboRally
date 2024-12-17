package client;

import client.controller.StartController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RRStartApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Creating the selection screen loader
        FXMLLoader startLoader = new FXMLLoader(RRChatApplication.class.getResource("Start.fxml"));
        Parent startRoot = startLoader.load();
        StartController startController = startLoader.getController();
        startController.setStage(stage);

        // Show the selection screen first
        Scene startScene = new Scene(startRoot, 400, 300);
        stage.setScene(startScene);
        stage.setTitle("Select Game Mode");
        stage.show();
    }
}
