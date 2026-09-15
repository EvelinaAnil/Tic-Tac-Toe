package games.tictactoe;

import framework.Game;

public class TicTacToe extends Game {

    @Override
    public String getName() {
        return "Tic-Tac-Toe";
    }

    @Override
    public void start() {
        System.out.println("Tic-Tac-Toe gestart!");
    }
}