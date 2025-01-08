package server.game;

public class WormCard extends Cards{
    public WormCard() {
        super("worm");
    }

    @Override
    public void playCardEffect(Player player){
        player.getPlayerRobot().rebootRobot(new Position(0,0)); //todo position of the reboot
    }
}
