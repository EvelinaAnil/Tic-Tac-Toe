package com.example.menutictactoe.tictactoe;

import com.example.menutictactoe.framework.Game;

import java.util.Scanner;

public class TicTacToe extends Game {

    @Override
    public String getName() {
        return "Tic-Tac-Toe";
    }

    @Override
    public void start(Scanner scanner) {
        Board board = new Board();
        char currentPlayer = 'X';

        System.out.println("Tic-Tac-Toe gestart!");
        System.out.println("Kies een vakje zoals A1 of C2.");

        while (true) {
            board.print();
            System.out.print("Speler " + currentPlayer + ", kies een vakje: ");
            String input = scanner.nextLine().trim();

            if (!board.placeMark(input, currentPlayer)) {
                System.out.println("Ongeldige zet, probeer opnieuw.");
                continue;
            }

            if (board.hasWinner(currentPlayer)) {
                board.print();
                System.out.println("Speler " + currentPlayer + " wint!");
                break;
            }

            if (board.isFull()) {
                board.print();
                System.out.println("Gelijkspel!");
                break;
            }

            if (currentPlayer == 'X') {
                currentPlayer = 'O';
            } else {
                currentPlayer = 'X';
            }
        }
    }
}
