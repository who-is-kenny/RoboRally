package server;

import java.util.List;

public class MessageBody{
    // establish connection (2)
    private String protocol;
    private String group;
    private Boolean isAI;
    private int clientID;
    // lobby(3)
    private String name;
    private int figure;
    private boolean ready;
    private String map;
    private List<String> availableMaps;
    // chatnachrichten (4)
    private String message;
    private int to;
    private boolean isPrivate;
    // error message (5)
    private String error;
    //cards (6)
    private String card;
    // player turn(7)
    private int phase;
    // Aufbauphase (7.1)
    private int x;
    private int y;
    // Programmierphase (7.3)
    private List<String> cardsInHand;
    private int register;
    private boolean filled;
    private List<Integer> clientIDs;
    private List<String> cards;



    public MessageBody() {
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Boolean getAI() {
        return isAI;
    }

    public void setAI(Boolean AI) {
        isAI = AI;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


}
