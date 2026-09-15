package framework;

import java.util.Scanner;

public class MainMenu {

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
                    System.out.println("Game kiezen komt hier.");
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
}