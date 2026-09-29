package com.example.tictactoe2;

import games.tictactoe.Board;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * JavaFX view over the ported {@link Board} logic. This is the JavaFX
 * equivalent of the old Swing GameScreen: it renders a 3x3 grid of buttons
 * and delegates all rules (placing marks, win/draw detection) to Board.
 */
public class TicTacToeController implements Initializable {

    @FXML
    private GridPane grid;

    @FXML
    private Label lblStatus;

    private Board board;
    private char currentPlayer;
    private boolean gameOver;

    private final Button[][] buttons = new Button[3][3];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                final int r = row;
                final int c = col;

                Button button = new Button(" ");
                button.setPrefSize(90, 90);

                // When this square is clicked, play a move there.
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        handleClick(r, c);
                    }
                });

                buttons[row][col] = button;
                grid.add(button, col, row);
            }
        }
        resetGame();
    }

    private void handleClick(int row, int col) {
        if (gameOver) {
            return;
        }
        if (!board.placeMark(row, col, currentPlayer)) {
            return;
        }

        buttons[row][col].setText(String.valueOf(currentPlayer));

        if (board.hasWinner(currentPlayer)) {
            lblStatus.setText("Player " + currentPlayer + " wins!");
            gameOver = true;
            return;
        }
        if (board.isFull()) {
            lblStatus.setText("It's a draw!");
            gameOver = true;
            return;
        }

        if (currentPlayer == 'X') {
            currentPlayer = 'O';
        } else {
            currentPlayer = 'X';
        }
        lblStatus.setText("Player " + currentPlayer + "'s turn");
    }

    @FXML
    private void onReset() {
        resetGame();
    }

    @FXML
    private void onBack() {
        Navigator.goTo("/SelectMenu.fxml");
    }

    private void resetGame() {
        board = new Board();
        currentPlayer = 'X';
        gameOver = false;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col].setText(" ");
            }
        }
        lblStatus.setText("Player X's turn");
    }
}
