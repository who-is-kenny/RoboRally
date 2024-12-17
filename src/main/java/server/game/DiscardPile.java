package server.game;

import java.util.ArrayList;

public class DiscardPile {
    private ArrayList<Cards> discardpile;

    // Constructor
    public DiscardPile() {
        discardpile = new ArrayList<>();
    }

    // Add a card to the discard discardpile
    public void discardCard(Cards card) {
        discardpile.add(card);
    }

    // Add multiple cards to the discard discardpile
    public void discardCards(ArrayList<Cards> cards) {
        discardpile.addAll(cards);
    }

    // Get the size of the discard discardpile
    public int size() {
        return discardpile.size();
    }

    // Retrieve all cards from the discard discardpile (for reshuffling into the deck)
    public ArrayList<Cards> retrieveAllCards() {
        ArrayList<Cards> cards = new ArrayList<>(discardpile);
        discardpile.clear();
        return cards;
    }

    // Check if the discard discardpile is empty
    public boolean isEmpty() {
        return discardpile.isEmpty();
    }
}

