package server.message;

import com.google.gson.*;

import java.lang.reflect.Type;

public class MessageSerializer implements JsonSerializer<Message> {

    @Override
    public JsonElement serialize(Message message, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        // Add  messageType field
        jsonObject.addProperty("messageType", message.getMessageType());

        // Serialize  messageBody based on messageType
        MessageBody messageBody = message.getMessageBody();
        if (messageBody != null) {
            JsonObject messageBodyJson = new JsonObject();

            switch (message.getMessageType()) {
                case "HelloClient":
                    messageBodyJson.addProperty("protocol", messageBody.getProtocol());
                    break;
                case "Alive":
                    // Empty message body, nothing to add
                    break;

                case "HelloServer":
                    if (messageBody.getGroup() != null) {
                        messageBodyJson.addProperty("group", messageBody.getGroup());
                    }
                    if (messageBody.getAI() != null) {
                        messageBodyJson.addProperty("isAI", messageBody.getAI());
                    }
                    if (messageBody.getProtocol() != null) {
                        messageBodyJson.addProperty("protocol", messageBody.getProtocol());
                    }
                    break;

                case "Welcome":
                    messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    break;

                case "PlayerValues":
                    if (messageBody.getName() != null) {
                        messageBodyJson.addProperty("name", messageBody.getName());
                    }
                    messageBodyJson.addProperty("figure", messageBody.getFigure());
                    break;

                case "PlayerAdded":
                    messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    if (messageBody.getName() != null) {
                        messageBodyJson.addProperty("name", messageBody.getName());
                    }
                    messageBodyJson.addProperty("figure", messageBody.getFigure());
                    break;

                case "SetStatus":
                    messageBodyJson.addProperty("ready", messageBody.isReady());
                    break;

                case "PlayerStatus":
                    messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    messageBodyJson.addProperty("ready", messageBody.isReady());
                    break;

                case "SelectMap":
                    if (messageBody.getAvailableMaps() != null && !messageBody.getAvailableMaps().isEmpty()) {
                        messageBodyJson.add("availableMaps", context.serialize(messageBody.getAvailableMaps()));
                    }
                    break;

                case "MapSelected":
                    if (messageBody.getMap() != null) {
                        messageBodyJson.addProperty("map", messageBody.getMap());
                    }
                    break;
                case "SendChat":
                    if (messageBody.getMessage() != null) {
                        messageBodyJson.addProperty("message", messageBody.getMessage());
                    }
                    messageBodyJson.addProperty("to", messageBody.getTo());
                    break;

                case "ReceivedChat":
                    if (messageBody.getMessage() != null) {
                        messageBodyJson.addProperty("message", messageBody.getMessage());
                    }
                    messageBodyJson.addProperty("from", messageBody.getFrom());
                    messageBodyJson.addProperty("isPrivate", messageBody.isPrivate());
                    break;
                case "Error":
                    if (messageBody.getError() != null) {
                        messageBodyJson.addProperty("error", messageBody.getError());
                    }
                    break;

                case "PlayCard":
                    if (messageBody.getCard() != null) {
                        messageBodyJson.addProperty("card", messageBody.getCard());
                    }
                    break;

                case "CardPlayed":
                    messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    if (messageBody.getCard() != null) {
                        messageBodyJson.addProperty("card", messageBody.getCard());
                    }
                    break;

                case "CurrentPlayer":
                    messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    break;

                case "ActivePhase":
                    messageBodyJson.addProperty("phase", messageBody.getPhase());
                    break;

                case "SetStartingPoint":
                    messageBodyJson.addProperty("x", messageBody.getX());
                    messageBodyJson.addProperty("y", messageBody.getY());
                    break;

                case "StartingPointTaken":
                    messageBodyJson.addProperty("x", messageBody.getX());
                    messageBodyJson.addProperty("y", messageBody.getY());
                    if (messageBody.getDirection() != null) {
                        messageBodyJson.addProperty("direction", messageBody.getDirection());
                    }
                    messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    break;
                case "YourCards":
                    if (messageBody.getCardsInHandAsList() != null) {
                        messageBodyJson.add("cardsInHand", context.serialize(messageBody.getCardsInHandAsList()));
                    }
                    break;

                case "NotYourCards":
                    if (messageBody.getCardsInHandAsInteger() != null) {
                        messageBodyJson.addProperty("cardsInHand", messageBody.getCardsInHandAsInteger());
                    }
                    break;
                case "ShuffleCoding":
                    messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    break;

                case "SelectedCard":
                    if (messageBody.getCard() != null) {
                        messageBodyJson.addProperty("card", messageBody.getCard());
                    }
                    messageBodyJson.addProperty("register", messageBody.getRegister());
                    break;

                case "CardSelected":
                    messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    messageBodyJson.addProperty("register", messageBody.getRegister());
                    messageBodyJson.addProperty("filled", messageBody.isFilled());
                    break;

                case "SelectionFinished":
                    messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    break;

                case "TimerStarted":
                    // Empty message body, nothing to add
                    break;

                case "TimerEnded":
                    if (messageBody.getClientIDs() != null && !messageBody.getClientIDs().isEmpty()) {
                        messageBodyJson.add("clientIDs", context.serialize(messageBody.getClientIDs()));
                    }
                    break;

                case "CardsYouGotNow":
                    if (messageBody.getCards() != null && !messageBody.getCards().isEmpty()) {
                        messageBodyJson.add("cards", context.serialize(messageBody.getCards()));
                    }
                    break;
                case "CurrentCards":
                    if (messageBody.getActiveCards() != null && !messageBody.getActiveCards().isEmpty()) {
                        JsonArray activeCardsArray = new JsonArray();
                        for (ActiveCard activeCard : messageBody.getActiveCards()) {
                            JsonObject activeCardJson = new JsonObject();
                            activeCardJson.addProperty("clientID", activeCard.getClientID());
                            activeCardJson.addProperty("card", activeCard.getCard());
                            activeCardsArray.add(activeCardJson);
                        }
                        messageBodyJson.add("activeCards", activeCardsArray);
                    }
                    break;

                case "ReplaceCard":
                    messageBodyJson.addProperty("register", messageBody.getRegister());
                    if (messageBody.getNewCard() != null) {
                        messageBodyJson.addProperty("newCard", messageBody.getNewCard());
                    }
                    messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    break;
                case "Movement":
                    messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    messageBodyJson.addProperty("x", messageBody.getX());
                    messageBodyJson.addProperty("y", messageBody.getY());
                    break;

                case "PlayerTurning":
                    messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    if (messageBody.getRotation() != null) {
                        messageBodyJson.addProperty("rotation", messageBody.getRotation());
                    }
                    break;
                case "DrawDamage":
                    // Handle DrawDamage case
                    if (messageBody.getClientID() != -1) {
                        messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    }
                    if (messageBody.getCards() != null && !messageBody.getCards().isEmpty()) {
                        messageBodyJson.add("cards", context.serialize(messageBody.getCards()));
                    }
                    break;

                case "PickDamage":
                    // Handle PickDamage case
                    messageBodyJson.addProperty("count", messageBody.getCount());
                    if (messageBody.getAvailablePiles() != null && !messageBody.getAvailablePiles().isEmpty()) {
                        messageBodyJson.add("availablePiles", context.serialize(messageBody.getAvailablePiles()));
                    }
                    break;

                case "SelectedDamage":
                    // Handle SelectedDamage case
                    if (messageBody.getCards() != null && !messageBody.getCards().isEmpty()) {
                        messageBodyJson.add("cards", context.serialize(messageBody.getCards()));
                    }
                    break;


                case "Animation":
                    if (messageBody.getType() != null) {
                        messageBodyJson.addProperty("type", messageBody.getType());
                    }
                    break;

                case "Reboot":
                    messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    break;

                case "RebootDirection":
                    if (messageBody.getDirection() != null) {
                        messageBodyJson.addProperty("direction", messageBody.getDirection());
                    }
                    break;

                case "Energy":
                    messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    messageBodyJson.addProperty("count", messageBody.getCount());
                    if (messageBody.getSource() != null) {
                        messageBodyJson.addProperty("source", messageBody.getSource());
                    }
                    break;

                case "CheckPointReached":
                    messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    messageBodyJson.addProperty("number", messageBody.getNumber());
                    break;

                case "GameFinished":
                    messageBodyJson.addProperty("clientID", messageBody.getClientID());
                    break;
                case "GameStarted":
                    if (messageBody.getGameMap() != null) {
                        messageBodyJson.add("gameMap", context.serialize(messageBody.getGameMap()));
                    }
                    break;


                default:
                    throw new IllegalArgumentException("Unknown messageType: " + message.getMessageType());
            }

            jsonObject.add("messageBody", messageBodyJson);
        }

        return jsonObject;
    }
}
