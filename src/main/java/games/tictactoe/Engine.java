package games.tictactoe;

public class Engine {

    public static final class MoveResult {
        public final boolean accepted;
        public final int row;
        public final int col;
        public final char mark;
        public final boolean win;
        public final boolean draw;
        public final char nextPlayer;

        private MoveResult(boolean accepted, int row, int col, char mark,
                           boolean win, boolean draw, char nextPlayer) {
            this.accepted = accepted;
            this.row = row;
            this.col = col;
            this.mark = mark;
            this.win = win;
            this.draw = draw;
            this.nextPlayer = nextPlayer;
        }

        static MoveResult rejected() {
            return new MoveResult(false, -1, -1, ' ', false, false, ' ');
        }

        static MoveResult placed(int row, int col, char mark, boolean win, boolean draw, char nextPlayer) {
            return new MoveResult(true, row, col, mark, win, draw, nextPlayer);
        }

        public boolean isGameOver() {
            return win || draw;
        }
    }

    private Board board;
    private char currentPlayer = 'X';
    private boolean gameOver;

    private final boolean vsComputer;
    private final boolean cvc;

    private final Computer computerX = new Computer();
    private final Computer computerO = new Computer();

    public Engine(boolean vsComputer, boolean cvc, String difficulty) {
        this.vsComputer = vsComputer;
        this.cvc = cvc;
        if (difficulty != null) {
            computerX.setDifficulty(difficulty);
            computerO.setDifficulty(difficulty);
        }
        reset();
    }

    public void reset() {
        board = new Board();
        currentPlayer = 'X';
        gameOver = false;
    }

    public Board getBoard() {
        return board;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isVsComputer() {
        return vsComputer;
    }

    public boolean isCvc() {
        return cvc;
    }

    public boolean isStarted() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board.getCell(row, col) != ' ') {
                    return true;
                }
            }
        }
        return false;
    }

    public void restore(String boardState, char savedPlayer) {
        board = new Board();
        gameOver = false;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                char mark = boardState.charAt(row * 3 + col);
                board.setCell(row, col, mark);
            }
        }
        currentPlayer = savedPlayer;
    }

    public String serializeBoard() {
        StringBuilder state = new StringBuilder();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                state.append(board.getCell(row, col));
            }
        }
        return state.toString();
    }

    public MoveResult playerMove(int row, int col) {
        if (gameOver || cvc) {
            return MoveResult.rejected();
        }
        if (vsComputer && currentPlayer == 'O') {
            return MoveResult.rejected();
        }
        return applyMove(row, col, currentPlayer);
    }

    public MoveResult computerMove() {
        if (gameOver) {
            return MoveResult.rejected();
        }
        char opponentMark = (currentPlayer == 'X') ? 'O' : 'X';
        Computer active = (currentPlayer == 'X') ? computerX : computerO;
        int[] move = active.getBestMove(board, currentPlayer, opponentMark);
        if (move == null) {
            return MoveResult.rejected();
        }
        return applyMove(move[0], move[1], currentPlayer);
    }

    private MoveResult applyMove(int row, int col, char mark) {
        if (!board.placeMark(row, col, mark)) {
            return MoveResult.rejected();
        }

        if (board.hasWinner(mark)) {
            gameOver = true;
            return MoveResult.placed(row, col, mark, true, false, mark);
        }
        if (board.isFull()) {
            gameOver = true;
            return MoveResult.placed(row, col, mark, false, true, mark);
        }

        currentPlayer = (mark == 'X') ? 'O' : 'X';
        return MoveResult.placed(row, col, mark, false, false, currentPlayer);
    }
}
