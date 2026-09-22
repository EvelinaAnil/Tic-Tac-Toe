package com.example.menutictactoe.tictactoe.gui;

import com.example.menutictactoe.framework.gui.AppWindow;
import com.example.menutictactoe.tictactoe.Board;

import javax.swing.*;
import java.awt.*;

public class GameScreen extends JPanel {

    private Board board = new Board();
    private char currentPlayer = 'X';
    private boolean gameOver = false;

    private JButton[][] buttons = new JButton[3][3];
    private JLabel status = new JLabel();

    public GameScreen(AppWindow window) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel grid = new JPanel(new GridLayout(3, 3));
        grid.setBackground(Color.WHITE);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                JButton button = new JButton(" ");
                button.setFont(new Font("SansSerif", Font.BOLD, 48));
                button.setForeground(Color.BLACK);
                button.setBackground(Color.WHITE);
                button.setFocusPainted(false);

                final int r = row;
                final int c = col;
                button.addActionListener(e -> handleClick(r, c));

                buttons[row][col] = button;
                grid.add(button);
            }
        }

        status.setText("Speler X is aan de beurt");
        status.setForeground(Color.BLACK);
        status.setHorizontalAlignment(SwingConstants.CENTER);
        status.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JButton reset = new JButton("Nieuw spel");
        reset.addActionListener(e -> resetGame());

        JButton back = new JButton("Terug");
        back.addActionListener(e -> window.showMainMenu());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(back, BorderLayout.WEST);
        bottom.add(status, BorderLayout.CENTER);
        bottom.add(reset, BorderLayout.EAST);

        add(grid, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void handleClick(int row, int col) {
        if (gameOver) {
            return;
        }

        if (!board.placeMark(row, col, currentPlayer)) {
            return;
        }

        buttons[row][col].setText(String.valueOf(currentPlayer));

        if (board.hasWinner(currentPlayer)) {
            status.setText("Speler " + currentPlayer + " wint!");
            gameOver = true;
            return;
        }

        if (board.isFull()) {
            status.setText("Gelijkspel!");
            gameOver = true;
            return;
        }

        if (currentPlayer == 'X') {
            currentPlayer = 'O';
        } else {
            currentPlayer = 'X';
        }

        status.setText("Speler " + currentPlayer + " is aan de beurt");
    }

    private void resetGame() {
        board = new Board();
        currentPlayer = 'X';
        gameOver = false;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col].setText(" ");
            }
        }

        status.setText("Speler X is aan de beurt");
    }
}
