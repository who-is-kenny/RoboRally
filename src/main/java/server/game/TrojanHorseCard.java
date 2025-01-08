package server.game;

public class TrojanHorseCard extends Cards{
    public TrojanHorseCard() {
        super("trojanhorse");
    }

    @Override
    public void playCardEffect(Player player){
        player.getPlayerProgrammingCards().getProgrammingDeck().getLast()
                .playCardEffect(player); //play card on top of the programming deck
                // error handling if programming deck is empty todo ..
        player.addCardToDischargeDeck(new SpamCard());
        player.addCardToDischargeDeck(new SpamCard());
    }

}
