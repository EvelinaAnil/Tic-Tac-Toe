package com.example.tictactoe2;

import framework.GameManager;
import framework.User;

// Remembers who is logged in and holds the list of games.
// We use simple static fields so every screen can read the same values.
public class Session {

    /**
     * Remembers who is logged in and holds the list of games.
     * We use simple static fields so every screen can read the same values.
     */

    private static User user;
    private static GameManager gameManager = new GameManager();

    private static boolean vsComputer = true;
    private static String difficulty = "MEDIUM";

    public static User getUser() {
        return user;
    }

    public static void setUser(User newUser) {
        user = newUser;
    }

    public static GameManager getGameManager() {
        return gameManager;
    }

    public static boolean isVsComputer(){
        return vsComputer;
    }

    public static void setVsComputer(boolean vsComp){
        vsComputer = vsComp;
    }

    public static String getDifficulty() {
        return difficulty;
    }

    public static void setDifficulty(String diff) {
        difficulty = diff;
    }

}
