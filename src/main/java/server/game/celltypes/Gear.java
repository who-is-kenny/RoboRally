package server.game.celltypes;

import content.OrientationEnum;
import server.game.Interact;
import server.game.Position;
import server.game.Robot;

public class Gear extends Cell implements Interact {
    private boolean isClockwise;

    public Gear(Position position, Boolean isClockwise){
        super(position);
        this.create(isClockwise);
    }
    public Gear(Integer positionX, Integer positionY, Boolean isClockwise){
        this(new Position(positionX,positionY), isClockwise);
    }



    private void create(Boolean isClockwise){
        this.isClockwise = isClockwise;
    }

    // todo remove prints later
    public void robotMovement(Robot r){
        OrientationEnum orientation;
        if (this.isClockwise){
            System.out.println("turning clockwise from gear");
            orientation = OrientationEnum.matchOrientation((r.getOrientation().getAngle() + 90) % 360);
            r.ownedBy().passTurnCWMessage();
        } else{
            System.out.println("turning counterclockwise from gear");
            orientation = OrientationEnum.matchOrientation((r.getOrientation().getAngle() + 270) % 360);
            r.ownedBy().passTurnCCWMessage();
        }
        r.setOrientation(orientation);
    }
    }

