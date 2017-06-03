package QuickStartBasics;

/**
 *
 * @author Philippe
 */
public class InputItem {
    
    public static final int ON_PRESS = 0;
    public static final int ON_RELEASE = 1;
    public static final int ON_UP = 2;
    public static final int ON_DOWN = 3;

    public int state;
    public Button btn;
    public Runnable action;

    public InputItem(int state, Button btn, Runnable action) {
        this.state = state;
        this.btn = btn;
        this.action = action;
    }

}
