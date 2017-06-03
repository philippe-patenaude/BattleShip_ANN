package battleship_ann;

import NeuralNetwork2.FFNetwork;
import QuickStartBasics.IM;
import QuickStartBasics.seng2.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 *
 * @author Philippe
 */
public class StateGame implements GameState {
    
    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 40;
    private static final int BOARD_SPACE = 30;
    private static final int SQUARE_SIZE = 25;

    private final BattleShipGame game = new BattleShipGame();
    private BattleShipPlayer player1, player2;
    
    // game states
    private static final int STATE_INIT = 0;
    private static final int STATE_PLAY = 1;
    private static final int STATE_MENU = 2; // let's a user choose what kind of game to run
    private int state;
    
    private boolean mousePressed = false;
    
    private String errorString;
    
    @Override
    public void update() {
        
        switch (state) {
            case STATE_MENU:
                player1 = new DumbAI("Dummy");
                //player1 = new SimpleAI("Simpleton");
                //player1 = new HumanPlayer("Me");
                //player1 = new ANNAI("ANNAI2", FFNetwork.loadFromFile("BS_AI1_Good2.txt"));
                //player1 = new ANNAI("ANNAI2", FFNetwork.create(FFNetwork.tanh, FFNetwork.sigmoid, 10, 1, 20));
                //player2 = new ANNAI("ANNAI", FFNetwork.loadFromFile("BS_AI1.txt"));
                //player2 = new ANNAI("ANNAI", FFNetwork.loadFromFile("TempAI.txt"));
                //player2 = new ANNAI("ANNAI", FFNetwork.loadFromFile("BS_AI1_Good.txt"));
                player2 = new ANNAI("ANNAI2", FFNetwork.loadFromFile("BS_AI1_Good2.txt"));
                //player2 = new ANNAI("ANNAI", FFNetwork.create(FFNetwork.tanh, FFNetwork.sigmoid, 10, 1, 20));
                //player2 = new SimpleAI("Simpleton");
                state = STATE_INIT;
                break;
            case STATE_INIT:
                game.resetGame();
                errorString = "";
                try {
                    game.addPlayer(player1);
                    game.addPlayer(player2);
                } catch (Exception e) {
                    errorString = "Error adding a player: " + e.getMessage();
                }
                state = STATE_PLAY;
                break;
            case STATE_PLAY:
                if (IM.leftMouseButtonDown == true && mousePressed == false) {
                    mousePressed = true;
                    if (game.getCurrentPlayer().getClass().equals(HumanPlayer.class)) {
                        HumanPlayer currentPlayer = (HumanPlayer)game.getCurrentPlayer();
                        if ((currentPlayer == player2 && game.getGameState() == BattleShipGame.STATE_PLAYER_MOVE) ||
                                  (currentPlayer == player1 && game.getGameState() == BattleShipGame.STATE_PLAYER_PLACE)) {
                            int x, y;
                            x = IM.mouseX - X_OFFSET;
                            x /= SQUARE_SIZE;
                            y = IM.mouseY - Y_OFFSET;
                            y /= SQUARE_SIZE;
                            currentPlayer.setX(x);
                            currentPlayer.setY(y);
                        } else if ((currentPlayer == player1 && game.getGameState() == BattleShipGame.STATE_PLAYER_MOVE) ||
                            (currentPlayer == player2 && game.getGameState() == BattleShipGame.STATE_PLAYER_PLACE)) {
                            int x, y;
                            x = IM.mouseX - X_OFFSET - BOARD_SPACE - SQUARE_SIZE*BattleShipGame.BOARD_SIZE;
                            x /= SQUARE_SIZE;
                            y = IM.mouseY - Y_OFFSET;
                            y /= SQUARE_SIZE;
                            currentPlayer.setX(x);
                            currentPlayer.setY(y);
                        }
                    }
                } else if (IM.leftMouseButtonDown == false) {
                    mousePressed = false;
                }
                game.playGame();
                if (game.getCurrentPlayer().errorString != null) {
                    errorString = game.getCurrentPlayer().errorString;
                    game.getCurrentPlayer().errorString = null;
                } else {
                    errorString = "";
                }
                if (game.isOver() && IM.keys[KeyEvent.VK_R]) {
                    game.resetGame();
                }
                break;
        }
        
    }

    @Override
    public void draw(Graphics2D g) {
        if (state != STATE_MENU) {
            drawGame(g);
        }
    }
    
    private void drawGame(Graphics2D g) {
        
        g.setColor(new Color(225,255,225));
        g.fillRect(0, 0, BattleShip_ANN.SCREEN_WIDTH, BattleShip_ANN.SCREEN_HEIGHT);
        
        g.setColor(Color.red);
        g.drawString(errorString, 10, 20);
        
        // draw game state
        g.setColor(Color.blue);
        String str = "";
        switch (game.getGameState()) {
            case (BattleShipGame.STATE_INIT):
                str = "The game is loading...";
                break;
            case (BattleShipGame.STATE_PLAYER_PLACE):
                str = "Place your units.";
                str += " It is " + game.getCurrentPlayer().getName() + "'s turn.";
//                if (game.getCurrentPlayer() == player1) {
//                    str += " It is player 1's turn.";
//                } else {
//                    str += " It is player 2's turn.";
//                }
                break;
            case (BattleShipGame.STATE_PLAYER_MOVE):
                str = "Make your moves.";
                str += " It is " + game.getCurrentPlayer().getName() + "'s turn.";
//                if (game.getCurrentPlayer() == player1) {
//                    str += " It is player 1's turn.";
//                } else {
//                    str += " It is player 2's turn.";
//                }
                break;
            case (BattleShipGame.STATE_GAME_OVER):
                str = "The game is over.";
                str += " " + game.getCurrentPlayer().getName() + " has emerged victorious!";
//                if (game.getCurrentPlayer() == player1) {
//                    //str += " Player 1 has emerged victorious!";
//                } else {
//                    str += " Player 2 has emerged victorious!";
//                }
                break;
        }
        g.drawString(str,10,480-30);
        
//        int value;
//        
//        for (int y = 0; y < BattleShipGame.BOARD_SIZE; y++) {
//            for (int x = 0; x < BattleShipGame.BOARD_SIZE; x++) {
//                
//                // draw board 1
//                value = game.getBoard1ValueAt(x, y);
//                if (value < BattleShipGame.EMPTY) {
//                    g.setColor(Color.gray);
//                } else if (value == BattleShipGame.HIT) {
//                    g.setColor(Color.red);
//                } else if (value == BattleShipGame.MISS) {
//                    g.setColor(Color.white);
//                } else {
//                    g.setColor(Color.lightGray);
//                }
//                g.fillRect(X_OFFSET + x*SQUARE_SIZE, Y_OFFSET + y*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
//                
//                // draw board 2
//                value = game.getBoard2ValueAt(x, y);
//                if (value < BattleShipGame.EMPTY) {
//                    g.setColor(Color.gray);
//                } else if (value == BattleShipGame.HIT) {
//                    g.setColor(Color.red);
//                } else if (value == BattleShipGame.MISS) {
//                    g.setColor(Color.white);
//                } else {
//                    g.setColor(Color.lightGray);
//                }
//                g.fillRect(X_OFFSET + x*SQUARE_SIZE + BOARD_SPACE + SQUARE_SIZE*BattleShipGame.BOARD_SIZE, Y_OFFSET + y*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
//                
//            }
//        }
        
        drawBoards(g);
        
    }
    
    private void drawBoards(Graphics2D g) {
        
        //int value;
        
        for (int y = 0; y < BattleShipGame.BOARD_SIZE; y++) {
            for (int x = 0; x < BattleShipGame.BOARD_SIZE; x++) {
                
//                // draw board 1
//                value = game.getBoard1ValueAt(x, y);
//                if (value < BattleShipGame.EMPTY) {
//                    g.setColor(Color.gray);
//                } else if (value == BattleShipGame.HIT) {
//                    g.setColor(Color.red);
//                } else if (value == BattleShipGame.MISS) {
//                    g.setColor(Color.white);
//                } else {
//                    g.setColor(Color.lightGray);
//                }
//                g.fillRect(X_OFFSET + x*SQUARE_SIZE, Y_OFFSET + y*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
//                
                /*if (game.getPlayer1() == player1) {
                    if (game.getCurrentPlayer() == player2 && player2.getClass().equals(HumanPlayer.class)) {
                        drawBoardSquareLeft(g, x, y, game.getBoard1ValueAt(x,y), true);
                    } else {
                        drawBoardSquareLeft(g, x, y, game.getBoard1ValueAt(x,y), false);
                    }
                    if (game.getCurrentPlayer() == player1 && player1.getClass().equals(HumanPlayer.class)) {
                        drawBoardSquareRight(g, x, y, game.getBoard2ValueAt(x,y), true);
                    } else {
                        drawBoardSquareRight(g, x, y, game.getBoard2ValueAt(x,y), false);
                    }
                } else {
                    if (game.getCurrentPlayer() == player2 && player2.getClass().equals(HumanPlayer.class)) {
                        drawBoardSquareLeft(g, x, y, game.getBoard2ValueAt(x,y), true);
                    } else {
                        drawBoardSquareLeft(g, x, y, game.getBoard2ValueAt(x,y), false);
                    }
                    if (game.getCurrentPlayer() == player1 && player1.getClass().equals(HumanPlayer.class)) {
                        drawBoardSquareRight(g, x, y, game.getBoard1ValueAt(x,y), true);
                    } else {
                        drawBoardSquareRight(g, x, y, game.getBoard1ValueAt(x,y), false);
                    }
                }*/
                if (game.getPlayer1() == player1) {
                    if (player1.getClass().equals(HumanPlayer.class)) {
                        drawBoardSquareLeft(g, x, y, game.getBoard1ValueAt(x,y), false);
                        drawBoardSquareRight(g, x, y, game.getBoard2ValueAt(x,y), true);
                    } else {
                        drawBoardSquareLeft(g, x, y, game.getBoard1ValueAt(x,y), false);
                        drawBoardSquareRight(g, x, y, game.getBoard2ValueAt(x,y), false);
                    }
                } else {
                    if (player1.getClass().equals(HumanPlayer.class)) {
                        drawBoardSquareLeft(g, x, y, game.getBoard2ValueAt(x,y), false);
                        drawBoardSquareRight(g, x, y, game.getBoard1ValueAt(x,y), true);
                    } else {
                        drawBoardSquareLeft(g, x, y, game.getBoard2ValueAt(x,y), false);
                        drawBoardSquareRight(g, x, y, game.getBoard1ValueAt(x,y), false);
                    }
                }
            }
        }
        
    }
    
    private void drawBoardSquareLeft(Graphics2D g, int x, int y, int value, boolean censored) {

        if (value < BattleShipGame.EMPTY && !censored) {
            g.setColor(Color.gray);
        } else if (value == BattleShipGame.HIT) {
            g.setColor(Color.red);
        } else if (value == BattleShipGame.MISS) {
            g.setColor(Color.white);
        } else {
            g.setColor(Color.lightGray);
        }
        g.fillRect(X_OFFSET + x*SQUARE_SIZE, Y_OFFSET + y*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                
    }
    
    private void drawBoardSquareRight(Graphics2D g, int x, int y, int value, boolean censored) {

        if (value < BattleShipGame.EMPTY && !censored) {
            g.setColor(Color.gray);
        } else if (value == BattleShipGame.HIT) {
            g.setColor(Color.red);
        } else if (value == BattleShipGame.MISS) {
            g.setColor(Color.white);
        } else {
            g.setColor(Color.lightGray);
        }
        g.fillRect(X_OFFSET + x*SQUARE_SIZE + BOARD_SPACE + SQUARE_SIZE*BattleShipGame.BOARD_SIZE, Y_OFFSET + y*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                
    }

    @Override
    public String getName() {
        return "Game";
    }

    @Override
    public String checkForStateChange() {
        return null;
    }

    @Override
    public void init(Object obj) {
        state = STATE_MENU;
        errorString = "";
    }

    @Override
    public void exit() {
        
    }
    
}
