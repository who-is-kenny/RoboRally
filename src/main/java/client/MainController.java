package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;



public class MainController implements Initializable {

    @FXML
    private VBox registerContainer;
    @FXML
    private VBox gameBoardContainer;
    @FXML
    private VBox chatContainer;

    private Client client;
    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
        initializeControllers();
    }

    public MainController(){}

    private void initializeControllers() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setChatRoot(Parent chatRoot) {
        chatContainer.getChildren().add(chatRoot);  // Add the chat to the VBox
    }

    public void setGameBoardRoot(Parent gameBoardRoot) {
        gameBoardContainer.getChildren().add(gameBoardRoot);  // Add the game board to the VBox
    }

    public void setRegisterRoot(Parent registerRoot){
        registerContainer.getChildren().add(registerRoot);

    }
}
