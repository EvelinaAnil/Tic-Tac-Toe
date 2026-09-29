package com.example.tictactoe2;

import javafx.animation.RotateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import javafx.util.Duration;

import java.util.Random;

public class BackgroundShapes {

    private static final Random rand = new Random();

    private static Shape createCross(double size, double thickness) {
        Polygon cross = new Polygon();
        double s = size / 2, t = thickness / 2;
        cross.getPoints().addAll(new Double[]{
                -t, -s,  t, -s,  t, -t,
                s, -t,  s,  t,  t,  t,
                t,  s, -t,  s, -t,  t,
                -s,  t, -s, -t, -t, -t
        });
        return cross;
    }

    public static void createBackgroundShapes(Pane pane) {
        addShape(pane, new Polygon(0.0,-28.0, 24.0,20.0, -24.0,20.0), Color.web("#86bcbd"), 50, 50);

        addShape(pane, new Circle(28), Color.web("#a4ce8b"), 170, 40);

        addShape(pane, new Polygon(0.0,-28.0, 24.0,20.0, -24.0,20.0), Color.web("#ba5a5a"), 310, 50);

        addShape(pane, new Circle(28), Color.web("#86bcbd"), 750, 38);

        addShape(pane, createCross(48, 16), Color.web("#a4ce8b"), 900, 40);

        addShape(pane, new Polygon(0.0,-28.0, 24.0,20.0, -24.0,20.0), Color.web("#ba5a5a"), 200, 300);

        addShape(pane, createCross(48, 16), Color.web("#a4ce8b"), 200, 200);

        addShape(pane, new Circle(28), Color.web("#86bcbd"), 60, 150);

        addShape(pane, new Polygon(0.0,-28.0, 24.0,20.0, -24.0,20.0), Color.web("#a4ce8b"), 700, 100);

        addShape(pane, new Circle(28), Color.web("#ba5a5a"), 900, 300);

        addShape(pane, new Polygon(0.0,-28.0, 24.0,20.0, -24.0,20.0), Color.web("#86bcbd"), 800, 205);

        addShape(pane, createCross(48, 16), Color.web("#ba5a5a"), 700, 400);

    }

    private static void addShape(Pane pane, Shape shape, Color color, double x, double y) {
        shape.setFill(color);
        shape.setLayoutX(x);
        shape.setLayoutY(y);
        pane.getChildren().add(shape);

        rotateShape(shape);
    }


    private static void rotateShape(Shape shape) {
        RotateTransition rotate = new RotateTransition();
        rotate.setNode(shape);

        //makes sure they don't spin in sync
        double seconds = 4 + rand.nextDouble() * 6;
        rotate.setDuration(Duration.seconds(seconds));

        rotate.setByAngle(360);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.setInterpolator(javafx.animation.Interpolator.LINEAR);

        // random spin direction
        if (rand.nextBoolean()) {
            rotate.setByAngle(-360);
        }

        rotate.play();
    }
}
