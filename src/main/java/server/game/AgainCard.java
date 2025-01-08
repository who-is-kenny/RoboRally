package server.game;

public class AgainCard extends Cards{

    public AgainCard() {
        super("again");
    }

    @Override
    public void playCardEffect(Player player){
        if(player.getCurrentRegisterRound() != 0){
            int registerRound = player.getCurrentRegisterRound();
            Cards newCard = player.getCardFromRegister(registerRound-1);
            player.playCardInRegister(registerRound, newCard.getCardName());// overwriting the element in the array ??
            newCard.playCardEffect(player);

            //player.getCardFromRegister(player.getCurrentRegisterRound() - 1).playCardEffect(player);
        }else{
            System.out.println("you cannot play the again card in the first register");
        }
    }

}
