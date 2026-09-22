package com.example.tictactoe2;

import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import javafx.animation.AnimationTimer;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Label lblStatus;

    @FXML
    private TextField txtUsername;

    @FXML
    private Pane curvedTextPane;

    @FXML
    private Pane bgShapesPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Point2D center = new Point2D(140, 250);
        Point2D center2 = new Point2D(160, 50);
        String text = "ISY GAMES";
        drawText(text, 32, center, center2);
        createBackgroundShapes(bgShapesPane);
        //animateShapes(bgShapesPane);
    }

    //center text! not perfect but IDK how to make the letters evenly spaced
    public void drawText(String word, double initialRotation, Point2D center, Point2D center2) {
        double curveAngle = 130; // 60 to 150 looks best
        char[] wordArray = word.toCharArray();

        double radius = 215;

        boolean aboveCenter = initialRotation < 180;

        final ObservableList<Text> parts = FXCollections.observableArrayList();
        final ObservableList<PathTransition> transitions = FXCollections.observableArrayList();

        for (int i = 0; i < word.length(); i++) {
            double finalAngle = (i+1)*curveAngle/word.length();

            Shape arc = createCurve(center, radius, curveAngle,  finalAngle, initialRotation, aboveCenter);

            Text part = new Text(String.valueOf(wordArray[i]));
            part.getStyleClass().add("curved-title-text");

            part.setFont(Font.font("Horizon", FontWeight.BOLD, 40));

            parts.add(part);

            transitions.add(createPathTransition(arc, part));
        }

        for (int i = 0; i < parts.size(); i++) {
            parts.get(i).setVisible(true);
            final Transition transition = transitions.get(i);
            transition.stop();
            transition.play();
        }

        curvedTextPane.getChildren().addAll(parts);
    }

    private PathTransition createPathTransition(Shape curve, Text text) {
        final PathTransition transition = new PathTransition(Duration.millis(1), curve, text);

        transition.setAutoReverse(false);
        transition.setCycleCount(1);
        transition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        transition.setInterpolator(Interpolator.LINEAR);

        return transition;
    }

    private Shape createCurve(Point2D center2, double radius, double totalAngle, double angel, double initialRotation, boolean revert) {
        Arc arc = new Arc();
        arc.setCenterX(center2.getX());
        arc.setCenterY(center2.getY());
        if (revert) {
            final double endAngle = initialRotation + totalAngle;
            arc.setStartAngle(endAngle);
            arc.setLength(-angel);
        } else {
            arc.setStartAngle(initialRotation);
            arc.setLength(angel);
        }
        arc.setRadiusX(radius);
        arc.setRadiusY(radius);
        return arc;
    }

    //background shapes
    private Shape createCross(double size, double thickness) {
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

    private void createBackgroundShapes(Pane pane) {
        addShape(pane, new Polygon(0.0,-28.0, 24.0,20.0, -24.0,20.0), Color.web("#86bcbd"), 30, 30);

        addShape(pane, new Circle(28), Color.web("#a4ce8b"), 170, 40);

        addShape(pane, new Polygon(0.0,-28.0, 24.0,20.0, -24.0,20.0), Color.web("#ba5a5a"), 330, 32);

        addShape(pane, new Circle(28), Color.web("#86bcbd"), 600, 38);

        addShape(pane, createCross(48, 16), Color.web("#a4ce8b"), 900, 40);

        addShape(pane, new Polygon(0.0,-28.0, 24.0,20.0, -24.0,20.0), Color.web("#ba5a5a"), 200, 300);

        addShape(pane, createCross(48, 16), Color.web("#a4ce8b"), 200, 200);

        addShape(pane, new Circle(28), Color.web("#86bcbd"), 60, 150);

        addShape(pane, new Polygon(0.0,-28.0, 24.0,20.0, -24.0,20.0), Color.web("#a4ce8b"), 700, 100);

        addShape(pane, new Circle(28), Color.web("#ba5a5a"), 900, 300);

        addShape(pane, new Polygon(0.0,-28.0, 24.0,20.0, -24.0,20.0), Color.web("#86bcbd"), 800, 205);

        addShape(pane, createCross(48, 16), Color.web("#ba5a5a"), 700, 400);

    }

    private void addShape(Pane pane, Shape shape, Color color, double x, double y) {
        shape.setFill(color);
        shape.setLayoutX(x);
        shape.setLayoutY(y);
        pane.getChildren().add(shape);
    }
    //TODO: animatie -nice to have?-
    /*
    private void animateShapes(Pane pane) {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (int i = 0; i < floatingShapes.size(); i++) {
                    Shape shape = floatingShapes.get(i);
                    double[] vel = velocities.get(i);

                    double newX = shape.getLayoutX() + vel[0];
                    double newY = shape.getLayoutY() + vel[1];

                    if (newX < 0 || newX > pane.getWidth()) {
                        vel[0] *= -1;
                        newX = shape.getLayoutX() + vel[0];
                    }

                    if (newY < 0 || newY > pane.getHeight()) {
                        vel[1] *= -1;
                        newY = shape.getLayoutY() + vel[1];
                    }

                    shape.setLayoutX(newX);
                    shape.setLayoutY(newY);
                }
            }
        };
        timer.start();
    }*/

    //login logic
    public void Login(ActionEvent event) {
        if (txtUsername.getText().equals("user")){
            lblStatus.setText("Welcome");
        } else  {
            lblStatus.setText("No user exists with that username");
        }
    }
}
