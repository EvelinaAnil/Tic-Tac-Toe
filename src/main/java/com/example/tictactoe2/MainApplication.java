package com.example.tictactoe2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
//import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.lang.reflect.Array;
import java.util.List;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApplication.class.getResource("/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1000, 500);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            Font.loadFont(getClass().getResource("/fonts/Horizon.otf").toExternalForm(), 10);
            Navigator.setScene(scene);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


