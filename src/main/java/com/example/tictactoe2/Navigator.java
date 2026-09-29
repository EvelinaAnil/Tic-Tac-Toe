package com.example.tictactoe2;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Central place for screen navigation.
 *
 * <p>All screens share one {@link Scene}; navigating just swaps the scene's
 * root node with a freshly loaded FXML. Frontenders can add a new screen by
 * dropping an FXML file in {@code resources/} and calling
 * {@link #goTo(String)} with its path.</p>
 */
public final class Navigator {

    private static Scene scene;

    private Navigator() {
    }

    /** Called once at startup with the primary scene. */
    public static void setScene(Scene scene) {
        Navigator.scene = scene;
    }

    /**
     * Loads the given FXML resource and makes it the current screen.
     *
     * @param fxmlPath resource path, e.g. {@code "/Login.fxml"}
     */
    public static void goTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxmlPath));
            Parent root = loader.load();
            scene.setRoot(root);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load screen: " + fxmlPath, e);
        }
    }
}
