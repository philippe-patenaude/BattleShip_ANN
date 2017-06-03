package battleship_ann;

/**
 *
 * @author Philippe
 */
public class BattleShipGame {
    
    // the board width and height; the board is a square
    public static final int BOARD_SIZE = 10;
    
    // the board tile states
    public static final int PATROL_BOAT = 0;
    public static final int DESTROYER = 1;
    public static final int SUBMARINE = 2;
    public static final int BATTLESHIP = 3;
    public static final int CARRIER = 4;
    // non ship tile states
    public static final int EMPTY = 5;
    public static final int MISS = 6;
    public static final int HIT = 7;
    
    // manage game states
    public static final int STATE_INIT = 0;
    public static final int STATE_PLAYER_PLACE = 1;
    public static final int STATE_PLAYER_MOVE = 2;
    public static final int STATE_GAME_OVER = 3;
    private int state;
    
    // the basic game boards
    private final int[][] board1;
    private final int[][] board2;
    private final BoardViewer board1Viewer;
    private final BoardViewer board2Viewer;
    
    // the players
    private BattleShipPlayer player1;
    private BattleShipPlayer player2;
    
    private boolean playerOnesTurn;
    private final ShipPlacer shipPlacer = new ShipPlacer(null);
    private final MoveMaker moveMaker = new MoveMaker(null);
    
    public BattleShipGame() {
        board1 = new int[BOARD_SIZE][BOARD_SIZE];
        board2 = new int[BOARD_SIZE][BOARD_SIZE];
        board1Viewer = new BoardViewer(this, true);
        board2Viewer = new BoardViewer(this, false);
        player1 = null;
        player2 = null;
        resetGame();
    }
    
    public final void resetGame() {
        
        // init boards
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                board1[y][x] = EMPTY;
                board2[y][x] = EMPTY;
            }
        }
        // init players
        //player1 = null;
        //player2 = null;
        
        // make sure player one goes first every time
        playerOnesTurn = true;
        
        state = STATE_INIT;
        
    }
    
    public void addPlayer(BattleShipPlayer p) throws Exception {
        if (p == null) {
            throw new Exception("You cannot add a null player to the game!");
        } else if (player1 == null) {
            player1 = p;
        } else if (player2 == null) {
            player2 = p;
        } else {
            throw new Exception("You cannot have more than two players in a game!");
        }
    }
    
    public void replacePlayer(BattleShipPlayer p, String playerToReplace) throws Exception {
        if (player1.getName().equalsIgnoreCase(playerToReplace)) {
            player1 = null;
        } else if (player2.getName().equalsIgnoreCase(playerToReplace)) {
            player2 = null;
        } else {
            throw new Exception("Player name \"" + playerToReplace + "\" doesn't exist.");
        }
        addPlayer(p);
    }
    
    
    public void playGame() {
        
        switch (state) {
            case STATE_INIT:
                // randomize who goes first
                if (Math.random() < 0.5) {
                    BattleShipPlayer temp = player1;
                    player1 = player2;
                    player2 = temp;
                }
                player1.newGame();
                player2.newGame();
                state = STATE_PLAYER_PLACE;
                break;
            case STATE_PLAYER_PLACE:
                if (playerOnesTurn == true) {
                    placeShips(board1, player1);
                    if (shipPlacer.placedAllShips() == true) {
                        playerOnesTurn = false;
                    }
                } else {
                    placeShips(board2, player2);
                    if (shipPlacer.placedAllShips() == true) {
                        playerOnesTurn = true;
                        state = STATE_PLAYER_MOVE;
                    }
                }
                break;
            case STATE_PLAYER_MOVE:
                if (isOver()) {
                    state = STATE_GAME_OVER;
                    break;
                }
                if (playerOnesTurn == true) {
                    makeMove(board2, player1);
                    if (moveMaker.madeMove() == true) {
                        if (isOver()) {
                            state = STATE_GAME_OVER;
                            break;
                        }
                        playerOnesTurn = !playerOnesTurn;
                    }
                } else {
                    makeMove(board1, player2);
                    if (moveMaker.madeMove() == true) {
                        if (isOver()) {
                            state = STATE_GAME_OVER;
                            break;
                        }
                        playerOnesTurn = !playerOnesTurn;
                    }
                }
                break;
            case STATE_GAME_OVER:
                break;
        }
        
    }
    
    private void placeShips(int[][] board, BattleShipPlayer p) {
        
        if (shipPlacer.placedAllShips() || shipPlacer.numberOfShipsPlaced() == 0) {
            
            shipPlacer.reset(board);

            try {
                shipPlacer.startPlacing();
            } catch (Exception e) {
                System.out.println("Error with placer: " + e.getMessage());
                e.printStackTrace();
            }

        }
        
        p.placeShip(shipPlacer);
        
        if (shipPlacer.placedAllShips()) {
            try {
                shipPlacer.endPlacing();
            } catch (Exception e) {
                System.out.println("Error with placer: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
    }
    
    // setup stage
        // both players place their ships
        // randomize who goes first
    
    private void makeMove(int[][] board, BattleShipPlayer p) {
        
        //if (moveMaker.madeMove() == true) {
            
            moveMaker.reset(board);
        
            try {
                moveMaker.startMove();
            } catch (Exception e) {
                System.out.println("Error with placer: " + e.getMessage());
                e.printStackTrace();
            }
            
        //}
        
        if (playerOnesTurn == true) {
            p.makeMove(moveMaker, board2Viewer);
        } else {
            p.makeMove(moveMaker, board1Viewer);
        }
        
        if (moveMaker.madeMove() == true) {
            try {
                moveMaker.endMove();
            } catch (Exception e) {
                System.out.println("Error with placer: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
    }
    
    // play stage
        // players take turns placing their attacks
        // game ends when all of one players ships are sunk
    
    public boolean isOver() {
        
        if (state == STATE_GAME_OVER) {
            return true;
        } else if (state != STATE_PLAYER_MOVE) {
            return false;
        }
        
        boolean p1in = false;
        boolean p2in = false;
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                if (board1[y][x] < EMPTY) {
                    p1in = true;
                }
                if (board2[y][x] < EMPTY) {
                    p2in = true;
                }
                if (p1in == true && p2in == true) {
                    return false; // both players are still in the game
                }
            }
        }
        return true;
    }
    
    public BattleShipPlayer getCurrentPlayer() {
        if (playerOnesTurn == true) {
            return player1;
        } else {
            return player2;
        }
    }
    
    public int getBoard1ValueAt(int x, int y) {
        if (x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE) {
            return -1;
        } else {
            return board1[y][x];
        }
    }
    
    public int getBoard2ValueAt(int x, int y) {
        if (x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE) {
            return -1;
        } else {
            return board2[y][x];
        }
    }
    
    public int getGameState() {
        return state;
    }
    
    public BattleShipPlayer getPlayer1() {
        return player1;
    }
    
    public BattleShipPlayer getPlayer2() {
        return player2;
    }
    
}
