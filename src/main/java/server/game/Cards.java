package server.game;

public abstract class Cards {
    private String cardName;

    public Cards(String cardName){
        this.cardName = cardName;
    }

    //play card effect
    public void playCardEffect(Player player){
        //card effect
    }

    /**card names are taken from clientHandler.sampleCards
     *
     * @param cardName string to be converted into card.
     * @return the card read from the string, null if name is not defined
     */
    public static Cards stringToCardForClient(String cardName){
        System.out.println("CARDS.stringToCardForClient(" + cardName + ")");
        switch (cardName){
            case "MoveI":
                return new Move1ForwardCard();
            case "MoveII":
                return new Move2ForwardCard();
            case "MoveIII":
                return new Move3ForwardCard();
            case "TurnLeft":
                return new LeftTurnCard();
            case "TurnRight":
                return new RightTurnCard();
            case "Again":
                System.out.println("returns new AgainCard()");
                return new AgainCard();
            case "UTurn":
                System.out.println("returns new UTurnCard()");
                return new UTurnCard();
            case "BackUp":    // wtf
                System.out.println("returns new Move1BackwardCard()");
                return new Move1BackwardCard();
            case "PowerUp":
                System.out.println("returns new PowerUpCard()");
                return new PowerUpCard();
            case "Spam":
                return new SpamCard();
            case "Virus":
                return new VirusCard();
            case "TrojanHorse":
                return new TrojanHorseCard();
            case "Worm":
                return new WormCard();
            default:
                return null;
        }
    }

    /**
     * translates a card object to a String understandable by the client defined in clientHandler.sampleCards
     * @param card to be translated
     * @return String for client
     */
    public static String cardToStringForClient(Cards card){
        switch(card.getCardName()){
            case "moveback":
                return "BackUp";
            case "move1":
                return "MoveI";
            case "move2":
                return "MoveII";
            case "move3":
                return "MoveIII";
            case "uturn":
                return "UTurn";
            case "leftturn":
                return "TurnLeft";
            case "rightturn":
                return "TurnRight";
            case "powerup":
                return "PowerUp";
            case "again":
                return "Again";
            case "spam":
                return "Spam";
            case "trojanhorse":
                return "TrojanHorse";
            case "virus":
                return "Virus";
            case "worm":
                return "Worm";
            default:
                return null;

        }
    }

    //get name of the card
    public String getCardName(){
        return cardName;
    }

    //set card name
    public void setCardName(String newCardName){
        cardName = newCardName;
    }

}
