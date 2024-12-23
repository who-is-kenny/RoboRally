package server.game;

import java.util.ArrayList;
import java.util.Collections;

public class ProgrammingDeck {
    private ArrayList<Cards> deck;

    // Constructor
    public ProgrammingDeck() {
        deck = new ArrayList<>();

        for (int i = 0; i < 5; i++){ //add move 1 card 5 times
            deck.add(new Move1Card());
        }
        for (int i = 0; i < 3; i++){ //add move 2 card, turn left/right 3 times
            deck.add(new Move2Card());
            deck.add(new TurnLeftCard());
            deck.add(new TurnLeftCard());
        }
        for(int i = 0; i < 2; i++){ // add again card 2 times
            deck.add(new AgainCard());
        }
        deck.add(new Move3Card()); // add 3 move card once
        deck.add(new MoveBackCard()); // add move 1 back card once
        deck.add(new PowerUpCard()); // add power up card once
        deck.add(new UTurnCard());
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

    // Draw 9 cards, ensuring there are enough cards available
    public ArrayList<Cards> drawPlayerCards(DiscardPile discardPile) {
        // Ensure there are at least 9 cards in the deck
        ensureMinimumCards(9, discardPile);


        if (deck.size() < 9) {
            throw new IllegalStateException("Not enough cards in the programming deck to draw 9 cards!");
        }

        ArrayList<Cards> drawnCards = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            drawnCards.add(drawCard());
        }

        return drawnCards;
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

    public static void main(String[] args) {
    }
}
