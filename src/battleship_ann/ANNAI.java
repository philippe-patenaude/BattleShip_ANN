package battleship_ann;

import NeuralNetwork2.*;

/**
 *
 * @author Philippe
 */
public class ANNAI extends BattleShipPlayer {
        
    private static final int SIZE = BattleShipGame.BOARD_SIZE;
    private static final int MAX_HIT_COUNT = 5+4+3+3+2; // each of the ships' sizes
    private static final int MAX_SHOT_COUNT = SIZE*SIZE;

    private final FFNetwork net;
    
    private class BoardCount {
        public int upCount, downCount, leftCount, rightCount; // the distance in said direction until something is run into
        public BoardCount() {
            upCount = 0;
            downCount = 0;
            leftCount = 0;
            rightCount = 0;
        }
    }
    
    // optimization variables
    private int hitCount = 0;
    private int shotCount = 0;
    private final double[] inputs = new double[10];
    private final BoardCount[][] hitDistCount = new BoardCount[SIZE][SIZE];
    private final BoardCount[][] missDistCount = new BoardCount[SIZE][SIZE];
    
    
    public ANNAI(String name, FFNetwork network) {
        super(name);
        net = network;
        
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                hitDistCount[y][x] = new BoardCount();
                missDistCount[y][x] = new BoardCount();
            }
        }
        
    }
    
    @Override
    public void newGame() {
        hitCount = 0;
        shotCount = 0;
        
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                
                hitDistCount[y][x].downCount = SIZE-y;
                hitDistCount[y][x].upCount = y;
                hitDistCount[y][x].rightCount = SIZE-x;
                hitDistCount[y][x].leftCount = x;
                
                missDistCount[y][x].downCount = SIZE-y;
                missDistCount[y][x].upCount = y;
                missDistCount[y][x].rightCount = SIZE-x;
                missDistCount[y][x].leftCount = x;
                
            }
        }
        
    }
    
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
        
//        int hitCount = 0;
//        int shotCount = 0;
//        for (int y = 0; y < SIZE; y++) {
//            for (int x = 0; x < SIZE; x++) {
//                int value = viewer.getBoardValueAt(x, y);
//                if (value == BattleShipGame.HIT) {
//                    hitCount++;
//                    shotCount++;
//                } else if (value == BattleShipGame.MISS) {
//                    shotCount++;
//                }
//            }
//        }
        
        // calculate high level information
        double hitPercent = (double)hitCount/(double)MAX_HIT_COUNT;
        double shotPercent = (double)shotCount/(double)MAX_SHOT_COUNT;
                    
        inputs[8] = hitPercent;
        inputs[9] = shotPercent;
        
        double bestValue = -100.0;
        int bestX = 0, bestY = 0;
        
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                
                int value = viewer.getBoardValueAt(x, y);
                if (value == BattleShipGame.EMPTY) {
                    
//                    inputs[0] = (double)upCount(viewer, x, y, BattleShipGame.HIT)/(double)SIZE;
//                    inputs[1] = (double)downCount(viewer, x, y, BattleShipGame.HIT)/(double)SIZE;
//                    inputs[2] = (double)leftCount(viewer, x, y, BattleShipGame.HIT)/(double)SIZE;
//                    inputs[3] = (double)rightCount(viewer, x, y, BattleShipGame.HIT)/(double)SIZE;
//                    
//                    inputs[4] = (double)upCount(viewer, x, y, BattleShipGame.MISS)/(double)SIZE;
//                    inputs[5] = (double)downCount(viewer, x, y, BattleShipGame.MISS)/(double)SIZE;
//                    inputs[6] = (double)leftCount(viewer, x, y, BattleShipGame.MISS)/(double)SIZE;
//                    inputs[7] = (double)rightCount(viewer, x, y, BattleShipGame.MISS)/(double)SIZE;
                    
                    inputs[0] = (double)hitDistCount[y][x].upCount/(double)SIZE;
                    inputs[1] = (double)hitDistCount[y][x].downCount/(double)SIZE;
                    inputs[2] = (double)hitDistCount[y][x].leftCount/(double)SIZE;
                    inputs[3] = (double)hitDistCount[y][x].rightCount/(double)SIZE;
                    
                    inputs[4] = (double)missDistCount[y][x].upCount/(double)SIZE;
                    inputs[5] = (double)missDistCount[y][x].downCount/(double)SIZE;
                    inputs[6] = (double)missDistCount[y][x].leftCount/(double)SIZE;
                    inputs[7] = (double)missDistCount[y][x].rightCount/(double)SIZE;
        
                    double out = net.fire(inputs)[0];
                    
                    if (out > bestValue) {
                        bestX = x;
                        bestY = y;
                        bestValue = out;
                    }
                    
                }
                
            }
        }
        
        try {
            mm.makeMove(bestX, bestY);
            if (viewer.getBoardValueAt(bestX, bestY) == BattleShipGame.HIT) {
                hitCount++;
                shotCount++;
                upCount(viewer, bestX, bestY, hitDistCount);
                downCount(viewer, bestX, bestY, hitDistCount);
                leftCount(viewer, bestX, bestY, hitDistCount);
                rightCount(viewer, bestX, bestY, hitDistCount);
            } else if (viewer.getBoardValueAt(bestX, bestY) == BattleShipGame.MISS) {
                shotCount++;
                upCount(viewer, bestX, bestY, missDistCount);
                downCount(viewer, bestX, bestY, missDistCount);
                leftCount(viewer, bestX, bestY, missDistCount);
                rightCount(viewer, bestX, bestY, missDistCount);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage()); // temp
        }
        
    }
    
    private void upCount(BoardViewer viewer, int x, int y, BoardCount[][] counter) {
        int count = 0;
        for (int c = y-1; c >= 0; c--) {
            int value = viewer.getBoardValueAt(x, c);
            if (value == BattleShipGame.EMPTY) {
                count++;
                counter[c][x].downCount = count;
            } else {
                return;
            }
        }
    }
    
    private void downCount(BoardViewer viewer, int x, int y, BoardCount[][] counter) {
        int count = 0;
        for (int c = y+1; c < BattleShipGame.BOARD_SIZE; c++) {
            int value = viewer.getBoardValueAt(x, c);
            if (value == BattleShipGame.EMPTY) {
                count++;
                counter[c][x].upCount = count;
            } else {
                return;
            }
        }
    }
    
    private void leftCount(BoardViewer viewer, int x, int y, BoardCount[][] counter) {
        int count = 0;
        for (int c = x-1; c >= 0; c--) {
            int value = viewer.getBoardValueAt(c, y);
            if (value == BattleShipGame.EMPTY) {
                count++;
                counter[y][c].rightCount = count;
            } else {
                return;
            }
        }
    }
    
    private void rightCount(BoardViewer viewer, int x, int y, BoardCount[][] counter) {
        int count = 0;
        for (int c = x+1; c < BattleShipGame.BOARD_SIZE; c++) {
            int value = viewer.getBoardValueAt(c, y);
            if (value == BattleShipGame.EMPTY) {
                count++;
                counter[y][c].leftCount = count;
            } else {
                return;
            }
        }
    }
    
//    private int upCount(BoardViewer viewer, int x, int y, int searchValue) {
//        int count = 0;
//        for (int c = y; c >= 0; c--) {
//            int value = viewer.getBoardValueAt(x, c);
//            if (value == searchValue) {
//                return count;
//            } else {
//                count++;
//            }
//        }
//        return count;
//    }
//    
//    private int downCount(BoardViewer viewer, int x, int y, int searchValue) {
//        int count = 0;
//        for (int c = y; c < BattleShipGame.BOARD_SIZE; c++) {
//            int value = viewer.getBoardValueAt(x, c);
//            if (value == searchValue) {
//                return count;
//            } else {
//                count++;
//            }
//        }
//        return count;
//    }
//    
//    private int leftCount(BoardViewer viewer, int x, int y, int searchValue) {
//        int count = 0;
//        for (int c = x; c >= 0; c--) {
//            int value = viewer.getBoardValueAt(c, y);
//            if (value == searchValue) {
//                return count;
//            } else {
//                count++;
//            }
//        }
//        return count;
//    }
//    
//    private int rightCount(BoardViewer viewer, int x, int y, int searchValue) {
//        int count = 0;
//        for (int c = x; c < BattleShipGame.BOARD_SIZE; c++) {
//            int value = viewer.getBoardValueAt(c, y);
//            if (value == searchValue) {
//                return count;
//            } else {
//                count++;
//            }
//        }
//        return count;
//    }
    
}
