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

package com.biancama.utils;

import java.awt.Color;
import java.awt.HeadlessException;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.biancama.EasyShipmentInitFlags;
import com.biancama.HostPluginWrapper;
import com.biancama.config.Configuration;
import com.biancama.config.SubConfiguration;
import com.biancama.controlling.DownloadController;
import com.biancama.controlling.ProgressController;
import com.biancama.events.BiancaController;
import com.biancama.events.MessageEvent;
import com.biancama.events.MessageListener;
import com.biancama.gui.UserIO;
import com.biancama.gui.easyShipment.util.nativeintegration.LocalBrowser;
import com.biancama.gui.swing.GuiRunnable;
import com.biancama.gui.swing.SwingGui;
import com.biancama.gui.swing.components.Balloon;
import com.biancama.gui.swing.easyShipment.GUIUtils;
import com.biancama.gui.swing.easyShipment.utils.EasyShipmentGuiConstants;
import com.biancama.http.Browser;
import com.biancama.http.URLConnectionAdapter;
import com.biancama.log.BiancaLogger;
import com.biancama.update.BiancaUpdateUtils;
import com.biancama.update.FileUpdate;
import com.biancama.update.WebUpdater;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.locale.BiancaL;

public class WebUpdate {
    private static Logger logger = BiancaLogger.getLogger();
    private static int waitingUpdates = 0;

    private static boolean DYNAMIC_PLUGINS_FINISHED = false;
    private static boolean UPDATE_IN_PROGRESS = false;

    public static void dynamicPluginsFinished() {
        DYNAMIC_PLUGINS_FINISHED = true;
    }

    private static String getUpdaterMD5(int trycount) {
        return WebUpdater.UPDATE_MIRROR[trycount % WebUpdater.UPDATE_MIRROR.length] + "jdupdate.jar.md5";
    }

    private static String getUpdater(int trycount) {
        return WebUpdater.UPDATE_MIRROR[trycount % WebUpdater.UPDATE_MIRROR.length] + "jdupdate.jar";
    }

    public static boolean updateUpdater() {
        final ProgressController progress = new ProgressController(BiancaL.L("wrapper.webupdate.updatenewupdater", "Downloading new jdupdate.jar"));
        progress.increase(1);
        Thread ttmp = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                    if (progress.getValue() > 95) {
                        progress.setStatus(10);
                    }
                    progress.increase(1);
                }

            }
        };
        WebUpdater.randomizeMirrors();
        ttmp.start();
        Browser br = new Browser();
        br.setReadTimeout(20 * 1000);
        br.setConnectTimeout(10 * 1000);
        File file;
        String localHash = HashUtils.getMD5(file = URLUtils.getResourceFile("jdupdate.jar"));
        String remoteHash = null;
        for (int trycount = 0; trycount < 10; trycount++) {
            if (remoteHash == null) {
                try {
                    remoteHash = br.getPage(getUpdaterMD5(trycount) + "?t=" + System.currentTimeMillis()).trim();
                } catch (Exception e) {
                    remoteHash = null;
                    errorWait();
                    continue;
                }
            }
            if (localHash != null && remoteHash != null && remoteHash.equalsIgnoreCase(localHash)) {
                ttmp.interrupt();
                progress.doFinalize();
                logger.info("Updater is still up2date!");
                return true;
            }
            if (localHash == null || !remoteHash.equalsIgnoreCase(localHash)) {
                logger.info("Download " + file.getAbsolutePath() + "");
                URLConnectionAdapter con = null;
                try {
                    con = br.openGetConnection(getUpdater(trycount) + "?t=" + System.currentTimeMillis());
                    if (con.isOK()) {
                        File tmp;
                        Browser.download(tmp = new File(file.getAbsolutePath() + ".tmp"), con);
                        localHash = HashUtils.getMD5(tmp);
                        if (remoteHash.equalsIgnoreCase(localHash)) {
                            if ((!file.exists() || file.delete()) && tmp.renameTo(file)) {
                                ttmp.interrupt();
                                progress.doFinalize(2000);
                                logger.info("Update of " + file.getAbsolutePath() + " successfull");
                                return true;
                            } else {
                                ttmp.interrupt();
                                logger.severe("Rename error: jdupdate.jar");
                                progress.setColor(Color.RED);
                                progress.setStatusText(BiancaL.LF("wrapper.webupdate.updateUpdater.error_rename", "Could not rename jdupdate.jar.tmp to jdupdate.jar"));
                                progress.doFinalize(5000);
                                return false;
                            }
                        } else {
                            logger.severe("CRC Error while downloading jdupdate.jar");
                        }
                    } else {
                        con.disconnect();
                    }
                } catch (Exception e) {
                    try {
                        con.disconnect();
                    } catch (Exception e2) {
                    }
                }
                new File(file.getAbsolutePath() + ".tmp").delete();
            }
        }
        ttmp.interrupt();
        progress.setColor(Color.RED);
        progress.setStatusText(BiancaL.LF("wrapper.webupdate.updateUpdater.error_reqeust2", "Could not download new jdupdate.jar"));
        progress.doFinalize(5000);
        logger.info("Update of " + file.getAbsolutePath() + " failed");
        return false;
    }

    private static void errorWait() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param forceguiCall
     *            : Updatemeldung soll erscheinen, auch wenn user updates
     *            deaktiviert hat
     */
    public static synchronized void doUpdateCheck(final boolean forceguiCall) {
        if (UPDATE_IN_PROGRESS) {
            logger.info("Update is already running");
            Balloon.show(BiancaL.L("jd.utils.webupdate.ballon.title", "Update"), UserIO.getInstance().getIcon(UserIO.ICON_WARNING), BiancaL.L("jd.utils.webupdate.ballon.message.updateinprogress", "There is already an update in progress."));
            return;
        }
        final ProgressController guiPrgs;
        if (forceguiCall) {
            guiPrgs = new ProgressController(BiancaL.L("init.webupdate.progress.0_title", "Webupdate"), 9);
            guiPrgs.setStatus(3);
        } else {
            guiPrgs = null;
        }
        UPDATE_IN_PROGRESS = true;

        final String id = BiancaController.requestDelayExit("doUpdateCheck");

        final WebUpdater updater = new WebUpdater();

        updater.ignorePlugins(false);
        logger.finer("Checking for available updates");

        final ArrayList<FileUpdate> files;
        try {
            files = updater.getAvailableFiles();
            if (updater.sum.length > 100) {
                SubConfiguration.getConfig("a" + "pckage").setProperty(new String(new byte[] { 97, 112, 99, 107, 97, 103, 101 }), updater.sum);
                SubConfiguration.getConfig("a" + "pckage").save();
            }
        } catch (Exception e) {
            UPDATE_IN_PROGRESS = false;
            BiancaController.releaseDelayExit(id);
            return;
        }

        new Thread() {
            @Override
            public void run() {
                MessageListener messageListener = null;
                if (files != null) {

                    updater.filterAvailableUpdates(files);

                    boolean coreUp2Date = true;
                    ArrayList<FileUpdate> tmpfiles = new ArrayList<FileUpdate>();
                    for (FileUpdate f : files) {
                        // check if jdownloader.jar is up2date
                        if (f.getLocalFile().equals(URLUtils.getResourceFile("JDownloader.jar"))) {
                            coreUp2Date = false;
                        }
                        if (f.getLocalFile().getName().endsWith(".class")) {
                            tmpfiles.add(f);
                        }

                    }
                    if (coreUp2Date) {
                        doPluginUpdate(updater, tmpfiles);

                        for (FileUpdate f : tmpfiles) {
                            if (f.equals()) {
                                files.remove(f);
                            }
                        }
                    }
                    WebUpdate.setWaitingUpdates(files.size());

                    if (files.size() > 0) {
                        new GuiRunnable<Object>() {
                            @Override
                            public Object runSave() {
                                SwingGui.getInstance().getMainFrame().setTitle(ApplicationUtils.getTitle());
                                if (guiPrgs != null) {
                                    guiPrgs.setStatus(9);
                                    guiPrgs.doFinalize(3000);
                                }
                                return null;
                            }
                        }.start();
                    } else {
                        if (guiPrgs != null) {
                            guiPrgs.setStatus(9);
                            guiPrgs.setColor(Color.RED);
                            guiPrgs.setStatusText(BiancaL.L("jd.utils.WebUpdate.doUpdateCheck.noupdates", "No Updates available"));
                            guiPrgs.doFinalize(3000);
                        }
                    }

                }

                // only ignore updaterequest of all plugins are present
                if (HostPluginWrapper.getHostWrapper().size() > 50 && !EasyShipmentInitFlags.SWITCH_RETURNED_FROM_UPDATE && !forceguiCall && SubConfiguration.getConfig("WEBUPDATE").getBooleanProperty(Configuration.Param.PARAM_WEBUPDATE_DISABLE.toString(), false)) {
                    logger.severe("Webupdater disabled");
                    /*
                     * autostart downloads if not autostarted yet and
                     * autowebupdate is also enabled
                     */
                    if (GUIUtils.getConfig().getBooleanProperty(EasyShipmentGuiConstants.PARAM_START_DOWNLOADS_AFTER_START.toString(), false) && FileSystemUtils.getConfiguration().getBooleanProperty(Configuration.Param.PARAM_WEBUPDATE_AUTO_RESTART.toString(), false)) {
                        BiancaController.getInstance().autostartDownloadsonStartup();
                    }
                    BiancaController.releaseDelayExit(id);
                    UPDATE_IN_PROGRESS = false;
                    return;
                }

                if (files == null || files.size() == 0) {

                    // ask to restart if there are updates left in the /update/
                    // folder
                    File[] updates = URLUtils.getResourceFile("update").listFiles();
                    if (updates != null && updates.length > 0) {

                        int ret = UserIO.getInstance().requestConfirmDialog(UserIO.DONT_SHOW_AGAIN | UserIO.DONT_SHOW_AGAIN_IGNORES_CANCEL, BiancaL.L("jd.update.Main.error.title.old", "Updates found!"), BiancaL.L("jd.update.Main.error.message.old", "There are uninstalled updates. Install them now?"), null, null, null);
                        if (FlagsUtils.hasAllFlags(ret, UserIO.RETURN_OK)) {
                            BiancaController.releaseDelayExit(id);
                            ApplicationUtils.restartApplicationAndWait();
                            return;
                        }

                    }
                    if (updater.getBetaBranch() != null && !SubConfiguration.getConfig("WEBUPDATE").getBooleanProperty(updater.getBetaBranch(), false)) {

                        SubConfiguration.getConfig("WEBUPDATE").setProperty(updater.getBetaBranch(), true);
                        SubConfiguration.getConfig("WEBUPDATE").save();

                        int ret = UserIO.getInstance().requestConfirmDialog(UserIO.DONT_SHOW_AGAIN, BiancaL.L("updater.newbeta.title", "New BETA available"), BiancaL.L("updater.newbeta.message", "Do you want to try the new BETA?\r\nClick OK to get more Information."));
                        if (UserIO.isOK(ret)) {
                            try {
                                LocalBrowser.openDefaultURL(new URL("http://jdownloader.org/beta"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    logger.severe("Webupdater offline or nothing to update");
                    /*
                     * autostart downloads if not autostarted yet and
                     * autowebupdate is also enabled
                     */
                    if (GUIUtils.getConfig().getBooleanProperty(EasyShipmentGuiConstants.PARAM_START_DOWNLOADS_AFTER_START.toString(), false) && FileSystemUtils.getConfiguration().getBooleanProperty(Configuration.Param.PARAM_WEBUPDATE_AUTO_RESTART.toString(), false)) {
                        BiancaController.getInstance().autostartDownloadsonStartup();
                    }
                    BiancaController.releaseDelayExit(id);
                    UPDATE_IN_PROGRESS = false;
                    return;
                }
                int org;

                if (files.size() > 0) {

                    final ProgressController progress = new ProgressController(BiancaL.L("init.webupdate.progress.0_title", "Webupdate"), 100);
                    updater.getBroadcaster().addListener(messageListener = new MessageListener() {
                        public void onMessage(MessageEvent event) {
                            progress.setStatusText(event.getSource() + ": " + event.getMessage());
                        }
                    });

                    progress.setRange(org = files.size());
                    progress.setStatusText(BiancaL.L("init.webupdate.progress.1_title", "Update Check"));
                    progress.setStatus(org - (files.size()));
                    logger.finer(updater.getBranch() + "");
                    logger.finer("Files to update: " + files);

                    if (FileSystemUtils.getConfiguration().getBooleanProperty(Configuration.Param.PARAM_WEBUPDATE_AUTO_RESTART.toString(), false)) {
                        UserIO.setCountdownTime(5);
                        int answer = UserIO.getInstance().requestConfirmDialog(UserIO.STYLE_HTML, BiancaL.L("init.webupdate.auto.countdowndialog2", "Automatic update."), BiancaL.LF("jd.utils.webupdate.message", "<font size=\"2\" face=\"Verdana, Arial, Helvetica, sans-serif\">%s update(s) available. Install now?</font>", files.size()), BiancaTheme.II("gui.splash.update", 32, 32), null, null);
                        UserIO.setCountdownTime(-1);

                        if (FlagsUtils.hasSomeFlags(answer, UserIO.RETURN_OK, UserIO.RETURN_COUNTDOWN_TIMEOUT)) {
                            doUpdate(updater, files);
                        } else {
                            UPDATE_IN_PROGRESS = false;
                        }
                    } else {
                        try {

                            String html = BiancaL.L("jd.utils.webupdate.whatchangedlink", "<hr/><a href='http://jdownloader.org/latestchanges'>What has changed?</a>");
                            int answer = UserIO.getInstance().requestConfirmDialog(UserIO.STYLE_HTML, BiancaL.L("system.dialogs.update", "Updates available"), BiancaL.LF("jd.utils.webupdate.message2", "<font size=\"4\" face=\"Verdana, Arial, Helvetica, sans-serif\">%s update(s) available. Install now?</font>", files.size()) + html, BiancaTheme.II("gui.splash.update", 32, 32), null, null);

                            if (FlagsUtils.hasAllFlags(answer, UserIO.RETURN_OK)) {
                                doUpdate(updater, files);
                            } else {
                                UPDATE_IN_PROGRESS = false;
                            }
                        } catch (HeadlessException e) {
                            BiancaLogger.exception(e);
                            UPDATE_IN_PROGRESS = false;
                        }
                    }
                    progress.doFinalize();

                }
                if (messageListener != null) {
                    updater.getBroadcaster().removeListener(messageListener);
                }
                /*
                 * autostart downloads if not autostarted yet and autowebupdate
                 * is also enabled
                 */
                if (GUIUtils.getConfig().getBooleanProperty(EasyShipmentGuiConstants.PARAM_START_DOWNLOADS_AFTER_START.toString(), false) && FileSystemUtils.getConfiguration().getBooleanProperty(Configuration.Param.PARAM_WEBUPDATE_AUTO_RESTART.toString(), false)) {
                    BiancaController.getInstance().autostartDownloadsonStartup();
                }
                BiancaController.releaseDelayExit(id);
            }
        }.start();
    }

    private static void doPluginUpdate(final WebUpdater updater, final ArrayList<FileUpdate> files) {

        final ProgressController pc = new ProgressController("", files.size());

        try {
            updater.getBroadcaster().addListener(new MessageListener() {

                public void onMessage(MessageEvent event) {
                    pc.setStatusText(event.getMessage() + " " + BiancaL.L("jd.utils.WebUpdate.doPluginUpdate", "[Restart on plugin out of date errors]"));
                }

            });

            System.out.println("Update: " + files);

            updater.updateFiles(files, pc);// copies plugins

            // please check:
            boolean restart = false;
            for (FileUpdate f : files) {
                // try to rename NOW
                if (!((!f.getLocalFile().exists() || f.getLocalFile().delete()) && (f.getLocalTmpFile().renameTo(f.getLocalFile())))) {
                    restart = true;
                    // has not been updated
                    // it.remove();
                } else {
                    File parent = f.getLocalTmpFile().getParentFile();

                    while (parent.listFiles() != null && parent.listFiles().length < 1) {
                        parent.delete();
                        parent = parent.getParentFile();
                    }
                }
            }
            if (restart) {

                Balloon.show(BiancaL.L("jd.utils.WebUpdate.doPluginUpdate.title", "Restart recommended"), null, BiancaL.L("jd.utils.WebUpdate.doPluginUpdate.message", "Some Plugins have been updated\r\nYou should restart JDownloader."));
            }

        } catch (Exception e) {
            System.err.println("EXCEPTION");
            BiancaLogger.exception(e);
            e.printStackTrace();

        }
        pc.doFinalize();

    }

    private static void doUpdate(final WebUpdater updater, final ArrayList<FileUpdate> files) {

        new Thread() {
            @Override
            public void run() {
                final String id = BiancaController.requestDelayExit("doUpdate");
                try {
                    int i = 0;
                    while (!DYNAMIC_PLUGINS_FINISHED) {
                        try {
                            Thread.sleep(1000);
                            i++;
                            logger.severe("Waiting on DynamicPlugins since " + i + " secs!");
                        } catch (InterruptedException e) {
                        }
                    }

                    DownloadController dlc = DownloadController.getInstance();
                    if (dlc != null) {
                        BiancaUpdateUtils.backupDataBase();
                    } else {
                        logger.severe("Could not backup. downloadcontroller=null");
                    }

                    if (!WebUpdate.updateUpdater()) {

                    }

                    final ProgressController pc = new ProgressController(BiancaL.L("jd.utils.webupdate.progresscontroller.text", "Update is running"), 10);

                    try {

                        updater.getBroadcaster().addListener(new MessageListener() {

                            public void onMessage(MessageEvent event) {
                                pc.setStatusText(event.getSource().toString() + ": " + event.getMessage());

                            }

                        });
                        pc.increase(10);

                        System.out.println("Update: " + files);
                        updater.cleanUp();

                        updater.updateFiles(files, pc);
                        if (updater.getErrors() > 0) {
                            System.err.println("ERRO");
                            int ret = UserIO.getInstance().requestConfirmDialog(UserIO.NO_COUNTDOWN | UserIO.DONT_SHOW_AGAIN | UserIO.DONT_SHOW_AGAIN_IGNORES_CANCEL, BiancaL.L("jd.update.Main.error.title", "Errors occured"), BiancaL.LF("jd.update.Main.error.message", "Errors occured!\r\nThere were %s error(s) while updating. Do you want to update anyway?", updater.getErrors()), UserIO.getInstance().getIcon(UserIO.ICON_WARNING), null, null);
                            if (FlagsUtils.hasAllFlags(ret, UserIO.RETURN_OK)) {
                                BiancaController.releaseDelayExit(id);
                                ApplicationUtils.restartApplicationAndWait();
                            }

                        } else {
                            System.err.println("OK RESTART");
                            BiancaController.releaseDelayExit(id);
                            ApplicationUtils.restartApplicationAndWait();
                        }

                    } catch (Exception e) {
                        System.err.println("EXCEPTION");
                        BiancaLogger.exception(e);
                        e.printStackTrace();

                    }
                    pc.doFinalize();
                } finally {
                    BiancaController.releaseDelayExit(id);
                    UPDATE_IN_PROGRESS = false;
                }

            }
        }.start();
    }

    private static void setWaitingUpdates(int i) {
        waitingUpdates = Math.max(0, i);
    }

    public static int getWaitingUpdates() {
        return waitingUpdates;
    }

}
