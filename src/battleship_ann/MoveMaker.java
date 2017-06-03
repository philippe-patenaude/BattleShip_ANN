package battleship_ann;

/**
 *
 * @author Philippe
 */
public final class MoveMaker {
    
    private int[][] board;
    private boolean madeMove;
    
    public MoveMaker(int[][] board) {
        reset(board);
    }
    
    public void reset(int[][] board) {
        this.board = board;
        madeMove = false;
    }
    
    public void startMove() throws Exception {
        if (board == null) {
            throw new Exception("You must have a board!");
        }
        if (board.length != BattleShipGame.BOARD_SIZE ||
                board[0].length != BattleShipGame.BOARD_SIZE) {
            throw new Exception("Your board must be the standard size!");
        }
//        for (int y = 0; y < BattleShipGame.BOARD_SIZE; y++) {
//            for (int x = 0; x < BattleShipGame.BOARD_SIZE; x++) {
//                if (board[y][x] != 0) {
//                    throw new Exception("Your board must be empty to place units!");
//                }
//            }
//        }
//        if (madeMove == true) {
//            throw new Exception("You need to reset the move maker!");
//        }
    }
    
    public void makeMove(int x, int y) throws Exception {
        
        if (x < 0 || x >= BattleShipGame.BOARD_SIZE ||
                y < 0 || y >= BattleShipGame.BOARD_SIZE) {
            throw new Exception("You must pick a place inside of the board!");
        }
        if (board[y][x] == BattleShipGame.HIT || board[y][x] == BattleShipGame.MISS) {
            throw new Exception("That spot has already been played!");
        }
        
        if (board[y][x] < BattleShipGame.EMPTY) {
            board[y][x] = BattleShipGame.HIT;
        } else {
            board[y][x] = BattleShipGame.MISS;
        }
        madeMove = true;
        
    }
    
    public void endMove() throws Exception {
        if (board == null) {
            throw new Exception("You must have a board!");
        }
        if (board.length != BattleShipGame.BOARD_SIZE ||
                board[0].length != BattleShipGame.BOARD_SIZE) {
            throw new Exception("Your board must be the standard size!");
        }
        if (madeMove == false) {
            throw new Exception("A move needs to be made!");
        }
    }
    
    public boolean madeMove() {
        return madeMove;
    }
    
}
