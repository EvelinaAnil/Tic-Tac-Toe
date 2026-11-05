package com.example.tictactoe2;

import framework.GameSave;
import framework.network.ServerConnection;
import framework.network.ServerListener;
import games.tictactoe.Board;
import games.tictactoe.Computer;
import games.tictactoe.Engine;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;


import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class TicTacToeController implements Initializable, ServerListener {

    @FXML
    private GridPane grid;

    @FXML
    private Label lblStatus;

    @FXML
    private Pane bgShapesPane;

    @FXML
    private HBox messageBox;

    @FXML private TextField txtMessage;    @FXML
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

    private Engine engine;
    private boolean isCvcMode = false;
    private boolean isVsComputer = true;

    private PauseTransition cvcPause;

    private Board onlineBoard;
    private boolean onlineGameOver;
    private boolean isOnlineMode = false;
    private ServerConnection serverConnection;
    private boolean isMyTurn = false;
    private char mySymbol = 'X';
    private char opponentSymbol = 'O';
    private boolean isOnlineAi = false;
    private Computer onlineAi;
    private static final int AI_MOVE_DELAY_MS = 800;
    private PauseTransition aiPause;

    private final Button[][] buttons = new Button[3][3];

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        BackgroundShapes.createBackgroundShapes(bgShapesPane);

        isVsComputer = Session.isVsComputer();
        isCvcMode = Session.isCvC();

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
        isVsComputer = Session.isVsComputer();
        isCvcMode = Session.isCvC();

        messageBox.setVisible(false);
        messageBox.setManaged(false);

        engine = new Engine(isVsComputer, isCvcMode, Session.getDifficulty());

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
        engine.restore(save.boardState, save.currentPlayer);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                char mark = save.boardState.charAt(row * 3 + col);
                buttons[row][col].setText(mark == ' ' ? " " : String.valueOf(mark));
            }
        }

        GameSave.delete(getUserName());

        char currentPlayer = engine.getCurrentPlayer();
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
        save.boardState = engine.serializeBoard();
        save.currentPlayer = engine.getCurrentPlayer();
        save.vsComputer = isVsComputer;
        save.cvc = isCvcMode;
        save.difficulty = Session.getDifficulty();
        GameSave.save(getUserName(), save);
    }

    private void initOnline() {
        resetBoardUI();
        isOnlineAi = Session.isCvC();
        if (isOnlineAi) {
            onlineAi = new Computer();
            onlineAi.setDifficulty(Session.getDifficulty());
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

    private void handleClick(int row, int col) {
        if (isOnlineMode) {
            handleOnlineClick(row, col);
        } else {
            handleLocalClick(row, col);
        }
    }

    private void handleOnlineClick(int row, int col) {
        if (isOnlineAi) return;
        if (onlineGameOver || !isMyTurn) {
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
        Engine.MoveResult result = engine.playerMove(row, col);
        if (!result.accepted) {
            return;
        }

        renderMove(result, "Player ");

        if (!result.isGameOver() && isVsComputer && engine.getCurrentPlayer() == 'O') {
            makeComputerMove();
        }
    }

    private void makeComputerMove() {
        Engine.MoveResult result = engine.computerMove();
        if (!result.accepted) {
            return;
        }
        renderMove(result, "Player ");
    }

    private void playCvcTurn() {
        if (engine.isGameOver() || !isCvcMode || isOnlineMode) {
            return;
        }

        Engine.MoveResult result = engine.computerMove();
        if (!result.accepted) {
            return;
        }

        renderMove(result, "Computer ");

        if (!result.isGameOver()) {
            // vertraging van 600ms tussen beurten zodat je de animatie/stappen ziet
            scheduleCvcTurn(600);
        }
    }

    private void renderMove(Engine.MoveResult result, String actorLabel) {
        buttons[result.row][result.col].setText(String.valueOf(result.mark));

        if (result.win) {
            lblStatus.setText(actorLabel + result.mark + " wins!");
            highlightWinningCombination(result.mark);
            return;
        }
        if (result.draw) {
            lblStatus.setText("It's a draw!");
            return;
        }
        lblStatus.setText(actorLabel + result.nextPlayer + "'s turn");
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

    private void highlightWinningCombination(char winner) {
        String mark = String.valueOf(winner);
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
            if (!engine.isGameOver() && !isCvcMode && engine.isStarted()) {
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
        engine.reset();
        clearBoardUI();
        lblStatus.setText(isCvcMode ? "Computer X's turn" : "Player X's turn");

        if (isCvcMode) {
            scheduleCvcTurn(500);
        }
    }

    private void resetBoardUI() {
        onlineBoard = new Board();
        onlineGameOver = false;
        clearBoardUI();
        isMyTurn = false;
    }

    private void clearBoardUI() {
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
                   if (onlineGameOver || !isMyTurn) {
                       return;
                   }
                   int[] move = onlineAi.getBestMove(onlineBoard, mySymbol, opponentSymbol);
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

            if (onlineBoard != null) {
                onlineBoard.placeMark(row, col, symbol);
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
            onlineGameOver = true;
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
