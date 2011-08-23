package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import lt.banelis.aurelijus.dinosy.prototype.arranging.SimpleArranging;
import lt.banelis.aurelijus.dinosy.prototype.arranging.Generalization;
import lt.banelis.parser.CacheException;
import lt.banelis.parser.ParserException;
import lt.banelis.parser.ParsingControllable;
import lt.banelis.parser.ParsingStatus;
import lt.banelis.parser.php.PhpFile;
import lt.banelis.parser.php.PhpProject;
import lt.dinosy.datalib.Source;

/**
 * Class to encapsulate PHP and UML functionality
 *
 * @author Aurelijus Banelis
 */
public class PhpUml {
    private ZoomPanel panel;
    private PhpProject project;
    private static boolean continueOpening = true;
    private JPopupMenu contextMenu;
    private TimerTask afterProjectLoaded = null;
    private LinkedList<ClassRepresentation> models;

    public PhpUml(ZoomPanel panel) {
        this.panel = panel;
        attatchPanelChaneListener();
        simpleGrid = new SimpleArranging(this);
        generalizationGrid = new Generalization(this);
    }

    public ZoomPanel getPanel() {
        return panel;
    }


    public void loadPhpProject(final String folder, final JProgressBar progressBar, TimerTask afterProjectLoaded) {
        this.afterProjectLoaded = afterProjectLoaded;
        loadPhpProject(folder, progressBar);
    }

    public void loadPhpProject(final String folder, final JProgressBar progressBar) {
        loadPhpProject(folder, progressBar, new JLabel());
    }

    public void loadPhpProject(final String folder, final JProgressBar progressBar, final JLabel statusLabel) {
            continueOpening = true;
            Thread openProject = new Thread() {
                @Override
                public void run() {
                    project = PhpProject.get(folder, new ParsingControllable() {
                        public boolean condinue() {
                            return continueOpening;
                        }

                        public void statusChangeEvent(ParsingStatus status) {
                            progressBar.setString(status.toString());
                            statusLabel.setText(status.toString());
                        }

                        public void statusChangeEvent(String filePath, ParsingStatus status, int fileNumber, int filesCount) {
                            String text = status.toString();
                            if (status == ParsingStatus.parsingFile || status == ParsingStatus.loadingCachedFile) {
                                text = status.toString() + ": " + filePath;
                            }
                            progressBar.setString(text);
                            progressBar.setToolTipText(text);
                            progressBar.setMaximum(filesCount);
                            progressBar.setValue(fileNumber);
                            statusLabel.setText(text);
                        }
                    });
                    try {
                        project.parse();
                    } catch (CacheException ex) {
                        System.err.println("Error: " + ex);
                    } catch (ParserException ex) {
                        System.err.println("Error: " + ex);
                    }
                    renderProject(progressBar, statusLabel);
                }
            };
            openProject.start();
    }

    public static void cancel() {
        continueOpening = false;
    }

    private void renderProject(JProgressBar progressBar, JLabel statusLabel) {
        String text = "Rendering";
        progressBar.setString(text);
        statusLabel.setText(text);
        progressBar.setMaximum(project.getFiles().size());
        int x = 0;
        int i = 0;
        int divider = (int) Math.sqrt(project.getFiles().size());
        //TODO: relative path whiout slah at the beginning
        Source.Project projectSource = getProjectSource(project);
        for (PhpFile phpFile : project.getFiles()) {
            for (lt.banelis.parser.Class classObject : phpFile.getClasses()) {
                ClassRepresentation classRepresentation = createClass(project, classObject, projectSource);
                classRepresentation.setLocation(x % divider * 200, x / divider * 200);
                panel.add(classRepresentation);
                x++;
                classRepresentation.setClonning(true);
            }
            if (!continueOpening) {
                break;
            } else {
                progressBar.setValue(i);
                i++;
            }
        }
        text = "Ready";
        progressBar.setValue(0);
        progressBar.setString(text);
        progressBar.setToolTipText(text);
        statusLabel.setText(text);
        progressBar.setMaximum(0);
        panel.repaint();
        if (afterProjectLoaded != null) {
            afterProjectLoaded.run();
        }
    }

    public ClassRepresentation createClass(PhpProject project, lt.banelis.parser.Class classObject, Source.Project projectSource) {
        File file = new File(classObject.getParentFile().getPath());
        Date fileDate = new Date();
        if (file.isFile()) {
            fileDate = new Date(file.lastModified());
        }
        String shortPath = stripProjectPath(project, classObject.getParentFile().getPath());
        Source.Model classSource = new Source.Model(fileDate, Source.Model.Language.php, shortPath, projectSource);
        return new ClassRepresentation(classSource, classObject);
    }

    private String stripProjectPath(PhpProject project, String filePath) {
        if (filePath.startsWith(project.getProjectDirectory())) {
            return filePath.substring(project.getProjectDirectory().length());
        } else {
            return filePath;
        }
    }

    public Source.Project getProjectSource(PhpProject project) {
        File projectDirectory = new File(project.getProjectDirectory());
        Date date = new Date();
        String name = "";
        if (projectDirectory.isDirectory()) {
            date = new Date(projectDirectory.lastModified());
            name = projectDirectory.getName();
        } else {
            name = getBaseName(project.getProjectDirectory());
        }
        String owner = "";
        try {
            owner = System.getProperty("user.name");
        } catch (SecurityException ex) { }
        return new Source.Project(date, name, project.getProjectDirectory(), owner, null);
    }

    private static String getBaseName(String url) {
        if (url.endsWith("/") || url.endsWith("\\")) {
            url = url.substring(0, url.length() - 1);
        }
        int slash = url.lastIndexOf("/");
        if (slash < 0) {
            slash = url.lastIndexOf("\\");
        }
        if (slash < 0) {
            return url;
        } else {
            return url.substring(slash + 1);
        }
    }

    public void addGeneralizations() {
        panel.removeConnections(Arrow.Generalization.class);
        for (Component component : panel.getComponents()) {
            if (component instanceof ClassRepresentation) {
                ClassRepresentation classModel = (ClassRepresentation) component;
                if (classModel.whatExtends() != null) {
                    ClassRepresentation destination = getClassRepresentation(classModel.whatExtends());
                    if (destination != null) {
                        panel.addConnnection(new Connection(component, destination, new Arrow.Generalization()));
                    }
                }
            }
        }
        panel.repaint();
    }

    private ClassRepresentation getClassRepresentation(String className) {
        for (Component component : panel.getComponents()) {
            if (component instanceof ClassRepresentation) {
                ClassRepresentation classModel = (ClassRepresentation) component;
                if (classModel.getClassName().equals(className)) {
                    return classModel;
                }
            }
        }
        return null;
    }

    public final List<ClassRepresentation> getModels() {
        if (models == null) {
            addAllModels();
        }
        return models;
    }

    private void addAllModels() {
        models = new LinkedList<ClassRepresentation>();
        for (Component component : panel.getComponents()) {
            if (component instanceof ClassRepresentation) {
                models.add((ClassRepresentation) component);
            }
        }
    }

    private void attatchPanelChaneListener() {
        panel.addChangeListener(new ZoomPanel.ContentChangeListener() {

            public void added(Component component) {
                if (component instanceof ClassRepresentation) {
                    getModels().add((ClassRepresentation) component);
                }
            }

            public void addedAll() {
                addAllModels();
            }

            public void removed(Component component) {
                if (component instanceof ClassRepresentation) {
                    getModels().remove((ClassRepresentation) component);
                }
            }

            public void removedAll() {
                getModels().clear();
            }
        });
    }

    private ClassRepresentation getRepresentation(String name, List<ClassRepresentation> container) {
        for (ClassRepresentation model : container) {
            if (model.getClassName().equals(name)) {
                return model;
            }
        }
        return null;
    }

    /*
     * Arranging
     */
    
    public final SimpleArranging simpleGrid;
    public final Generalization generalizationGrid;
    
    /*
     * GUI
     */

    public void clear() {
        panel.removeAll();
        panel.repaint();
    }

    public JPopupMenu getPopup() {
        if (contextMenu == null) {
            contextMenu = new JPopupMenu("Context menu");

            JMenuItem clear = new JMenuItem("Clear all");
            clear.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    clear();
                }
            });
            contextMenu.add(clear);

            JMenu arrange = new JMenu("Arrange");
            JMenuItem arrangeGrid = new JMenuItem("Simple grid");
            arrangeGrid.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    simpleGrid.arrange();
                }
            });
            JMenuItem arrangeGeneralization = new JMenuItem("Generalization");
            arrangeGeneralization.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addGeneralizations();
                    generalizationGrid.arrange();
                }
            });
            arrange.add(arrangeGrid);
            arrange.add(arrangeGeneralization);
            contextMenu.add(arrange);

            JMenu zoom = new JMenu("Zoom");
            JMenuItem zoomIn = new JMenuItem("Zoom in");
            JMenuItem zoomReset = new JMenuItem("Reset zoom");
            JMenuItem zoomOut = new JMenuItem("Zoom out");
            zoomIn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    panel.zoom(1.1);
                }
            });
            zoomOut.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    panel.zoom(0.9);
                }
            });
            zoomReset.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    panel.reset();
                }
            });
            zoom.add(zoomIn);
            zoom.add(zoomReset);
            zoom.add(zoomOut);
            contextMenu.add(zoom);

            contextMenu.addSeparator();

            final JCheckBoxMenuItem generalizations = new JCheckBoxMenuItem("Generalizations");
            generalizations.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (generalizations.isSelected()) {
                        addGeneralizations();
                    } else {
                        panel.removeConnections(Arrow.Generalization.class);
                        panel.repaint();
                    }
                }
            });
            contextMenu.add(generalizations);
        }
        return contextMenu;
    }
}
