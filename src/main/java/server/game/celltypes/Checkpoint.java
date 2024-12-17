package server.game.celltypes;

import server.game.Position;

public class Checkpoint extends Cell{
    private int checkPointNum;

    //constructors
    public Checkpoint(Position position, int checkPointNum) {
        super(position);
        this.checkPointNum = checkPointNum;
    }

    public Checkpoint(int positionX, int positionY, int checkPointNum) {
        super(positionX, positionY);
        this.checkPointNum = checkPointNum;
    }
    //getter
    public int getCheckPointNum() {
        return checkPointNum;
    }
    //setter
    public void setCheckPointNum(int checkPointNum) {
        this.checkPointNum = checkPointNum;
    }
}
