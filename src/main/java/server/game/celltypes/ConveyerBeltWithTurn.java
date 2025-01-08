package server.game.celltypes;

import content.OrientationEnum;
import server.game.Interact;
import server.game.Move;
import server.game.Position;
import server.game.Robot;

public class ConveyerBeltWithTurn extends Cell implements Interact{
    private OrientationEnum orientation;
    private int distance;
    private boolean isClockwise;


    public ConveyerBeltWithTurn(Position position, OrientationEnum orientation, Integer distance, Boolean isClockwise) {
        super(position);
        this.orientation = orientation;
        this.distance = distance;
        this.create(isClockwise);
    }
    private void create(Boolean isClockwise){
        this.isClockwise = isClockwise;
    }

    public ConveyerBeltWithTurn(Integer row, Integer col, OrientationEnum orientation, Integer distance, Boolean isClockwise) {
        this(new Position(row, col), orientation, distance, isClockwise);
    }


    @Override
    public void robotMovement (Robot r){
        OrientationEnum orientation;
        if (this.isClockwise)
            orientation = OrientationEnum.matchOrientation((r.getOrientation().getAngle() + 90) % 360);
        else
            orientation = OrientationEnum.matchOrientation((r.getOrientation().getAngle() + 270) % 360);
        r.setOrientation(orientation);

        for (int i = 0; i < distance; i++) {
            Position newPos = Move.calculateNewPosition(orientation, r.getRobotPosition(), 1);
            System.out.println("Attempting to move robot to: " + newPos);
            if (Move.validateMove(r, newPos.getPositionX(), newPos.getPositionY(), 1)) {
                r.setRobotPosition(newPos);
                System.out.println("Robot moved to: " + newPos);
            } else {
                System.out.println("Move out of bounds or invalid: " + newPos);
                break;
            }

        }
    }
}
