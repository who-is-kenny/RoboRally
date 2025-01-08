package server.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

//todo register in this class ???

public class ProgrammingCardDeck {
    private ArrayList<Cards> programmingDeck = new ArrayList<>();
    private final ArrayList<Cards> dischargePile = new ArrayList<>();

    public ProgrammingCardDeck(){
        initializeProgrammingDeck();
//        shuffleProgrammingDeck();
        Collections.shuffle(programmingDeck);
        //System.out.println(programmingDeck.size());
    }

    //only use for replacement cards
    public Cards getFirstCardFromDeck(){
        if(!programmingDeck.isEmpty()){
            Cards temporaryCard = programmingDeck.getLast();
            dischargePile.add(temporaryCard);
            programmingDeck.removeLast();
            return temporaryCard;
        }else{ //if deck is empty load discharge pile into deck and shuffle then draw
            loadDischargePileIntoProgrammingDeck();
            Cards temporaryCard = programmingDeck.getLast();
            dischargePile.add(temporaryCard);
            programmingDeck.removeLast();
            return programmingDeck.getLast();
        }
    }

    // ONLY used for random card replacement for damage cards
    public Cards getFirstCard(){
        if (programmingDeck.isEmpty()){
            loadDischargePileIntoProgrammingDeck();
        }
        return programmingDeck.getLast();
    }
    //  ONLY used for random card replacement for damage cards
    public void removeFromDeckAndDiscard(Cards cards){
        dischargePile.add(cards);
        programmingDeck.removeLast();
    }

    public void addCardToDischargeDeck(Cards card){
        dischargePile.add(card);
    }

    /*public void test(){
        for(Cards programmingCards : programmingDeck){
            System.out.println(programmingCards.getCardName());
        }
    }*/

    //initialize cards for a programming deck of 20 cards
    private void initializeProgrammingDeck(){
        for (int i = 0; i < 5; i++){ //add move 1 card 5 times
            programmingDeck.add(new Move1ForwardCard());
        }
        for (int i = 0; i < 3; i++){ //add move 2 card, turn left/right 3 times
            programmingDeck.add(new Move2ForwardCard());
            programmingDeck.add(new LeftTurnCard());
            programmingDeck.add(new RightTurnCard());
        }
        for(int i = 0; i < 2; i++){ // add again card 2 times
            programmingDeck.add(new AgainCard());
        }
        programmingDeck.add(new Move3ForwardCard()); // add 3 move card once
        programmingDeck.add(new Move1BackwardCard()); // add move 1 back card once
        programmingDeck.add(new PowerUpCard()); // add power up card once
        programmingDeck.add(new UTurnCard()); // add u turn card once
    }

    //shuffle the cards
    public void shuffleProgrammingDeck(){
        Random randomNumber = new Random();
        for(int i = 0; i < programmingDeck.size(); i++){
            int randomisation = randomNumber.nextInt(programmingDeck.size());
            Cards temporaryCard = programmingDeck.get(randomisation);
            programmingDeck.set(randomisation, programmingDeck.get(i));
            programmingDeck.set(i, temporaryCard);
        }
    }

    //draw the nine handcards at the second stage todo get arraylist from method #check#
    public ArrayList<Cards> drawHandCards(){
        ArrayList<Cards> handCards = new ArrayList<>();
        System.out.println("Programming deck:" + this.programmingDeck);
        System.out.println("discahrge pile:" + this.dischargePile);
        if(this.programmingDeck.size() < 9){ //if there are less then 9 cards draw the rest mix the discharge pile and draw until you got nine cards
            System.out.println("PROGRAMMINGCARDDECK.drawHandCards(): remaining Deck size = " + this.programmingDeck.size());
            int cardAmount = programmingDeck.size();
            handCards = new ArrayList<>(programmingDeck);// put current cards into hand
            this.programmingDeck.clear(); // clear hand
            loadDischargePileIntoProgrammingDeck();
            for(int i = cardAmount; i < 9; i++){
                handCards.add(this.programmingDeck.get(this.programmingDeck.size()-1));
                //if damage cards dont add to the pile but only if they are played int the register so do it later in the register
//                this.dischargePile.add(this.programmingDeck.getLast()); // maybe better todo later to play from the register
                this.programmingDeck.removeLast();
            }
        }else{// if the programming deck has more or nine cards draw them
            System.out.println("PROGRAMMINGCARDDECK.drawHandCards(): remaining Deck size >= 9");
            for(int i = 0; i < 9; i++){
                handCards.add(this.programmingDeck.getLast());
//                this.dischargePile.add(this.programmingDeck.getLast());// maybe better todo later to play from the register
                this.programmingDeck.removeLast();
            }
        }
        this.dischargePile.addAll(handCards);
        System.out.println("hand:" + handCards);
        return handCards;
    }

    private void loadDischargePileIntoProgrammingDeck(){
        //programmingDeck = dischargePile;
        programmingDeck.addAll(dischargePile);
        dischargePile.clear();
//        shuffleProgrammingDeck();
        Collections.shuffle(programmingDeck);
    }

    public ArrayList<Cards> getProgrammingDeck(){
        return programmingDeck;
    }

    public void removeDamage(String damage){
        switch (damage){
            case "spam":
                for (int i = 0; i < dischargePile.size(); i++) {
                    if (dischargePile.get(i) instanceof SpamCard) {
                        dischargePile.remove(i);
                        System.out.println("Removed a SpamCard from the discharge pile.");
                        return; // Exit after removing the first matching card
                    }
                }
                System.out.println("No SpamCard found in the discharge pile.");
                break;
            case "virus":
                for (int i = 0; i < dischargePile.size(); i++) {
                    if (dischargePile.get(i) instanceof VirusCard) {
                        dischargePile.remove(i);
                        System.out.println("Removed a VirusCard from the discharge pile.");
                        return; // Exit after removing the first matching card
                    }
                }
                System.out.println("No VirusCard found in the discharge pile.");
                break;
            case "trojanhorse":
                for (int i = 0; i < dischargePile.size(); i++) {
                    if (dischargePile.get(i) instanceof TrojanHorseCard) {
                        dischargePile.remove(i);
                        System.out.println("Removed a TorjanhorseCard from the discharge pile.");
                        return; // Exit after removing the first matching card
                    }
                }
                System.out.println("No TorjanhorseCard found in the discharge pile.");
                break;
            case "worm":
                for (int i = 0; i < dischargePile.size(); i++) {
                    if (dischargePile.get(i) instanceof WormCard) {
                        dischargePile.remove(i);
                        System.out.println("Removed a WormCard from the discharge pile.");
                        return; // Exit after removing the first matching card
                    }
                }
                System.out.println("No WormCard found in the discharge pile.");
                break;
            default:
                System.out.println("Invalid damage type: " + damage);
                break;
        }
    }

    public static void main(String[] args) {
        ProgrammingCardDeck programmingCardDeck = new ProgrammingCardDeck();
        programmingCardDeck.initializeProgrammingDeck();
        programmingCardDeck.dischargePile.add(new SpamCard());
        programmingCardDeck.dischargePile.add(new SpamCard());
        programmingCardDeck.removeDamage("spam");
        programmingCardDeck.removeDamage("spam");
        programmingCardDeck.removeDamage("spam");
    }

}
