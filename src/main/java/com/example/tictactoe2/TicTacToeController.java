package com.example.tictactoe2;

import games.tictactoe.Board;
import games.tictactoe.Computer;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * JavaFX view over the ported {@link Board} logic. This is the JavaFX
 * equivalent of the old Swing GameScreen: it renders a 3x3 grid of buttons
 * and delegates all rules (placing marks, win/draw detection) to Board.
 */
public class TicTacToeController implements Initializable {

    @FXML private GridPane grid;

    @FXML private Label lblStatus;

    private Board board;
    private char currentPlayer;
    private boolean gameOver;

    // declaratie van de computer
    private Computer computer;
    private Computer computerO;
    private boolean isCvcMode = false;
    private  boolean isVsComputer = true;

    private Timeline cvcTimeline; // Animatie timer voor CvC zetten

    /**
     * JavaFX view over the ported {@link Board} logic. This is the JavaFX
     * equivalent of the old Swing GameScreen: it renders a 3x3 grid of buttons
     * and delegates all rules (placing marks, win/draw detection) to Board.
     */



    private final Button[][] buttons = new Button[3][3];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        computer = new Computer();
        computerO = new Computer();

        isVsComputer = Session.isVsComputer();
        isCvcMode = Session.isCvC();


        // instellingen op uit de Session
        isVsComputer = Session.isVsComputer();
        if (isVsComputer) {
            computer.setDifficulty(Session.getDifficulty());
            computerO.setDifficulty(Session.getDifficulty());
        }

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
        if (gameOver || isCvcMode) {
            return;
        }

        if (isVsComputer && currentPlayer == 'O'){
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

        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        lblStatus.setText("Player " + currentPlayer + "'s turn");

        if (isVsComputer && !gameOver && currentPlayer == 'O') {
            makeComputerMove();
        }
    }

    private void makeComputerMove(){
        int[] move = computer.getBestMove(board, currentPlayer, 'X');
        int row = move[0];
        int col = move[1];

        // voer de zet uit voor de computer
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

        // terug naar speler X
        currentPlayer = 'X';
        lblStatus.setText("Player " + currentPlayer + "'s turn");

    }

    private void playCvcTurn() {
        if (gameOver || !isCvcMode) {
            return;
        }

        // bepaal welke computer aan de beurt is
        Computer activeComputer = (currentPlayer == 'X') ? computer : computerO;
        char opponentMark = (currentPlayer == 'X') ? 'O' : 'X';

        int[] move = activeComputer.getBestMove(board, currentPlayer, opponentMark);
        if (move == null) return;

        int row = move[0];
        int col = move[1];

        board.placeMark(row, col, currentPlayer);
        buttons[row][col].setText(String.valueOf(currentPlayer));

        if (board.hasWinner(currentPlayer)) {
            lblStatus.setText("Computer " + currentPlayer + " wins!");
            gameOver = true;
            return;
        }

        if (board.isFull()) {
            lblStatus.setText("It's a draw!");
            gameOver = true;
            return;
        }

        // wissel
        currentPlayer = opponentMark;
        lblStatus.setText("Computer " + currentPlayer + "'s turn");

        // vertraging van 600ms tussen beurten zodat je de animatie/stappen ziet
        PauseTransition pause = new PauseTransition(Duration.millis(600));
        pause.setOnFinished(e -> playCvcTurn());
        pause.play();
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
        lblStatus.setText(isCvcMode ? "Computer X's turn" : "Player X's turn");

        if (isCvcMode) {
            PauseTransition startDelay = new PauseTransition(Duration.millis(500));
            startDelay.setOnFinished(e -> playCvcTurn());
            startDelay.play();
        }
    }
}
