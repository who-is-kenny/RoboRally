package server.game.celltypes;

import server.game.Game;
import server.game.Player;
import server.game.Position;

public class Laser extends Cell {
    private final int damage = 1;
     public Laser(Position position, Boolean isVertical){
        super(position);
        create(isVertical);
    }
    public Laser(Integer positionX, Integer positionY, Boolean isVertical){
        this(new Position(positionX, positionY), isVertical);
    }

    private void create(Boolean isVertical){}

    public void applyEffect(Player player){
         if(player.getRobot().getRobotPosition().equals(this.getPosition())){
             Game.getInstance().givePlayerSpamCard(player);
             player.passDrawDamage(); // for laser effect
         }
    }
}
