package com.example.menutictactoe.framework.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMenu extends JPanel {

    public MainMenu(AppWindow window) {
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        String username = window.getUser().getUsername();

        JLabel welcome = new JLabel("Welkom " + username);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcome.setForeground(Color.BLACK);
        welcome.setAlignmentX(CENTER_ALIGNMENT);

        JButton play = new JButton("Spel kiezen");
        play.setAlignmentX(CENTER_ALIGNMENT);
        play.setMaximumSize(new Dimension(200, 40));
        play.addActionListener(e -> window.showGameSelect());

        JButton scores = new JButton("Scores");
        scores.setAlignmentX(CENTER_ALIGNMENT);
        scores.setMaximumSize(new Dimension(200, 40));
        scores.addActionListener(e -> welcome.setText("Scores komen hier."));

        JButton exit = new JButton("Afsluiten");
        exit.setAlignmentX(CENTER_ALIGNMENT);
        exit.setMaximumSize(new Dimension(200, 40));
        exit.addActionListener(e -> System.exit(0));

        add(Box.createVerticalStrut(60));
        add(welcome);
        add(Box.createVerticalStrut(40));
        add(play);
        add(Box.createVerticalStrut(15));
        add(scores);
        add(Box.createVerticalStrut(15));
        add(exit);
    }
}
