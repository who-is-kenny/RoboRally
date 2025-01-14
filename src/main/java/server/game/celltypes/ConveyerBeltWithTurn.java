package server.game.celltypes;

import content.OrientationEnum;
import server.game.*;

public class ConveyerBeltWithTurn extends Cell implements Interact{
    private OrientationEnum orientation;
    private int distance;
    private boolean isClockwise;

    public OrientationEnum getOrientation() {
        return orientation;
    }
    public void setOrientation(OrientationEnum orientation) {
        this.orientation = orientation;
    }
    public int getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public boolean isClockwise() {
        return isClockwise;
    }
    public void setClockwise(boolean clockwise) {
        isClockwise = clockwise;
    }

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
    public void robotMovement(Robot r) {
        OrientationEnum currentOrientation = orientation;
        for (int i = 0; i < distance; i++) {
            Position newPos = Move.calculateNewPosition(currentOrientation, r.getRobotPosition(), 1);
            System.out.println("Attempting to move robot to: " + newPos);

            if (Move.validateMove(r, newPos.getPositionY(), newPos.getPositionX(), 1)) { // switch xy
                r.setRobotPosition(newPos);
                System.out.println("Robot moved to: " + newPos);

                // Check if the new position is a ConveyorBeltWithTurn
                Cell cellAtNewPos = Course.getInstance().getCellAtPosition(newPos);
                if (cellAtNewPos instanceof ConveyerBeltWithTurn) {
                    ConveyerBeltWithTurn conveyerWithTurn = (ConveyerBeltWithTurn) cellAtNewPos;

                    // Adjust robot's orientation based on the conveyor belt's turn direction
                    if (conveyerWithTurn.isClockwise()) {
                        orientation = OrientationEnum.matchOrientation((r.getOrientation().getAngle() + 90) % 360);
                        r.ownedBy().passTurnCWMessage();
                        System.out.println("Robot turned left. New orientation: " + r.getOrientation());
                    } else{
                        orientation = OrientationEnum.matchOrientation((r.getOrientation().getAngle() + 270) % 360);
                        r.ownedBy().passTurnCCWMessage();
                        System.out.println("Robot turned right. New orientation: " + r.getOrientation());
                    }

                    currentOrientation = conveyerWithTurn.getOrientation();
                }

            } else {
                System.out.println("Move out of bounds or invalid: " + newPos);
                break;
            }
        }
    }

//    @Override
//    public void robotMovement (Robot r){
//        OrientationEnum orientation;
//        if (this.isClockwise)
//            orientation = OrientationEnum.matchOrientation((r.getOrientation().getAngle() + 90) % 360);
//        else
//            orientation = OrientationEnum.matchOrientation((r.getOrientation().getAngle() + 270) % 360);
//        r.setOrientation(orientation);
//
//        for (int i = 0; i < distance; i++) {
//            Position newPos = Move.calculateNewPosition(orientation, r.getRobotPosition(), 1);
//            System.out.println("Attempting to move robot to: " + newPos);
//            if (Move.validateMove(r, newPos.getPositionX(), newPos.getPositionY(), 1)) {
//                r.setRobotPosition(newPos);
//                System.out.println("Robot moved to: " + newPos);
//            } else {
//                System.out.println("Move out of bounds or invalid: " + newPos);
//                break;
//            }
//
//        }
//    }
}
