//    jDownloader - Downloadmanager
//    Copyright (C) 2008  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.biancama.update;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;

import com.biancama.config.SubConfiguration;
import com.biancama.events.MessageEvent;
import com.biancama.events.MessageListener;
import com.biancama.gui.UserIO;
import com.biancama.http.Browser;
import com.biancama.utils.DatabaseUtils;
import com.biancama.utils.FormatterUtils;
import com.biancama.utils.HashUtils;
import com.biancama.utils.URLUtils;
import com.biancama.utils.gui.ContainerUtils;
import com.biancama.utils.gui.ExecuterUtils;
import com.biancama.utils.gui.io.BiancaIO;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class Main {

    private static int BOTHRESIZE = GridBagConstraints.BOTH;
    private static Insets INSETS = new Insets(5, 5, 5, 5);
    private static int NORESIZE = GridBagConstraints.NONE;
    private static int NORTHWEST = GridBagConstraints.NORTHWEST;
    private static int REL = GridBagConstraints.RELATIVE;
    private static int REM = GridBagConstraints.REMAINDER;
    private static SubConfiguration guiConfig;
    private static StringBuilder log;
    private static JFrame frame;
    private static JTextArea logWindow;
    public static ArrayList<Server> clonePrefix = new ArrayList<Server>();
    public static boolean clone = false;
    private static JProgressBar progressload;
    private static JTextPane warnings;
    private static int TICKET_TIME = -1;
    private static Logger logger;
    private static boolean RESTORE = false;

    private static void log(StringBuilder log, String string) {
        if (log != null) {
            log.append(string);
        }
        System.out.println(string);

    }

    public static void main(String args[]) {
        try {
            log = new StringBuilder();
            FileHandler handler = new FileHandler("jdupdate.log", false);

            // Add to the desired logger
            logger = Logger.getLogger("org.jdownloader.jdupdate");
            logger.addHandler(handler);

            boolean OSFilter = true;

            File cfg;
            if ((cfg = URLUtils.getResourceFile("jdownloader.config")).exists()) {

                URLUtils.getResourceFile("backup/").mkdirs();

                cfg.renameTo(URLUtils.getResourceFile("backup/jdownloader.config.outdated"));

            }

            for (int i = 0; i < args.length; i++) {
                String p = args[i];
                if (p.trim().equalsIgnoreCase("-noosfilter")) {
                    OSFilter = false;

                } else if (p.trim().equalsIgnoreCase("-full")) {

                    OSFilter = false;
                } else if (p.trim().equalsIgnoreCase("-brdebug")) {
                    Browser.setVerbose(true);
                } else if (p.trim().equalsIgnoreCase("-restore")) {
                    RESTORE = true;

                } else if (p.trim().equalsIgnoreCase("-branch")) {
                    String br = args[++i];
                    if (br.equalsIgnoreCase("reset")) {
                        br = null;
                    }
                    SubConfiguration.getConfig("WEBUPDATE").setProperty(WebUpdater.PARAM_BRANCH, br);
                    SubConfiguration.getConfig("WEBUPDATE").save();
                    System.out.println("Switched branch: " + br);
                } else if (p.trim().equalsIgnoreCase("-clone")) {

                    OSFilter = false;
                    clone = true;
                } else if (clone && clonePrefix.size() == 0) {
                    clonePrefix.add(new Server(100, p.trim()));
                }
            }

            Browser.init();

            guiConfig = SubConfiguration.getConfig("WEBUPDATE");

            log.append("Update JDownloader  at " + URLUtils.getResourceFile(".") + "\r\n");
            log.append(SubConfiguration.getConfig("WEBUPDATE").getProperties() + "\r\n");
            System.out.println(SubConfiguration.getConfig("WEBUPDATE").getProperties() + "\r\n");
            System.out.println(SubConfiguration.getConfig("PACKAGEMANAGER").getProperties() + "\r\n");
            log.append(SubConfiguration.getConfig("PACKAGEMANAGER").getProperties() + "\r\n");

            initGUI();

            try {
                Browser br = new Browser();
                TICKET_TIME = Integer.parseInt(br.getPage("http://update0.jdownloader.org/tickettime.txt").trim());

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TICKET_TIME < 0) {
                warnings.setText("There are currently no free update slots. Please try again later!\r\n\r\nYou can close this Programm now.\r\nWe advise you to download the latest version from http://jdownloader.org/download!");

                if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(frame, "There are currently no free update slots. Please try again later!\r\n Start JDownloader now?")) {

                } else {

                    while (true) {
                        Thread.sleep(10000000);
                    }
                }
            } else {
                warnings.setText("\r\n\r\n\r\n");
                while (TICKET_TIME > 0) {
                    Thread.sleep(1000);
                    warnings.setText("Update starts in " + FormatterUtils.formatSeconds(TICKET_TIME / 1000) + ".\r\n\r\n\r\nIf you do not want to wait, we suggest you to download the latest version from http://jdownloader.org/download!");

                    TICKET_TIME -= 1000;

                }
                warnings.setText("Update is too slow and takes too much time?\r\n\r\n\r\nDownload latest Version no at http://jdownloader.org/download");
            }
            for (int i = 0; i < args.length; i++) {
                Main.log(log, "Parameter " + i + " " + args[i] + " " + System.getProperty("line.separator"));
                logWindow.setText(log.toString());
            }
            final WebUpdater updater = new WebUpdater();
            updater.setOSFilter(OSFilter);
            updater.getBroadcaster().addListener(new MessageListener() {

                private String msg0 = "";
                private String msg1 = "";
                private String msg2 = "";

                public void onMessage(MessageEvent event) {

                    if (updater.getErrors() > 0) {

                        JOptionPane.showConfirmDialog(frame, "Update Server are too busy.\r\n Please try again later or download update at http://jdownloader.org/download!", "UPDATE WARNINGS", JOptionPane.YES_OPTION, JOptionPane.WARNING_MESSAGE);
                        Main.log(log, "Abort due to errors ");

                        Main.log(log, "Local: " + new File("").getAbsolutePath());
                        Main.log(log, "Start java -jar -Xmx512m JDownloader.jar in " + URLUtils.getResourceFile(".").getAbsolutePath());

                        ExecuterUtils.runCommand("java", new String[] { "-Xmx512m", "-jar", "JDownloader.jar", "-rfu" }, URLUtils.getResourceFile(".").getAbsolutePath(), 0);

                        logWindow.setText(log.toString());
                        BiancaIO.writeLocalFile(URLUtils.getResourceFile("updateLog.txt"), log.toString());
                        System.exit(0);
                        return;

                    }
                    if (event.getID() == WebUpdater.DO_UPDATE_FAILED || event.getID() == FileUpdate.ERROR) {

                        msg2 = msg1;
                        msg1 = msg0;
                        msg0 = event.getMessage();

                        warnings.setText(msg2 + "\r\n" + msg1 + "\r\n" + msg0 + "\r\n               Download latest Version no at http://jdownloader.org/download");

                    }

                    if (event.getID() == WebUpdater.DO_UPDATE_SUCCESS) {
                        warnings.setText("Update is too slow and takes too much time?\r\n\r\n\r\nDownload latest Version at http://jdownloader.org/download");

                    }
                    Main.log(log, event.getMessage() + "\r\n");

                }

            });
            Main.log(log, "Current Date:" + new Date() + "\r\n");
            checkBackup();
            updater.ignorePlugins(false);

            checkUpdateMessage();
            updater.setLogger(log);

            updater.setDownloadProgress(progressload);
            Main.trace("Start Webupdate");

            if (RESTORE) {
                // Remove.extracts
                BiancaIO.removeByPattern(URLUtils.getResourceFile("jd").getParentFile(), Pattern.compile(".*\\.extract", Pattern.CASE_INSENSITIVE));
            }
            ArrayList<FileUpdate> files;
            try {
                files = updater.getAvailableFiles();
            } catch (Exception e) {
                Main.trace("Update failed");
                Main.log(log, "Update failed");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                }
                files = new ArrayList<FileUpdate>();
            }

            if (files != null) {

                updater.filterAvailableUpdates(files);

                BiancaUpdateUtils.backupDataBase();
                updater.updateFiles(files, null);
            }

            Restarter.main(new String[] {});

            // if (!clone) installAddons(URLUtils.getResourceFile("."));
            Main.trace(updater.getLogger().toString());
            Main.trace("End Webupdate");

            logWindow.setText(log.toString());
            Main.trace(URLUtils.getResourceFile("updateLog.txt").getAbsoluteFile());

            if (URLUtils.getResourceFile("webcheck.tmp").exists()) {
                URLUtils.getResourceFile("webcheck.tmp").delete();
            }
            Main.log(log, "Local: " + URLUtils.getResourceFile(".").getAbsolutePath());

            Main.log(log, "Start java -jar -Xmx512m JDownloader.jar in " + URLUtils.getResourceFile(".").getAbsolutePath());
            DatabaseUtils.getDatabaseConnector().shutdownDatabase();
            ExecuterUtils.runCommand("java", new String[] { "-Xmx512m", "-jar", "JDownloader.jar", "-rfu" }, URLUtils.getResourceFile(".").getAbsolutePath(), 0);

            logWindow.setText(log.toString());
            BiancaIO.writeLocalFile(URLUtils.getResourceFile("updateLog.txt"), log.toString());
            Main.log(log, "Errors: " + updater.getErrors());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();

            Main.log(log, "ERROR " + e.getLocalizedMessage());

        }
    }

    // public static void installAddons(File root) {
    // SubConfiguration jdus = WebUpdater.getConfig("JDU");
    // ArrayList<PackageData> data = jdus.getGenericProperty("PACKAGEDATA", new
    // ArrayList<PackageData>());
    //
    // for (PackageData pa : data) {
    // if (!pa.isDownloaded()) continue;
    // File zip = new File(pa.getStringProperty("LOCALPATH"));
    //
    // Main.log(log, "Install: " + zip + System.getProperty("line.separator") +
    // System.getProperty("line.separator"));
    //
    // UnZip u = new UnZip(zip, root);
    // File[] efiles;
    // try {
    // efiles = u.extract();
    // if (efiles != null) {
    //
    // for (File element : efiles) {
    // Main.log(log, "       extracted: " + element +
    // System.getProperty("line.separator"));
    // if (element.getAbsolutePath().endsWith("readme.html")) {
    // pa.setProperty("README", element.getAbsolutePath());
    //
    // }
    // }
    // pa.setInstalled(true);
    // pa.setUpdating(false);
    // pa.setDownloaded(false);
    // pa.setInstalledVersion(Integer.parseInt(pa.getStringProperty("version")));
    //
    // Main.log(log, "Installation successfull: " + zip +
    // System.getProperty("line.separator"));
    //
    // System.out.println("Delete " + zip.delete());
    // zip.deleteOnExit();
    //
    // }
    // } catch (Exception e) {
    //
    // e.printStackTrace();
    //
    // StackTraceElement[] trace = e.getStackTrace();
    // for (int i = 0; i < trace.length; i++)
    // Main.log(log, "\tat " + trace[i] + "\r\n");
    //
    // zip.delete();
    // zip.deleteOnExit();
    //
    // pa.setInstalled(true);
    // pa.setUpdating(false);
    // pa.setDownloaded(false);
    //
    // }
    //
    // }
    // jdus.save();
    // File afile[] = (URLUtils.getResourceFile("packages")).listFiles();
    // if (afile != null) {
    // for (int l = 0; l < afile.length; l++) {
    // File jdu = afile[l];
    // if (jdu.getName().toLowerCase().endsWith("jdu")) {
    // jdu.delete();
    // jdu.deleteOnExit();
    // log(log, (new StringBuilder("delete: ")).append(jdu).toString());
    // }
    // }
    // }
    //
    // }

    private static void checkUpdateMessage() throws IOException {
        try {
            if (URLUtils.getResourceFile("updateLog.txt").exists()) {
                String warnHash = HashUtils.getMD5(URLUtils.getResourceFile("updatewarnings.html"));

                Browser.download(URLUtils.getResourceFile("updatewarnings.html"), "http://update1.jdownloader.org/messages/updatewarning.html");
                String hash2 = HashUtils.getMD5(URLUtils.getResourceFile("updatewarnings.html"));
                if (hash2 != null && !hash2.equals(warnHash)) {
                    String str;
                    str = BiancaIO.readFileToString(new File("updatewarnings.html"));
                    if (str.trim().length() > 0) {
                        if (JOptionPane.showConfirmDialog(frame, str, "UPDATE WARNINGS", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
                            Main.log(log, "Abort due to warnings " + str);

                            URLUtils.getResourceFile("updatewarnings.html").delete();
                            URLUtils.getResourceFile("updatewarnings.html").deleteOnExit();
                            if (URLUtils.getResourceFile("webcheck.tmp").exists()) {
                                URLUtils.getResourceFile("webcheck.tmp").delete();
                            }
                            Main.log(log, "Local: " + new File("").getAbsolutePath());
                            Main.log(log, "Start java -jar -Xmx512m JDownloader.jar in " + URLUtils.getResourceFile(".").getAbsolutePath());

                            ExecuterUtils.runCommand("java", new String[] { "-Xmx512m", "-jar", "JDownloader.jar", "-rfu" }, URLUtils.getResourceFile(".").getAbsolutePath(), 0);

                            logWindow.setText(log.toString());
                            BiancaIO.writeLocalFile(URLUtils.getResourceFile("updateLog.txt"), log.toString());
                            System.exit(0);
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void checkBackup() {
        if (URLUtils.getResourceFile("updateLog.txt").exists()) {
            if (!URLUtils.getResourceFile("/backup/").exists()) {
                URLUtils.getResourceFile("/backup/").mkdirs();
                /*
                 * Ordner wurde doch gerade angelegt, warum dann Fehlermeldung
                 * dass er nicht angelegt werden konnte? - Greeny
                 */
                Main.log(log, "Not found: " + (URLUtils.getResourceFile("/backup/").getAbsolutePath()) + "\r\n");
                UserIO.getInstance().requestMessageDialog("JDownloader could not create a backup. Please make sure that\r\n " + URLUtils.getResourceFile("/backup/").getAbsolutePath() + " exists and is writable before starting the update");
                ExecuterUtils.runCommand("java", new String[] { "-Xmx512m", "-jar", "JDownloader.jar", "-rfu" }, URLUtils.getResourceFile(".").getAbsolutePath(), 0);
                System.exit(0);
                return;
            }

            File lastBackup = URLUtils.getResourceFile("/backup/links.linkbackup");

            Main.log(log, "Backup found. date:" + new Date(lastBackup.lastModified()) + "\r\n");
            if (!lastBackup.exists() || (System.currentTimeMillis() - lastBackup.lastModified()) > 5 * 60 * 1000) {

                URLUtils.getResourceFile("/backup/").mkdirs();
                String msg = "";

                if (!lastBackup.exists()) {

                    msg = "Do you want to continue without a backup? Your queue may get lost.\r\nLatest backup found: NONE!\r\nNote: You can ignore this message if this is a fresh JD-Installation and your linklist is empty anyway";
                } else {
                    msg = "Do you want to continue without a backup? Your queue may get lost.\r\nLatest backup found: " + new Date(lastBackup.lastModified()) + "\r\nin " + lastBackup.getAbsolutePath();
                }

                if (JOptionPane.showConfirmDialog(frame, msg, "There is no backup of your current Downloadqueue", JOptionPane.WARNING_MESSAGE) != JOptionPane.OK_OPTION) {
                    ExecuterUtils.runCommand("java", new String[] { "-Xmx512m", "-jar", "JDownloader.jar", "-rfu" }, URLUtils.getResourceFile(".").getAbsolutePath(), 0);
                    System.exit(0);
                    return;
                }

            }
        }

    }

    private static void initGUI() {
        UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
        String paf = guiConfig.getStringProperty("PLAF", null);
        boolean plafisSet = false;

        if (paf != null) {
            for (LookAndFeelInfo element : info) {
                if (element.getName().equals(paf)) {
                    try {
                        UIManager.setLookAndFeel(element.getClassName());
                        plafisSet = true;
                        break;
                    } catch (UnsupportedLookAndFeelException e) {
                    } catch (ClassNotFoundException e) {
                    } catch (InstantiationException e) {
                    } catch (IllegalAccessException e) {
                    }
                }
            }
        } else {
            for (int i = 0; i < info.length; i++) {
                if (!info[i].getName().matches("(?is).*(metal|motif).*")) {
                    try {
                        UIManager.setLookAndFeel(info[i].getClassName());
                        plafisSet = true;
                        break;
                    } catch (UnsupportedLookAndFeelException e) {
                    } catch (ClassNotFoundException e) {
                    } catch (InstantiationException e) {
                    } catch (IllegalAccessException e) {
                    }
                }
            }
        }
        if (!plafisSet) {
            try {
                UIManager.setLookAndFeel(new WindowsLookAndFeel());
            } catch (UnsupportedLookAndFeelException e) {
            }
        }

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("JD Update");
        frame.setLayout(new GridBagLayout());

        progressload = new JProgressBar();
        progressload.setMaximum(100);
        progressload.setStringPainted(true);

        warnings = new JTextPane();
        warnings.setForeground(Color.RED);
        warnings.setText("Please wait.. check ticket");
        warnings.setOpaque(false);
        warnings.setBackground(null);
        warnings.setEditable(false);
        warnings.setPreferredSize(new Dimension(500, 80));
        warnings.setMinimumSize(new Dimension(50, 80));
        warnings.putClientProperty("Synthetica.opaque", Boolean.FALSE);

        logWindow = new JTextArea(30, 120);
        JScrollPane scrollPane = new JScrollPane(logWindow);
        scrollPane.setAutoscrolls(true);
        logWindow.setEditable(false);
        logWindow.setAutoscrolls(true);
        ContainerUtils.addToGridBag(frame, warnings, REL, REL, REM, 1, 0, 0, INSETS, BOTHRESIZE, NORTHWEST);

        ContainerUtils.addToGridBag(frame, new JLabel("Webupdate is running..."), REL, REL, REM, 1, 0, 0, INSETS, NORESIZE, NORTHWEST);

        ContainerUtils.addToGridBag(frame, new JLabel("Download: "), REL, REL, REL, 1, 0, 0, INSETS, NORESIZE, NORTHWEST);
        ContainerUtils.addToGridBag(frame, progressload, REL, REL, REM, 1, 1, 0, INSETS, BOTHRESIZE, NORTHWEST);
        Main.log(log, "Starting...");
        logWindow.setText(log.toString());
        ContainerUtils.addToGridBag(frame, scrollPane, REL, REL, REM, 1, 1, 1, INSETS, BOTHRESIZE, NORTHWEST);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        int n = 5;
        ((JComponent) frame.getContentPane()).setBorder(new EmptyBorder(n, n, n, n));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    logWindow.setText(log.toString());
                    logWindow.setCaretPosition(logWindow.getText().length());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }.start();

    }

    public static void trace(Object arg) {
        try {
            System.out.println(arg.toString());
        } catch (Exception e) {
            System.out.println(arg);
        }
    }

}
