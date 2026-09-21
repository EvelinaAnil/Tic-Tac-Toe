package framework.gui;

import framework.Game;
import framework.GameManager;

import games.tictactoe.gui.GameScreen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameSelectScreen extends JPanel {

    private GameManager gameManager = new GameManager();

    public GameSelectScreen(AppWindow window) {
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Kies een spel");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.BLACK);
        title.setAlignmentX(CENTER_ALIGNMENT);

        add(Box.createVerticalStrut(50));
        add(title);
        add(Box.createVerticalStrut(30));

        List<Game> games = gameManager.getGames();
        for (Game game : games) {
            JButton button = new JButton(game.getName());
            button.setAlignmentX(CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(200, 40));
            button.addActionListener(e -> launchGame(window, game));
            add(button);
            add(Box.createVerticalStrut(15));
        }

        JButton back = new JButton("Terug");
        back.setAlignmentX(CENTER_ALIGNMENT);
        back.setMaximumSize(new Dimension(200, 40));
        back.addActionListener(e -> window.showMainMenu());
        add(back);
    }

    private void launchGame(AppWindow window, Game game) {
        if (game.getName().equals("Tic-Tac-Toe")) {
            GameScreen screen = new GameScreen(window);
            window.showPanel(screen, "game");
        }
    }
}
