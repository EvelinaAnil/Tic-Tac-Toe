//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.menutictactoe;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.Transition;
import javafx.animation.PathTransition.OrientationType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

public class Login_controller implements Initializable {
    @FXML
    private Label lblStatus;
    @FXML
    private TextField txtUsername;
    @FXML
    private Pane curvedTextPane;
    @FXML
    private Pane bgShapesPane;

    public Login_controller() {
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        Point2D center = new Point2D((double)140.0F, (double)250.0F);
        Point2D center2 = new Point2D((double)160.0F, (double)50.0F);
        String text = "ISY GAMES";
        this.drawText(text, (double)32.0F, center, center2);
        this.createBackgroundShapes(this.bgShapesPane);
    }

    public void drawText(String word, double initialRotation, Point2D center, Point2D center2) {
        double curveAngle = (double)130.0F;
        char[] wordArray = word.toCharArray();
        double radius = (double)215.0F;
        boolean aboveCenter = initialRotation < (double)180.0F;
        ObservableList<Text> parts = FXCollections.observableArrayList();
        ObservableList<PathTransition> transitions = FXCollections.observableArrayList();

        for(int i = 0; i < word.length(); ++i) {
            double finalAngle = (double)(i + 1) * curveAngle / (double)word.length();
            Shape arc = this.createCurve(center, radius, curveAngle, finalAngle, initialRotation, aboveCenter);
            Text part = new Text(String.valueOf(wordArray[i]));
            part.getStyleClass().add("curved-title-text");
            part.setFont(Font.font("Horizon", FontWeight.BOLD, (double)40.0F));
            parts.add(part);
            transitions.add(this.createPathTransition(arc, part));
        }

        for(int i = 0; i < parts.size(); ++i) {
            ((Text)parts.get(i)).setVisible(true);
            Transition transition = (Transition)transitions.get(i);
            transition.stop();
            transition.play();
        }

        this.curvedTextPane.getChildren().addAll(parts);
    }

    private PathTransition createPathTransition(Shape curve, Text text) {
        PathTransition transition = new PathTransition(Duration.millis((double)1.0F), curve, text);
        transition.setAutoReverse(false);
        transition.setCycleCount(1);
        transition.setOrientation(OrientationType.ORTHOGONAL_TO_TANGENT);
        transition.setInterpolator(Interpolator.LINEAR);
        return transition;
    }

    private Shape createCurve(Point2D center2, double radius, double totalAngle, double angel, double initialRotation, boolean revert) {
        Arc arc = new Arc();
        arc.setCenterX(center2.getX());
        arc.setCenterY(center2.getY());
        if (revert) {
            double endAngle = initialRotation + totalAngle;
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

    private Shape createCross(double size, double thickness) {
        Polygon cross = new Polygon();
        double s = size / (double)2.0F;
        double t = thickness / (double)2.0F;
        cross.getPoints().addAll(new Double[]{-t, -s, t, -s, t, -t, s, -t, s, t, t, t, t, s, -t, s, -t, t, -s, t, -s, -t, -t, -t});
        return cross;
    }

    private void createBackgroundShapes(Pane pane) {
        this.addShape(pane, new Polygon(new double[]{(double)0.0F, (double)-28.0F, (double)24.0F, (double)20.0F, (double)-24.0F, (double)20.0F}), Color.web("#86bcbd"), (double)30.0F, (double)30.0F);
        this.addShape(pane, new Circle((double)28.0F), Color.web("#a4ce8b"), (double)170.0F, (double)40.0F);
        this.addShape(pane, new Polygon(new double[]{(double)0.0F, (double)-28.0F, (double)24.0F, (double)20.0F, (double)-24.0F, (double)20.0F}), Color.web("#ba5a5a"), (double)330.0F, (double)32.0F);
        this.addShape(pane, new Circle((double)28.0F), Color.web("#86bcbd"), (double)600.0F, (double)38.0F);
        this.addShape(pane, this.createCross((double)48.0F, (double)16.0F), Color.web("#a4ce8b"), (double)900.0F, (double)40.0F);
        this.addShape(pane, new Polygon(new double[]{(double)0.0F, (double)-28.0F, (double)24.0F, (double)20.0F, (double)-24.0F, (double)20.0F}), Color.web("#ba5a5a"), (double)200.0F, (double)300.0F);
        this.addShape(pane, this.createCross((double)48.0F, (double)16.0F), Color.web("#a4ce8b"), (double)200.0F, (double)200.0F);
        this.addShape(pane, new Circle((double)28.0F), Color.web("#86bcbd"), (double)60.0F, (double)150.0F);
        this.addShape(pane, new Polygon(new double[]{(double)0.0F, (double)-28.0F, (double)24.0F, (double)20.0F, (double)-24.0F, (double)20.0F}), Color.web("#a4ce8b"), (double)700.0F, (double)100.0F);
        this.addShape(pane, new Circle((double)28.0F), Color.web("#ba5a5a"), (double)900.0F, (double)300.0F);
        this.addShape(pane, new Polygon(new double[]{(double)0.0F, (double)-28.0F, (double)24.0F, (double)20.0F, (double)-24.0F, (double)20.0F}), Color.web("#86bcbd"), (double)800.0F, (double)205.0F);
        this.addShape(pane, this.createCross((double)48.0F, (double)16.0F), Color.web("#ba5a5a"), (double)700.0F, (double)400.0F);
    }

    private void addShape(Pane pane, Shape shape, Color color, double x, double y) {
        shape.setFill(color);
        shape.setLayoutX(x);
        shape.setLayoutY(y);
        pane.getChildren().add(shape);
    }

    public void Login(ActionEvent event) {
        if (this.txtUsername.getText().equals("user")) {
            this.lblStatus.setText("Welcome");
        } else {
            this.lblStatus.setText("No user exists with that username");
        }

    }
}
