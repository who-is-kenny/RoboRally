package server.game.celltypes;

import server.game.Game;
import server.game.Player;
import server.game.Position;

public class Checkpoint extends Cell{
    private int checkPointNum;
    public Checkpoint(Position position){
        super(position);
    }
    public Checkpoint(Integer positionX, Integer positionY){
        this(new Position(positionX,positionY));
    }
    public void setCheckPointNum(int checkPointNum){
        this.checkPointNum = checkPointNum;
    }
    public int getCheckPointNum(){
        return checkPointNum;
    }

    public void applyEffect(Player player){
        if ((player.getCheckpoint() + 1) == checkPointNum){
            System.out.println("applying checkpoint effect");
            player.addCheckpoint();
            player.passCheckPointMessage(checkPointNum);
            Game.getInstance().checkGameOver(player);
        }
        System.out.println("player on wrong checkpoint");
        //todo check if player has reached total number of checkpoints.
    }
}
