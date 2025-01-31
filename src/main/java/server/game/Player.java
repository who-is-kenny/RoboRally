package server.game;

import server.ClientHandler;
import server.message.ActiveCard;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;

public class Player {

    //todo after one round the register can be cleared, at the drawing all cards get immediately added to the discharge pile

    private String playerName;
    private final Robot robot;
    private ProgrammingCardDeck playerProgrammingCards;
    private ArrayList<Cards> playerHand = new ArrayList<>();
    private final Register playerRegister = new Register();
    private int checkpoint = 0; // counting up check is it is the next checkpoint
    private double distanceFromAntenna;
    private boolean finishedProgramming; //[timo, 8.12.] deprecated
    private ClientHandler clientHandler;
    private int orderNumber;

    public Player(String playerName, Robot robot){
        this.playerName = playerName;
        this.robot = robot;
        this.playerProgrammingCards = new ProgrammingCardDeck(); // here deck is already initialized
    }
    //[timo, 6.12.] clientHandler is set for access to client sent cards, register etc.
    //it is not done during initialization so AI does not neet do provide a clientHandler

    public int getOrderNumber(){
        return orderNumber;
    }

    public void setOrderNumber(int number){
        orderNumber = number;
    }

    /**
     * sets the clientHandler for the player to send and get data
     * @param clientHandler of the players client
     */
    public void setClientHandler(ClientHandler clientHandler){
        this.clientHandler = clientHandler;
    }

    //deprecated
    public void setFinishedProgramming(boolean isFinished){
        finishedProgramming = isFinished;
    } //[timo, 8.12.] not necessary

    /**
     * wrapper to clientHandler.switchToProgrammingPhase()
     */
    public void startProgrammingPhase(){
        this.clientHandler.switchToProgrammingPhase();
        this.clientHandler.setProgrammingFlag();
        this.clientHandler.setTimerStatus(false);
    }

    public void passSwitchToAktivierungsPhase(){
        this.clientHandler.switchToAktivierungPhase();
        this.clientHandler.sendTimerEnded(null);
    }

    /**
     * only needs to be called once, sends Aktivierungsphase message to client
     */
    public void startActivationPhase(){
        clientHandler.switchToAktivierungPhase();
    }

    /**
     * reads the status directly from the clientHandler
     * @return True iff the client has finished programming
     */
    public boolean isFinishedProgramming(){
        return this.clientHandler.isFinishedProgramming();
    }

    /**
     *
     * @return the logical distance from the antenna to compute turn order
     */
    public double getDistanceFromAntenna(){
        return distanceFromAntenna;
    }

    public void calculateDistanceToAntenna(Position antennaPosition){
        distanceFromAntenna = returnDistanceRobotAntenna(antennaPosition);
    }

    private double returnDistanceRobotAntenna(Position antennaPosition){
        return euclideanDistance(antennaPosition);
    }

    private double euclideanDistance(Position antennaPosition){
        return sqrt(
                (robot.getRobotPosition().getPositionX() - antennaPosition.getPositionX())^2 +
                (robot.getRobotPosition().getPositionY() - antennaPosition.getPositionY())^2);
    }

    public int getCheckpoint(){
        return checkpoint;
    }

    public void setCheckpoint(int newCheckPoint){
        checkpoint = newCheckPoint;
    }

    public void addCheckpoint(){
        checkpoint++;
        System.out.println("player has: " + checkpoint + " checkpoints.");
    }

    //player gets his 20 programming cards then the get shuffled + discharge pile // not needed right now
    public void setUpPlayer(){
        playerProgrammingCards = new ProgrammingCardDeck();
    }

    //get player name
    public String getPlayerName(){
        return playerName;
    }

    //set player name
    public void setPlayerName(String newPlayerName){
        playerName = newPlayerName;
    }

    //ger energy cubes amount
    public int getEnergyCubes(){
        return robot.getEnergy();
    }

    //clear player register
    public void clearPlayerRegister(){
        playerRegister.clearRegister();
    }

    //clear player hands
    public void clearPlayerHandcards(){
        playerHand.clear();
    }

    //set energy cubes
    public void setEnergyCubes(int newAmountOfEnergyCubes){
        //clientHandler.sendEnergyMessage(this.clientHandler.getClientID(), 5, "game setup"); // removed
        robot.setEnergy(newAmountOfEnergyCubes);
    }

    public void passMovementMessage(Position newPosition){
        this.clientHandler.sendMovementMessage(this.clientHandler.getClientID(), newPosition.getPositionX(), newPosition.getPositionY());
    }

    //return programming deck
    public ProgrammingCardDeck getPlayerProgrammingCards(){
        return playerProgrammingCards;
    }

    //test function
    public int getCurrentRegisterRound(){
        return Game.getInstance().getCurrentRegisterRound();
    }

    //play the card in the register
    public void playRegisterCard(int registerNumber){
        //playerRegister.getCardInRegister(registerNumber).playCardEffect(this);
        String cardName = (playerRegister.getCardInRegister(registerNumber).getCardName());
        String previousCard;
        if (registerNumber == 0){
            previousCard = null;
        }else{
            previousCard = (playerRegister.getCardInRegister(registerNumber -1 ).getCardName());
        }
        System.out.println("PLAYER.playRegisterCard(" + registerNumber + ") reads Card " + cardName);
        Cards randomCard = playerProgrammingCards.getFirstCard();
        switch (cardName){
                case "spam":
                    this.clientHandler.sendReplaceCard(registerNumber, Cards.cardToStringForClient(randomCard), this.clientHandler.getClientID());
                    playCardsAndSendMessageToClient(randomCard.getCardName() , previousCard);
                    playerProgrammingCards.removeFromDeckAndDiscard(randomCard);
                    playerProgrammingCards.removeDamage(cardName);//remove damage after programming it

                    break;
                case "worm":
                    Position rebootPosition = Course.getInstance().getRebootPoints().get(0).getPosition();
                    this.getRobot().rebootRobot(rebootPosition);
                    playerProgrammingCards.removeDamage(cardName);//remove damage after programming it
//                    this.clientHandler.sendReplaceCard(registerNumber, Cards.cardToStringForClient(randomCard), this.clientHandler.getClientID());
//                    playCardsAndSendMessageToClient(randomCard.getCardName()  , previousCard);
//                    playerProgrammingCards.removeFromDeckAndDiscard(randomCard);
                    break;
//                case "again":
//                    this.clientHandler.sendReplaceCard(registerNumber, Cards.cardToStringForClient(randomCard), this.clientHandler.getClientID());
//                    playCardsAndSendMessageToClient(randomCard.getCardName()  , previousCard);
//                    break;
                case "trojanhorse":
                    // trojan horse effect
                    Game.getInstance().givePlayerSpamCard(this);
                    Game.getInstance().givePlayerSpamCard(this);

                    this.clientHandler.sendReplaceCard(registerNumber, Cards.cardToStringForClient(randomCard), this.clientHandler.getClientID());
                    playCardsAndSendMessageToClient(randomCard.getCardName()  , previousCard);
                    playerProgrammingCards.removeFromDeckAndDiscard(randomCard);

                    playerProgrammingCards.removeDamage(cardName);//remove damage after programming it

                    break;
                case "virus":
                    //virus effect
                    ArrayList<Player> tempPlayers = Game.getInstance().getPlayersInGame();
                    int x1 = this.getRobot().getRobotPosition().getPositionX() + 6;
                    int x2 = this.getRobot().getRobotPosition().getPositionX() - 6;
                    int y1 = this.getRobot().getRobotPosition().getPositionY() + 6;
                    int y2 = this.getRobot().getRobotPosition().getPositionY() - 6;
                    for (Player player : tempPlayers){
                        if (player != this){
                            int tempx = player.getRobot().getRobotPosition().getPositionX();
                            int tempy = player.getRobot().getRobotPosition().getPositionY();
                            if ( (x2<= tempx && tempx <=x1)|| y2 <= tempy && tempy <= y1){
                                Game.getInstance().givePlayerSpamCard(this);
                            }
                        }
                    }

                    this.clientHandler.sendReplaceCard(registerNumber, Cards.cardToStringForClient(randomCard), this.clientHandler.getClientID());
                    playCardsAndSendMessageToClient(randomCard.getCardName()  , previousCard);
                    playerProgrammingCards.removeFromDeckAndDiscard(randomCard);

                    playerProgrammingCards.removeDamage(cardName);//remove damage after programming it

                    break;
                default:
                    playCardsAndSendMessageToClient(cardName  , previousCard);
            }
        //handle damage cards and again switch cases
    }

    //new method
    private void playCardsAndSendMessageToClient(String cardName , String previousCard){
        System.out.println("PLAYER.playCardsAndSendMessageToClient(\"" + cardName + "\")");
        if(cardName.equals("move1")|| cardName.equals("move2") || cardName.equals("move3")) {
            //System.out.println("PLAYER.playCardsAndSendMessageToClient(): detected move" + Integer.parseInt(cardName.substring(4, 5)));
            for (int i = 0; i < Integer.parseInt(cardName.substring(4, 5)); i++) {
                this.robot.robotMovement(this.robot, 1);
            }
        } else if (cardName.equals("moveback")){
            this.robot.robotMovement(this.robot, -1);
        }else if(cardName.equals("leftturn")){
            this.robot.rotate(270);
        }else if ( cardName.equals("rightturn")) {
            this.robot.rotate(90);
            //Client uses clockwise, counterclockwise. Robot.facingDirection not applicable here
            //clientHandler.sendRotationMessage(this.clientHandler.getClientID(), Direction.convertTurnStringForClient(cardName));
        }else if(cardName.equals("uturn")){
            this.robot.rotate(180);
            //clientHandler.sendRotationMessage(this.clientHandler.getClientID(), "clockwise");
            //clientHandler.sendRotationMessage(this.clientHandler.getClientID(), "clockwise");
        }else if(cardName.equals("powerup")){
            this.robot.addOneEnergy();
        } else if (cardName.equals("again")) {
            playCardsAndSendMessageToClient(previousCard, null);
        }

    }

    /**
     * propagates Protocol 7.4(1) CurrentCards Message type. Is only to be called for one player
     * @param activeCards
     */
    public void sendCurrentCardsToClient(ArrayList<ActiveCard> activeCards){
        this.clientHandler.sendCurrentCards(activeCards);
    }
    //draw the nine handcards

    /**
     * draws 9 cards from programmingCardDeck and sends them to the client
     */
    public void drawHandCards(){
        playerHand = playerProgrammingCards.drawHandCards();
        ArrayList<String> cards = convertTypeCardsToString(playerHand);
        System.out.println("PLAYER.drawHandCards(): dealt: " + cards + " to " + this.playerName);
        clientHandler.sendYourCards(cards);
        clientHandler.sendNotYourCards(this.clientHandler.getClientID());
        //send all the cards to the client
        /*
        for(Cards handcards : playerHand){
            clientHandler.sendYourCards(convertTypeCardsToString(playerHand));
        }
         */
    }

    //new method for correct type, listarray<cards> to list<string>
    private ArrayList<String> convertTypeCardsToString(ArrayList<Cards> cards){
        ArrayList<String> playerHandString = new ArrayList<>();
        for(Cards playerCards : cards){
            playerHandString.add(Cards.cardToStringForClient(playerCards));
        }
        return playerHandString;
    }

    //get corresponding player roboter
    public Robot getPlayerRobot(){
        return robot;
    }

    //play card in register with register number and cardname
    //[timo, 8.12.] isnt this replaced by playRegisterCard()???
    public void playCardInRegister(int registerNumber, String cardName){
        playerRegister.playCardInRegister(registerNumber, getCardFromName(cardName));
    }
    //deprecated
    private Cards getCardFromName(String cardName){
        switch (cardName){
            case "move1":
                return new Move1ForwardCard();
            case "move2":
                return new Move2ForwardCard();
            case "move3":
                return new Move3ForwardCard();
            case "moveback":
                return new Move1BackwardCard();
            case "leftturn":
                return new LeftTurnCard();
            case "rightturn":
                return new RightTurnCard();
            case "uturn":
                return new UTurnCard();
            case "again":
                return new AgainCard();
            case "powerup":
                return new PowerUpCard();
            default:
                System.out.println("cardtype doesnt exist");
        }
        return new MoveCard(1); //error
    }

    //get card from register with register number
    public Cards getCardFromRegister(int registerNumber){
        return playerRegister.getCardInRegister(registerNumber);
    }

    //get cardname from register with register number
    public String getCardNameFromRegister(int registerNumber){
        return playerRegister.getCardInRegister(registerNumber).getCardName();
    }

    //add damage cards to the pile of players
    public void addCardToDischargeDeck(Cards card){
        playerProgrammingCards.addCardToDischargeDeck(card);
    }

    // added by Timo for Game.showScore()
    public String getName() {
        return this.playerName;
    }

    /**
     * gets cards in the register sent by client and buffered in clientHandler.
     * Fills null Cards with random cards from the players' hand and sends them to the client.
     * Stores the resulting register in Player.playerRegister.
     */
    public void loadRegisterFromClient(){
        ArrayList<String> cards = this.clientHandler.getCardsInHand();
        ArrayList<String> randomCards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            this.playerRegister.playCardInRegister(i, Cards.stringToCardForClient(cards.get(i)));
            if (this.playerRegister.getCardInRegister(i) == null){
                //creates an ArrayList of playerHand without registerCards, chooses a random one and sets it to the null entry
                /*ArrayList<Cards> notPlayedCards = notPlayedCards();
                Cards card = notPlayedCards.get(ThreadLocalRandom.current().nextInt(0, notPlayedCards.size()));
                this.playerRegister.playCardInRegister(i, card);
                randomCards.add(Cards.cardToStringForClient(card));*/
                Cards card = this.playerProgrammingCards.getFirstCardFromDeck();
                this.playerRegister.playCardInRegister(i, card);
                randomCards.add(Cards.cardToStringForClient(card));
            }
        }
        for(int i = 0; i<5; i++){
            randomCards.add(Cards.cardToStringForClient(this.playerRegister.getCardInRegister(i)));
        }

        this.clientHandler.sendCardsYouGotNow(randomCards);
        this.clientHandler.resetCardsInHand();
    }

    // returns Player.handCards/player.Register
    private ArrayList<Cards> notPlayedCards(){
        ArrayList<Cards> notPlayedCards = (ArrayList<Cards>) playerHand.clone();
        for (int i = 0; i < 5; i++) {
            notPlayedCards.remove(playerRegister.getCardInRegister(i));
        }
        return notPlayedCards;
    }

    public int getClientID(){
        return this.clientHandler.getClientID();
    }

    //get player hand
    public ArrayList<Cards> getPlayerHand(){
        return playerHand;
    }

    public Robot getRobot() {
        return robot;
    }

    public boolean timerEnded(){
        return this.clientHandler.getTimerStatus();
    }

    public Register getPlayerRegister(){
        return this.playerRegister;
    }

    public void passTimerEndedMessage(){
        this.clientHandler.shutdownTimer();
        this.clientHandler.sendTimerEnded(null);
    }

    public void passEnergyFromCardMessage(){
        clientHandler.sendEnergyMessage(this.clientHandler.getClientID(), 1, "powerup card");
    }

    public void passTurnCWMessage(){
        clientHandler.sendRotationMessage(this.clientHandler.getClientID(), "clockwise");
    }

    public void passTurnCCWMessage(){
        clientHandler.sendRotationMessage(this.clientHandler.getClientID(), "counterclockwise");
    }

    public void passRebootMessage(){
        clientHandler.sendRebootMessage(this.clientHandler.getClientID());
    }

    public void passEnergySpaceMessage(){
        clientHandler.sendEnergyMessage(this.clientHandler.getClientID(),1,"energy space");
    }

    public void passPickDamageMessage(){
        clientHandler.sendPickDamageMessage(1, List.of("Worm" , "Virus" , "TrojanHorse"));
    }

    public void passCheckPointMessage(int checkpoint){
        clientHandler.sendCheckPointMessage(this.clientHandler.getClientID(),checkpoint);
    }

    public void passGameFinishedMessage(int winnerID){
        clientHandler.sendGameOverMessage(winnerID);
    }

    public void passDrawDamage(){
        clientHandler.sendDamageMessage(this.clientHandler.getClientID(),List.of()); // sends and empty list
    }

    public void passShootLaser(){
        clientHandler.sendAnimationMessage("laser");
    }

    public void passTwisterCheckpoint(int checkpoint , int x, int y){
        clientHandler.sendTwisterAnimationMessage(checkpoint,x,y);
    }


}
