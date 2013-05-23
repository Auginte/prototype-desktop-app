package lt.banelis.aurelijus.dinosy.prototype.helpers;

import lt.dinosy.datalib.Source;

/**
 * Common functions to work with information sources.
 *
 * @author Aurelijus Banelis
 */
public class SourceHelper {

    private Source defaultSource = null;

    public void setDefaultSource(Source defaultSource) {
        this.defaultSource = defaultSource;
    }

    public Source getDefaultSource() {
        return defaultSource;
    }
}
