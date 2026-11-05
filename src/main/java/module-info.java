module com.example.tictactoe2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.sql;

    opens com.example.tictactoe2 to javafx.fxml;
    opens framework to javafx.base;

    exports com.example.tictactoe2;
    exports framework.network;
}