package server.game.celltypes;

import server.game.Position;

public class StartPoint extends Cell{

    //constructors
    public StartPoint(Position position) {
        super(position);
    }

    public StartPoint(int positionX, int positionY) {
        super(positionX, positionY);
    }
}