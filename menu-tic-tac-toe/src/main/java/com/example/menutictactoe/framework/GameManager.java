package com.example.menutictactoe.framework;

import com.example.menutictactoe.tictactoe.TicTacToe;

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