package server.game;

public class RegisterEntry {

    //todo if register is empty handling
    private Cards registerCard;
    private final int registerNumber;

    public RegisterEntry(int registerNumber){
        this.registerNumber = registerNumber;
        registerCard = null;
    }

    public Cards getRegisterCard() {
        return registerCard;
    }

    public void setRegisterCard(Cards cardPlayedInRegister){
        registerCard = cardPlayedInRegister;
    }

}
