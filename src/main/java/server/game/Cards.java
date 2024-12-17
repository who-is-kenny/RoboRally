package server.game;

public abstract class Cards {
    private final String name;

    // Constructor
    public Cards(String name) {
        this.name = name;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Abstract method for the card's effect
    public abstract void playCardEffect(Player player);
}

