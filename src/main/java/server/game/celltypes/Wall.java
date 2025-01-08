package server.game.celltypes;

import content.OrientationEnum;
import server.game.Position;


public class Wall extends Cell{

    private OrientationEnum orientation;

    public Wall(Position position, OrientationEnum orientation){
        super(position);
        this.create(orientation);
    }

    public Wall(Integer positionX, Integer positionY, OrientationEnum orientation){
        this(new Position(positionX,positionY), orientation);
    }

    private void create(OrientationEnum orientation){
        this.orientation = orientation;
    }

    public OrientationEnum getOrientation() {
        return orientation;
    }
}

