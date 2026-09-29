package com.example.tictactoe2;

import framework.GameManager;
import framework.User;

// Remembers who is logged in and holds the list of games.
// We use simple static fields so every screen can read the same values.
public class Session {

    private static User user;
    private static GameManager gameManager = new GameManager();

    public static User getUser() {
        return user;
    }

    public static void setUser(User newUser) {
        user = newUser;
    }

    public static GameManager getGameManager() {
        return gameManager;
    }
}
