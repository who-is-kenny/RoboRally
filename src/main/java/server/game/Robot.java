package server.game;

import content.OrientationEnum;
import content.RobotNameEnum;
import server.game.celltypes.Cell;

public class Robot {

    private String facingDirection;
    private Position robotPosition;
    private String robotName;
    private boolean rebootRobot;
    private int energy = 5;
    private OrientationEnum orientation;
    private Player ownedBy = null;

    public Robot(RobotNameEnum robotName) {
        this.robotName = robotName.getName();
        this.robotPosition = new Position();
        this.orientation = OrientationEnum.R;
    }

    public Robot(RobotNameEnum robotName, int positionX, int positionY) {
        this.robotName = robotName.getName();
        this.robotPosition = new Position(positionX, positionY);
        this.orientation = OrientationEnum.R;
    }
    public Robot(RobotNameEnum robotName, int[] startingPos){
        //[timo, 6.12.] to make game initialization better readable, might be changed later
        this.robotName = robotName.getName();
        this.robotPosition = new Position(startingPos[0], startingPos[1]);
    }

    //shoot laser maybe not void and Robot instead/ returns hit robot
    public void shootLaser() {

    }


    public void setEnergy(int energy) {
        if (energy <= 5) this.energy = energy;

    }

    //get position of robot
    public Position getRobotPosition() {
        return robotPosition;
    }

    //get facing direction
    public String getFacingDirection() {
        return facingDirection;
    }

    //get robot name
    public String getRobotName() {
        return robotName;
    }

    //set robot name
    public void setRobotName(String newRobotName) {
        robotName = newRobotName;
    }

    //set robot position
    public void setRobotPosition(Position newRobotPosition) {
        this.ownedBy.passMovementMessage(newRobotPosition);
        robotPosition = newRobotPosition;
    }

    //set facing direction
    /*public void setOrientation(String newRobotDirection) {
        facingDirection = newRobotDirection;
    } // "right"*/

    //reboot robot
    public void rebootRobot(Position rebootPosition) {

        this.robotPosition = rebootPosition;
        this.ownedBy.passMovementMessage(robotPosition);
        this.ownedBy.passRebootMessage();
        //Get reboot position from map and set position of robot there
//        Position flippedReboot = new Position(rebootPosition.getPositionY(),rebootPosition.getPositionX());
//        this.robotPosition = flippedReboot;
//        this.ownedBy.passMovementMessage(flippedReboot);
        this.rebootRobot = true;
        System.out.println("giving player 2 spam cards for reboot");
        Game.getInstance().givePlayerSpamCard(this.ownedBy);
        Game.getInstance().givePlayerSpamCard(this.ownedBy);
    }

    private void move(Position newPos, int movement) {
        Cell cell = Course.getInstance().getCellAtPosition(newPos);
        Robot robotAtPos = Game.getInstance().getRobotAtPosition(newPos);

        // there is a robot in the new position
        if (robotAtPos != null) {
            robotAtPos.robotMovement(this, movement);
            robotAtPos = Game.getInstance().getRobotAtPosition(newPos);
        }
        if (robotAtPos == null) {
            this.robotPosition = newPos;
        }
    }


    public void robotMovement(Robot robot, int movement) {
        Position newPos = Move.calculateNewPosition(robot.getOrientation(), robot.getRobotPosition(), 1);
        if (movement == 1) {
            newPos = Move.calculateNewPosition(robot.getOrientation(), this.getRobotPosition(), 1);
        } else if (movement == -1) {
            newPos = Move.calculateNewPosition(robot.getOrientation().getOpposite(), this.getRobotPosition(), 1);
        }
        //this.ownedBy.passMovementMessage(newPos);
        OrientationEnum initialOrientation = this.getOrientation();
        this.orientation = robot.getOrientation();
        // check if the new position is valid
        System.out.println("ROBOT.robotMovement(): old Pos: (" + this.robotPosition.getPositionX() + ", " + this.robotPosition.getPositionY() + "), new Pos (" + newPos.getPositionX() + ", " + newPos.getPositionY() + ")");
        if (this.tryMove(newPos, 1 )&& !rebootRobot) { // check for reboot
            this.setOrientation(initialOrientation);
            this.ownedBy.passMovementMessage(newPos);

        }
    }

    public boolean tryMove (Position newPos,int movement){
        if (Move.validateMove(this, newPos.getPositionY(),newPos.getPositionX(), movement) && !rebootRobot ) { //switch x y & check for reboot
            this.move(newPos, movement);
            return true;
        }
        return false;
    }




    public void rotate(int degrees) {
        //String[] directions = {"up", "down", "left", "right"};
       // String[] directions = {"up", "right", "down", "left"};
        //int currentIndex = java.util.Arrays.asList(directions).indexOf(facingDirection.toLowerCase());
        int steps = (degrees / 90) % 4;
        if (steps == 1) {
            this.orientation = orientation.getRight();
            this.ownedBy.passTurnCWMessage();
        }
        else if (steps == 3) {
            this.orientation = orientation.getLeft();
            this.ownedBy.passTurnCCWMessage();
        }
        else if (steps == 2){
            this.orientation = orientation.getOpposite();
            this.ownedBy.passTurnCCWMessage();
            this.ownedBy.passTurnCCWMessage();
        }
        //facingDirection = directions[(currentIndex + steps + 4) % 4];
    }

    public void takeLaserDamage(int damage) {
        energy = Math.max(0, energy - damage);
    }

    public void reachCheckpoint() {
        System.out.println(robotName + " reached a checkpoint!");
    }

    public void collectEnergyCube() {
        energy++;
    }

    public boolean isRobotRebooting() {
        return rebootRobot;
    }

    public int getEnergy() {
        return energy;
    }

    public void addOneEnergy(){
        energy++;
        this.ownedBy.passEnergyFromCardMessage();
        System.out.println("total energy: " + getEnergy()); //todo remove print later
    }

    public OrientationEnum getOrientation() {
        return orientation;
    }

    public void setOrientation(OrientationEnum orientation) {
        this.orientation = orientation;
    }

    /**
     * sets the instance of the Player this robot is owned by
     * @param player
     */
    public void setPlayerThatOwnsThisRobot(Player player){
        this.ownedBy = player;
    }

    /**
     *
     * @return the instance of the Player this robot is owned by
     */
    public Player ownedBy(){
        return this.ownedBy;
    }

    public void setRebootRobot(boolean rebootRobot) {
        this.rebootRobot = rebootRobot;
    }

    //not tested yet
    public void handleRebootDirection(String direction){
        switch (direction){
            case "Right":
                setOrientation(OrientationEnum.R);
                break;
            case "Left":
                setOrientation(OrientationEnum.L);
                break;
            case "Down":
                setOrientation(OrientationEnum.D);
                break;
            case "Up":
                setOrientation(OrientationEnum.U);
                break;
        }
    }

}
