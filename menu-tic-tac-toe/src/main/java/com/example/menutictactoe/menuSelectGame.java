package com.example.menutictactoe;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class menuSelectGame implements Initializable {

    @FXML private Pane headerPane;
    @FXML private HBox headerBox;

    @FXML private Label select_lable;

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
        // --- TEKST ("SELECT A GAME") ---
        //!!

//        String text = "SELECT A GAME";
//        Random random = new Random();
//
//        titleContainer.getChildren().clear();
//
//        for (char c : text.toCharArray()) {
//            Label letterLabel = new Label(String.valueOf(c));
//            letterLabel.setFont(Font.font("Comic Sans MS", 40.0));
//
//            // Pick a random color from your array
//            String randomColor = colors[random.nextInt(colors.length)];
//            letterLabel.setStyle("-fx-text-fill: " + randomColor + ";");
//
//            // Optional: Handle spaces so they don't collapse weirdly
//            if (c == ' ') {
//                letterLabel.setMinWidth(15);
//            }
//
//            titleContainer.getChildren().add(letterLabel);
//        }

        // --- BESTAANDE ANIMATIES VOOR FIGUREN ---

        // Animation for right figures
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

        // Animation for left figures
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



        // Animation for Cards
        makeCardZoomable(redCard);
        makeCardZoomable(greenCard);
        makeCardZoomable(blueCard);

    }

    private void  makeCardZoomable(VBox card){
        if (card == null) {
            return;
        }

        //when cursor on card
        card.setOnMouseEntered(event -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.05); //width to 5%
            scale.setToY(1.05); //height
            scale.play();
        });

        //when cursor off card
        card.setOnMouseExited(event -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), card);
            scale.setToX(1.0); // normale size
            scale.setToY(1.0);
            scale.play();
        });

//        javaFX   ik wil doen labale dat elke letter gekleurd met 3 colors was bijvoorbeels s- yellow e-green en zo itets met "select a game"  ik will ook doen dat "select" was met s omhoog e omlaag en zo iet met "select". colors "BA5A5A" A4CE8B rgb(134, 188, 189)

    }
}




