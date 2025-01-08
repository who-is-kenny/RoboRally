package server.game.celltypes;

import server.game.Position;

public class Cell {
    private Position position;
    public Cell(){
        this(0, 0);
    }

    public Cell(Position position){
        this.position = position;

    }
    public Cell(int positionX, int positionY){
        this(new Position(positionX,positionY));
    }
    //test return position
    public Position getCellPosition(){
        return position;
    }

    public Position getPosition() {
        return position;
    }
}

