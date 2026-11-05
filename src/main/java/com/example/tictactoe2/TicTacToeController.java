package com.example.tictactoe2;

import framework.GameSave;
import framework.Scoreboard;
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
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * JavaFX controller for Tic-Tac-Toe.
 * Handles local games, computer games and online games.
 */
public class TicTacToeController implements Initializable, ServerListener {

    @FXML
    private GridPane grid;

    @FXML
    private Label lblStatus;

    @FXML
    private Pane bgShapesPane;

    @FXML
    private HBox messageBox;

    @FXML
    private TextField txtMessage;


    private Board board;
    private char currentPlayer;
    private boolean gameOver;

    // Computers
    private Computer computer;
    private Computer computerO;

    private boolean isCvcMode = false;
    private boolean isVsComputer = true;

    private PauseTransition cvcPause;

    // Online game
    private boolean isOnlineMode = false;
    private ServerConnection serverConnection;

    private boolean isMyTurn = false;

    private char mySymbol = 'X';
    private char opponentSymbol = 'O';

    private boolean isOnlineAi = false;

    private static final int AI_MOVE_DELAY_MS = 800;

    private PauseTransition aiPause;

    // Scoreboard
    private final Scoreboard scoreBoard = new Scoreboard();

    // Prevents saving the same match more than once
    private boolean scoreSaved = false;

    // Game buttons
    private final Button[][] buttons = new Button[3][3];


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        BackgroundShapes.createBackgroundShapes(bgShapesPane);

        computer = new Computer();
        computerO = new Computer();

        isVsComputer = Session.isVsComputer();
        isCvcMode = Session.isCvC();

        if (isVsComputer || isCvcMode) {
            computer.setDifficulty(Session.getDifficulty());
            computerO.setDifficulty(Session.getDifficulty());
        }

        // Create the 3x3 game board
        for (int row = 0; row < 3; row++) {

            for (int col = 0; col < 3; col++) {

                final int r = row;
                final int c = col;

                Button button = new Button(" ");
                button.setPrefSize(90, 90);

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
        isOnlineMode = Session.isOnline();

        if (isOnlineMode) {
            initOnline();
        } else {
            initLocal();
        }
    }


    // =========================================================
    // INITIALIZATION
    // =========================================================

    private void initLocal() {

        computer = new Computer();
        computerO = new Computer();

        isVsComputer = Session.isVsComputer();
        isCvcMode = Session.isCvC();

        System.out.println("Player vs Computer: " + isVsComputer);
        System.out.println("Computer vs Computer: " + isCvcMode);

        messageBox.setVisible(false);
        messageBox.setManaged(false);

        if (isVsComputer || isCvcMode) {
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


    private void initOnline() {

        resetBoardUI();

        scoreSaved = false;

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

        if (Session.isTournament()) {
            lblStatus.setText("Verbonden. Wachten tot het toernooi start...");
        } else {
            lblStatus.setText("Wachten op tegenstander...");
        }

        isMyTurn = false;
    }


    // =========================================================
    // SAVE AND LOAD GAME
    // =========================================================

    private boolean loadSavedGame() {

        GameSave save = GameSave.load(getUserName());

        if (save == null || save.boardState == null ||
                save.boardState.length() != 9) {

            return false;
        }

        stopCvc();

        board = new Board();
        gameOver = false;
        scoreSaved = false;

        for (int row = 0; row < 3; row++) {

            for (int col = 0; col < 3; col++) {

                char mark = save.boardState.charAt(row * 3 + col);

                board.setCell(row, col, mark);

                buttons[row][col].setText(
                        mark == ' ' ? " " : String.valueOf(mark)
                );
            }
        }

        currentPlayer = save.currentPlayer;

        GameSave.delete(getUserName());

        lblStatus.setText(
                (isCvcMode ? "Computer " : "Player ")
                        + currentPlayer + "'s turn"
        );

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


    // =========================================================
    // SCOREBOARD
    // =========================================================

    /**
     * Saves the result of a finished game in the database.
     * <p>
     * WIN  - the logged-in player won.
     * LOSS - the logged-in player lost.
     * DRAW - the game ended in a draw.
     * <p>
     * Computer vs Computer games are also saved in the history,
     * but their results are not counted as player wins.
     */
//    private void saveResult(String result) {
//
//        // Prevent duplicate database entries
//        if (scoreSaved) {
//            return;
//        }
//
//        String username = getUserName();
//
//        if (username == null || username.isBlank()) {
//
//            System.err.println(
//                    "Score niet opgeslagen: geen ingelogde gebruiker."
//            );
//
//            return;
//        }
//
//        String gameMode = getGameMode();
//
//        scoreBoard.saveScore(
//                username,
//                "Tic-Tac-Toe",
//                gameMode,
//                result
//        );
//
//        scoreSaved = true;
//
//        System.out.println(
//                "Score opgeslagen: " + username
//                        + " | " + gameMode
//                        + " | " + result
//        );
//    }


    private void saveResult(String result) {

        // Prevent duplicate database entries.
        if (scoreSaved) {
            return;
        }

        String username = getUserName();

        if (username == null || username.isBlank()) {

            System.err.println(
                    "Score niet opgeslagen: geen ingelogde gebruiker."
            );

            return;
        }

        String gameMode = getGameMode();

        boolean saved = scoreBoard.saveScore(
                username,
                "Tic-Tac-Toe",
                gameMode,
                result
        );

        // Mark the result as saved only if the database insert succeeds.
        if (saved) {

            scoreSaved = true;

            System.out.println(
                    "Score opgeslagen: " + username
                            + " | " + gameMode
                            + " | " + result
            );
        }
    }

    /**
     * Determines which game mode was played.
     */
    private String getGameMode() {

        if (isOnlineMode) {

            if (Session.isTournament()) {
                return "Online Tournament";
            }

            if (isOnlineAi) {
                return "Online vs Computer";
            }

            return "Online Player vs Player";
        }

        if (isCvcMode) {
            return "Computer vs Computer";
        }

        if (isVsComputer) {
            return "Player vs Computer";
        }

        return "Player vs Player";
    }


    /**
     * Converts the server result into a standard database value.
     */
    private String normalizeOnlineResult(String result) {

        if (result == null || result.isBlank()) {
            return "UNKNOWN";
        }

        String normalized = result.trim().toUpperCase();

        if (normalized.contains("DRAW") || normalized.contains("TIE")) {
            return "DRAW";
        }

        if (normalized.equals("WIN") || normalized.contains("WON")) {
            return "WIN";
        }

        if (normalized.equals("LOSS") || normalized.contains("LOSE")) {
            return "LOSS";
        }

        return normalized;
    }


    // =========================================================
    // HANDLE PLAYER MOVES
    // =========================================================

    private void handleClick(int row, int col) {

        if (isOnlineMode) {

            handleOnlineClick(row, col);

        } else {

            handleLocalClick(row, col);
        }
    }


    private void handleLocalClick(int row, int col) {

        if (gameOver || isCvcMode) {
            return;
        }

        // The computer plays as O
        if (isVsComputer && currentPlayer == 'O') {
            return;
        }

        if (!board.placeMark(row, col, currentPlayer)) {
            return;
        }

        buttons[row][col].setText(String.valueOf(currentPlayer));

        // Check whether the current player has won
        if (board.hasWinner(currentPlayer)) {

            lblStatus.setText("Player " + currentPlayer + " wins!");

            gameOver = true;

            highlightWinningCombination();

            // The logged-in player is X.
            // This works for Player vs Computer and local Player vs Player.
            if (currentPlayer == 'X') {
                saveResult("WIN");
            } else {
                saveResult("LOSS");
            }

            return;
        }

        // Check for a draw
        if (board.isFull()) {

            lblStatus.setText("It's a draw!");

            gameOver = true;

            saveResult("DRAW");

            return;
        }

        // Switch player
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';

        lblStatus.setText("Player " + currentPlayer + "'s turn");

        // Computer makes its move
        if (isVsComputer && !gameOver && currentPlayer == 'O') {

            makeComputerMove();
        }
    }


    // =========================================================
    // COMPUTER MOVE
    // =========================================================

    private void makeComputerMove() {

        if (gameOver) {
            return;
        }

        int[] move = computer.getBestMove(board, currentPlayer, 'X');

        if (move == null) {
            return;
        }

        int row = move[0];
        int col = move[1];

        if (!board.placeMark(row, col, currentPlayer)) {
            return;
        }

        buttons[row][col].setText(String.valueOf(currentPlayer));

        // Computer wins
        if (board.hasWinner(currentPlayer)) {

            lblStatus.setText("Player " + currentPlayer + " wins!");

            gameOver = true;

            highlightWinningCombination();

            saveResult("LOSS");

            return;
        }

        // Draw
        if (board.isFull()) {

            lblStatus.setText("It's a draw!");

            gameOver = true;

            saveResult("DRAW");

            return;
        }

        // Return to player X
        currentPlayer = 'X';

        lblStatus.setText("Player " + currentPlayer + "'s turn");
    }


    // =========================================================
    // COMPUTER VS COMPUTER
    // =========================================================

    private void playCvcTurn() {

        if (gameOver || !isCvcMode || isOnlineMode) {
            return;
        }

        Computer activeComputer =
                (currentPlayer == 'X') ? computer : computerO;

        char opponentMark = (currentPlayer == 'X') ? 'O' : 'X';

        int[] move = activeComputer.getBestMove(
                board,
                currentPlayer,
                opponentMark
        );

        if (move == null) {
            return;
        }

        int row = move[0];
        int col = move[1];

        if (!board.placeMark(row, col, currentPlayer)) {
            return;
        }

        buttons[row][col].setText(String.valueOf(currentPlayer));

        // Computer wins
        if (board.hasWinner(currentPlayer)) {

            lblStatus.setText("Computer " + currentPlayer + " wins!");

            gameOver = true;

            highlightWinningCombination();

            // Save the finished CVC match in the game history
            saveResult("COMPUTER_" + currentPlayer + "_WIN");

            return;
        }

        // Draw
        if (board.isFull()) {

            lblStatus.setText("It's a draw!");

            gameOver = true;

            saveResult("DRAW");

            return;
        }

        // Switch computer
        currentPlayer = opponentMark;

        lblStatus.setText("Computer " + currentPlayer + "'s turn");

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


    // =========================================================
    // ONLINE GAME
    // =========================================================

    private void handleOnlineClick(int row, int col) {

        if (isOnlineAi) {
            return;
        }

        if (gameOver || !isMyTurn) {
            return;
        }

        if (!buttons[row][col].getText().trim().isEmpty()) {
            return;
        }

        if (serverConnection == null) {
            return;
        }

        int moveIndex = row * 3 + col;

        isMyTurn = false;

        lblStatus.setText("Zet versturen...");

        serverConnection.sendMove(moveIndex);
    }


    @FXML
    private void onSendMessage() {

        if (!isOnlineMode || serverConnection == null) {
            return;
        }

        String text = txtMessage.getText().trim();

        if (text.isEmpty()) {
            return;
        }

        serverConnection.sendMessage(text);

        txtMessage.clear();
    }


    // =========================================================
    // HIGHLIGHT WINNING COMBINATION
    // =========================================================

    private void highlightWinningCombination() {

        String mark = String.valueOf(currentPlayer);

        String highlightStyle =
                "-fx-background-color: #ba5a5a; "
                        + "-fx-text-fill: black; "
                        + "-fx-font-weight: bold;";

        // Rows
        for (int r = 0; r < 3; r++) {

            if (buttons[r][0].getText().equals(mark)
                    && buttons[r][1].getText().equals(mark)
                    && buttons[r][2].getText().equals(mark)) {

                buttons[r][0].setStyle(highlightStyle);
                buttons[r][1].setStyle(highlightStyle);
                buttons[r][2].setStyle(highlightStyle);

                return;
            }
        }

        // Columns
        for (int c = 0; c < 3; c++) {

            if (buttons[0][c].getText().equals(mark)
                    && buttons[1][c].getText().equals(mark)
                    && buttons[2][c].getText().equals(mark)) {

                buttons[0][c].setStyle(highlightStyle);
                buttons[1][c].setStyle(highlightStyle);
                buttons[2][c].setStyle(highlightStyle);

                return;
            }
        }

        // Diagonal: top-left to bottom-right
        if (buttons[0][0].getText().equals(mark)
                && buttons[1][1].getText().equals(mark)
                && buttons[2][2].getText().equals(mark)) {

            buttons[0][0].setStyle(highlightStyle);
            buttons[1][1].setStyle(highlightStyle);
            buttons[2][2].setStyle(highlightStyle);

            return;
        }

        // Diagonal: top-right to bottom-left
        if (buttons[0][2].getText().equals(mark)
                && buttons[1][1].getText().equals(mark)
                && buttons[2][0].getText().equals(mark)) {

            buttons[0][2].setStyle(highlightStyle);
            buttons[1][1].setStyle(highlightStyle);
            buttons[2][0].setStyle(highlightStyle);
        }
    }


    // =========================================================
    // BUTTONS
    // =========================================================

    @FXML
    private void onReset() {

        if (Session.isTournament()) {
            return;
        }

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
    private void onBack() {

        stopCvc();

        stopAiPause();

        if (isOnlineMode) {

            if (serverConnection != null) {
                serverConnection.forfeit();
            }

        } else {

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


    @FXML
    private void openScoreBoard() {

        stopCvc();

        stopAiPause();

        Navigator.goTo("/ScoreBoard.fxml");
    }


    @FXML
    private void stopAiPause() {

        if (aiPause != null) {

            aiPause.stop();

            aiPause = null;
        }
    }


    // =========================================================
    // RESET GAME
    // =========================================================

    private void resetGame() {

        // A new match can now be saved
        scoreSaved = false;

        stopCvc();

        stopAiPause();

        clearBoardUI();

        currentPlayer = 'X';

        lblStatus.setText(
                isCvcMode
                        ? "Computer X's turn"
                        : "Player X's turn"
        );

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

                buttons[row][col].setStyle("");
            }
        }
    }


    // =========================================================
    // SERVER LISTENER
    // =========================================================

    @Override
    public void onMatchStarted(
            String gameType,
            String opponent,
            boolean isStartingPlayer
    ) {

        Platform.runLater(() -> {

            resetBoardUI();

            // A new online match has started
            scoreSaved = false;

            if (isStartingPlayer) {

                mySymbol = 'X';
                opponentSymbol = 'O';

                lblStatus.setText(
                        "Match tegen " + opponent + "! Jij bent X (begint)"
                );

            } else {

                mySymbol = 'O';
                opponentSymbol = 'X';

                lblStatus.setText(
                        "Match tegen " + opponent + "! Jij bent O"
                );
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

                aiPause = new PauseTransition(
                        Duration.millis(AI_MOVE_DELAY_MS)
                );

                aiPause.setOnFinished(e -> {

                    if (gameOver || !isMyTurn || serverConnection == null) {
                        return;
                    }

                    int[] move = computer.getBestMove(
                            board,
                            mySymbol,
                            opponentSymbol
                    );

                    if (move == null) {
                        return;
                    }

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

            if (move < 0 || move >= 9) {
                return;
            }

            int row = move / 3;
            int col = move % 3;

            boolean isSelf = (
                    serverConnection != null
                            && player != null
                            && player.equalsIgnoreCase(getUserName())
            );

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

            // Save the online result in the database
            String databaseResult = normalizeOnlineResult(result);

            saveResult(databaseResult);

            if ("WIN".equals(databaseResult)) {

                lblStatus.setText("Gewonnen! " + comment);

            } else if ("LOSS".equals(databaseResult)) {

                lblStatus.setText("Verloren! " + comment);

            } else if ("DRAW".equals(databaseResult)) {

                lblStatus.setText("Gelijkspel! " + comment);

            } else {

                lblStatus.setText(comment);
            }
        });
    }


    @Override
    public void onError(String errorMessage) {

        Platform.runLater(() ->
                lblStatus.setText("Fout: " + errorMessage)
        );
    }


    @Override
    public void onConnected() {
    }


    @Override
    public void onChallengeReceived(
            String challenger,
            String gameType,
            String challengeNumber
    ) {
    }


    @Override
    public void onChallengeCancelled(String challengeNumber) {
    }


    @Override
    public void onGameListReceived(List<String> games) {
    }


    @Override
    public void onPlayerListReceived(List<String> players) {
    }


    // =========================================================
    // USER
    // =========================================================

    private String getUserName() {

        if (Session.getUser() != null) {
            return Session.getUser().getUsername();
        }

        System.err.println("Score niet opgeslagen: geen ingelogde gebruiker.");
        return "";
    }
}