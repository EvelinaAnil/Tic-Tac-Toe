package com.example.tictactoe2;

import framework.Game;
import games.tictactoe.TicTacToe;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Lists the games from {@link framework.GameManager} as buttons, mirroring the
 * old Swing GameSelectScreen. Selecting a game opens its screen.
 */
public class GameSelectController implements Initializable {

    @FXML
    private VBox gamesBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (Game game : Session.get().getGameManager().getGames()) {
            Button button = new Button(game.getName());
            button.setMaxWidth(220.0);
            button.setPrefHeight(40.0);
            button.setOnAction(e -> launch(game));
            gamesBox.getChildren().add(button);
        }
    }

    private void launch(Game game) {
        // Each game maps to its own screen. Add more cases as games are added.
        if (game instanceof TicTacToe) {
            Navigator.goTo("/TicTacToe.fxml");
        }
    }

    @FXML
    private void onBack() {
        Navigator.goTo("/MainMenu.fxml");
    }
}
