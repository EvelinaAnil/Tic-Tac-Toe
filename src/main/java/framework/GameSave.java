package framework;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GameSave {

    private static final Path DIR = Paths.get("saves");

    public String boardState;
    public char currentPlayer;
    public boolean vsComputer;
    public boolean cvc;
    public String difficulty;

    private static Path fileFor(String username) {
        return DIR.resolve(username + ".sav");
    }

    public static boolean exists(String username) {
        return username != null && Files.exists(fileFor(username));
    }

    public static void save(String username, GameSave game) {
        if (username == null) {
            return;
        }
        List<String> lines = new ArrayList<>();
        lines.add(game.boardState);
        lines.add(String.valueOf(game.currentPlayer));
        lines.add(String.valueOf(game.vsComputer));
        lines.add(String.valueOf(game.cvc));
        lines.add(game.difficulty);
        try {
            Files.createDirectories(DIR);
            Files.write(fileFor(username), lines);
        } catch (IOException e) {
            System.err.println("Kon spel niet opslaan: " + e.getMessage());
        }
    }

    public static GameSave load(String username) {
        if (!exists(username)) {
            return null;
        }
        try {
            List<String> lines = Files.readAllLines(fileFor(username));
            if (lines.size() < 5) {
                return null;
            }
            GameSave game = new GameSave();
            game.boardState = lines.get(0);
            game.currentPlayer = lines.get(1).isEmpty() ? 'X' : lines.get(1).charAt(0);
            game.vsComputer = Boolean.parseBoolean(lines.get(2));
            game.cvc = Boolean.parseBoolean(lines.get(3));
            game.difficulty = lines.get(4);
            return game;
        } catch (IOException e) {
            System.err.println("Kon spel niet laden: " + e.getMessage());
            return null;
        }
    }

    public static void delete(String username) {
        try {
            Files.deleteIfExists(fileFor(username));
        } catch (IOException e) {
            System.err.println("Kon opgeslagen spel niet verwijderen: " + e.getMessage());
        }
    }
}
