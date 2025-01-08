package server.game.celltypes;

import server.game.Position;

public class StartPoint extends Cell{
    public StartPoint(Position position){
        super(position);
    }

    public StartPoint(Integer positionX, Integer positionY){
        this(new Position(positionX,positionY));
    }


}