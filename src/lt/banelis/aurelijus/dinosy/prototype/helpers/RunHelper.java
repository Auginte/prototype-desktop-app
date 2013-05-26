package lt.banelis.aurelijus.dinosy.prototype.helpers;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableImage;
import lt.dinosy.datalib.Settings;

/**
 * Common functions to work with external programs.
 *
 * @author Aurelijus Banelis
 */
public class RunHelper {

    public void runExternal(final String[] commands) {
        runExternal(commands, true);
    }

    public static Runnable runExternal(final String[] commands, boolean start) {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    Process proc = Runtime.getRuntime().exec(commands);
                    consumeAll(proc.getInputStream());
                    consumeAll(proc.getErrorStream());
                    proc.waitFor();
                } catch (Exception ex) {
                    Logger.getLogger(ZoomableImage.class.getName()).log(Level.SEVERE, "Error running eternal program" + Arrays.toString(commands), ex);
                }
            }
        };
        if (start) {
            new Thread(runnable).start();
        }
        return runnable;
    }

    public static void consumeAll(InputStream stream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                if ("debug".equals("on")) {
                    System.err.println(line);
                }
            }
        } catch (IOException ex) {
        }
    }

    public void openWebpage(String address) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        boolean success = false;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(address));
                success = true;
            } catch (Exception ex) {
                success = false;
            }
        }
        if (!success) {
            String executable = Settings.getInstance().getBrowserProgram();
            runExternal(new String[]{executable, address});
        }
    }

    public void openEditProgram(String file) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        boolean success = false;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.edit(new File(file));
                success = true;
            } catch (Exception ex) {
                success = false;
            }
        }
        if (!success) {
            String executable = Settings.getInstance().getTextEditorProgram();
            if (executable != null) {
                runExternal(new String[]{executable, file});
            }
        }
    }

    public void openImageProgram(String file) {
        String executable = Settings.getInstance().getPaintingProgram();
        if (executable != null) {
            runExternal(new String[]{executable, file});
        } else {
            openViewProgram(file);
        }
    }

    public void openPdfProgram(String file, int page) {
        String executable = Settings.getInstance().getPdfViewer();
        if (executable != null) {
            runExternal(new String[]{executable, "-p", String.valueOf(page), file});
        } else {
            openViewProgram(file);
        }
    }

    public void openInFileBrowser(String file) {
        String executable = Settings.getInstance().getFileBrowser();
        if (executable != null) {
            runExternal(new String[]{executable, file});
        } else {
            openViewProgram(file);
        }
    }

    public void openViewProgram(String file) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.open(new File(file));
            } catch (Exception ex) {
                Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, "Cannot open view program" + file, ex);
            }
        }
    }
}
