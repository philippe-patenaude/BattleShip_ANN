package QuickStartBasics;

/**
 *
 * @author Philippe
 */
public class BtnKey extends Button {

    private int keyNumber;

    public BtnKey(int keyNumber) {
        this.keyNumber = keyNumber;
    }

    public boolean isDown() {
        return IM.keys[keyNumber];
    }

}
