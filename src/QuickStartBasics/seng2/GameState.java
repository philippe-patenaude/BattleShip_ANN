package QuickStartBasics.seng2;

import java.awt.Graphics2D;

/**
 *
 * @author Philippe
 */
public interface GameState <T extends Object> extends BasicState <T> {
    public void update();
    public void draw(Graphics2D g);
}
