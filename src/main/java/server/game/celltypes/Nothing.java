package server.game.celltypes;

import server.game.Position;

public class Nothing extends Cell{
    public Nothing(Position position){
        super(position);
    }

    public Nothing(Integer positionX, Integer positionY){
        this(new Position(positionX,positionY));
    }

}
