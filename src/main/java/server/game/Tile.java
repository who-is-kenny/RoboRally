package server.game;

public class Tile {

    private final int priority; //used to determine the order of the tile effect
    private final Position tilePosition;
    private final Direction tileDirection;
    private final boolean tileHasEffect;

    public Tile(int priority, Position tilePosition, Direction tileDirection, boolean tileHasEffect){
        this.priority = priority;
        this.tilePosition = tilePosition;
        this.tileDirection = tileDirection;
        this.tileHasEffect = tileHasEffect;
    }

    public void playTileEffect(){
        //Tile effect
    }

    public int getPriority(){
        return priority;
    }

    public Position getTilePosition(){
        return tilePosition;
    }

    public Direction getDirection() {
        return tileDirection;
    }

    public boolean doesTileHasEffect(){
        return tileHasEffect;
    }

}
