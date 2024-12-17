package server.game.celltypes;

import server.game.Position;

public class PushPanel extends Cell{
    private final String orientation;


    //constructors
    public PushPanel(int positionX, int positionY, String orientation) {
        super(positionX, positionY);
        this.orientation = orientation;
    }

    public PushPanel(Position position, String orientation) {
        super(position);
        this.orientation = orientation;
    }

    //    @Override
//    public void robotMovement(Robot r) {
//        Position newPos = Move.calculateNewPosition(orientation, r.getRobotPosition(), 1);
//
//        if (Move.validateMove(r, newPos.getPositionX(), newPos.getPositionY(), 1)) {
//            Robot robotAtPos = Game.getInstance().getRobotAtPosition(newPos);
//
//            if (robotAtPos != null) {
//                r.robotMovement(robotAtPos, 1);
//            }
//            r.setRobotPosition(newPos);
//        }
//    }
}