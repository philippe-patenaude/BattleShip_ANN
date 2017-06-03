package QuickStartBasics;

import java.util.ArrayList;
//import nw.objects.OM;

/**
 *
 * @author Philippe
 */
public final class IM {

    public static final int NUM_OF_KEYS = 525;

    public static final int LEFT_MOUSE_BUTTON = 0;
    public static final int MIDDLE_MOUSE_BUTTON = 1;
    public static final int RIGHT_MOUSE_BUTTON = 2;

    private IM() {}

    // map inputs to actions
    // how do I manage multiple commands on one key?
    //     example: arrow keys can move the player but can also move through options in the menu when the game is paused
    // (map<String, Action>?)

    // create an input handler class that will contain all the classes for the actions of inputs.
            // in input action include a name and description so it will be easier to customize the mapping of items to inputs
            // allow hotkeys for items(like buttons and interface items)

    // create lists for mouse(or joystick) moved events as well

    public static boolean[] keys = new boolean[NUM_OF_KEYS];

    public static boolean leftMouseButtonDown = false;
    public static boolean rightMouseButtonDown = false;
    public static boolean middleMouseButtonDown = false;

    public static int mouseX, mouseY;

    private static ArrayList<InputItem> lstTemp = new ArrayList<InputItem>();

    private static ArrayList<Button> lstOnPress = new ArrayList<Button>();
    private static ArrayList<Button> lstIsDown = new ArrayList<Button>();
    private static ArrayList<Button> lstOnRelease = new ArrayList<Button>();
    private static ArrayList<Button> lstIsUp = new ArrayList<Button>();


    private static boolean doClearMappings = false;

    public static void update() {

        for (Button btn : lstOnPress) {
            if (btn.isDown() == true && btn.oldState == false) {
                btn.action.run();
            }
            btn.oldState = btn.isDown();
        }

        for (Button btn : lstOnRelease) {
            if (btn.isDown() == false && btn.oldState == true) {
                btn.action.run();
            }
            btn.oldState = btn.isDown();
        }

        for (Button btn : lstIsUp) {
            if (btn.isDown() == false) {
                btn.action.run();
            }
        }

        for (Button btn : lstIsDown) {
            if (btn.isDown() == true) {
                btn.action.run();
            }
        }
        
        if (doClearMappings == true) {
            doClearMappings = false;
            clearMappingsNow();
        }

        for (InputItem itm : lstTemp) {
            itm.btn.action = itm.action;
            itm.btn.oldState = false;
            switch(itm.state) {
                case InputItem.ON_PRESS:
                    lstOnPress.add(itm.btn);
                    break;
                case InputItem.ON_RELEASE:
                    lstOnRelease.add(itm.btn);
                    break;
                case InputItem.ON_UP:
                    lstIsUp.add(itm.btn);
                    break;
                case InputItem.ON_DOWN:
                    lstIsDown.add(itm.btn);
                    break;
            }
        }
        lstTemp.clear();

    }

    public static void clearMappings() {
        doClearMappings = true;
    }

    public static void clearMappingsNow() {
        lstOnPress.clear();
        lstIsDown.clear();
        lstOnRelease.clear();
        lstIsUp.clear();
        lstTemp.clear();
    }

//    public static Runnable get(String name) {
//        Runnable action = OM.getProp(name);
//        if (action == null) {
//            System.out.println("Error in Input Manager: Action \"" + name + "\" doesn't exist");
//        }
//        return action;
//    }
//
//    public static void add(String name, Runnable action) {
//        OM.addProp(name, action);
//    }

    //<editor-fold defaultstate="collapsed" desc="Add Button to Action Mapping">
    public static void addOnPress(Button btn, Runnable action) {
        lstTemp.add(new InputItem(InputItem.ON_PRESS, btn, action));
    }

    public static void addIsDown(Button btn, Runnable action) {
        lstTemp.add(new InputItem(InputItem.ON_DOWN, btn, action));
    }

    public static void addOnRelease(Button btn, Runnable action) {
        lstTemp.add(new InputItem(InputItem.ON_RELEASE, btn, action));
    }

    public static void addIsUp(Button btn, Runnable action) {
        lstTemp.add(new InputItem(InputItem.ON_UP, btn, action));
    }

//    public static void addOnPress(Button btn, String action) {
//        addOnPress(btn, get(action));
//    }
//
//    public static void addIsDown(Button btn, String action) {
//        addIsDown(btn, get(action));
//    }
//
//    public static void addOnRelease(Button btn, String action) {
//        addOnRelease(btn, get(action));
//    }
//
//    public static void addIsUp(Button btn, String action) {
//        addIsUp(btn, get(action));
//    }
    //</editor-fold>

}
