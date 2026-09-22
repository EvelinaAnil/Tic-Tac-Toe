package com.example.menutictactoe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
//import javafx.event.ActionEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class main_Login extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(main_Login.class.getResource("Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1000, 500);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            Font.loadFont(getClass().getResource("/fonts/Horizon.otf").toExternalForm(), 10);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


