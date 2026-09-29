package com.example.tictactoe2;

import framework.Game;
import java.util.List;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

// Shows one button for each game we have.
// The list of games comes from the GameManager (in the framework package).
public class GameSelectController implements Initializable {

    @FXML
    private VBox gamesBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Game> games = Session.getGameManager().getGames();

        for (int i = 0; i < games.size(); i++) {
            final Game game = games.get(i);

            Button button = new Button(game.getName());
            button.setMaxWidth(220.0);
            button.setPrefHeight(40.0);

            // When the button is clicked, open that game.
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    openGame(game);
                }
            });

            gamesBox.getChildren().add(button);
        }
    }

    private void openGame(Game game) {
        // Each game has its own screen. Check the name to know which one to open.
        if (game.getName().equals("Tic-Tac-Toe")) {
            Navigator.goTo("/TicTacToe.fxml");
        }
    }

    @FXML
    private void onBack() {
        Navigator.goTo("/MainMenu.fxml");
    }
}
