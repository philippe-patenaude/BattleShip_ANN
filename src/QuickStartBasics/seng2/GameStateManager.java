package QuickStartBasics.seng2;

import java.awt.Graphics2D;

/**
 *
 * @author Philippe
 */
public class GameStateManager <T extends GameState, S extends Object> extends StateManager <T,S> {

    public GameStateManager(S devices) {
        super(devices);
    }

    public void update() {

        if (getCurrentState() == null) return;

        getCurrentState().update();

        changeState();

    }

    public void draw(Graphics2D g) {

        if (getCurrentState() == null) return;

        getCurrentState().draw(g);

    }

}
