package lt.banelis.aurelijus.dinosy.prototype.helpers;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import lt.banelis.aurelijus.dinosy.prototype.ImageLoader;
import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableComponent;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableImage;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableLabel;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.Okular;
import lt.dinosy.datalib.Settings;
import lt.dinosy.datalib.Source;

/**
 * Common functions to add new elements.
 *
 * @author Aurelijus Banelis
 */
public class AddingHelper {

    private static String externalSketching = Settings.getInstance().getSketchingProgram();
    private StorageHelper storageHelper;
    private SourceHelper sourceHelper;
    private RunHelper runHelper;

    public AddingHelper(StorageHelper storageHelper, SourceHelper sourceHelper, RunHelper runHelper) {
        this.storageHelper = storageHelper;
        this.sourceHelper = sourceHelper;
        this.runHelper = runHelper;
    }

    public ZoomableLabel addText(String text, ZoomPanel panel, int x, int y, int widh, int height, boolean edit) {
        return addText(new Data.Plain(text, getSource()), panel, x, y, widh, height, edit);
    }

    public ZoomableLabel addText(Data.Plain data, ZoomPanel panel, int x, int y, int widh, int height, boolean edit) {
        ZoomableLabel label = new ZoomableLabel(data);
        if (edit) {
            label.switchEditable();
        }
        ZoomableComponent component = panel.addComponent(label);
        component.setLocation(x, y);
        component.setSize(new Dimension(widh, height));
        label.requestFocusInWindow();
        return label;
    }

    //TODO: use robot.
    public void addScreenShot(final ZoomPanel panel, final long delay, final File outputDirectory) {
        final String executable = Settings.getInstance().getScreenCaptureProgram();
        if (executable != null) {
            Thread captureProgram = new Thread() {
                @Override
                public void run() {
                    if (delay > 100) {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, "ScreenShot delay interupted", ex);
                        }
                    }
                    if (outputDirectory.isDirectory()) {
                        outputDirectory.mkdirs();
                    }
                    String fileName = outputDirectory.getPath() + "/" + getTimeForFile() + ".jpg";
                    Okular.run(new String[]{executable, fileName});
                    addImage(fileName, panel, panel.getWidth() / 2, panel.getHeight() / 2);
                }
            };
            captureProgram.start();
        }
    }

    public ZoomableComponent addImage(String path, ZoomPanel panel, Integer x, Integer y) {
        ZoomableImage image = new ZoomableImage(path, getSource());
        ZoomableComponent component = panel.addComponent(image);
        if (x == null || y == null) {
            setToCenter(component, panel);
        } else {
            component.setLocation(x, y);
        }
        image.setSize(ZoomableComponent.deafaultBounding.width, ZoomableComponent.deafaultBounding.height);
        image.loadImage();
        ((JComponent) component.getComponent()).setToolTipText(path);
        return component;
    }

    public void addImage(Source source, String file, BufferedImage image, ZoomPanel panel) {
        ZoomableImage component = new ZoomableImage(new Data.Image(file, source));
        ImageLoader.getInstance().addImage(component, file);
        component.save(image);
        component.setSize(image.getWidth(null), image.getHeight(null));
        ZoomableComponent zoomableComponent = panel.addComponent(component);
        setToCenter(zoomableComponent, panel);
    }

    private void setToCenter(ZoomableComponent component, ZoomPanel panel) {
        int x = (panel.getWidth() / 2) - (component.getSize().width / 2);
        int y = (panel.getHeight() / 2) - (component.getSize().height / 2);
        component.setLocation(x, y);
    }

    private Source getSource() {
        return sourceHelper.getDefaultSource();
    }

    public void addSketch(final ZoomPanel panel) {
        if (externalSketching != null) {
            String directory = Settings.getInstance().getDateCacheDirecotry().getPath();
            final String sketchFile = directory + "/sketch-" + getTimeForFile() + ".png";
            final Runnable externalEditing = RunHelper.runExternal(new String[]{externalSketching, sketchFile}, false);
            Thread adding = new Thread() {
                @Override
                public void run() {
                    externalEditing.run();
                    ZoomableComponent component = addImage(sketchFile, panel, null, null);
                }
            };
            adding.start();
        } else {
            JOptionPane.showMessageDialog(null, "No sketching program specifed.");
        }
    }

    public void openSketchingProgram(final ZoomPanel panel) {
        final String sketchFile = Settings.getInstance().getDateCacheDirecotry() + "/sketch-" + StorageHelper.getTimeForFile() + ".png";
        final Runnable externalEditing = RunHelper.runExternal(new String[]{externalSketching, sketchFile}, false);
        Thread adding = new Thread() {
            @Override
            public void run() {
                externalEditing.run();
                ZoomableComponent component = addImage(sketchFile, panel, null, null);
            }
        };
        adding.start();
    }

    private String getTimeForFile() {
        return StorageHelper.getTimeForFile();
    }
}
