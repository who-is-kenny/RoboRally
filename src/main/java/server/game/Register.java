package server.game;

import java.util.ArrayList;

public class Register {

    private final ArrayList<RegisterEntry> register = new ArrayList<>();

    public Register(){
        initializeRegister();
    }

    private void initializeRegister(){
        for(int i = 0; i < 5; i++){
            register.add(new RegisterEntry(i));
        }
    }

    public void playCardInRegister(int registerNumber, Cards cardPlayed){
        RegisterEntry registerEntry = register.get(registerNumber);
        registerEntry.setRegisterCard(cardPlayed);
    }

    public Cards getCardInRegister(int registerNumber){
        RegisterEntry registerEntry = register.get(registerNumber);
        return registerEntry.getRegisterCard();
    }

    public void clearRegister(){
        //register.clear(); [timo, 6.12.]
        for (RegisterEntry registerEntry : register) {
            registerEntry.setRegisterCard(null);
        }
    }

    public String toString(){
        String out = "[";
        for (RegisterEntry registerEntry : register) {
            out = out + registerEntry.getRegisterCard().getCardName() + ", ";
        }
        return out + "] ";
    }

}
