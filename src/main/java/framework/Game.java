package framework;

/**
 * A game that can be plugged into the framework.
 *
 * <p>The original console version required a {@code Scanner} to drive input.
 * The JavaFX frontend drives games through their own screens instead, so a
 * game only needs to expose its display name here. Each game supplies its own
 * FXML screen (see the controllers in {@code com.example.tictactoe2}).</p>
 */
public abstract class Game {
    public abstract String getName();
}
