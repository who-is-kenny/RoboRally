package server.game.celltypes;

import server.game.Position;

public abstract class Cell {
    private final Position position;
    //constructors
    public Cell(Position position){
        this.position = position;

    }
    public Cell(int positionX, int positionY){
        this.position = new Position(positionX,positionY);
    }

    //getter
    public Position getPosition() {
        return position;
    }
}

