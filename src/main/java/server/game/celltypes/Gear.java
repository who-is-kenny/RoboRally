package server.game.celltypes;


import server.game.Position;
import server.game.Robot;

public class Gear extends Cell {
    private boolean isClockwise;


    //constructors
    public Gear(Position position, boolean isClockwise) {
        super(position);
        this.isClockwise = isClockwise;
    }

    public Gear(int positionX, int positionY, boolean isClockwise) {
        super(positionX, positionY);
        this.isClockwise = isClockwise;
    }

    //    public void robotMovement(Robot r){
//        OrientationEnum orientation;
//        if (this.isClockwise)
//            orientation = OrientationEnum.matchOrientation((r.getOrientation().getAngle() + 90) % 360);
//        else
//            orientation = OrientationEnum.matchOrientation((r.getOrientation().getAngle() + 270) % 360);
//        r.setOrientation(orientation);
//    }
}

