package client.controller;

import client.Client;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class GameBoardController implements Initializable {

    @FXML
    private ImageView DizzyHighway;

    @FXML
    private GridPane game_grid;

    @FXML
    private ImageView gear1;

    @FXML
    private ImageView gear2;

    @FXML
    private ImageView gear3;

    @FXML
    private ImageView gear4;

    @FXML
    private ImageView gear5;

    @FXML
    private ImageView gear6;

    @FXML
    private Pane mainPane;

    private Client client;
    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }

    public GameBoardController(){}


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }




}
