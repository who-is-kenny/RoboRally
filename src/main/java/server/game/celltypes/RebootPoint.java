package server.game.celltypes;

import server.game.Game;
import server.game.Player;
import server.game.Position;

public class RebootPoint extends Cell {

    //constructors
    public RebootPoint(int positionX, int positionY) {
        super(positionX, positionY);
    }

    public RebootPoint(Position position) {
        super(position);
    }



    //    public void applyEffect(Player player) {
//        if (player.getRobot().getRobotPosition().equals(this.getPosition())) {
//            Game.getInstance().givePlayerSpamCard(player);
//            Game.getInstance().givePlayerSpamCard(player);
//        }
//    }
}
