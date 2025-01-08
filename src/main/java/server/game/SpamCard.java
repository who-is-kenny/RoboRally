package server.game;

public class SpamCard extends Cards{
    public SpamCard() {
        super("spam");
    }

    @Override
    public void playCardEffect(Player player){
        player.getPlayerProgrammingCards().getProgrammingDeck().get(
                player.getPlayerProgrammingCards().getProgrammingDeck().size() - 1).playCardEffect(player); //play card on top of the programming deck;
    }

}
