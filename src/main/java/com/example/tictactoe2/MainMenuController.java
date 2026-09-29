package com.example.tictactoe2;

import framework.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main menu. Mirrors the old Swing MainMenu: greet the user and offer
 * "choose a game", "scores", and "log out".
 */
public class MainMenuController implements Initializable {

    @FXML
    private Label lblWelcome;

    @FXML
    private Label lblStatus;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User user = Session.get().getUser();
        String name = user != null ? user.getUsername() : "player";
        lblWelcome.setText("Welcome " + name);
    }

    @FXML
    private void onPlay() {
        Navigator.goTo("/GameSelect.fxml");
    }

    @FXML
    private void onScores() {
        lblStatus.setText("Scores coming soon.");
    }

    @FXML
    private void onLogout() {
        Session.get().setUser(null);
        Navigator.goTo("/Login.fxml");
    }
}
