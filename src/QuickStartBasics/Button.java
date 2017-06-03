package QuickStartBasics;

/**
 *
 * @author Philippe
 */
public abstract class Button {

    public boolean oldState;
    public Runnable action;

    public abstract boolean isDown();

}
