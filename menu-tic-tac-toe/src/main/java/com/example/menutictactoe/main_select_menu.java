package com.example.menutictactoe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class main_select_menu extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(main_select_menu.class.getResource("select_menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("JavaFx Menu!");
        stage.setScene(scene);
        stage.show();
    }
}
