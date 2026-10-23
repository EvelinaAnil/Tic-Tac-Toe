package framework;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameSave {

    public String boardState;
    public char currentPlayer;
    public boolean vsComputer;
    public boolean cvc;
    public String difficulty;

    public static boolean exists(String username) {
        return load(username) != null;
    }

    public static void save(String username, GameSave game) {
        if (username == null) {
            return;
        }
        String sql = "INSERT INTO game_saves (username, board_state, current_player, vs_computer, cvc, difficulty) "
                + "VALUES (?, ?, ?, ?, ?, ?) "
                + "ON CONFLICT(username) DO UPDATE SET "
                + "board_state = excluded.board_state, "
                + "current_player = excluded.current_player, "
                + "vs_computer = excluded.vs_computer, "
                + "cvc = excluded.cvc, "
                + "difficulty = excluded.difficulty";
        try (PreparedStatement statement = Database.get().prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, game.boardState);
            statement.setString(3, String.valueOf(game.currentPlayer));
            statement.setInt(4, game.vsComputer ? 1 : 0);
            statement.setInt(5, game.cvc ? 1 : 0);
            statement.setString(6, game.difficulty);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kon spel niet opslaan: " + e.getMessage());
        }
    }

    public static GameSave load(String username) {
        if (username == null) {
            return null;
        }
        String sql = "SELECT board_state, current_player, vs_computer, cvc, difficulty "
                + "FROM game_saves WHERE username = ?";
        try (PreparedStatement statement = Database.get().prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) {
                    return null;
                }
                GameSave game = new GameSave();
                game.boardState = rows.getString("board_state");
                String player = rows.getString("current_player");
                game.currentPlayer = (player == null || player.isEmpty()) ? 'X' : player.charAt(0);
                game.vsComputer = rows.getInt("vs_computer") == 1;
                game.cvc = rows.getInt("cvc") == 1;
                game.difficulty = rows.getString("difficulty");
                return game;
            }
        } catch (SQLException e) {
            System.err.println("Kon spel niet laden: " + e.getMessage());
            return null;
        }
    }

    public static void delete(String username) {
        if (username == null) {
            return;
        }
        String sql = "DELETE FROM game_saves WHERE username = ?";
        try (PreparedStatement statement = Database.get().prepareStatement(sql)) {
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kon opgeslagen spel niet verwijderen: " + e.getMessage());
        }
    }
}
