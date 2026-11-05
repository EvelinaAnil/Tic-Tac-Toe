
package framework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Scoreboard {

    // Save a finished game.
    // Returns true only if the database insert succeeds.

    public boolean saveScore(
            String username,
            String game,
            String gameMode,
            String result
    ) {

        String sql = """
                INSERT INTO scores
                (username, game, game_mode, result, played_at)
                VALUES (?, ?, ?, ?, datetime('now'))
                """;

        try (Connection connection = Database.get();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, game);
            statement.setString(3, gameMode);
            statement.setString(4, result);

            statement.executeUpdate();

            System.out.println("Score successfully saved!");

            return true;

        } catch (SQLException | IllegalStateException e) {

            System.err.println(
                    "Kon score niet opslaan: " + e.getMessage()
            );

            return false;
        }
    }


    // Load all game results.

    public List<Score> getScores() {

        List<Score> scores = new ArrayList<>();

        String sql = """
                SELECT username, game, game_mode, result, played_at
                FROM scores
                ORDER BY played_at DESC
                """;

        try (Connection connection = Database.get();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {

            while (rows.next()) {

                Score score = new Score(
                        rows.getString("username"),
                        rows.getString("game"),
                        rows.getString("game_mode"),
                        rows.getString("result"),
                        rows.getString("played_at")
                );

                scores.add(score);
            }

        } catch (SQLException | IllegalStateException e) {

            System.err.println(
                    "Kon scores niet laden: " + e.getMessage()
            );
        }

        System.out.println("Scores loaded: " + scores.size());

        return scores;
    }


    // Load the leaderboard.
    // Computer vs Computer games are excluded.

    public List<String> getHighscores() {

        List<String> highscores = new ArrayList<>();

        String sql = """
                SELECT username,
                       SUM(
                           CASE
                               WHEN result = 'WIN' THEN 1
                               ELSE 0
                           END
                       ) AS wins,
                       COUNT(*) AS games
                FROM scores
                WHERE game = 'Tic-Tac-Toe'
                  AND COALESCE(game_mode, '') <> 'Computer vs Computer'
                GROUP BY username
                ORDER BY wins DESC, games DESC, username ASC
                """;

        try (Connection connection = Database.get();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet rows = statement.executeQuery()) {

            int rank = 1;

            while (rows.next()) {

                highscores.add(
                        rank + ". "
                                + rows.getString("username")
                                + " — Wins: "
                                + rows.getInt("wins")
                                + ", Games: "
                                + rows.getInt("games")
                );

                rank++;
            }

        } catch (SQLException | IllegalStateException e) {

            System.err.println(
                    "Kon highscores niet laden: " + e.getMessage()
            );
        }

        return highscores;
    }
}
