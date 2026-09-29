package games.tictactoe;

import java.util.Random;

public class Computer {
    private final Random random = new Random();
    private String difficulty = "MEDIUM";

    public void setDifficulty(String difficulty){
        this.difficulty = difficulty;
    }
    public int[] getBestMove(Board board, char computerMark, char playerMark) {
        switch (difficulty.toUpperCase()) {
            case "EASY":
                return getRandomMove(board);
            case "MEDIUM":
                return getMediumMove(board, computerMark, playerMark);
            case "HARD":
                return getBestMinimaxMove(board, computerMark, playerMark);
            default:
                return getMediumMove(board, computerMark, playerMark);
        }

    }

    // --- EASY: Puur willekeurig ---
    private int[] getRandomMove(Board board) {
        while (true) {
            int row = random.nextInt(3);
            int col = random.nextInt(3);
            if (board.isCellEmpty(row, col)) {
                return new int[]{row, col};
            }
        }
    }

    // --- MEDIUM: Kan winnen of speler blokkeren, anders willekeurig ---
    private int[] getMediumMove(Board board, char compMark, char playerMark) {
        int[] winningMove = findWinningMove(board, compMark);
        if (winningMove != null) return winningMove;

        int[] blockingMove = findWinningMove(board, playerMark);
        if (blockingMove != null) return blockingMove;

        return getRandomMove(board);
    }

    private int[] findWinningMove(Board board, char mark) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board.isCellEmpty(r, c)) {
                    // zet tijdelijk
                    board.placeMark(r, c, mark);

                    // check winnende
                    boolean wins = board.hasWinner(mark);
                    board.clearCell(r, c);

                    // do
                    if (wins) {
                        return new int[] { r, c };
                    }
                }
            }
        }
        // geen winnende
        return null;
    }





    // --- HARD: Minimax algoritme (Onverslaanbaar) ---
    private int[] getBestMinimaxMove(Board board, char compMark, char playerMark) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board.isCellEmpty(row, col)) {
                    // zet neer
                    board.placeMark(row, col, compMark);
                    // count the best score voor computer
                    int score = minimax(board, 0, false, compMark, playerMark);

                    board.clearCell(row, col);

                    //als het beste dan vorige remember
                    if (score > bestScore){
                        bestScore = score;
                        bestMove = new int[] {row, col};
                    }
                }
            }
        }
        // fallback als minimax nog gekoppeld moet worden
        return bestMove != null ? bestMove : getRandomMove((board));
    }

    private int minimax(Board board, int depth, boolean isMaximizing, char compMark, char playerMark) {
        if (board.hasWinner(compMark)) return 10 - depth;
        if (board.hasWinner(playerMark)) return depth - 10;
        if (board.isFull()) return 0;

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (board.isCellEmpty(r, c)) {
                        board.placeMark(r, c, compMark);
                        int eval = minimax(board, depth + 1, false, compMark, playerMark);
                        board.clearCell(r, c);
                        maxEval = Math.max(maxEval, eval);
                    }
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (board.isCellEmpty(r, c)) {
                        board.placeMark(r, c, playerMark);
                        int eval = minimax(board, depth + 1, true, compMark, playerMark);
                        board.clearCell(r, c);
                        minEval = Math.min(minEval, eval);
                    }
                }
            }
            return minEval;
        }
    }


    private boolean isCellEmpty(Board board, int row, int col) {

        return board.isCellEmpty(row, col);
    }
}
