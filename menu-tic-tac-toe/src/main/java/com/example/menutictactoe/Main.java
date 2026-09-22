package com.example.menutictactoe;

import com.example.menutictactoe.framework.gui.AppWindow;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppWindow window = new AppWindow();
            window.start();
        });
    }
}
