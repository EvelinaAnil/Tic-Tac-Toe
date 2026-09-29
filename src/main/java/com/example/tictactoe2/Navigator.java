package com.example.tictactoe2;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Handles switching between screens.
 * All screens share one Scene. To show another screen we load its FXML
 * file and put it inside the same Scene.
 */

public class Navigator {

    private static Scene scene;

    // Called once when the program starts.
    public static void setScene(Scene newScene) {
        scene = newScene;
    }

    // Loads an FXML file (for example "/Login.fxml") and shows it.
    public static void goTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxmlPath));
            Parent root = loader.load();
            scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
