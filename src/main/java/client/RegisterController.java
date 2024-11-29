package client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private Button card1;

    @FXML
    private Button card2;

    @FXML
    private Button card3;

    @FXML
    private Button card4;

    @FXML
    private Button card5;

    @FXML
    private Button card6;

    @FXML
    private Button card7;

    @FXML
    private Button card8;

    @FXML
    private Button card9;

    @FXML
    private Pane selectedCard1;

    @FXML
    private Pane selectedCard2;

    @FXML
    private Pane selectedCard3;

    @FXML
    private Pane selectedCard4;

    @FXML
    private Pane selectedCard5;

    @FXML
    private Button submitButton;

    @FXML
    private Button resetButton;


    //for connection to client
    private Client client;
    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }

    public RegisterController(){}



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
