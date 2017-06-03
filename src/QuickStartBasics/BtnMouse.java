package QuickStartBasics;

/**
 *
 * @author Philippe
 */
public class BtnMouse extends Button {

    private int mouseButton;

    public BtnMouse(int mouseButton) {
        this.mouseButton = mouseButton;
    }

    public boolean isDown() {
        switch (mouseButton) {
            case (IM.LEFT_MOUSE_BUTTON):
                return IM.leftMouseButtonDown;
            case (IM.MIDDLE_MOUSE_BUTTON):
                return IM.middleMouseButtonDown;
            case (IM.RIGHT_MOUSE_BUTTON):
                return IM.rightMouseButtonDown;
            default:
                return false; // any other number is not handled
        }
    }

}
