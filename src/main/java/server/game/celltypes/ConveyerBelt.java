package server.game.celltypes;

import content.OrientationEnum;
import server.game.*;


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
                    if(conveyerWithTurn.getOrientation() != orientation){
                        OrientationEnum robotOrientation;
                        if (conveyerWithTurn.isClockwise()) {
                            robotOrientation = OrientationEnum.matchOrientation((r.getOrientation().getAngle() + 90) % 360);
                            r.ownedBy().passTurnCWMessage();
                            System.out.println("Robot turned left. New orientation: " + r.getOrientation());
                        } else{
                            robotOrientation = OrientationEnum.matchOrientation((r.getOrientation().getAngle() + 270) % 360);
                            r.ownedBy().passTurnCCWMessage();
                            System.out.println("Robot turned right. New orientation: " + r.getOrientation());
                        }
                        r.setOrientation(robotOrientation);

                        currentOrientation = conveyerWithTurn.getOrientation();
                    }
                    }


            } else {
                System.out.println("Move out of bounds or invalid: " + newPos);
                break;
            }
        }
    }

//
//    @Override
//    public void robotMovement(Robot r) {
//        /*if (this.orientation.getAngle() == r.getOrientation().getAngle()) {*/
//        for (int i = 0; i < distance; i++) {
//        Position newPos = Move.calculateNewPosition(orientation, r.getRobotPosition(), 1);
//            System.out.println("Attempting to move robot to: " + newPos);
//            if (Move.validateMove(r, newPos.getPositionY(), newPos.getPositionX(), 1)) { //switch xy
//                r.setRobotPosition(newPos);
//                System.out.println("Robot moved to: " + newPos);
//                /*Robot robotAtPos = Game.getInstance().getRobotAtPosition(newPos);
//                if (robotAtPos != null) {
//                    r.robotMovement(robotAtPos, 1);
//                }
//                r.setRobotPosition(newPos);*/
//            } else {
//                System.out.println("Move out of bounds or invalid: " + newPos);
//                break;
//            }
//
//
//    }
//}
}

