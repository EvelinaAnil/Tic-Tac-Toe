package games.tictactoe;

public class Board {

    private char[][] cells = new char[3][3];

    public Board() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                cells[row][col] = ' ';
            }
        }
    }

    public void print() {
        System.out.println();
        System.out.println("    A   B   C");
        for (int row = 0; row < 3; row++) {
            System.out.println("  +---+---+---+");
            System.out.print((row + 1) + " ");
            for (int col = 0; col < 3; col++) {
                System.out.print("| " + cells[row][col] + " ");
            }
            System.out.println("|");
        }
        System.out.println("  +---+---+---+");
        System.out.println();
    }

    public boolean placeMark(String position, char mark) {
        if (position.length() != 2) {
            return false;
        }

        int col = columnFromLetter(position.charAt(0));
        int row = rowFromNumber(position.charAt(1));

        if (col == -1 || row == -1) {
            return false;
        }

        if (cells[row][col] != ' ') {
            return false;
        }

        cells[row][col] = mark;
        return true;
    }

    private int columnFromLetter(char letter) {
        char upper = Character.toUpperCase(letter);
        if (upper == 'A') {
            return 0;
        }
        if (upper == 'B') {
            return 1;
        }
        if (upper == 'C') {
            return 2;
        }
        return -1;
    }

    private int rowFromNumber(char number) {
        if (number == '1') {
            return 0;
        }
        if (number == '2') {
            return 1;
        }
        if (number == '3') {
            return 2;
        }
        return -1;
    }

    public boolean hasWinner(char mark) {
        for (int i = 0; i < 3; i++) {
            if (cells[i][0] == mark && cells[i][1] == mark && cells[i][2] == mark) {
                return true;
            }
            if (cells[0][i] == mark && cells[1][i] == mark && cells[2][i] == mark) {
                return true;
            }
        }

        if (cells[0][0] == mark && cells[1][1] == mark && cells[2][2] == mark) {
            return true;
        }
        if (cells[0][2] == mark && cells[1][1] == mark && cells[2][0] == mark) {
            return true;
        }

        return false;
    }

    public boolean isFull() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (cells[row][col] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }
}
