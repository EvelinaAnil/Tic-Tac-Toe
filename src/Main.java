import framework.MainMenu;
import framework.User;
import framework.UserManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Gebruikersnaam: ");
        String username = scanner.nextLine();

        UserManager userManager = new UserManager();
        User user = userManager.login(username);

        MainMenu mainMenu = new MainMenu();
        mainMenu.show(user, scanner);
    }
}