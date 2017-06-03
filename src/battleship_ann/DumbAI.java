package battleship_ann;

/**
 *
 * @author Philippe
 */
public class DumbAI extends BattleShipPlayer {

    public DumbAI(String name) {
        super(name);
    }
    
    @Override
    public void newGame() {}
    
    @Override
    public void placeShip(ShipPlacer sp) {
        
        for (int shipType = 0; shipType < 5; shipType++) {
            
            while (sp.hasPlaced(shipType) == false) {
                int x = (int)(Math.random()*BattleShipGame.BOARD_SIZE);
                int y = (int)(Math.random()*BattleShipGame.BOARD_SIZE);

                try {
                    sp.placeShip(x, y, shipType, Math.random()<0.5);
                } catch (Exception e) {
                    // ignore errors
                    //errorString = e.getMessage();
                }
            }
            
        }
        
    }

    @Override
    public void makeMove(MoveMaker mm, BoardViewer viewer) {
        while (mm.madeMove() == false) {
            try {

                int x = (int)(Math.random()*BattleShipGame.BOARD_SIZE);
                int y = (int)(Math.random()*BattleShipGame.BOARD_SIZE);

                mm.makeMove(x, y);

            } catch (Exception e) {
                // ignore errors
                //errorString = e.getMessage();
            }
        }
        
    }
    
}
