package lt.banelis.aurelijus.dinosy.prototype.helpers;

import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import lt.banelis.aurelijus.dinosy.prototype.DataRepresentation;
import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableComponent;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableImage;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableLabel;
import lt.banelis.aurelijus.dinosy.prototype.operations.Progress;
import lt.banelis.aurelijus.dinosy.prototype.relations.Arrow;
import lt.banelis.aurelijus.dinosy.prototype.relations.Connection;
import lt.dinosy.datalib.Controller;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.NotUniqueIdsException;
import lt.dinosy.datalib.Relation;
import lt.dinosy.datalib.Representation;
import org.xml.sax.SAXException;

/**
 * Common functions for saving or loading operations.
 *
 * @author Aurelijus Banelis
 */
public class StorageHelper {

    private String savedTo;
    private Progress progress = emptyProgress;
    private Controller storage = new Controller();
    private Color defaultForeground = ColorsHelper.getDefaultForeground();

    public void open(ZoomPanel panel) {
        JFileChooser jfc = new JFileChooser();
        updateFileTypes(jfc);
        if (jfc.showOpenDialog(panel.getParent()) == JFileChooser.APPROVE_OPTION) {
            loadData(jfc.getSelectedFile().getPath(), panel);
            setSavedTo(jfc.getSelectedFile().getPath());
        }
    }

    public void loadData(final String file, ZoomPanel panel) {
        loadData(file, progress, panel);
    }

    public void loadData(final String file, final Progress progress, final ZoomPanel panel) {
        Thread openning = new Thread() {
            @Override
            public void run() {
                panel.setLoading(true);
                progress.update(0, "Loading XML file");
                try {
                    if (storage.openFile(file)) {
                        progress.update(0.2, "Creating elements");
                        List<Representation> representations = new LinkedList<Representation>();
                        HashMap<Component, Integer> zOrders = new HashMap<Component, Integer>(storage.getData().size());
                        /* Representations */
                        int n = storage.getData().values().size();
                        int i = 0;
                        for (Data data : storage.getData().values()) {
                            for (Representation representation : data.getRepresentations()) {
                                ZoomableComponent component;
                                if (data instanceof Data.Image) {
                                    ZoomableImage image = new ZoomableImage((Data.Image) data);
                                    component = panel.addComponent(image);
                                } else {
                                    component = panel.addComponent(new ZoomableLabel(data));
                                }
                                RepresentationsHelper.updateRepresentation(component, representation, panel);
                                if (representation instanceof Representation.Element) {
                                    zOrders.put(component.getComponent(), ((Representation.Element) representation).getZIndex());
                                }
                                representations.add(representation);
                            }
                            progress.update(0.2 + (i / (double) n * 0.6), "Creating elements");
                            i++;
                        }
                        progress.update(0.8, "Drawing relations");

                        /* Relations */
                        //TODO: optimize relalions
                        if (storage.hasRelations()) {
                            for (Representation representation : representations) {
                                for (Relation relation : representation.getData().getRelations()) {
                                    if (relation.getFrom() == representation.getData()) {
                                        Component from = (Component) representation.getAssigned();
                                        Component to = getComponent(relation.getTo(), representations);
                                        assert to != null;
                                        if (relation instanceof Relation.Association) {
                                            String name = ((Relation.Association) relation).getName();
                                            panel.addConnnection(new Connection(from, to, new Arrow.Association(name)));
                                        } else if (relation instanceof Relation.Generalization) {
                                            panel.addConnnection(new Connection(from, to, new Arrow.Generalization()));
                                        } else {
                                            panel.addConnnection(new Connection(from, to));
                                        }
                                    }
                                }
                            }
                        }
                        progress.update(0.9, "Setting Z-Order");

                        /* Z Order */
                        //FIXME: optimized z-Order
//                        int n = panel.getComponentCount();
//                        for (Component component : zOrders.keySet()) {
//                            if (zOrders.get(component) < n) {
//                                panel.setComponentZOrder(component, zOrders.get(component));
//                            }
//                        }

                        setSavedTo(file);
                        panel.repaint();
                    }

                    //TODO: normal exception handling
                } catch (URISyntaxException ex) {
                    Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, "URISyntaxException loading data", ex);
                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, "XML ParserConfigurationException loading data", ex);
                } catch (SAXException ex) {
                    Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, "SAXException loading data", ex);
                } catch (IOException ex) {
                    Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, "IOException loading data", ex);
                } catch (Controller.BadVersionException ex) {
                    Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, "Not compatible file version", ex);
                } finally {
                    panel.setLoading(false);
                    progress.update(1, "Loaded");
                }
            }
        };
        openning.setPriority(Thread.MAX_PRIORITY - 1);
        openning.start();
    }

    public void save(List<Component> components, String file, ZoomPanel panel) {
        save(components, file, progress, panel);
    }

    public void save(List<Component> components, String file, Progress progress, ZoomPanel panel) {
        try {
            panel.setLoading(true);
            progress.update(0, "Saving elements");
            List<Data> data = new LinkedList<Data>();
            Set<Representation> representations = new HashSet<Representation>();
            int n = components.size();
            int i = 0;
            for (Component component : components) {
                if (component instanceof DataRepresentation) {
                    DataRepresentation object = (DataRepresentation) component;
                    ZoomableComponent zoomableComponent = panel.getZoomableComponent(component);
                    object.updateData(zoomableComponent);
                    data.add(object.getData());
                    for (Representation representation : object.getData().getRepresentations()) {
                        if (representation instanceof Representation.Element) {
                            Representation.Element element = (Representation.Element) representation;
                            element.setZIndex(panel.getComponentZOrder(component));
                            element.setForeground(component.getForeground(), defaultForeground);
                            if (component instanceof JComponent) {
                                JComponent jComponent = (JComponent) component;
                                element.setBackground(jComponent.getBackground(), jComponent.isOpaque());
                            }
                        }
                        representations.add(representation);
                    }
                }
                progress.update(i / n * 0.8, "Converting to XML");
                i++;
            }
            progress.update(0.8, "Saving to file system");
            storage.save(data, representations, file);
            setSavedTo(file);
        } catch (NotUniqueIdsException ex) {
            Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StorageHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            panel.setLoading(false);
            progress.update(1, "Saved");
        }
    }

    public void setSavedTo(String savedTo) {
        this.savedTo = savedTo;
    }

    public String getSavedTo() {
        return savedTo;
    }

    public void save(ZoomPanel panel) {
        if (savedTo != null) {
            save(Arrays.asList(panel.getComponents()), savedTo, panel);
        } else {
            saveAs(panel);
        }
    }

    public void saveAs(ZoomPanel panel) {
        JFileChooser jfc = new JFileChooser();
        updateFileTypes(jfc);
        jfc.setSelectedFile(new File("projektas.zip"));
        if (jfc.showSaveDialog(panel.getParent()) == JFileChooser.APPROVE_OPTION) {
            List<Component> components = Arrays.asList(panel.getComponents());
            save(components, jfc.getSelectedFile().getPath(), panel);
            savedTo = jfc.getSelectedFile().getPath();
        }
    }

    private void updateFileTypes(JFileChooser jfc) {
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter zipFilter = new FileNameExtensionFilter("Archive with images", "zip");
        jfc.addChoosableFileFilter(new FileNameExtensionFilter("Local DiNoSy xml", "xml"));
        jfc.addChoosableFileFilter(zipFilter);
        jfc.setFileFilter(zipFilter);
    }

    private Component getComponent(Data data, List<Representation> representations) {
        for (Representation representation : representations) {
            if (representation.getData() == data) {
                return (Component) representation.getAssigned();
            }
        }
        return null;
    }
    /*
     * Progress
     */
    private static Progress emptyProgress = new Progress() {
        public void update(double percent, String operaion) {
        }
    };

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public static String getTimeForFile() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH;mm;ss").format(new Date());
    }

    public void exportToHtml(List<ZoomableComponent> selected, String file) throws IOException {
        /* Converting elements to HTML */
        StringBuilder html = new StringBuilder();
        for (ZoomableComponent zoomableComponent : selected) {
            if (zoomableComponent.getSize().width > 1) {
                if (zoomableComponent.getComponent() instanceof ZoomableLabel) {
                    ZoomableLabel label = (ZoomableLabel) zoomableComponent.getComponent();
                    if (label.getFontSize() > 0) {
                        int fontSize = label.getFontSize();
                        html.append("<span class=\"zoomable\" style=\"").append(getHtmlStyle(zoomableComponent));
                        html.append(" font-size: ").append(fontSize);
                        html.append("px;\">").append(stripHtml(label.getText()));
//                        if (label.getData().getSource() instanceof Source.Internet) {
//                            Source.Internet source = (Source.Internet) label.getData().getSource();
//                            html.append("<a href=\"").append(urlEncode(source.getSource()));
//                            html.append("\">[^]</a>");
//                        }
                        html.append("</span>\n");
                    }
                } else if (zoomableComponent.getComponent() instanceof ZoomableImage) {
                    ZoomableImage image = (ZoomableImage) zoomableComponent.getComponent();
                    File cachedImage = new File(image.getCached());
                    File directory = new File(file).getParentFile();
                    File copyTo = new File(directory.getPath() + File.separator + "img" + File.separator + cachedImage.getName());
                    if (cachedImage.exists()) {
                        copyTo.getParentFile().mkdirs();
                        copyFile(cachedImage, copyTo);
                        html.append("<img class=\"zoomable\" style=\"").append(getHtmlStyle(zoomableComponent));
                        html.append("\" src=\"img/").append(urlEncode(cachedImage.getName())).append("\" alt=\"");
                        html.append(image.getData()).append("\"/>\n");
                    }
                }
            }
        }

        /* Saving output to file */
        BufferedReader reader = new BufferedReader(new InputStreamReader(VisualizationHelper.class.getResourceAsStream("zoomoozTemplate.html")));
        BufferedWriter writter = new BufferedWriter(new FileWriter(file));
        String line;
        String nl = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            if (line.equalsIgnoreCase("[Code]")) {
                writter.write(html.toString() + nl);
            } else {
                writter.write(line + nl);
            }
        }
        reader.close();
        writter.close();
    }

    private static String getHtmlStyle(ZoomableComponent component) {
        String width = "px; width: " + (int) component.getSize().width;
        if (component.getComponent() instanceof ZoomableLabel) {
            width = "";
        }
        return "position: absolute; left: " + (int) component.getLocation().getX()
               + "px; top: " + (int) component.getLocation().getY()
               + width
               + "px; height: " + (int) component.getSize().height
               + "px;";
    }

    private static String stripHtml(String text) {
        return text.replaceAll("<(script|style)[^>]*?>(?:.|\\n)*?</\\s*\\1\\s*>", text);
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    private static String urlEncode(String text) {
        try {
            text = java.net.URLEncoder.encode(text, "UTF-8");
            return text.replace("+", "%2B");
        } catch (UnsupportedEncodingException ex) {
            return text;
        }
    }
}
