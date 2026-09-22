package com.example.menutictactoe.framework;

import java.util.List;
import java.util.Scanner;

public class MainMenu {

    private final GameManager gameManager = new GameManager();

    public void show(User user, Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("Welkom " + user.getUsername());
            System.out.println("1. Spel kiezen");
            System.out.println("2. Scores");
            System.out.println("3. Afsluiten");

            System.out.print("Keuze: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showGames(scanner);
                    break;

                case "2":
                    System.out.println("Scores komen hier.");
                    break;

                case "3":
                    running = false;
                    System.out.println("Programma afgesloten.");
                    break;

                default:
                    System.out.println("Ongeldige keuze.");
            }
        }
    }

    private void showGames(Scanner scanner) {
        List<Game> games = gameManager.getGames();

        System.out.println();
        System.out.println("Kies een spel:");

        System.out.println("0. Terug");

        for (int i = 0; i < games.size(); i++) {
            System.out.println((i + 1) + ". " + games.get(i).getName());
        }

        System.out.print("Keuze: ");
        int choice = Integer.parseInt(scanner.nextLine());

        if (choice == 0) {
            return;
        }

        if (choice < 1 || choice > games.size()) {
            System.out.println("Ongeldige keuze.");
            return;
        }

        Game selectedGame = games.get(choice - 1);
        selectedGame.start(scanner);
    }
}