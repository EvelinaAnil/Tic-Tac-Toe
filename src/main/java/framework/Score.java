package framework;

public class Score {

    private final String username;
    private final String game;
    private final String gameMode;
    private final String result;
    private final String playedAt;

    public Score(String username, String game, String gameMode,
                 String result, String playedAt) {

        this.username = username;
        this.game = game;
        this.gameMode = gameMode;
        this.result = result;
        this.playedAt = playedAt;
    }

    public String getUsername() {
        return username;
    }

    public String getGame() {
        return game;
    }

    public String getGameMode() {
        return gameMode;
    }

    public String getResult() {
        return result;
    }

    public String getPlayedAt() {
        return playedAt;
    }
}