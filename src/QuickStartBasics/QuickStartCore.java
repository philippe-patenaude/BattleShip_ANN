package QuickStartBasics;

import QuickStartBasics.seng2.GameState;
import QuickStartBasics.seng2.GameStateManager;
import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

import java.awt.event.*;

/**
 *
 * @author Philippe
 */
public class QuickStartCore extends JPanel implements KeyListener, MouseListener, MouseMotionListener {

    private static GraphicsDevice gDevice;

    public static QuickStartCore device;

    public static void createWindow(String name, int locationX, int locationY, int windowWidth, int windowHeight, QuickStartCore content, boolean fullScreen) {

        device = content;

        JFrame window = new JFrame(name);
        window.setContentPane(content);
        content.setPreferredSize( new Dimension(windowWidth, windowHeight) );
        window.setLocation(locationX, locationY);
        window.setResizable(false);
        window.pack();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        
        window.setFocusTraversalKeysEnabled(false); // these two here are so I can use tab and shift tab
        content.setFocusTraversalKeysEnabled(false);

        if (fullScreen == true) {
            GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            gDevice = environment.getDefaultScreenDevice();
            gDevice.setFullScreenWindow(window);
            gDevice.setDisplayMode(new DisplayMode(windowWidth, windowHeight, 32, 0));
        }

    }

    // class starts here

    private GameStateManager<GameState, QuickStartCore> gsm;
    private BufferedImage backBuffer;
    private long ctr;
    private int fps;
    private int screenWidth, screenHeight;

    private int cFPS; // current frames per second

    public QuickStartCore(int screenWidth, int screenHeight, int fps) {

        this.fps = fps;

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // normal
        backBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);

        gsm = new GameStateManager<GameState, QuickStartCore>(this);
        ctr = 0;

        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.requestFocus();

    }

    public void addState(GameState state) {
        gsm.addState(state);
    }

    public void setState(String name) {
        gsm.setState(name);
    }

    public int getFramesPerSecond() {
        return cFPS;
    }

    private class Increment implements Runnable {
        public void run() {
            ctr++;
        }
    }
    private Increment increment;

    private class Decrement implements Runnable {
        public void run() {
            ctr--;
        }
    }
    private Decrement decrement;

    private class TimerThread extends Thread {

        private long startTime;
        private long nspf; // nanoseconds per frame
        private Runnable counter;

        private long frames;
        private int i;

        private boolean exit;

        public TimerThread(int fps, Runnable counterObj) {
            nspf = (1000000000/fps);
            startTime = System.nanoTime();
            counter = counterObj;
            exit = false;
        }

        public void exit() {
            exit = true;
        }

        @Override
        public void run() {
            while (exit == false) {
                frames = (System.nanoTime() - startTime)/nspf;
                for (i = 0; i < frames; i++) {
                    counter.run();
                }
                startTime += frames*nspf;
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//
//                }
            }
        }

    }

    public void run() {

        boolean doExit = false;

        long fpsStartTime;
        int fpsCounter = 0;

        increment = new Increment();
        decrement = new Decrement();
        TimerThread timer = new TimerThread(fps, increment);
        timer.start();

        fpsStartTime = System.nanoTime();

        long loopStart;
        while (! doExit) {

            loopStart = System.nanoTime();
            while (ctr > 0) {

                // update input
                IM.update();

                gsm.update();
                doExit = gsm.didExit();
                decrement.run();

                // increment total time
                GlobalTimer.incrementTotalTime();
                // if greater than one fifteenth of a second has been spent updating then draw once
                if (System.nanoTime()-loopStart >= (1000000000/15)) {
                    if (ctr > 5) {
                        ctr = 0; // reset the counter as well so the game doesn't speed up
                    }
                    break;
                }
            }

            // draw to back buffer
            gsm.draw(backBuffer.createGraphics());
            drawUI(backBuffer.createGraphics());
            // draw back buffer to screen
            getGraphics().drawImage(backBuffer, 0, 0, null);

            fpsCounter++;
            if (System.nanoTime() - fpsStartTime >= 1000000000) { // if one second has passed
                cFPS = fpsCounter;
                fpsCounter = 0;
                fpsStartTime = System.nanoTime();
            } else {
                cFPS = (int)(((double)fpsCounter)/((double)(System.nanoTime()-fpsStartTime)/1000000000.0));
            }

        }

        timer.exit();

        System.exit(0);

    }

    private void drawUI(Graphics2D g) {
        this.paintComponents(g);
    }

    //<editor-fold defaultstate="collapsed" desc="Input Events">
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code < IM.NUM_OF_KEYS) {
            IM.keys[code] = true;
        }
    }
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code < IM.NUM_OF_KEYS) {
            IM.keys[code] = false;
        }
    }
    public void keyTyped(KeyEvent e) {}

    public void mousePressed(MouseEvent e) {
        device.requestFocus();
        int btn = e.getButton();
        if (btn == MouseEvent.BUTTON1) {
            IM.leftMouseButtonDown = true;
        } else if (btn == MouseEvent.BUTTON2) {
            IM.middleMouseButtonDown = true;
        } else if (btn == MouseEvent.BUTTON3) {
            IM.rightMouseButtonDown = true;
        }
    }
    public void mouseReleased(MouseEvent e) {
        int btn = e.getButton();
        if (btn == MouseEvent.BUTTON1) {
            IM.leftMouseButtonDown = false;
        } else if (btn == MouseEvent.BUTTON2) {
            IM.middleMouseButtonDown = false;
        } else if (btn == MouseEvent.BUTTON3) {
            IM.rightMouseButtonDown = false;
        }
    }
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {
        IM.mouseX = e.getX();
        IM.mouseY = e.getY();
    }
    public void mouseDragged(MouseEvent e) {
        IM.mouseX = e.getX();
        IM.mouseY = e.getY();
    }
    //</editor-fold>

}
