package battleship_ann;

import java.awt.Point;

/**
 *
 * @author Philippe
 */
public abstract class BattleShipPlayer {
    
    protected String errorString = null;
    private final String name;
    
    public BattleShipPlayer(String name) {
        this.name = name;
    }
    
    public abstract void newGame();
    public abstract void placeShip(ShipPlacer sp);
    public abstract void makeMove(MoveMaker mm, BoardViewer viewer);
    
    public String getErrorString() {
        String temp = errorString;
        errorString = null;
        return temp;
    }
    
    public String getName() {
        return name;
    }
    
}
