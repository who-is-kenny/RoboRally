package server.message;

public class ActiveCard {
    private int clientID;
    private String card;

    public ActiveCard(int clientID, String card) {
        this.clientID = clientID;
        this.card = card;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
