package com.example.tictactoe2;

import framework.GameManager;
import framework.User;

/**
 * Holds application-wide state that needs to survive across screens,
 * such as the logged-in user and the shared game list.
 *
 * <p>Kept deliberately tiny: a single shared instance the controllers read
 * from. Frontenders generally won't need to touch this.</p>
 */
public final class Session {

    private static final Session INSTANCE = new Session();

    private final GameManager gameManager = new GameManager();
    private User user;

    private Session() {
    }

    public static Session get() {
        return INSTANCE;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
