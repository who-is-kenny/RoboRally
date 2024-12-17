package server.game;

import java.util.ArrayList;
import java.util.Collections;

public class ProgrammingDeck {
    private ArrayList<Cards> deck;

    // Constructor
    public ProgrammingDeck() {
        deck = new ArrayList<>();
    }

    // Add a card to the deck
    public void addCard(Cards card) {
        deck.add(card);
    }

    // Shuffle the deck
    public void shuffle() {
        Collections.shuffle(deck);
    }

    // Draw a card from the top of the deck
    public Cards drawCard() {
        if (deck.isEmpty()) {
            throw new IllegalStateException("The programming deck is empty!");
        }
        return deck.removeLast(); // Remove the last card (top of the deck)
    }

    // Ensure at least 9 cards are in the deck, refilling from discard pile if needed
    public void ensureMinimumCards(int minimum, DiscardPile discardPile) {
        while (deck.size() < minimum) {
            if (discardPile.isEmpty()) {
                break; // No more cards available
            }

            // Retrieve all cards from discard pile and add to the deck
            ArrayList<Cards> retrievedCards = discardPile.retrieveAllCards();
            deck.addAll(retrievedCards);
        }

        // Shuffle the deck after refilling
        shuffle();
    }

    // Check if the deck is empty
    public boolean isEmpty() {
        return deck.isEmpty();
    }

    // Get the size of the deck
    public int size() {
        return deck.size();
    }
}
