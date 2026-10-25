package com.example.tictactoe2;

import framework.AvatarLoader;
import framework.Game;
import framework.network.ServerConnection;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainMenu implements Initializable {

    @FXML
    private VBox TicTacToe;
    @FXML
    private VBox zeeSlag;
    @FXML
    private VBox GO;

    @FXML
    private Circle lCircle;
    @FXML
    private Polygon lTriangle;
    @FXML
    private SVGPath lCross;

    @FXML
    private Polygon rTrangle;
    @FXML
    private Circle rCircle;
    @FXML
    private SVGPath rCross;

    @FXML
    private Circle profile;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        //show profile picture in main menu
        String username = Session.getUser().getUsername();
        AvatarLoader.applyTo(profile, username);
        setupCards();
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

    private void setupCards() {
        addCardContent(TicTacToe, "/img/tictactoe.png", "TicTacToe");
        TicTacToe.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Navigator.goTo("/SelectMenu.fxml");
            }
        });
        addCardContent(zeeSlag, "/avatar/whateverBro.png", "boop");
        zeeSlag.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Navigator.goTo("/SelectMenu.fxml");
            }
        });
        addCardContent(GO, "/avatar/hello.png", "beep");
        GO.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Navigator.goTo("/SelectMenu.fxml");
            }
        });
    }

        private void openMenuByName(String name) {
        List<Game> games = Session.getGameManager().getGames();
        for (Game game : Session.getGameManager().getGames()) {
            if  (game.getName().equals(name)) {
                Navigator.goTo(game.getMenu());
                return;
            }
        }
    }
}
