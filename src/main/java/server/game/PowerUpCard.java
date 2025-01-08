package server.game;

public class PowerUpCard extends Cards {
    public PowerUpCard() {
        super("powerup");
    }

    @Override
    public void playCardEffect(Player player){
        player.setEnergyCubes(player.getEnergyCubes() + 1);
    }
}
