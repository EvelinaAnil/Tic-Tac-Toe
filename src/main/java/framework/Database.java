package framework;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    public static final String URL = "jdbc:sqlite:tictactoe.db";

    private static Connection connection;

    public static Connection get() {
        if (connection == null) {
            connect();
        }
        return connection;
    }

    private static void connect() {
        try {
            connection = DriverManager.getConnection(URL);
            createTables();
        } catch (SQLException e) {
            System.err.println("Kon geen verbinding maken met de database: " + e.getMessage());
        }
    }

    private static void createTables() {
        String users = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT UNIQUE NOT NULL, "
                + "display_name TEXT, "
                + "profile_pic TEXT)";

        String saves = "CREATE TABLE IF NOT EXISTS game_saves ("
                + "username TEXT PRIMARY KEY, "
                + "board_state TEXT NOT NULL, "
                + "current_player TEXT NOT NULL, "
                + "vs_computer INTEGER NOT NULL, "
                + "cvc INTEGER NOT NULL, "
                + "difficulty TEXT)";

        String scores = "CREATE TABLE IF NOT EXISTS scores ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL, "
                + "game TEXT NOT NULL, "
                + "result TEXT NOT NULL, "
                + "played_at TEXT NOT NULL)";

        try (Statement statement = connection.createStatement()) {
            statement.execute(users);
            statement.execute(saves);
            statement.execute(scores);
        } catch (SQLException e) {
            System.err.println("Kon tabellen niet aanmaken: " + e.getMessage());
        }
    }
}
