package lt.banelis.aurelijus.dinosy.prototype.relations;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;

/**
 * Managing relations according to position in space.
 *
 * @author Aurelijus Banelis
 */
public class VisualGrouping {

    private ZoomPanel panel;
    private double threshold = 2;
    private Map<Component, Integer> subGroups = new HashMap<Component, Integer>();
    private int nGroups = 0;

    public VisualGrouping(ZoomPanel panel) {
        this.panel = panel;
    }

    public void paintComponent(Graphics g) {
        updateConnections();
//        paintConnections(g);
        paintGroups(g);
    }

    private void paintConnections(Graphics g) {
        for (int index = 0; index < nGroups; index++) {
            List<Component> group = getGroup(index);
            for (int i = 0; i < group.size(); i++) {
                Component me = group.get(i);
                int x1 = me.getX() + me.getWidth() / 2;
                int y1 = me.getY() + me.getHeight() / 2;
                for (int j = i + 1; j < group.size(); j++) {
                    Component peer = group.get(j);
                    int x2 = peer.getX() + peer.getWidth() / 2;
                    int y2 = peer.getY() + peer.getHeight() / 2;
                    g.setColor(Color.WHITE);
                    g.drawLine(x1, y1, x2, y2);
                }
            }
        }
    }

    private void updateConnections() {
        subGroups = new HashMap<Component, Integer>();
        nGroups = 0;
        Component[] array = panel.getComponents();
        for (int i = 0; i < array.length; i++) {
            Component me = array[i];
            int x1 = me.getX() + me.getWidth() / 2;
            int y1 = me.getY() + me.getHeight() / 2;
            double sizeMe = Math.sqrt(me.getSize().getWidth()
                                      * me.getSize().getHeight());
            for (int j = i + 1; j < array.length && isViewable(me); j++) {
                Component peer = array[j];
                if (isViewable(peer)) {
                    int x2 = peer.getX() + peer.getWidth() / 2;
                    int y2 = peer.getY() + peer.getHeight() / 2;
                    double x = (x2 - x1);
                    double y = (y2 - y1);
                    double distance = Math.sqrt(x * x + y * y);
                    double sizePeer = Math.sqrt(peer.getSize().getWidth()
                                                * peer.getSize().getHeight());
                    double scaleDifference = Math.max(sizeMe, sizePeer)
                                             / Math.min(sizeMe, sizePeer);
                    double scaleAverage = (sizeMe + sizePeer) / 2;

                    double clouseness = distance / scaleAverage;
                    double proximity = Math.abs(clouseness * scaleDifference);
                    if (proximity < threshold) {
                        saveToGroup(me, peer);
                    }
                }
            }
        }
    }

    private boolean isViewable(Component component) {
        int x1 = component.getX();
        int y1 = component.getY();
        int x2 = x1 + component.getWidth();
        int y2 = y1 + component.getHeight();
        int width = panel.getWidth();
        int height = panel.getHeight();
        int min = 3;

        return x2 > 0 && y2 > 0 && x1 < width && y2 < height
               && component.getWidth() > min && component.getHeight() > min;
    }

    private void saveToGroup(Component c1, Component c2) {
        if (subGroups.containsKey(c1)) {
            subGroups.put(c2, subGroups.get(c1));
        } else if (subGroups.containsKey(c2)) {
            subGroups.put(c1, subGroups.get(c2));
        } else {
            Integer group = nGroups++;
            subGroups.put(c1, group);
            subGroups.put(c2, group);
        }
    }

    private void paintGroups(Graphics g) {
        g.setColor(Color.gray);
        for (int i = 0; i < nGroups; i++) {
            final int border = 5;
            Boundary boundary = getBoundary(i);
            g.drawRect(boundary.getX() - border,
                       boundary.getY() - border,
                       boundary.getWidth() + border,
                       boundary.getHeight() + border);
            g.drawString("" + i, boundary.getX() + boundary.getWidth() + border, boundary.getY());
        }
    }

    private Boundary getBoundary(int index) {
        List<Component> group = getGroup(index);
        if (group.size() > 0) {
            Component first = group.get(0);
            Boundary boundary = new Boundary(first.getX(), first.getY(), first.getWidth(), first.getHeight());
            for (Component component : group) {
                boundary.putIntoBoundary(component.getX(),
                                         component.getY(),
                                         component.getWidth(),
                                         component.getHeight());
            }
            return boundary;
        } else {
            return new Boundary(0, 0, 0, 0);
        }
    }

    //TODO: optimise with iterators
    private List<Component> getGroup(int index) {
        LinkedList<Component> components = new LinkedList<Component>();
        for (Component component : subGroups.keySet()) {
            if (subGroups.get(component) == index) {
                components.add(component);
            }
        }
        return components;
    }

    public List<Component> getGroup(Component member) {
        Integer index = subGroups.get(member);
        if (index != null) {
            return getGroup(index);
        } else {
            return Collections.EMPTY_LIST;
        }
    }
}
