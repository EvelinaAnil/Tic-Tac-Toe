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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
        System.out.println("DEBUG: setupCards() wordt uitgevoerd!"); // Kijken of de methode überhaupt start

        addCardContent(redCard, "/img/pvp.png", "vs Speler");
        redCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) { Session.setVsComputer(false); openGameByName("Tic-Tac-Toe");}
        });

        addCardContent(greenCard, "/img/pvc.png", "vs Computer");
        greenCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Session.setVsComputer(true);
                showDifficultyMenu();
            }

        });

        addCardContent(blueCard, "/img/cvc.png", "Coming soon");
    }



    private void addCardLabel(VBox card, String text) {
        if (card == null) {
            return;
        }
        // wis eerst
        card.getChildren().clear();


        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        card.getChildren().add(label);
    }

    private void addCardContent(VBox card, String imagePath, String text) {
        if (card == null) {
            return;
        }
        card.getChildren().clear();

        card.setPadding(new javafx.geometry.Insets(20, 15, 20, 15));
        card.setSpacing(15);

        card.setAlignment(javafx.geometry.Pos.CENTER);
        try {
            var resourceStream = getClass().getResourceAsStream(imagePath);
            if (resourceStream == null) {
                System.err.println("FOUT: Bestand niet gevonden: " + imagePath);
                return;
            }

            Image image = new Image(resourceStream);
            if (image.isError()) {
                System.err.println("FOUT bij decoderen van afbeelding " + imagePath + ": " + image.getException());
                throw new Exception("Afbeelding is ongeldig of kan niet gelezen worden.");
            }

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(210);
            imageView.setFitHeight(210);
            imageView.setPreserveRatio(true);

            Label label = new Label(text);
            label.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

            card.getChildren().addAll(imageView, label);
            System.out.println("SUCCES: Afbeelding succesvol getoond op scherm: " + imagePath);

        } catch (Exception e) {
            System.err.println("FOUT: " + e.getMessage());
            Label label = new Label(text);
            label.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
            card.getChildren().add(label);
        }
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

    private void showDifficultyMenu() {
        addCardContent(redCard, "/img/easy.png", "Easy");
        redCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Session.setDifficulty("EASY"); // Sla op in Session
                openGameByName("Tic-Tac-Toe");
            }
        });

        addCardContent(greenCard, "/img/normal.png", "Medium");
        greenCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Session.setDifficulty("MEDIUM"); // Sla op in Session
                openGameByName("Tic-Tac-Toe");
            }
        });

        addCardContent(blueCard, "/img/hard.png", "Hard");
        blueCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Session.setDifficulty("HARD"); // Sla op in Session
                openGameByName("Tic-Tac-Toe");
            }
        });
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
