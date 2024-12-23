package server.game;

import server.ClientHandler;

import java.util.ArrayList;

public class Player {

    private String playerName;
    private ProgrammingDeck programmingDeck;
    private DiscardPile discardPile;
    private ArrayList<Cards> playerHand = new ArrayList<>();
    private final Register playerRegister = new Register();
    private int checkpoint = 0; // counting up check is it is the next checkpoint
    private double distanceFromAntenna;
    private ClientHandler clientHandler;

    //attributes for robot:
    private Position robotPosition;
    private boolean isRebooting;
    private int energy = 5;
    private String direction;



















    //getters and setters
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public double getDistanceFromAntenna() {
        return distanceFromAntenna;
    }

    public void setDistanceFromAntenna(double distanceFromAntenna) {
        this.distanceFromAntenna = distanceFromAntenna;
    }

    public int getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(int checkpoint) {
        this.checkpoint = checkpoint;
    }

    public Register getPlayerRegister() {
        return playerRegister;
    }

    public ArrayList<Cards> getPlayerHand() {
        return playerHand;
    }

    public void setPlayerHand(ArrayList<Cards> playerHand) {
        this.playerHand = playerHand;
    }

    public ProgrammingDeck getProgrammingDeck() {
        return programmingDeck;
    }

    public void setProgrammingDeck(ProgrammingDeck programmingDeck) {
        this.programmingDeck = programmingDeck;
    }

    public DiscardPile getDiscardPile() {
        return discardPile;
    }

    public void setDiscardPile(DiscardPile discardPile) {
        this.discardPile = discardPile;
    }


    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public boolean isRebooting() {
        return isRebooting;
    }

    public void setRebooting(boolean rebooting) {
        isRebooting = rebooting;
    }

    public Position getRobotPosition() {
        return robotPosition;
    }

    public void setRobotPosition(Position robotPosition) {
        this.robotPosition = robotPosition;
    }
}
