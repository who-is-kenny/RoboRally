package server.game.celltypes;

import content.OrientationEnum;
import server.game.Interact;
import server.game.Move;
import server.game.Position;
import server.game.Robot;


public class ConveyerBelt extends Cell implements Interact{
    private OrientationEnum orientation;
    private int distance;

    public ConveyerBelt(Position position, OrientationEnum orientation, Integer distance) {
        super(position);
        this.create(orientation, distance);
    }

    public ConveyerBelt(Integer row, Integer col, OrientationEnum orientation, Integer distance) {
        this(new Position(row, col), orientation, distance);
    }

    private void create(OrientationEnum orientation, int distance) {
        this.orientation = orientation;
        this.distance = distance;
    }

    @Override
    public void robotMovement(Robot r) {
        /*if (this.orientation.getAngle() == r.getOrientation().getAngle()) {*/
        for (int i = 0; i < distance; i++) {
        Position newPos = Move.calculateNewPosition(orientation, r.getRobotPosition(), 1);
            System.out.println("Attempting to move robot to: " + newPos);
            if (Move.validateMove(r, newPos.getPositionY(), newPos.getPositionX(), 1)) { //switch xy
                r.setRobotPosition(newPos);
                System.out.println("Robot moved to: " + newPos);
                /*Robot robotAtPos = Game.getInstance().getRobotAtPosition(newPos);
                if (robotAtPos != null) {
                    r.robotMovement(robotAtPos, 1);
                }
                r.setRobotPosition(newPos);*/
            } else {
                System.out.println("Move out of bounds or invalid: " + newPos);
                break;
            }
    }
}}
