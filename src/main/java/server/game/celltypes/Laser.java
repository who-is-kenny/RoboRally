package server.game.celltypes;

import server.game.Game;
import server.game.Player;
import server.game.Position;

public class Laser extends Cell {
    private final int damage;
    private final boolean isVertical;


    //constructors
    public Laser(Position position, int damage, boolean isVertical) {
        super(position);
        this.damage = damage;
        this.isVertical = isVertical;
    }

    public Laser(int positionX, int positionY, int damage, boolean isVertical) {
        super(positionX, positionY);
        this.damage = damage;
        this.isVertical = isVertical;
    }


//    public void applyEffect(Player player){
//         if(player.getRobot().getRobotPosition().equals(this.getPosition())){
//             Game.getInstance().givePlayerSpamCard(player);
//         }
//    }
}
