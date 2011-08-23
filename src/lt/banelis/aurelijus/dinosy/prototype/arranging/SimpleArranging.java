package lt.banelis.aurelijus.dinosy.prototype.arranging;

import java.util.List;
import lt.banelis.aurelijus.dinosy.prototype.ClassRepresentation;
import lt.banelis.aurelijus.dinosy.prototype.PhpUml;
import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableComponent;

/**
 * Arranges all Objects to square.
 *
 * @author Aurelijus Banelis
 */
public class SimpleArranging {
    private int space = 40;
    private int wholeHeight = 0;

    private PhpUml phpUml;

    public SimpleArranging(PhpUml phpUml) {
        this.phpUml = phpUml;
    }

    public void arrange() {
        arrange(phpUml.getModels());
    }

    public int arrange(List<ClassRepresentation> models) {
        int y = 0;
        if (!models.isEmpty()) {
            int size = (int) Math.round(Math.sqrt(models.size()));
            int[] width = new int[size];
            //TODO: heigh array size, after round
            int[] height = new int[models.size() / size + models.size() % size];

            /* Getting widths */
            int i = 0;
            for (ClassRepresentation model : models) {
                int w = i % size;
                int h = i / size;
                int newWidth = model.getSize().width + space;
                int newHeight = model.getSize().height + space;
                if (width[w] < newWidth) {
                    width[w] = newWidth;
                }
                if (height[h] < newHeight) {
                    height[h] = newHeight;
                }
                i++;
            }

            /* Rearranging */
            int left = 0;
            int top = 0;
            i = 0;
            for (ClassRepresentation model : models) {
                ZoomableComponent ClassRepresentation = phpUml.getPanel().getZoomableComponent(model);
                ClassRepresentation.setLocation(left, top);
                int w = i % size;
                int h = i / size;
                assert ClassRepresentation.getSize().getWidth() < width[w];
                assert ClassRepresentation.getSize().getHeight() < height[h];
                left += width[w];
                if (w == width.length - 1) {
                    top += height[h];
                    left = 0;
                }
                i++;
            }

            /* Whole height */
            wholeHeight = 0;
            for (int j : height) {
                wholeHeight += j;
            }

            return wholeHeight + space;
        } else {
            return 0;
        }
    }

    public int getHeight() {
        return wholeHeight;
    }

    protected List<ClassRepresentation> getModels() {
        return phpUml.getModels();
    }

    protected ZoomPanel getPanel() {
        return phpUml.getPanel();
    }

    protected PhpUml getPhpUml() {
        return phpUml;
    }

}
