package battleship_ann;

/**
 *
 * @author Philippe
 */
public class HumanPlayer extends BattleShipPlayer {

    private int x = -1, y = -1;
    
    public HumanPlayer(String name) {
        super(name);
    }
    
    @Override
    public void newGame() {}
    
    public void makeMove(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setX(int value) {x=value;}
    public void setY(int value) {y=value;}
    
    @Override
    public void placeShip(ShipPlacer sp) {
        if (x != -1 && y != -1){
            try {
                int rand = 0;
                int count = 0;
                while (sp.hasPlaced(rand) == true || count < 100) {
                    rand = (int)(Math.random()*5.0);
                    count++;
                }
                sp.placeShip(x, y, rand, Math.random()<0.5);
                x = -1; y = -1;
            } catch (Exception e) {
                errorString = e.getMessage();
            }
        }
    }

    @Override
    public void makeMove(MoveMaker mm, BoardViewer viewer) {
        if (x != -1 && y != -1){
            try {
                mm.makeMove(x, y);
                x = -1; y = -1;
            } catch (Exception e) {
                errorString = e.getMessage();
            }
        }
    }
    
}
