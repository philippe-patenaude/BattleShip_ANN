package QuickStartBasics.seng2;

/**
 *
 * @author Philippe
 */
public interface BasicState <T extends Object> {
    public String getName();
    public String checkForStateChange(); // returns the name of the new state or null for no state change
    public void init(T obj);
    public void exit();
}
