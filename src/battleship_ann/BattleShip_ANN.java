package battleship_ann;

import QuickStartBasics.*;
import QuickStartBasics.seng2.*;

import NeuralNetwork2.*;

/*

    The saved AI are:
        BS_AI1.txt - gets saved once the end parameters are met (number of generations reached or percent fitness acheived)
        TempAI.txt - gets saved every generation
        BS_AI1_Good.txt - an AI that works pretty well
        BS_AI1_Good2.txt - an AI that works pretty well

*/

/**
 *
 * @author Philippe
 */
public class BattleShip_ANN {


    public static final int SCREEN_WIDTH = 640;
    public static final int SCREEN_HEIGHT = 480;

    public static final int FPS = 60;
    
    private static final boolean FULL_SCREEN = false;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //trainNetworks();
        
        runGame();
        
    }
    
    private static void runGame() {
        
        QuickStartCore content = new QuickStartCore(SCREEN_WIDTH, SCREEN_HEIGHT, FPS);
        content.addState(new StateGame());
        content.setState("Game");
        QuickStartCore.createWindow("BattleShip", 100, 50, SCREEN_WIDTH, SCREEN_HEIGHT, content, FULL_SCREEN);

        content.run();
        
    }
    
    private static void trainNetworks() {
        FFNetwork net;
        // nearest hit up; nearest hit down; nearest hit left; nearest hit right
        // nearest shot up; nearest shot down; nearest shot left; nearest shot right
        // total shots taken; total shots hit
        
        //net = Evolve2.evolve(100, rater, ticker, FFNetwork.tanh, FFNetwork.sigmoid, 10000, 0.01, 10, 1, 10, 10);
        
        //net = Evolve2.evolve(100, rater, ticker, 10000, 0.01, FFNetwork.loadFromFile("BS_AI1.txt"));
        //net = Evolve2.evolve(100, rater, ticker, 10000, 0.01, FFNetwork.loadFromFile("BS_AI1_Good.txt"));
        //net = Evolve2.evolve(100, rater, ticker, 10000, 0.01, FFNetwork.loadFromFile("BS_AI1_Good2.txt"));
        net = Evolve2.evolve(100, rater, ticker, 10000, 0.01, FFNetwork.loadFromFile("TempAI.txt"));
        
        FFNetwork.saveToFile("BS_AI1.txt", net);
        
    }
    
    private static final OnGenerationTick2 ticker = new OnGenerationTick2() {

        @Override
        public void tick(FFNetwork bestNet, long generationCount) {
            System.out.println("Generation: " + generationCount + " : Fitness: " + bestNet.fitness);
            FFNetwork.saveToFile("TempAI.txt", bestNet);
        }
        
    };
    
    private static final Rater2 rater = new Rater2() {

        @Override
        public double rate(FFNetwork net) {
            
            final int NUMBER_OF_GAMES_TO_PLAY = 200;
            
            BattleShipGame game = new BattleShipGame();
            int winCount = 0;
            try {
                
                game.addPlayer(new ANNAI("ANNAI", net));
                //game.addPlayer(new DumbAI("Dummy"));
                //game.addPlayer(new SimpleAI("Simpleton"));
                
                BattleShipPlayer temp1 = new DumbAI("Dummy");
                BattleShipPlayer temp2 = new SimpleAI("Simpleton");
                
                game.addPlayer(temp2);
                
                for (int i = 0; i < NUMBER_OF_GAMES_TO_PLAY; i++) {
                    if (i%2 == 0) {
                        game.replacePlayer(temp1, "Simpleton");
                    } else {
                        game.replacePlayer(temp2, "Dummy");
                    }
                    while (game.isOver() == false) {
                        game.playGame();
                    }
                    if (game.getCurrentPlayer().getName().equalsIgnoreCase("ANNAI")) {
                        winCount++;
                    }
                    game.resetGame();
                }
                
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            
            return 1.0 - ((double)winCount/(double)NUMBER_OF_GAMES_TO_PLAY);
            
        }
        
    };
    
}
