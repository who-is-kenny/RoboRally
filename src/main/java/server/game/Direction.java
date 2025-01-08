package server.game;

public class Direction {
    private final boolean up;
    private final boolean down;
    private final boolean left;
    private final boolean right;

    /*init direction
        for tiles its true if you can leave/join the tile in this direction
     */
    public Direction (boolean up, boolean down, boolean left, boolean right){
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
    }

    public boolean getRight(){
        return right;
    }

    public static String convertTurnStringForClient(String turn){
        switch(turn){
            case "rightturn":
                return "clockwise";
            case "leftturn":
                return "counterclockwise";
            default:
                return null;
        }
    }

    public boolean getLeft(){
        return left;
    }

    public  boolean getUp(){
        return up;
    }

    public boolean getDown(){
        return down;
    }

}
