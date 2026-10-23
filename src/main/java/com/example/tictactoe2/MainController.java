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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import framework.User;
import framework.UserManager;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
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

    @FXML
    private Circle mainAvatarView;

    @FXML
    private HBox avatarGrid;

    //list of preselected images found in avatar resources folder
    private final List<String> AVATAR_PATHS = Arrays.asList(
            "/avatar/nr1Winner.png",
            "/avatar/hello.png",
            "/avatar/whateverBro.png"
    );

    private Circle selectedGridItem = null;
    private String selectedAvatarPath = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Point2D center = new Point2D(140, 180);
        Point2D center2 = new Point2D(160, 50);
        String text = "ISY GAMES";
        drawText(text, 32, center, center2);
        BackgroundShapes.createBackgroundShapes(bgShapesPane);
        setupAvatarDimensions();
        buildAvatarSelectionGrid();
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

    //image picker
    private void setupAvatarDimensions() {
        mainAvatarView.setLayoutX(140.0);
        mainAvatarView.setLayoutY(60.0);

        mainAvatarView.setCursor(javafx.scene.Cursor.HAND);

        mainAvatarView.setOnMouseEntered(e -> mainAvatarView.setStroke(Color.web("#ba5a5a")));
        mainAvatarView.setOnMouseExited(e -> mainAvatarView.setStroke(Color.TRANSPARENT));

        mainAvatarView.setOnMouseClicked(e -> {
            boolean isNowVisible = !avatarGrid.isVisible();
            avatarGrid.setVisible(isNowVisible);
            avatarGrid.setManaged(isNowVisible);
        });

        setMainAvatar(AVATAR_PATHS.get(0));
    }

    private void buildAvatarSelectionGrid() {
        for (String path : AVATAR_PATHS) {
            InputStream stream = getClass().getResourceAsStream(path);

            if (stream == null) {
                System.err.println("[Avatar Error] Cannot find file at resource path: " + path);
                continue;
            }

            try {
                Image img = new Image(stream);

                if (img.isError()) {
                    System.err.println("[Avatar Error] Corrupted image bytes at: " + path);
                    continue;
                }

                Circle gridItem = new Circle(22);
                gridItem.setFill(new ImagePattern(img));
                gridItem.setStroke(Color.TRANSPARENT);
                gridItem.setStrokeWidth(2.5);

                if (AVATAR_PATHS.indexOf(path) == 0) {
                    highlightSelection(gridItem);
                }

                configureGridItemEvents(gridItem, path);
                avatarGrid.getChildren().add(gridItem);

            } catch (Exception e) {
                System.err.println("Failed compiling avatar pattern node for: " + path);
            }
        }
    }

    private void configureGridItemEvents (Circle gridItem, String path){
        gridItem.setOnMouseEntered(e -> {
            if (gridItem != selectedGridItem) gridItem.setStroke(Color.web("#86BCBD"));
        });

        gridItem.setOnMouseExited(e -> {
            if (gridItem != selectedGridItem) gridItem.setStroke(Color.TRANSPARENT);
        });

        gridItem.setOnMouseClicked(e -> {
            setMainAvatar(path);
            highlightSelection(gridItem);
        });

        gridItem.setOnMouseClicked(e -> {
            setMainAvatar(path);
            highlightSelection(gridItem);

            avatarGrid.setVisible(false);
            avatarGrid.setManaged(false);
        });
    }

    private void setMainAvatar (String path){
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream != null) {
            Image img = new Image(stream);
            if (!img.isError()) {
                mainAvatarView.setFill(new ImagePattern(img));
                this.selectedAvatarPath = path;
            }
        } else {
            System.err.println("[Avatar Error] Main fallback stream is null for: " + path);
        }
    }

    private void highlightSelection (Circle targetCircle){
        if (selectedGridItem != null) {
            selectedGridItem.setStroke(Color.TRANSPARENT);
        }
        selectedGridItem = targetCircle;
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
