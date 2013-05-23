package lt.banelis.aurelijus.dinosy.prototype.operations;

import java.awt.event.KeyEvent;

/**
 * Keyboard modifier.
 *
 * @author Aurelijus Banelis
 */
public enum KeyModifier {

    NONE(0),
    CTRL(KeyEvent.CTRL_DOWN_MASK),
    ALT(KeyEvent.ALT_DOWN_MASK),
    SHIFT(KeyEvent.SHIFT_DOWN_MASK),
    CTRL_ALT(KeyEvent.CTRL_DOWN_MASK + KeyEvent.ALT_DOWN_MASK),
    CTRL_SHIFT(KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK),
    CTRL_ALT_SHIFT(KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK + KeyEvent.ALT_DOWN_MASK);
    private int modifier;

    private KeyModifier(int modifier) {
        this.modifier = modifier;
    }

    public int getModifier() {
        return modifier;
    }
}
