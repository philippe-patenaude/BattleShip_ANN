package battleship_ann;

/**
 *
 * @author Philippe
 */
public final class ShipPlacer {
    
    private final boolean[] shipsPlaced;
    private int[][] board;
    
    public ShipPlacer(int[][] board) {
        shipsPlaced = new boolean[5];
        reset(board);
    }
    
    public void reset(int[][] board) {
        for (int i = 0; i < 5; i++) {
            shipsPlaced[i] = false;
        }
        this.board = board;
    }
    
    public void startPlacing() throws Exception {
        if (board == null) {
            throw new Exception("You must have a board!");
        }
        if (board.length != BattleShipGame.BOARD_SIZE ||
                board[0].length != BattleShipGame.BOARD_SIZE) {
            throw new Exception("Your board must be the standard size!");
        }
        for (int y = 0; y < BattleShipGame.BOARD_SIZE; y++) {
            for (int x = 0; x < BattleShipGame.BOARD_SIZE; x++) {
                if (board[y][x] != BattleShipGame.EMPTY) {
                    throw new Exception("Your board must be empty to place units!");
                }
            }
        }
        for (int i = 0; i < 5; i++) {
            if (shipsPlaced[i] == true) {
                throw new Exception("The ships must not be placed before placing");
            }
        }
    }
    
    public void placeShip(int x, int y, int type, boolean isHorizontal) throws Exception {
        
        if (x < 0 || x >= BattleShipGame.BOARD_SIZE ||
                y < 0 || y >= BattleShipGame.BOARD_SIZE) {
            throw new Exception("You must pick a place inside of the board!");
        }
        if (type < 0 || type >= BattleShipGame.EMPTY) {
            throw new Exception("You must pick a valid ship!");
        }
        if (shipsPlaced[type] != false) {
            throw new Exception("You already played that ship!");
        }
        int size = 2;
        switch (type) {
            case BattleShipGame.PATROL_BOAT: size = 2; break;
            case BattleShipGame.DESTROYER: size = 3; break;
            case BattleShipGame.SUBMARINE: size = 3; break;
            case BattleShipGame.BATTLESHIP: size = 4; break;
            case BattleShipGame.CARRIER: size = 5; break;
        }
        if (isHorizontal == true) {
            for (int i = x; i < x+size; i++) {
                if (i >= BattleShipGame.BOARD_SIZE) {
                    throw new Exception("You must pick a place inside of the board!");
                }
                if (board[y][i] != BattleShipGame.EMPTY) {
                    throw new Exception("That spot is blocked!");
                }
            }
            for (int i = x; i < x+size; i++) {
                board[y][i] = type;
                shipsPlaced[type] = true;
            }
        } else {
            for (int i = y; i < y+size; i++) {
                if (i >= BattleShipGame.BOARD_SIZE) {
                    throw new Exception("You must pick a place inside of the board!");
                }
                if (board[i][x] != BattleShipGame.EMPTY) {
                    throw new Exception("That spot is blocked!");
                }
            }
            for (int i = y; i < y+size; i++) {
                board[i][x] = type;
                shipsPlaced[type] = true;
            }
        }
        
    }
    
    public void endPlacing() throws Exception {
        if (board == null) {
            throw new Exception("You must have a board!");
        }
        if (board.length != BattleShipGame.BOARD_SIZE ||
                board[0].length != BattleShipGame.BOARD_SIZE) {
            throw new Exception("Your board must be the standard size!");
        }
        for (int i = 0; i < 5; i++) {
            if (shipsPlaced[i] == false) {
                throw new Exception("The ships must all be placed after placing");
            }
        }
    }
    
    public int numberOfShipsPlaced() {
        int shipCount = 0;
        for (int i = 0; i < 5; i++) {
            if (shipsPlaced[i] == true) {
                shipCount++;
            }
        }
        return shipCount;
    }
    
    public boolean placedAllShips() {
        for (int i = 0; i < 5; i++) {
            if (shipsPlaced[i] == false) {
                return false;
            }
        }
        return true;
    }
    
    public boolean hasPlaced(int shipType) {
        if (shipType < 0 || shipType >= 5) {
            return false;
        } else {
            return shipsPlaced[shipType];
        }
    }
    
}
