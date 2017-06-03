package battleship_ann;

/**
 *
 * @author Philippe
 */
public class SimpleAI extends BattleShipPlayer {

    public SimpleAI(String name) {
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
        
        try {
            for (int y = 0; y < BattleShipGame.BOARD_SIZE; y++) {
                for (int x = 0; x < BattleShipGame.BOARD_SIZE; x++) {
                    if (viewer.getBoardValueAt(x, y) == BattleShipGame.HIT) {
                        int valX = getBoardSquareX(x, y, viewer);
                        int valY = getBoardSquareY(x, y, viewer);
                        if (valX != -1 && valY != -1) {
                            if (Math.random() < 0.5) {
                                mm.makeMove(valX, y);
                            } else {
                                mm.makeMove(x, valY);
                            }
                        } else if (valX != -1) {
                            mm.makeMove(valX, y);
                        } else if (valY != -1) {
                            mm.makeMove(x, valY);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
    
    private int getBoardSquareX(int x, int y, BoardViewer viewer) {
        boolean left = false, right = false;
        if (x >= 1) {
            if (viewer.getBoardValueAt(x-1, y) == BattleShipGame.EMPTY) {
                left = true;
            }
        }
        if (x <= BattleShipGame.BOARD_SIZE - 2) {
            if (viewer.getBoardValueAt(x+1, y) == BattleShipGame.EMPTY) {
                right = true;
            }
        }
        
        if (left == true && right == true) {
            if (Math.random() < 0.5) {
                return x-1;
            } else {
                return x+1;
            }
        } else if (left == true) {
            return x-1;
        } else if (right == true) {
            return x+1;
        } else {
            return -1;
        }
    }
    
    private int getBoardSquareY(int x, int y, BoardViewer viewer) {
        boolean up = false, down = false;
        if (y >= 1) {
            if (viewer.getBoardValueAt(x, y-1) == BattleShipGame.EMPTY) {
                up = true;
            }
        }
        if (y <= BattleShipGame.BOARD_SIZE - 2) {
            if (viewer.getBoardValueAt(x, y+1) == BattleShipGame.EMPTY) {
                down = true;
            }
        }
        
        if (up == true && down == true) {
            if (Math.random() < 0.5) {
                return y-1;
            } else {
                return y+1;
            }
        } else if (up == true) {
            return y-1;
        } else if (down == true) {
            return y+1;
        } else {
            return -1;
        }
    }
    
}
