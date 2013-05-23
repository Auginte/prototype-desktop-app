package lt.banelis.aurelijus.dinosy.prototype.operations;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableComponent;

/**
 * Keyboard shortcuts handler.
 *
 * @author Aurelijus Banelis
 */
public class Key {

    public int modifier;
    public int code;
    public boolean actOnRelease = false;
    private boolean nowPressed = false;

    public Key(int modifier, int keyCode) {
        this.modifier = modifier;
        this.code = keyCode;
    }

    public Key(KeyModifier modifier, int keyCode) {
        this(modifier.getModifier(), keyCode);
    }

    public Key(KeyModifier modifier, int keyCode, boolean actOnRelease) {
        this(modifier.getModifier(), keyCode, actOnRelease);
    }

    public Key(int modifier, int keyCode, boolean actOnRelease) {
        this.modifier = modifier;
        this.code = keyCode;
        this.actOnRelease = actOnRelease;
    }

    public boolean isKeyOwner(KeyEvent event) {
        boolean keyCode = (code == event.getKeyCode());
        int hightModifiers = 32752;
        boolean keyModifier = (event.getModifiersEx() & hightModifiers) == (modifier & hightModifiers);
        boolean stateDown = (event.getID() == KeyEvent.KEY_PRESSED);
        boolean stateUp = (event.getID() == KeyEvent.KEY_RELEASED);
        boolean state = (stateDown && !actOnRelease) || (stateUp && actOnRelease);
        if (keyCode && keyModifier && !actOnRelease) {
            nowPressed = !stateUp;
        }
        if (keyCode && keyModifier && state) {
            return true;
        }
        return false;
    }

    /**
     * @return <code>true</code> if key is not released
     */
    public boolean isNowPressed() {
        return nowPressed;
    }

    public void release() {
        nowPressed = false;
    }

    @Override
    public String toString() {
        return KeyEvent.getKeyModifiersText(modifier) + " + " + KeyEvent.getKeyText(code);
    }

    public static KeyListener getDefaultKeyShortCuts(final List<Operation> operations, final ZoomPanel panel) {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                for (Operation operation : operations) {
                    checkAllKeys(operation, e);
                }
                for (ZoomableComponent zoomableComponent : panel.getSelected()) {
                    checkKeyForSelected(zoomableComponent.getComponent(), panel, e);
                }
            }

            @Override
            public synchronized void keyReleased(KeyEvent e) {
                for (Operation operation : operations) {
                    checkAllKeys(operation, e);
                }
                for (ZoomableComponent zoomableComponent : panel.getSelected()) {
                    checkKeyForSelected(zoomableComponent.getComponent(), panel, e);
                }
            }
        };
    }

    private static void checkKeyForSelected(Component component, ZoomPanel panel, KeyEvent e) {
        if (component instanceof HavingOperations) {
            HavingOperations specialized = (HavingOperations) component;
            for (Operation operation : specialized.getOperations(panel)) {
                checkAllKeys(operation, e);
            }
        }
    }

    private static void checkAllKeys(Operation operation, KeyEvent e) {
        boolean onKeyPressed = (e.getID() == KeyEvent.KEY_PRESSED);
        for (Key key : operation.getKeys()) {
            if (key.isKeyOwner(e) || (onKeyPressed && key.isNowPressed())) {
                operation.perform();
                if (onKeyPressed && key.isNowPressed()) {
                    key.release();
                }
                break;
            }
        }
    }
}
