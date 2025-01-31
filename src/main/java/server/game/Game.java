package server.game;

import content.CourseNameEnum;
import content.OrientationEnum;
import content.RobotNameEnum;
import server.ClientHandler;
import server.game.celltypes.*;
import server.message.ActiveCard;
import server.message.MessageBody;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game implements Runnable {
    private static final int TURN_DURATION_SECONDS = 30;
    private Timer turnTimer;
    private boolean turnTimerExpired;
    private final ArrayList<Player> playersInGame = new ArrayList<>();
    private final ArrayList<Player> playerOrder = new ArrayList<>();

    private int spamCards;
    private int virusCards;
    private int  wormCards;
    private int trojanHorse;

    /*
    private final ArrayList<Cards> spamCards = new ArrayList<>();
    private final ArrayList<Cards> virusCards = new ArrayList<>();
    private final ArrayList<Cards> wormCards = new ArrayList<>();
    private final ArrayList<Cards> trojanHorse = new ArrayList<>();
    */

    private boolean gameIsOver; //=false;
    private int winnerID;
    private int totalCheckPoints;
    private boolean programmingPhase;
    private static Game INSTANCE;

    private int roundCounter = 0;

    public String getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(String selectedMap) {
        this.selectedMap = selectedMap;
    }

    private String selectedMap;

    private Position[][] twisterArray = new Position[4][4];


    public static Game getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Game();
        }
        return INSTANCE;
    }

    public void removeData() {
        INSTANCE = new Game();
    }

    private boolean gameIsOver() {
        int currentCheckpointIndex = 0;

        for (Player player : playersInGame) {
            if (player.getCheckpoint() == currentCheckpointIndex) {
                currentCheckpointIndex++;
            } else {
                return false;
            }
        }

        return false;
    }


    private int registerRound;

    private int gamePhase; //

    @Override
    public void run() {
        setUpGame();
        while (!gameIsOver) {
            startNewRound();
        }
        // passes game over message when game is finished.
        System.out.println("game over sending messages to all players");
        for (Player player : playersInGame){
            player.passGameFinishedMessage(winnerID);
        }
    }
    private Game(){
    }
    public void initialize(ArrayList<ClientHandler> clientHandlers){
        //[tim0, 6.12.] maps the course naming from ClientHandler to the CourseNameEnum. might be removed later
        Hashtable<String, CourseNameEnum> courseMapper = new Hashtable();

        for (ClientHandler clientHandler : clientHandlers) {
            Player player = new Player( //when the player is initialized the carddeck is also initialized in the constructor of the player
                    clientHandler.getName(),
                    new Robot(RobotNameEnum.values()[clientHandler.getRobotID()],
                            clientHandler.getStartingPosition()));
            player.getRobot().setPlayerThatOwnsThisRobot(player);
            player.setClientHandler(clientHandler);
            if (clientHandler.getSelectedMap().equals("DeathTrap")){
                player.getRobot().setOrientation(OrientationEnum.L);
            }else{
                player.getRobot().setOrientation(OrientationEnum.R);
            }

            setSelectedMap(clientHandler.getSelectedMap());

            // initialise total number of check points for map
            switch (clientHandler.getSelectedMap()){
                case "DeathTrap":
                    totalCheckPoints = 5;
                    break;
                case "DizzyHighway":
                    totalCheckPoints = 1;
                    break;
                case "ExtraCrispy", "LostBearings","Twister":
                    totalCheckPoints = 4;
                    break;
            }

            if (clientHandler.getSelectedMap().equals("Twister")){
                twisterArray[0][0] = new Position(10,1);
                twisterArray[0][1] = new Position(6,7);
                twisterArray[0][2] = new Position(5,3);
                twisterArray[0][3] = new Position(9,7);
                twisterArray[1][0] = new Position(11,2);
                twisterArray[1][1] = new Position(5,8);
                twisterArray[1][2] = new Position(4,2);
                twisterArray[1][3] = new Position(10,6);
                twisterArray[2][0] = new Position(10,3);
                twisterArray[2][1] = new Position(4,7);
                twisterArray[2][2] = new Position(5,1);
                twisterArray[2][3] = new Position(11,7);
                twisterArray[3][0] = new Position(9,2);
                twisterArray[3][1] = new Position(5,6);
                twisterArray[3][2] = new Position(6,2);
                twisterArray[3][3] = new Position(10,8);
            }
//            player.getRobot().setOrientation(OrientationEnum.getFromString(clientHandler.getFacingDirection()));
            this.playersInGame.add(player);
            if (clientHandler.getSelectedMap() != null){
                try {
                    Course course = Course.getInstance();
                    course.init(CourseNameEnum.getFromString(clientHandler.getSelectedMap()));

                } catch (Exception e){
                    System.out.println("ERROR: Map could not be initialized:");
                    e.printStackTrace();
                }
            }
        }
        //give every player their start amount of energycubes
        setEnergyCubesForAllPlayer();
        //init damage cards for the game
        setUpDamageCards(); //maybe make them just integers
        //deal player cards
        //dealPlayerCards(); [timo, 8.12.] moved to gameloop since this gets executed every round
        //begin of programming phase
        //programmingPhase = true;
    }

    public void dealPlayerCards(){
        for(Player players : playersInGame){
            players.drawHandCards();//9 cards will be drawn
        }
    }

    public Game(ArrayList<ClientHandler> clientHandlers) {
        //need server as obj in this class for handling sockets input and output ??? -> [timo, 6.12.] handled by initialize
    }

    //not needed information comes from client and done in initialize
    private void selectRobots() {
        //player set robots
        //get json obj from client??? and read it and set it for each player ???
    }

    //also not needed done in initialize
    private void chooseRacingCourse() {


    }

    //not needed done in initialize
    private void setRacingCourse(String racingCourseName) {
        //set the racing course/ load it in the Gameboard
    }

    //not needed initialize replaces it
    private void setUpGame() {
        //[timo, 6.12.] already implemented in Game.initialize()
        // chooseRacingCourse();
        //selectRobots();
        System.out.println("GAME.setupGame() starting, Game is initialized. Registered Players:");
        for (Player p: playersInGame) {
            System.out.println(p.getName());
        }
        setUpPlayerCards();
        //special Programming cards
        setUpDamageCards();
        setEnergyCubesForAllPlayer();
        placeRobotStartPosition();
        System.out.println("GAME.setupGame() done entering Game.programmingPhase()");
        //temporary for testing, would then be put in Game.run() in the loop:

        gameIsOver = false;

    }

    //also not needed done in initialize
    public void placeRobotStartPosition() {

    }


    //every player gets 5 energy cubes, start of game
    private void setEnergyCubesForAllPlayer() {
        for (Player players : playersInGame) {
            players.setEnergyCubes(5);
        }
    }

    //not needed right now
    private void setUpUpgradeCards() {
        //which upgrade cards todo later
    }

    //set up the damage cards (worm, spam, virus, trojan)
    private void setUpDamageCards() {
        spamCards = 38;
        virusCards = 18;
        trojanHorse = 12;
        wormCards = 6;

        /*
        for (int i = 0; i < 38; i++) {
            spamCards.add(new SpamCard());
        }
        for (int i = 0; i < 18; i++) {
            virusCards.add(new VirusCard());
        }
        for (int i = 0; i < 12; i++) {
            trojanHorse.add(new TrojanHorseCard());
        }
        for (int i = 0; i < 6; i++) {
            wormCards.add(new WormCard());
        }
        */
    }

    //each player in game receives his programming cards + discharge pile, not needed happening already when creating new Player
    private void setUpPlayerCards() {
        for (Player players : playersInGame) {
            players.setUpPlayer();
        }
    }

    //next thing DONE
    private void programmingPhase() {
        programmingPhase = true;

        // Initialize players for the programming phase
        for (Player p : this.playersInGame) {
            p.getRobot().setRebootRobot(false);
            p.drawHandCards();
            p.startProgrammingPhase();
        }

        System.out.println("GAME.programmingPhase(): waiting for clients to return their registers");

        // Create a scheduled executor to handle the timer
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        synchronized (this) {
            while (!someoneFinishedProgramming()) { // Wait for at least one player to finish
                try {
                    wait(1000); // Check every second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // Start a timer that will execute after 30 seconds
        scheduler.schedule(() -> {
            System.out.println("GAME.programmingPhase(): Timer expired!");
            synchronized (this) {
                notifyAll(); // Wake up the waiting thread
            }
        }, 30, TimeUnit.SECONDS);

        // Wait until either the timer expires or all players finish programming
        synchronized (this) {
            while (!allFinishedProgramming()) {
                try {
                    wait(1000); // Check every second if all players are done
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // If all players finished before the timer, cancel the scheduled task
        scheduler.shutdownNow();

        System.out.println("GAME.programmingPhase(): detected all players finished or timer ran out. Reading registers...");

        for (Player p : this.playersInGame) {
            p.loadRegisterFromClient();
            System.out.println(p.getName() + ": " + p.getPlayerRegister().toString());
            p.passTimerEndedMessage();
        }

        programmingPhase = false;
    }

    private boolean programmingTimerEnded(){
        for (Player p: this.playersInGame){
            if (!p.timerEnded()){
                return false;
            }
        }
        return true;
    }

    private boolean allFinishedProgramming(){
        for (Player p: this.playersInGame) {
            if (!p.isFinishedProgramming()){

                return false;
            }
        }
        return true;
    }

    private boolean someoneFinishedProgramming(){
        for (Player p: this.playersInGame) {
            if (p.isFinishedProgramming()){
                return true;
            }
        }
        return false;
    }


    private void playTileEffect(int registerRound){
        Course course = Course.getInstance();
        roundCounter++; //for twister
        if (selectedMap.equals("Twister")){
            for(Player player : playersInGame){
                int index = roundCounter % 4;
                player.passTwisterCheckpoint(1,twisterArray[index][0].getPositionX(),twisterArray[index][0].getPositionY());
                player.passTwisterCheckpoint(2,twisterArray[index][1].getPositionX(),twisterArray[index][1].getPositionY());
                player.passTwisterCheckpoint(3,twisterArray[index][2].getPositionX(),twisterArray[index][2].getPositionY());
                player.passTwisterCheckpoint(4,twisterArray[index][3].getPositionX(),twisterArray[index][3].getPositionY());
            }
        }

        for(Player player : playersInGame){
            Cell cell = course.getCellAtPosition(player.getRobot().getRobotPosition());
            System.out.println("robot at:" + player.getRobot().getRobotPosition().getPositionX() + ", " + player.getRobot().getRobotPosition().getPositionY());
            System.out.println("GAME.playTileEffect(): Player sitting on " + cell.toString());
            if(cell instanceof Laser){
                ((Laser) cell).applyEffect(player);
            } else if (cell instanceof Pit){
                ((Pit) cell).robotMovement(player.getRobot());
            } else if (cell instanceof RebootPoint){
                ((RebootPoint) cell).applyEffect(player);
            } else if (cell instanceof ConveyerBelt) {
                ((ConveyerBelt) cell).robotMovement(player.getRobot());
            } else if (cell instanceof ConveyerBeltWithTurn) {
                ((ConveyerBeltWithTurn) cell).robotMovement(player.getRobot());
            } else if (cell instanceof ConveyerBeltWithLaser) {
                ((ConveyerBeltWithLaser) cell).robotMovement(player.getRobot());
            } else if (cell instanceof WallWithLaser) {
                ((WallWithLaser) cell).robotMovement(player.getRobot());
            } else if (cell instanceof Gear){
                ((Gear) cell).robotMovement(player.getRobot());
            } else if (cell instanceof Checkpoint){
                ((Checkpoint) cell).applyEffect(player);
            } else if (cell instanceof EnergySpace) {
                ((EnergySpace) cell).robotMovement(player.getRobot());
            }else if(cell instanceof PushPanel){
                ((PushPanel) cell).pushRobot(player.getRobot() , registerRound);
            }
            try{
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        }

        // check if player i standing on checkpoint on twister
        if (getSelectedMap().equals("Twister")){
            for(Player player : playersInGame){
                for(int i=0; i<4; i++ ){
                    if (player.getCheckpoint()+1 == i && player.getRobot().getRobotPosition() == twisterArray[roundCounter % 4][i]){
                        player.addCheckpoint();
                        player.passCheckPointMessage(i);
                        Game.getInstance().checkGameOver(player);
                    }
                }
            }
        }
    }
    // If this is part of the game loop
    public void updateRobotPosition(Robot r) {
        Position currentPosition = r.getRobotPosition();
        Cell currentCell = Course.getInstance().getCellAtPosition(currentPosition);

        if (currentCell instanceof ConveyerBelt) {
            ConveyerBelt conveyerBelt = (ConveyerBelt) currentCell;
            conveyerBelt.robotMovement(r);
        }
    }

    private void executePrograms() {
        registerRound = 0;
        this.playersInGame.getFirst().passSwitchToAktivierungsPhase();
        for(int i = 0; i < 5; i ++){
            if (!gameIsOver){ // doesnt execute if game is over
                sendCurrentCards();
                moveRobots();
                playTileEffect(registerRound);
                playersInGame.getFirst().passShootLaser(); // todo this is to do laser effect for now include in shootlasers later
                shootLasers2();
                registerRound++;
            }
        }
    }
    private void sendCurrentCards() {
        ArrayList<ActiveCard> activeCards = new ArrayList<ActiveCard>();
        for (Player p: playersInGame) {
            activeCards.add(new ActiveCard(p.getClientID(), Cards.cardToStringForClient(p.getCardFromRegister(this.registerRound))));
        }
        //only needs to be sent once, is broadcasted in clientHandler
        playersInGame.getFirst().sendCurrentCardsToClient(activeCards);
    }
    //when damage card played will be added to pile
    private void moveRobots() {
        declarePlayOrder(new Position(1,5));
        System.out.println("GAME.moveRobots(): playerOrder.size = " + playerOrder.size());
        for (Player player : playersInGame) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
            if (!player.getPlayerRobot().isRobotRebooting()) { //else play first card from programmingdeck
                String cardPlayedName = player.getCardFromRegister(registerRound).getCardName();
                switch (cardPlayedName){
                    case "worm":
                        wormCards++;
                        break;
                    case "virus":
                        virusCards++;
                        break;
                    case "spam":
                        spamCards++;
                        break;
                    case "trojanhorse":
                        trojanHorse++;
                        break;
                    default:
//                        player.playRegisterCard(registerRound);
                        System.out.println("not a spam card");
                }
                player.playRegisterCard(registerRound);

            }
        }
    }

    public void givePlayerSpamCard(Player player){
        if(spamCards > 0){
            player.addCardToDischargeDeck(new SpamCard());
            spamCards--;
        }else{
            player.passPickDamageMessage();
            System.out.println("no spamcards left");
        }
    }

    public void givePlayerSelectedDamage(MessageBody messageBody , int clientID){
        List<String>  damageCards = messageBody.getCards();
        for (Player player : playersInGame){
            if (player.getClientID() == clientID){
                for (String card : damageCards){
                    player.addCardToDischargeDeck(Cards.stringToCardForClient(card));
                    if (card.equals("Worm")){
                        wormCards--;
                    }
                    if (card.equals("Virus")){
                        virusCards--;
                    }
                    if (card.equals("TrojanHorse")){
                        trojanHorse--;
                    }
                }
            }
        }
    }

    //add spawm card to discharge pile when player gets hit todo send message to client
    private void shootLasers() {
        for (Player players : playersInGame) {
            switch (players.getPlayerRobot().getOrientation().toString()) {
                case "up":
                    for (Player comparePlayers : playersInGame) {
                        if (players != comparePlayers) {
                            if (players.getPlayerRobot().getRobotPosition().getPositionY() < comparePlayers.getPlayerRobot().getRobotPosition().getPositionY()) {
                                comparePlayers.passDrawDamage();// makes player glow red since they take damage
                                givePlayerSpamCard(comparePlayers);
                            }
                        }
                    }
                    break;
                case "down":
                    for (Player comparePlayers : playersInGame) {
                        if (players != comparePlayers) {
                            if (players.getPlayerRobot().getRobotPosition().getPositionY() > comparePlayers.getPlayerRobot().getRobotPosition().getPositionY()) {
                                comparePlayers.passDrawDamage();// makes player glow red since they take damage
                                givePlayerSpamCard(comparePlayers);
                            }
                        }
                    }
                    break;
                case "left":
                    for (Player comparePlayers : playersInGame) {
                        if (players != comparePlayers) {
                            if (players.getPlayerRobot().getRobotPosition().getPositionX() > comparePlayers.getPlayerRobot().getRobotPosition().getPositionX()) {
                                comparePlayers.passDrawDamage(); // makes player glow red since they take damage
                                givePlayerSpamCard(comparePlayers);
                            }
                        }
                    }
                    break;
                case "right":
                    for (Player comparePlayers : playersInGame) {
                        if (players != comparePlayers) {
                            if (players.getPlayerRobot().getRobotPosition().getPositionX() < comparePlayers.getPlayerRobot().getRobotPosition().getPositionX()) {
                                comparePlayers.passDrawDamage(); // makes player glow red since they take damage
                                givePlayerSpamCard(comparePlayers);
                            }
                        }
                    }
                    break;
                default:
                    System.out.println("facing direction of robot is not correct");
            }
        }
    }

    private void shootLasers2() {
        for (Player shooter : playersInGame) {
            if (!shooter.getRobot().isRobotRebooting()){
                for (Player target : playersInGame) {
                    if (shooter != target && isShootingAt(shooter, target) && !isWallBlocking(shooter, target)) {
                        target.passDrawDamage(); // Apply damage effect to the target
                        givePlayerSpamCard(target); // Give spam card to the target
                    }
                }
            }
        }
    }

    private boolean isShootingAt(Player shooter, Player target) {
        // Get the shooter's robot position and orientation
        int shooterX = shooter.getPlayerRobot().getRobotPosition().getPositionX();
        int shooterY = shooter.getPlayerRobot().getRobotPosition().getPositionY();
        OrientationEnum shooterOrientation = shooter.getPlayerRobot().getOrientation();

        // Get the target's robot position
        int targetX = target.getPlayerRobot().getRobotPosition().getPositionX();
        int targetY = target.getPlayerRobot().getRobotPosition().getPositionY();

        // Check if the target is in the direction of the shooter's laser
        switch (shooterOrientation) {
            case U: // Up
                return shooterX == targetX && shooterY > targetY; // Same column, above
            case D: // Down
                return shooterX == targetX && shooterY < targetY; // Same column, below
            case L: // Left
                return shooterY == targetY && shooterX > targetX; // Same row, to the left
            case R: // Right
                return shooterY == targetY && shooterX < targetX; // Same row, to the right
            default:
                return false; // Invalid orientation
        }
    }
    private boolean isWallBlocking(Player shooter, Player target) {
        int shooterX = shooter.getPlayerRobot().getRobotPosition().getPositionX();
        int shooterY = shooter.getPlayerRobot().getRobotPosition().getPositionY();
        int targetX = target.getPlayerRobot().getRobotPosition().getPositionX();
        int targetY = target.getPlayerRobot().getRobotPosition().getPositionY();
        OrientationEnum shooterOrientation = shooter.getPlayerRobot().getOrientation();

        // Check for wall on the shooter's current cell
        if (hasWall(shooterX, shooterY, shooterOrientation)) {
            return true; // The shooter is standing on a cell with a blocking wall
        }

        // Check for immediate wall
        switch (shooterOrientation) {
            case U:
                if (hasWall(shooterX, shooterY - 1, OrientationEnum.U)) {
                    return true;
                }
                for (int y = shooterY - 1; y >= targetY; y--) {
                    if (hasWall(shooterX, y, OrientationEnum.U) || hasWall(shooterX, y, OrientationEnum.D)) {
                        return true;
                    }
                }
                break;
            case D:
                if (hasWall(shooterX, shooterY + 1, OrientationEnum.D)) {
                    return true;
                }
                for (int y = shooterY + 1; y <= targetY; y++) {
                    if (hasWall(shooterX, y, OrientationEnum.D) || hasWall(shooterX, y, OrientationEnum.U)) {
                        return true;
                    }
                }
                break;
            case L:
                if (hasWall(shooterX - 1, shooterY, OrientationEnum.L)) {
                    return true;
                }
                for (int x = shooterX - 1; x >= targetX; x--) {
                    if (hasWall(x, shooterY, OrientationEnum.L) || hasWall(x, shooterY, OrientationEnum.R)) {
                        return true;
                    }
                }
                break;
            case R:
                if (hasWall(shooterX + 1, shooterY, OrientationEnum.R)) {
                    return true;
                }
                for (int x = shooterX + 1; x <= targetX; x++) {
                    if (hasWall(x, shooterY, OrientationEnum.R) || hasWall(x, shooterY, OrientationEnum.L)) {
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    private boolean hasWall(int x, int y, OrientationEnum direction) {
        Cell newCell = Course.getInstance().getCellAtPosition(new Position(x,y));
        if (newCell instanceof Wall || newCell instanceof WallWithLaser){
            assert newCell instanceof Wall;
            OrientationEnum wallOrientation = ((Wall) newCell).getOrientation();
            return wallOrientation == direction;
        }
        return false;
    }


    //declare player order
    private void declarePlayOrder(Position antenna) {
        Vector<Double> distances = new Vector<>(); //here all distances to the antenna are saved
        for (Player players : playersInGame) { //calculates for each player in game the distance to the antenna
            players.calculateDistanceToAntenna(antenna); //calculate distance
            distances.add(players.getDistanceFromAntenna());// add distance to vector
        }
        setPlayerOrderInArray(distances);// set player order corresponding to the distances of the players
    }

    //set player order for the playerorder array
    private void setPlayerOrderInArray(Vector<Double> distances) {
        playerOrder.clear(); //first clear playerorder
        ArrayList<Player> playersWithSameDistance = new ArrayList<>();
        double shortestDistance;
        for (int i = 0; i < playersInGame.size(); i++) { //get shortest distance and add the corresponding player to the playerorder array
            shortestDistance = Collections.min(distances);
            for (Player players : playersInGame) {
                if (players.getDistanceFromAntenna() == shortestDistance) {
                    playersWithSameDistance.add(players); //add shortest distance player
                    for (Player comparePlayers : playersInGame) {//check is another player has the same shortest distance
                        if (players != comparePlayers) {
                            if (players.getDistanceFromAntenna() == comparePlayers.getDistanceFromAntenna()) {//if so then apply rule from antenna clock wise
                                playersWithSameDistance.add(comparePlayers); //add player with the same distance
                                /*
                                if (players.getPlayerRobot().getRobotPosition().getPositionY() > comparePlayers.getPlayerRobot().getRobotPosition().getPositionY()) {
                                    if (!playerOrder.contains(players)) {
                                        playerOrder.add(players);
                                    }
                                } else {
                                    if (!playerOrder.contains(comparePlayers)) {
                                        playerOrder.add(comparePlayers);
                                    }
                                }
                            } else {
                                if (!playerOrder.contains(players)) {
                                    playerOrder.add(players);
                                }*/
                            }
                        }
                    }
                    applyAntennaRule(playersWithSameDistance);
                }
            }
            distances.remove(shortestDistance);
        }
    }

    private void applyAntennaRule(ArrayList<Player> playersWithSameDistance) {
        if (playersWithSameDistance.size() == 1) {
            playerOrder.add(playersWithSameDistance.get(0));
        } else {
            int antennaX = Antenna.getInstance().getPosition().getPositionX();
            int antennaY = Antenna.getInstance().getPosition().getPositionY();

            // Calculate and assign order numbers to players
            ArrayList<Integer> orderNumberList = new ArrayList<>();

            for (Player player : playersWithSameDistance) {
                int orderNumber;
                player.setOrderNumber(300); // Default high value to prevent uninitialized access
                int playerY = player.getPlayerRobot().getRobotPosition().getPositionY();

                if (antennaX == 0) { // Antenna on the left
                    orderNumber = Math.abs(playerY - antennaY);
                } else { // Antenna on the right
                    orderNumber = Math.abs(playerY - antennaY);
                }

                player.setOrderNumber(orderNumber);
                orderNumberList.add(orderNumber);
            }

            // Sort players based on their order numbers
            while (!orderNumberList.isEmpty()) {
                int shortestDistance = Collections.min(orderNumberList);

                Iterator<Player> playerIterator = playersWithSameDistance.iterator();
                while (playerIterator.hasNext()) {
                    Player player = playerIterator.next();

                    if (player.getOrderNumber() == shortestDistance) {
                        playerOrder.add(player);
                        playerIterator.remove(); // Remove player from playersWithSameDistance
                        orderNumberList.remove((Integer) shortestDistance); // Remove value from orderNumberList
                        break; // Ensure only one player is added for this distance
                    }
                }
            }
        }
    }

    private void upgradePhase() {
        //todo later if needed
    }

    private void startNewRound() {
        declarePlayOrder(Antenna.getInstance().getCellPosition()); //todo antenna position
        upgradePhase(); //todo later
        //startTurnTimer(); // should start when one player finished programming
        programmingPhase();
        executePrograms();
    }

    //[timo, 8.12.] deprecated, implemented in ClientHandler
    private void startTurnTimer() {
        turnTimerExpired = false;
        turnTimer = new Timer();
        turnTimer.schedule(new TurnTimerTask(), TURN_DURATION_SECONDS * 1000);
    }

    public Robot getRobotAtPosition(Position newPos) {
        for (Player player : this.playersInGame) {
            if (player.getRobot().getRobotPosition().equals(newPos)) {
                return player.getRobot();
            }
        }
        return null;
    }

    private class TurnTimerTask extends TimerTask {
        @Override
        public void run() {
            turnTimerExpired = true;
        }
    }
    public int getCurrentRegisterRound(){
        return this.registerRound;
    }
    public Position getRebootPoint() {
        List<RebootPoint> rebootPoints = Course.getInstance().getRebootPoints();
        if (!rebootPoints.isEmpty()) {
            return rebootPoints.get(0).getPosition();
        }
        throw new IllegalStateException("No reboot points available");
    }

    public void passRebootDirection(int ClientID , String direction){
        for (Player p : playersInGame){
            if (p.getClientID() == ClientID){
                p.getRobot().handleRebootDirection(direction);
            }
        }
    }
    public void checkGameOver(Player player){
        if (player.getCheckpoint() == totalCheckPoints){
            gameIsOver = true;
            winnerID = player.getClientID();
        }
    }

    public ArrayList<Player> getPlayersInGame() {
        return playersInGame;
    }
    public Player getPlayerByRobot(Robot robot) {
        for (Player player : playersInGame) {
            if (player.getRobot().equals(robot)) {
                return player;
            }
        }
        return null;
    }

}

