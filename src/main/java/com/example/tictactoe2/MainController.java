package com.example.tictactoe2;

import framework.Database;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import framework.User;
import framework.UserManager;

import java.io.IOException;
import java.nio.file.*;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.*;

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

    private File selectedAvatarFile = null;

    private static final String URL = Database.URL;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Point2D center = new Point2D(140, 180);
        Point2D center2 = new Point2D(160, 50);
        String text = "ISY GAMES";
        drawText(text, 32, center, center2);
        BackgroundShapes.createBackgroundShapes(bgShapesPane);
        setupAvatarDimensions();

        txtUsername.focusedProperty().addListener((obs, was, isNow) -> {
            if (!isNow) {
                String name = txtUsername.getText().trim();
                if (!name.isEmpty()) loadSavedAvatar(name);
            }
        });
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
        mainAvatarView.setCursor(Cursor.HAND);

        mainAvatarView.setOnMouseEntered(e -> mainAvatarView.setStroke(Color.web("#ba5a5a")));
        mainAvatarView.setOnMouseExited(e -> mainAvatarView.setStroke(Color.TRANSPARENT));

        mainAvatarView.setOnMouseClicked(e -> chooseAvatar());

        // default image
        setDefaultAvatar("/avatar/hello.png");
    }

    private void chooseAvatar() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose your avatar");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        // start in home folder
        File home = new File(System.getProperty("user.home"));
        if (home.isDirectory()) chooser.setInitialDirectory(home);

        Stage stage = (Stage) mainAvatarView.getScene().getWindow();
        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            setMainAvatar(file);
        }
    }

    private void setMainAvatar(File file) {
        Image img = new Image(file.toURI().toString(), 200, 200, false, true);
        if (img.isError()) {
            System.err.println("[Avatar Error] Could not load image: " + file);
            lblStatus.setText("That file isn't a valid image");
            return;
        }
        mainAvatarView.setFill(new ImagePattern(img));
        selectedAvatarFile = file;
    }

    private void setDefaultAvatar(String resourcePath) {
        InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream == null) {
            System.err.println("[Avatar Error] Missing default avatar: " + resourcePath);
            return;
        }
        Image img = new Image(stream);
        if (!img.isError()) {
            mainAvatarView.setFill(new ImagePattern(img));
        }
    }

    public static void saveAvatar(String username, String avatarPath) {
        String sql = """
            INSERT INTO users (username, profile_pic) VALUES (?, ?)
            ON CONFLICT(username) DO UPDATE SET profile_pic = excluded.profile_pic
            """;
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, avatarPath);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String loadAvatar(String username) {
        String sql = "SELECT profile_pic FROM users WHERE username = ?";
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("profile_pic") : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Creates a folder in user folder and saves selected image there as fallback */
    private Path copyAvatarToAppFolder(File source, String username) throws IOException {
        Path dir = Paths.get(System.getProperty("user.home"), ".tictactoe", "avatars");
        Files.createDirectories(dir);

        String name = source.getName();
        String ext = name.contains(".") ? name.substring(name.lastIndexOf('.')) : ".png";
        String safeUser = username.replaceAll("[^a-zA-Z0-9_-]", "_");

        Path target = dir.resolve(safeUser + ext);
        Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        return target;
    }

    private void loadSavedAvatar(String username) {
        String path = MainController.loadAvatar(username);
        if (path != null) {
            File f = new File(path);
            if (f.exists()) {
                setMainAvatar(f);
            }
        }
    }

    //login logic
    private final UserManager userManager = new UserManager();

    public void Login(ActionEvent event) {
        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            lblStatus.setText("Please enter a username");
            return;
        }

        if (selectedAvatarFile != null) {
            try {
                Path saved = copyAvatarToAppFolder(selectedAvatarFile, username);
                MainController.saveAvatar(username, saved.toString());
            } catch (IOException e) {
                e.printStackTrace();
                lblStatus.setText("Could not save avatar");
            }
        }

        User user = userManager.login(username);
        Session.setUser(user);
        lblStatus.setText("Welcome " + user.getUsername());
        Navigator.goTo("/MainMenu.fxml");
    }
}
