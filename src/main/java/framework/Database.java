
package framework;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    public static final String URL = "jdbc:sqlite:tictactoe.db";

    private static boolean initialized = false;

    public static Connection get() {

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(URL);

            synchronized (Database.class) {

                if (!initialized) {
                    createTables(connection);
                    initialized = true;
                }
            }

            return connection;

        } catch (SQLException e) {

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeError) {
                    System.err.println(
                            "Kon databaseverbinding niet sluiten: "
                                    + closeError.getMessage()
                    );
                }
            }

            throw new IllegalStateException(
                    "Kon geen verbinding maken met de database.",
                    e
            );
        }
    }

    private static void createTables(Connection connection)
            throws SQLException {

        System.out.println(
                "Database location: "
                        + new File("tictactoe.db").getAbsolutePath()
        );

        String users = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    display_name TEXT,
                    profile_pic TEXT
                )
                """;

        String saves = """
                CREATE TABLE IF NOT EXISTS game_saves (
                    username TEXT PRIMARY KEY,
                    board_state TEXT NOT NULL,
                    current_player TEXT NOT NULL,
                    vs_computer INTEGER NOT NULL,
                    cvc INTEGER NOT NULL,
                    difficulty TEXT
                )
                """;

        String scores = """
                CREATE TABLE IF NOT EXISTS scores (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL,
                    game TEXT NOT NULL,
                    game_mode TEXT NOT NULL,
                    result TEXT NOT NULL,
                    played_at TEXT NOT NULL
                )
                """;

        try (Statement statement = connection.createStatement()) {

            statement.execute(users);
            statement.execute(saves);
            statement.execute(scores);
        }

        // Check whether an existing scores table has game_mode.
        // CREATE TABLE IF NOT EXISTS does not update old tables.

        boolean hasGameMode = false;

        try (Statement statement = connection.createStatement();
             ResultSet columns = statement.executeQuery(
                     "PRAGMA table_info(scores)"
             )) {

            while (columns.next()) {

                if ("game_mode".equalsIgnoreCase(
                        columns.getString("name")
                )) {
                    hasGameMode = true;
                    break;
                }
            }
        }

        // Add the missing column without deleting old scores.

        if (!hasGameMode) {

            try (Statement statement = connection.createStatement()) {

                statement.execute("""
                        ALTER TABLE scores
                        ADD COLUMN game_mode TEXT NOT NULL
                        DEFAULT 'Player vs Player'
                        """);
            }

            System.out.println(
                    "Database updated: game_mode column added."
            );
        }

        System.out.println("Database initialized successfully.");
    }
}
