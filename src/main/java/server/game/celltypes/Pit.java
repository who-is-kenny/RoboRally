package server.game.celltypes;

import server.game.Game;
import server.game.Player;
import server.game.Position;

public class Pit extends Cell {


    //constructors
    public Pit(int positionX, int positionY) {
        super(positionX, positionY);
    }

    public Pit(Position position) {
        super(position);
    }

//    public void applyEffect(Player player) {
//        if (player.getRobot().getRobotPosition().equals(this.getPosition())) {
//            Position rebootPoint = Game.getInstance().getRebootPoint();
//            player.getRobot().setRobotPosition(rebootPoint);
//        }
//    }
}
