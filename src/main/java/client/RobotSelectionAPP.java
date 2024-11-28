package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RobotSelectionAPP extends Application {

    public void start(Stage stage) throws IOException {

        FXMLLoader robotLoader = new FXMLLoader(RobotSelectionAPP.class.getResource("DizzyHighway.fxml"));
        Scene scene = new Scene(robotLoader.load(), 600, 400);
        stage.setTitle("RoboRallyChat");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

