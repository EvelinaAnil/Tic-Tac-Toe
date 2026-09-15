package framework;

import games.tictactoe.TicTacToe;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private final List<Game> games = new ArrayList<>();

    public GameManager() {
        games.add(new TicTacToe());
    }

    public List<Game> getGames() {
        return games;
    }
}