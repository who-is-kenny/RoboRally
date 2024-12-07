package server.message;

import java.util.List;
import java.util.Map;

public class MessageBody {

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

    // extra attributes (3)
    //current player status used when new client connects
    private Map<Integer, Boolean> currentPlayerStatuses;
    public Map<Integer, Boolean> getCurrentPlayerStatuses() {
        return currentPlayerStatuses;
    }
    public void setCurrentPlayerStatuses(Map<Integer, Boolean> currentPlayerStatuses) {
        this.currentPlayerStatuses = currentPlayerStatuses;
    }
    // used to send all names and client IDs to create the chat
    private Map <Integer,String> clientIDName;
    public Map<Integer, String> getClientIDName() {
        return clientIDName;
    }
    public void setClientIDName(Map<Integer, String> clientIDName) {
        this.clientIDName = clientIDName;
    }
    // selectedRobots used when new client connects
    private Map<Integer, Integer> selectedRobots;
    public Map<Integer, Integer> getSelectedRobots() {
        return selectedRobots;
    }
    public void setSelectedRobots(Map<Integer, Integer> selectedRobots) {
        this.selectedRobots = selectedRobots;
    }


    // for long gameboard message:
    private Integer energy;
    private List<List<List<Tile>>> gameMap;

    public Integer getEnergy() {
        return energy;
    }

    public void setEnergy(Integer energy) {
        this.energy = energy;
    }

    public List<List<List<Tile>>> getGameMap() {
        return gameMap;
    }

    public void setGameMap(List<List<List<Tile>>> gameMap) {
        this.gameMap = gameMap;
    }

    // chatnachrichten (4)
    private String message;
    private int to;
    private int from;
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
    private String direction;
    // Programmierphase (7.3)
    private Object cardsInHand;
    private int register;
    private boolean filled;
    private List<Integer> clientIDs;
    private List<String> cards;
    //aktivierungsphase (7.4)
    private String newCard;
    private List<ActiveCard> activeCards;

    // Aktionen, Ereigniss und Effekte (8)
    // Bewegung DONE
    // Drehung
    private String rotation;



    // availablepiles
    private List<String> availablePiles;
    // Animation
    private String type;
    //neustart DONE
    // Energie
    private int count;
    private String source;
    // checkpoints
    private int number;






    public MessageBody() {
    }


    // establish connection (2)
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

    // lobby(3)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFigure() {
        return figure;
    }

    public void setFigure(int figure) {
        this.figure = figure;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public List<String> getAvailableMaps() {
        return availableMaps;
    }

    public void setAvailableMaps(List<String> availableMaps) {
        this.availableMaps = availableMaps;
    }


    // chatnachrichten (4)



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

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }


    // error (5)
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    // card (6)
    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }


    // game (7)


    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    // aufbauphase (7.1)

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    //TODO upgrade phase

    // Programmierphase (7.3)

    public List<String> getCardsInHandAsList() {
        if (cardsInHand instanceof List<?>) {
            return (List<String>) cardsInHand;
        }
        return null;
    }

    public Integer getCardsInHandAsInteger() {
        if (cardsInHand instanceof Integer) {
            return (Integer) cardsInHand;
        }
        return null;
    }

    public void setCardsInHand(Object cardsInHand) {
        this.cardsInHand = cardsInHand;
    }

    public int getRegister() {
        return register;
    }

    public void setRegister(int register) {
        this.register = register;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public List<Integer> getClientIDs() {
        return clientIDs;
    }

    public void setClientIDs(List<Integer> clientIDs) {
        this.clientIDs = clientIDs;
    }

    public List<String> getCards() {
        return cards;
    }

    public void setCards(List<String> cards) {
        this.cards = cards;
    }

    // Aktivierungsphase (7.4)


    public String getNewCard() {
        return newCard;
    }

    public void setNewCard(String newCard) {
        this.newCard = newCard;
    }

    public List<ActiveCard> getActiveCards() {
        return activeCards;
    }

    public void setActiveCards(List<ActiveCard> activeCards) {
        this.activeCards = activeCards;
    }


    // Aktionen, Ereigniss und Effekte (8)

    // Drehung
    public String getRotation() {
        return rotation;
    }
    public void setRotation(String rotation) {
        this.rotation = rotation;
    }

    // pick damage
    public List<String> getAvailablePiles() {
        return availablePiles;
    }
    public void setAvailablePiles(List<String> availablePiles) {
        this.availablePiles = availablePiles;
    }

    // Animation
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    // Energie
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    // checkpoints
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}
