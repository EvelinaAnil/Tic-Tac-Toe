package framework.network;

import java.util.List;

public interface ServerListener {
    void onConnected();
    void onMatchStarted(String gameType, String opponent, boolean isStartingPlayer);
    void onYourTurn(String turnMessage);
    void onMoveReceived(String player, int move, String details);
    void onGameOver(String result, String comment);
    void onChallengeReceived(String challenger, String gameType, String challengeNumber);
    void onChallengeCancelled(String challengeNumber);
    void onGameListReceived(List<String> games);
    void onPlayerListReceived(List<String> players);
    void onError(String errorMessage);
}