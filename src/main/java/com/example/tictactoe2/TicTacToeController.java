package com.example.tictactoe2;

import framework.GameSave;
import framework.network.ServerConnection;
import framework.network.ServerListener;
import games.tictactoe.Board;
import games.tictactoe.Computer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import javafx.scene.layout.Pane;


import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * JavaFX view over the ported {@link Board} logic. This is the JavaFX
 * equivalent of the old Swing GameScreen: it renders a 3x3 grid of buttons
 * and delegates all rules (placing marks, win/draw detection) to Board.
 */
public class TicTacToeController implements Initializable, ServerListener {

    @FXML
    private GridPane grid;

    @FXML
    private Label lblStatus;

    @FXML
    private Pane bgShapesPane;

    private Board board;
    private char currentPlayer;
    private boolean gameOver;

    // declaratie van de computer
    private Computer computer;
    private Computer computerO;
    private boolean isCvcMode = false;
    private  boolean isVsComputer = true;

    private PauseTransition cvcPause; // Animatie timer voor CvC zetten

    private boolean isOnlineMode = false;
    private ServerConnection serverConnection;
    private boolean isMyTurn = false;
    private char mySymbol = 'X';
    private char opponentSymbol = 'O';
    private boolean isOnlineAi = false;
    private static final int AI_MOVE_DELAY_MS = 800;
    private PauseTransition aiPause;

    /**
     * JavaFX view over the ported {@link Board} logic. This is the JavaFX
     * equivalent of the old Swing GameScreen: it renders a 3x3 grid of buttons
     * and delegates all rules (placing marks, win/draw detection) to Board.
     */



    private final Button[][] buttons = new Button[3][3];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        BackgroundShapes.createBackgroundShapes(bgShapesPane);

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

        serverConnection = Session.getServerConnection();
        isOnlineMode = (serverConnection != null);

        this.isOnlineMode = Session.isOnline();

        if (isOnlineMode) {
            initOnline();
        } else {
            initLocal();
        }
    }

    private void initLocal() {
        computer = new Computer();
        computerO = new Computer();

        isVsComputer = Session.isVsComputer();
        isCvcMode = Session.isCvC();

        // instellingen op uit de Session
        if (isVsComputer) {
            computer.setDifficulty(Session.getDifficulty());
            computerO.setDifficulty(Session.getDifficulty());
        }

        if (Session.isLoadSavedGame()) {
            Session.setLoadSavedGame(false);
            if (loadSavedGame()) {
                return;
            }
        }

        resetGame();
    }

    private boolean loadSavedGame() {
        GameSave save = GameSave.load(getUserName());
        if (save == null || save.boardState == null || save.boardState.length() != 9) {
            return false;
        }

        stopCvc();
        board = new Board();
        gameOver = false;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                char mark = save.boardState.charAt(row * 3 + col);
                board.setCell(row, col, mark);
                buttons[row][col].setText(mark == ' ' ? " " : String.valueOf(mark));
            }
        }

        currentPlayer = save.currentPlayer;
        GameSave.delete(getUserName());

        lblStatus.setText((isCvcMode ? "Computer " : "Player ") + currentPlayer + "'s turn");

        if (isCvcMode) {
            scheduleCvcTurn(500);
        } else if (isVsComputer && currentPlayer == 'O') {
            makeComputerMove();
        }
        return true;
    }

    private void saveGame() {
        GameSave save = new GameSave();
        StringBuilder state = new StringBuilder();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                state.append(board.getCell(row, col));
            }
        }
        save.boardState = state.toString();
        save.currentPlayer = currentPlayer;
        save.vsComputer = isVsComputer;
        save.cvc = isCvcMode;
        save.difficulty = Session.getDifficulty();
        GameSave.save(getUserName(), save);
    }

    private void initOnline() {
        resetBoardUI();
        isOnlineAi = Session.isCvC();
        if (isOnlineAi) {
            computer = new Computer();
            computer.setDifficulty(Session.getDifficulty());
        }
        serverConnection = Session.getServerConnection();

        if (serverConnection != null) {
            serverConnection.setListener(this);
            lblStatus.setText("Wachten op tegenstander...");
        } else {
            lblStatus.setText("Geen verbinding met de server.");
        }

        isMyTurn = false;
    }

    private void handleClick(int row, int col) {
        if (isOnlineMode) {
            handleOnlineClick(row, col);
        } else {
            handleLocalClick(row, col);
        }
    }

    private void handleOnlineClick(int row, int col) {
        if (isOnlineAi) return;
        if (gameOver || !isMyTurn) {
            return;
        }

        if (!buttons[row][col].getText().trim().isEmpty()) {
            return;
        }

        int moveIndex = row * 3 + col;
        isMyTurn = false;
        lblStatus.setText("Zet versturen...");

        serverConnection.sendMove(moveIndex);
    }

    private void handleLocalClick(int row, int col) {
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
            highlightWinningCombination();
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
            highlightWinningCombination();
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
        if (gameOver || !isCvcMode || isOnlineMode) {
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
            highlightWinningCombination();
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
        scheduleCvcTurn(600);
    }

    private void scheduleCvcTurn(int millis) {
        stopCvc();
        cvcPause = new PauseTransition(Duration.millis(millis));
        cvcPause.setOnFinished(e -> playCvcTurn());
        cvcPause.play();
    }

    private void stopCvc() {
        if (cvcPause != null) {
            cvcPause.stop();
            cvcPause = null;
        }
    }

    private void highlightWinningCombination() {
        String mark = String.valueOf(currentPlayer);
        String highlightStyle = "-fx-background-color: #ba5a5a; -fx-text-fill: black; -fx-font-weight: bold;";

        //Rows
        for (int r = 0; r < 3; r++) {
            if (buttons[r][0].getText().equals(mark) &&
                    buttons[r][1].getText().equals(mark) &&
                    buttons[r][2].getText().equals(mark)) {

                buttons[r][0].setStyle(highlightStyle);
                buttons[r][1].setStyle(highlightStyle);
                buttons[r][2].setStyle(highlightStyle);
                return;
            }
        }

        //Columns
        for (int c = 0; c < 3; c++) {
            if (buttons[0][c].getText().equals(mark) &&
                    buttons[1][c].getText().equals(mark) &&
                    buttons[2][c].getText().equals(mark)) {

                buttons[0][c].setStyle(highlightStyle);
                buttons[1][c].setStyle(highlightStyle);
                buttons[2][c].setStyle(highlightStyle);
                return;
            }
        }

        //Bottom-Right Diagonal
        if (buttons[0][0].getText().equals(mark) &&
                buttons[1][1].getText().equals(mark) &&
                buttons[2][2].getText().equals(mark)) {

            buttons[0][0].setStyle(highlightStyle);
            buttons[1][1].setStyle(highlightStyle);
            buttons[2][2].setStyle(highlightStyle);
            return;
        }

        //Bottom-Left Diagonal
        if (buttons[0][2].getText().equals(mark) &&
                buttons[1][1].getText().equals(mark) &&
                buttons[2][0].getText().equals(mark)) {

            buttons[0][2].setStyle(highlightStyle);
            buttons[1][1].setStyle(highlightStyle);
            buttons[2][0].setStyle(highlightStyle);
            return;
        }
    }

    @FXML
    private void onReset() {
        if (isOnlineMode) {
            if (serverConnection != null) {
                serverConnection.subscribe("Tic-tac-toe");
                lblStatus.setText("Zoeken naar nieuwe match...");
            }
        } else {
            resetGame();
        }
    }

    @FXML
    private void stopAiPause() {
        if (aiPause != null) {
            aiPause.stop();
            aiPause = null;
        }
    }

    @FXML
    private void onBack() {
        if (isOnlineMode) {
            if (serverConnection != null) {
                stopAiPause();
                serverConnection.forfeit();
            }
        } else {
            stopCvc();
            if (!gameOver && !isCvcMode && isGameStarted()) {
                Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
                dialog.setTitle("Spel opslaan");
                dialog.setHeaderText(null);
                dialog.setContentText("Wil je het spel opslaan?");

                ButtonType btnYes = new ButtonType("Ja");
                ButtonType btnNo = new ButtonType("Nee");
                dialog.getButtonTypes().setAll(btnYes, btnNo);

                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent() && result.get() == btnYes) {
                    saveGame();
                }
            }
        }
        Navigator.goTo("/SelectMenu.fxml");
    }

    private void resetGame() {
        stopCvc();
        clearBoardUI();
        currentPlayer = 'X';
        lblStatus.setText(isCvcMode ? "Computer X's turn" : "Player X's turn");

        if (isCvcMode) {
            scheduleCvcTurn(500);
        }
    }

    private void resetBoardUI() {
        clearBoardUI();
        isMyTurn = false;
    }

    private boolean isGameStarted() {
        if (board == null) {
            return false;
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board.getCell(row, col) != ' ') {
                    return true;
                }
            }
        }
        return false;
    }

    private void clearBoardUI() {
        board = new Board();
        gameOver = false;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col].setText(" ");
                buttons[row][col].setStyle(""); //clears highlight
            }
        }
    }

    @Override
    public void onMatchStarted(String gameType, String opponent, boolean isStartingPlayer) {
        Platform.runLater(() -> {
            resetBoardUI();
            if (isStartingPlayer) {
                mySymbol = 'X';
                opponentSymbol = 'O';
                lblStatus.setText("Match tegen " + opponent + "! Jij bent X (begint)");
            } else {
                mySymbol = 'O';
                opponentSymbol = 'X';
                lblStatus.setText("Match tegen " + opponent + "! Jij bent O");
            }
        });
    }

    @Override
    public void onYourTurn(String turnMessage) {
        Platform.runLater(() -> {
            isMyTurn = true;
            if (isOnlineAi) {
                lblStatus.setText("AI is thinking.. (" + mySymbol + ")");
               stopAiPause();
               aiPause = new PauseTransition(Duration.millis(AI_MOVE_DELAY_MS));
               aiPause.setOnFinished(e -> {
                   if (gameOver || !isMyTurn) {
                       return;
                   }
                   int[] move = computer.getBestMove(board, mySymbol, opponentSymbol);
                   isMyTurn = false;
                   serverConnection.sendMove(move[0] * 3 + move[1]);
               });
               aiPause.play();
            } else {
                lblStatus.setText("Jouw beurt! (" + mySymbol + ")");
            }
        });
    }

    @Override
    public void onMoveReceived(String player, int move, String details) {
        Platform.runLater(() -> {
            int row = move / 3;
            int col = move % 3;

            boolean isSelf = (serverConnection != null && player.equalsIgnoreCase(getUserName()));
            char symbol = isSelf ? mySymbol : opponentSymbol;

            if (board != null) {
                board.placeMark(row, col, symbol);
            }
            buttons[row][col].setText(String.valueOf(symbol));

            if (!isSelf) {
                lblStatus.setText(player + " heeft gezet.");
            }
        });
    }

    @Override
    public void onGameOver(String result, String comment) {
        Platform.runLater(() -> {
            stopAiPause();
            gameOver = true;
            isMyTurn = false;
            if ("WIN".equalsIgnoreCase(result)) {
                lblStatus.setText("Gewonnen! " + comment);
            } else if ("LOSS".equalsIgnoreCase(result)) {
                lblStatus.setText("Verloren! " + comment);
            } else {
                lblStatus.setText("Gelijkspel! " + comment);
            }
        });
    }

    @Override
    public void onError(String errorMessage) {
        Platform.runLater(() -> lblStatus.setText("Fout: " + errorMessage));
    }

    @Override
    public void onConnected() {}

    @Override
    public void onChallengeReceived(String challenger, String gameType, String challengeNumber) {}

    @Override
    public void onChallengeCancelled(String challengeNumber) {}

    @Override
    public void onGameListReceived(List<String> games) {}

    @Override
    public void onPlayerListReceived(List<String> players) {}

    private String getUserName() {
        if (Session.getUser() != null) {
            return Session.getUser().getUsername();
        }
        return "";
    }
}