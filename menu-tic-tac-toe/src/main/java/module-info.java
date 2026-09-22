module com.example.menutictactoe {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.menutictactoe to javafx.fxml;
    exports com.example.menutictactoe;
}