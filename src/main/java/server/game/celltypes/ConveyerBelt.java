package server.game.celltypes;

import server.game.Position;


public class ConveyerBelt extends Cell{
    private final String orientation;
    private final int distance;

    //constructors
    public ConveyerBelt(Position position, String orientation, Integer distance) {
        super(position);
        this.orientation = orientation;
        this.distance = distance;
    }

    public ConveyerBelt(int positionX, int positionY, String orientation, int distance) {
        super(positionX, positionY);
        this.orientation = orientation;
        this.distance = distance;
    }
//
//    @Override
//    public void robotMovement(Robot r) {
//        /*if (this.orientation.getAngle() == r.getOrientation().getAngle()) {*/
//        for (int i = 0; i < distance; i++) {
//        Position newPos = Move.calculateNewPosition(orientation, r.getRobotPosition(), 1);
//            System.out.println("Attempting to move robot to: " + newPos);
//            if (Move.validateMove(r, newPos.getPositionX(), newPos.getPositionY(), 1)) {
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
//    }
//}
}
