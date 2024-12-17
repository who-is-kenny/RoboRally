package server.game;

import java.util.ArrayList;

public class Register {

    private ArrayList<Cards> cards;

    // Constructor
    public Register() {
        // Initialize the ArrayList with a capacity of 5
        cards = new ArrayList<>(5);

        // Optionally, populate with null or placeholders
        for (int i = 0; i < 5; i++) {
            cards.add(null); // Initially, the slots are empty
        }
    }

    // Getter for the cards list
    public ArrayList<Cards> getCards() {
        return cards;
    }

    // Method to set a card in a specific register slot

    public void setCard(int index, Cards card) {
        if (index < 0 || index >= cards.size()) {
            throw new IndexOutOfBoundsException("Index must be between 0 and 4.");
        }
        cards.set(index, card);
    }

    // Method to get a card from a specific register slot
    public Cards getCard(int index) {
        if (index < 0 || index >= cards.size()) {
            throw new IndexOutOfBoundsException("Index must be between 0 and 4.");
        }
        return cards.get(index);
    }

    //resets register to all null like in the start
    public void resetRegisters() {
        for (int i = 0; i < cards.size(); i++) {
            cards.set(i, null);
        }
    }

    //print register
    public void printCardNames() {
        System.out.println("Register Contents:");
        for (int i = 0; i < cards.size(); i++) {
            Cards card = cards.get(i);
            if (card != null) {
                System.out.println("Slot " + i + ": " + card.getName());
            } else {
                System.out.println("Slot " + i + ": Empty");
            }
        }
    }

    public static void main(String[] args) {
//        Register register = new Register();
//        Cards move1 = new Move1Card();
//        Cards move2 = new Move2Card();
//
//        Cards move3 = new Move3Card();
//
//
//        register.setCard(0,move1);
//        register.setCard(3,move2);
//        register.setCard(4,move3);
//        register.printCardNames();
//        register.resetRegisters();
//
//        register.printCardNames();
    }


}

