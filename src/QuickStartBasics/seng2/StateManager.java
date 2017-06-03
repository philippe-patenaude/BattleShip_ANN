package QuickStartBasics.seng2;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Philippe
 */
public class StateManager <T extends BasicState, S extends Object> {

    public static final String EXIT = "_Exit";
    
    private boolean didExit;
    private T currentState;
    private S devices;
    private HashMap<String, T> states;

    public StateManager(S devices) {
        this.devices = devices;
        states = new HashMap<String, T>();
    }

    //<editor-fold defaultstate="collapsed" desc="Getting and Adding States">
    public Iterator<T> getStates() {
        return states.values().iterator();
    }
    public T getCurrentState() {
        return currentState;
    }
    public void addState(T state) {
        states.put(state.getName(), state);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="State Change">
    public void setState(String nextState) {

        if (currentState != null) {
            currentState.exit();
        }
        
        if (nextState.equalsIgnoreCase(EXIT)) {
            didExit = true;
        } else {
            currentState = states.get(nextState);
            if (currentState != null) {
                currentState.init(devices);
            }
        }

    }
    public void changeState() { // calls setNextState() for state changes
        
        if (currentState == null) return;

        String nextState = currentState.checkForStateChange();
        if (nextState != null) {
            setState(nextState);
        }

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Manage Devices">
    public void setDevices(S devices) { this.devices = devices; }
    public S getDevices() { return devices; }
    //</editor-fold>

    public boolean didExit() { return didExit; }

}
