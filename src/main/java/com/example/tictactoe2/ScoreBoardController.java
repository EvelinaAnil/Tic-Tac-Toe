package com.example.tictactoe2;

import framework.Score;
import framework.Scoreboard;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ScoreBoardController implements Initializable {

    @FXML
    private TableView<Score> scoresTable;

    @FXML
    private TableColumn<Score, String> usernameColumn;

    @FXML
    private TableColumn<Score, String> gameColumn;

    @FXML
    private TableColumn<Score, String> gameModeColumn;

    @FXML
    private TableColumn<Score, String> resultColumn;

    @FXML
    private TableColumn<Score, String> dateColumn;

    @FXML
    private ListView<String> highscoresList;

    private final Scoreboard scoreboard = new Scoreboard();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Связываем колонки таблицы с полями класса Score
        usernameColumn.setCellValueFactory(
                new PropertyValueFactory<>("username")
        );

        gameColumn.setCellValueFactory(
                new PropertyValueFactory<>("game")
        );

        gameModeColumn.setCellValueFactory(
                new PropertyValueFactory<>("gameMode")
        );

        resultColumn.setCellValueFactory(
                new PropertyValueFactory<>("result")
        );

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("playedAt")
        );

        // Загружаем данные из базы
        refreshData();
    }

    @FXML
    private void refreshData() {

        scoresTable.setItems(
                FXCollections.observableArrayList(
                        scoreboard.getScores()
                )
        );

        highscoresList.setItems(
                FXCollections.observableArrayList(
                        scoreboard.getHighscores()
                )
        );
    }

    @FXML
    private void goBack() {
        Navigator.goTo("/MainMenu.fxml");
    }

    @FXML
    private void goReturn() {
        Navigator.goTo("/TicTacToe.fxml");
    }
}