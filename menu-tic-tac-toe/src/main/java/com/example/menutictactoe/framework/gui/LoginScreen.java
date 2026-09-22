package com.example.menutictactoe.framework.gui;

import com.example.menutictactoe.framework.User;
import com.example.menutictactoe.framework.UserManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoginScreen extends JPanel {

    private UserManager userManager = new UserManager();

    public LoginScreen(AppWindow window) {
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Tic-Tac-Toe");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.BLACK);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel prompt = new JLabel("Gebruikersnaam:");
        prompt.setForeground(Color.BLACK);
        prompt.setAlignmentX(CENTER_ALIGNMENT);

        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(200, 30));
        field.setAlignmentX(CENTER_ALIGNMENT);

        JButton loginButton = new JButton("Inloggen");
        loginButton.setAlignmentX(CENTER_ALIGNMENT);

        JLabel error = new JLabel(" ");
        error.setForeground(Color.RED);
        error.setAlignmentX(CENTER_ALIGNMENT);

        loginButton.addActionListener(e -> {
            String username = field.getText().trim();
            if (username.isEmpty()) {
                error.setText("Vul een gebruikersnaam in.");
                return;
            }
            User user = userManager.login(username);
            window.setUser(user);
            window.showMainMenu();
        });

        add(javax.swing.Box.createVerticalStrut(60));
        add(title);
        add(javax.swing.Box.createVerticalStrut(40));
        add(prompt);
        add(javax.swing.Box.createVerticalStrut(10));
        add(field);
        add(javax.swing.Box.createVerticalStrut(15));
        add(loginButton);
        add(javax.swing.Box.createVerticalStrut(10));
        add(error);
    }
}
