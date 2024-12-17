package server.game.celltypes;

import server.game.Position;


public class Wall extends Cell{

    private final String orientation;

    //constructors
    public Wall(Position position, String orientation) {
        super(position);
        this.orientation = orientation;
    }

    public Wall(int positionX, int positionY, String orientation) {
        super(positionX, positionY);
        this.orientation = orientation;
    }

    //getter
    public String getOrientation() {
        return orientation;
    }
}

