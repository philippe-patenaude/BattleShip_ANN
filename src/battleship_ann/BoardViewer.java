package battleship_ann;

/**
 *
 * @author Philippe
 */
public class BoardViewer {
    
    private final BattleShipGame game;
    private final boolean boardOne;
    
    public BoardViewer(BattleShipGame game, boolean boardOne) {
        this.game = game;
        this.boardOne = boardOne;
    }
    
    public int getBoardValueAt(int x, int y) {
        int value;
        if (boardOne == true) {
            value = game.getBoard1ValueAt(x, y);
        } else {
            value = game.getBoard2ValueAt(x, y);
        }
        if (value < BattleShipGame.EMPTY) {
            value = BattleShipGame.EMPTY;
        }
        return value;
    }
    
}
