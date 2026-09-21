package framework.gui;

import framework.User;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class AppWindow {

    private JFrame frame = new JFrame("Tic-Tac-Toe");
    private CardLayout cardLayout = new CardLayout();
    private JPanel container = new JPanel(cardLayout);

    private User user;

    public AppWindow() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 460);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(container);

        container.add(new LoginScreen(this), "login");
    }

    public void start() {
        showLogin();
        frame.setVisible(true);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void showLogin() {
        cardLayout.show(container, "login");
    }

    public void showMainMenu() {
        MainMenu menu = new MainMenu(this);
        container.add(menu, "menu");
        cardLayout.show(container, "menu");
    }

    public void showGameSelect() {
        GameSelectScreen select = new GameSelectScreen(this);
        container.add(select, "select");
        cardLayout.show(container, "select");
    }

    public void showPanel(JPanel panel, String name) {
        container.add(panel, name);
        cardLayout.show(container, name);
    }
}
