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

import framework.User;
import framework.UserManager;

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
        BackgroundShapes.createBackgroundShapes(bgShapesPane);
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

    //login logic
    private final UserManager userManager = new UserManager();

    public void Login(ActionEvent event) {
        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            lblStatus.setText("Please enter a username");
            return;
        }

        User user = userManager.login(username);
        Session.setUser(user);
        lblStatus.setText("Welcome " + user.getUsername());
        Navigator.goTo("/SelectMenu.fxml");
    }
}
