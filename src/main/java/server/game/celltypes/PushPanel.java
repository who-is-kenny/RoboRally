package server.game.celltypes;

import content.OrientationEnum;
import server.game.*;

public class PushPanel extends Cell implements Interact {
    private final OrientationEnum orientation;
    private final Integer distance;
    private final boolean isEven;

    public PushPanel(Position position, OrientationEnum orientation , Integer distance , boolean isEven) {
        super(position);
        this.orientation = orientation;
        this.distance = distance;
        this.isEven = isEven;
    }

    public PushPanel(Integer row, Integer col, OrientationEnum orientation , Integer distance , boolean isEven) {
        this(new Position(row, col), orientation , distance , isEven);
    }

    @Override
    public void robotMovement(Robot r) {
        Position newPos = Move.calculateNewPosition(orientation, r.getRobotPosition(), 1);

        if (Move.validateMove(r, newPos.getPositionY(), newPos.getPositionX(), 1)) { //switch xy
            Robot robotAtPos = Game.getInstance().getRobotAtPosition(newPos);

            if (robotAtPos != null) {
                r.robotMovement(robotAtPos, 1);
            }
            r.setRobotPosition(newPos);
        }
    }

    public void pushRobot(Robot r , int currentRegister) { // need to add 1 to register since it starts at 0
        int c = currentRegister+1;
        System.out.println("the current register is: " + c);
        System.out.println("push panel is even: " + isEven);
        Position newPos = Move.calculateNewPosition(orientation, r.getRobotPosition(), 1);

        if (c % 2 == 0 && isEven) {
            if (Move.validateMove(r, newPos.getPositionY(), newPos.getPositionX(), 1)) { //switch xy
                Robot robotAtPos = Game.getInstance().getRobotAtPosition(newPos);

                if (robotAtPos != null) {
                    r.robotMovement(robotAtPos, 1);
                }
                r.setRobotPosition(newPos);
            }
        }else if (!(c % 2 == 0) && !isEven){
            if (Move.validateMove(r, newPos.getPositionY(), newPos.getPositionX(), 1)) { //switch xy
                Robot robotAtPos = Game.getInstance().getRobotAtPosition(newPos);

                if (robotAtPos != null) {
                    r.robotMovement(robotAtPos, 1);
                }
                r.setRobotPosition(newPos);
            }
        }
    }
}