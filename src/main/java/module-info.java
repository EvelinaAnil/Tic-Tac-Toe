module com.example.tictactoe2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.example.tictactoe2 to javafx.fxml;
    exports com.example.tictactoe2;
}