package com.example.tictactoe2;

import framework.Game;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

public class SelectMenuController implements Initializable {

    @FXML private Pane headerPane;
    @FXML private HBox headerBox;

    @FXML private Label selectLabel;

    @FXML private VBox redCard;
    @FXML private VBox greenCard;
    @FXML private VBox blueCard;

    @FXML private Circle lCircle;
    @FXML private Polygon lTriangle;
    @FXML private SVGPath lCross;

    @FXML private Polygon rTrangle;
    @FXML private Circle rCircle;
    @FXML private SVGPath rCross;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Timeline crossRotateL = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(lCross.rotateProperty(), 0)),
                new KeyFrame(Duration.seconds(7), new KeyValue(lCross.rotateProperty(), 360))
        );
        crossRotateL.setCycleCount(Animation.INDEFINITE);
        crossRotateL.play();

        Timeline triangleWobbleL = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(lTriangle.rotateProperty(), -10)),
                new KeyFrame(Duration.seconds(1.5), new KeyValue(lTriangle.rotateProperty(), 10)),
                new KeyFrame(Duration.seconds(3), new KeyValue(lTriangle.rotateProperty(), -10))
        );
        triangleWobbleL.setCycleCount(Animation.INDEFINITE);
        triangleWobbleL.play();

        Timeline circlePulseL = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(lCircle.scaleXProperty(), 1.0), new KeyValue(lCircle.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(lCircle.scaleXProperty(), 1.1), new KeyValue(lCircle.scaleYProperty(), 1.1)),
                new KeyFrame(Duration.seconds(2), new KeyValue(lCircle.scaleXProperty(), 1.0), new KeyValue(lCircle.scaleYProperty(), 1.0))
        );
        circlePulseL.setCycleCount(Animation.INDEFINITE);
        circlePulseL.play();

        Timeline crossRotateR = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(rCross.rotateProperty(), 0)),
                new KeyFrame(Duration.seconds(7), new KeyValue(rCross.rotateProperty(), -360))
        );
        crossRotateR.setCycleCount(Animation.INDEFINITE);
        crossRotateR.play();

        Timeline triangleWobbleR = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(rTrangle.rotateProperty(), -10)),
                new KeyFrame(Duration.seconds(1.5), new KeyValue(rTrangle.rotateProperty(), 10)),
                new KeyFrame(Duration.seconds(3), new KeyValue(rTrangle.rotateProperty(), -10))
        );
        triangleWobbleR.setCycleCount(Animation.INDEFINITE);
        triangleWobbleR.play();

        Timeline circlePulseR = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(rCircle.scaleXProperty(), 1.0), new KeyValue(rCircle.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(rCircle.scaleXProperty(), 1.1), new KeyValue(rCircle.scaleYProperty(), 1.1)),
                new KeyFrame(Duration.seconds(2), new KeyValue(rCircle.scaleXProperty(), 1.0), new KeyValue(rCircle.scaleYProperty(), 1.0))
        );
        circlePulseR.setCycleCount(Animation.INDEFINITE);
        circlePulseR.play();

        makeCardZoomable(redCard);
        makeCardZoomable(greenCard);
        makeCardZoomable(blueCard);

        setupCards();
    }

    private void setupCards() {
        List<Game> games = Session.getGameManager().getGames();

        addCardLabel(redCard, "Tic-Tac-Toe");
        redCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                openGameByName("Tic-Tac-Toe");
            }
        });

        addCardLabel(greenCard, "Coming soon");
        addCardLabel(blueCard, "Coming soon");
    }

    private void addCardLabel(VBox card, String text) {
        if (card == null) {
            return;
        }
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        card.getChildren().add(label);
    }

    private void openGameByName(String name) {
        List<Game> games = Session.getGameManager().getGames();
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            if (game.getName().equals(name)) {
                Navigator.goTo("/TicTacToe.fxml");
                return;
            }
        }
    }

    private void makeCardZoomable(VBox card) {
        if (card == null) {
            return;
        }

        card.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
                scale.setToX(1.05);
                scale.setToY(1.05);
                scale.play();
            }
        });

        card.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
            }
        });
    }
}
