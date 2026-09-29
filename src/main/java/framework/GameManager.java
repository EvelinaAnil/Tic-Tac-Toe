package framework;

import games.tictactoe.TicTacToe;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the collection of available games within the framework.
 *
 * <p>This class initializes and holds references to all playable games
 * (such as Tic-Tac-Toe) so that the UI can discover and launch them dynamically.</p>
 */

public class GameManager {

    private final List<Game> games = new ArrayList<>();

    public GameManager() {
        games.add(new TicTacToe());
    }

    public List<Game> getGames() {
        return games;
    }
}
