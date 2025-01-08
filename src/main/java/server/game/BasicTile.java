package server.game;

public class BasicTile extends Tile{
    /*
    init basic tile
    0 priority because it doesn't have an effect
    direction is true in all cases because you can leave/join at all directions
     */
    public BasicTile(Position tilePosition) {
        super(0, tilePosition, new Direction(true, true, true, true), false);
    }

    @Override
    public void playTileEffect(){
        //no effect
    }
}
