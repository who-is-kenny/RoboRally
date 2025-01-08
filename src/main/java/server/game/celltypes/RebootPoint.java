package server.game.celltypes;

import server.game.Player;
import server.game.Position;

public class RebootPoint extends Cell {
    public RebootPoint(Position position) {
        super(position);
    }

    public RebootPoint(Integer positionX, Integer positionY) {
        this(new Position(positionX, positionY));
    }

    public Position getPosition() {
        return super.getPosition();
    }

    public void applyEffect(Player player) { // shouldnt do anything when play is on the square after reboot
        System.out.println("player on reboot square");
//        if (player.getRobot().getRobotPosition().equals(this.getPosition())) {
//            Game.getInstance().givePlayerSpamCard(player);
//            Game.getInstance().givePlayerSpamCard(player);
//        }
    }
}
