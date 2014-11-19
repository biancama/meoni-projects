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

package com.biancama.controlling.reconnect;

import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.biancama.config.Configuration;
import com.biancama.config.SubConfiguration;
import com.biancama.controlling.DownloadWatchDog;
import com.biancama.controlling.LinkCheck;
import com.biancama.controlling.ProgressController;
import com.biancama.events.ControlEvent;
import com.biancama.gui.UserIF;
import com.biancama.gui.UserIO;
import com.biancama.log.BiancaLogger;
import com.biancama.plugins.DownloadLink;
import com.biancama.plugins.FilePackage;
import com.biancama.plugins.LinkStatus;
import com.biancama.utils.EventUtils;
import com.biancama.utils.FileSystemUtils;
import com.biancama.utils.FormatterUtils;
import com.biancama.utils.locale.BiancaL;
import com.biancama.utils.router.IPCheck;

public class Reconnecter {

    private static String CURRENT_IP = "";
    /**
     * Set to true only if there is a reconnect running currently
     */
    private static boolean RECONNECT_IN_PROGRESS = false;
    /**
     * Timestampo of the latest IP CHange
     */
    private static long LAST_UP_UPDATE_TIME = 0;
    private static Logger logger = BiancaLogger.getLogger();
    /**
     * Only true if a reconect has been requestst.
     */
    private static boolean RECONNECT_REQUESTED = false;

    public static void toggleReconnect() {
        boolean newState = !FileSystemUtils.getConfiguration().getBooleanProperty(Configuration.Param.PARAM_ALLOW_RECONNECT.toString(), true);
        FileSystemUtils.getConfiguration().setProperty(Configuration.Param.PARAM_ALLOW_RECONNECT.toString(), newState);
        FileSystemUtils.getConfiguration().save();
        if (!newState) {
            UserIF.getInstance().displayMiniWarning(BiancaL.L("gui.warning.reconnect.hasbeendisabled", "Reconnect deaktiviert!"), BiancaL.L("gui.warning.reconnect.hasbeendisabled.tooltip", "Um erfolgreich einen Reconnect durchführen zu können muss diese Funktion wieder aktiviert werden."));
        }
    }

    public static void setCurrentIP(String ip) {
        if (ip == null) {
            ip = "na";
        }
        CURRENT_IP = ip;
        LAST_UP_UPDATE_TIME = System.currentTimeMillis();
    }

    private static boolean checkExternalIPChange() {
        if (SubConfiguration.getConfig("DOWNLOAD").getBooleanProperty(Configuration.Param.PARAM_GLOBAL_IP_DISABLE.toString(), false)) { return false; }
        LAST_UP_UPDATE_TIME = System.currentTimeMillis();
        String tmp = CURRENT_IP;
        CURRENT_IP = IPCheck.getIPAddress();
        if (tmp == null) {
            tmp = CURRENT_IP;
        }
        if (!CURRENT_IP.equals("na") && tmp.length() > 0 && !tmp.equals(CURRENT_IP)) {
            logger.info("Detected external IP Change.");
            return true;
        }
        return false;
    }

    /**
     * Führt einen Reconnect durch.
     * 
     * @return <code>true</code>, wenn der Reconnect erfolgreich war, sonst
     *         <code>false</code>
     */
    public static boolean doReconnect() {
        boolean ipChangeSuccess = false;
        if (System.currentTimeMillis() - LAST_UP_UPDATE_TIME > (1000 * 60) * SubConfiguration.getConfig("DOWNLOAD").getIntegerProperty("EXTERNAL_IP_CHECK_INTERVAL2", 10)) {
            if (Reconnecter.checkExternalIPChange()) { return true; }
        }
        EventUtils.getController().fireControlEvent(new ControlEvent(EventUtils.getController(), ControlEvent.CONTROL_BEFORE_RECONNECT, null));
        int type = FileSystemUtils.getConfiguration().getIntegerProperty(ReconnectMethod.PARAM_RECONNECT_TYPE, ReconnectMethod.LIVEHEADER);
        logger.info("Try to reconnect...");
        /* laufende downloads stoppen */
        ArrayList<DownloadLink> disabled = DownloadWatchDog.getInstance().getRunningDownloads();
        if (disabled.size() != 0) {
            logger.info("Stopping all running downloads!");
        }
        for (DownloadLink link : disabled) {
            link.setEnabled(false);
        }
        /* warte bis alle gestoppt sind */
        for (int wait = 0; wait < 10; wait++) {
            if (DownloadWatchDog.getInstance().getActiveDownloads() == 0) {
                break;
            }
            try {
                Thread.sleep(1000);
                logger.info("Still waiting for all downloads to stop!");
            } catch (InterruptedException e) {
                break;
            }
        }
        if (DownloadWatchDog.getInstance().getActiveDownloads() > 0) {
            logger.severe("Could not stop all running downloads!");
        }

        try {
            switch (type) {
            case ReconnectMethod.EXTERN:
                ipChangeSuccess = new ExternReconnect().doReconnect();
                break;
            case ReconnectMethod.BATCH:
                ipChangeSuccess = new BatchReconnect().doReconnect();
                break;
            default:
                ipChangeSuccess = new HTTPLiveHeader().doReconnect();
            }
        } catch (Exception e) {
            logger.severe("ReconnectMethod failed!");
        }
        /* gestoppte downloads wieder aufnehmen */
        for (DownloadLink link : disabled) {
            link.setEnabled(true);
        }
        return ipChangeSuccess;
    }

    public static boolean isReconnecting() {
        return RECONNECT_IN_PROGRESS;
    }

    /**
     * Returns true, if there is a requested reconnect qaiting, and the user
     * selected not to start new downloads of reconnects are waiting
     * 
     * @return
     */
    public static boolean isReconnectPrefered() {
        return (isReconnectRequested() && FileSystemUtils.getConfiguration().getBooleanProperty(Configuration.Param.PARAM_ALLOW_RECONNECT.toString(), true) && SubConfiguration.getConfig("DOWNLOAD").getBooleanProperty("PARAM_DOWNLOAD_PREFER_RECONNECT", true));
    }

    public static boolean doReconnectIfRequested(boolean doit) {
        if (RECONNECT_IN_PROGRESS) { return false; }
        /* running linkgrabber will not allow a reconnect */
        if (LinkCheck.getLinkChecker().isRunning()) { return false; }
        /* not allowed to do a reconnect */
        if (EventUtils.getController().getForbiddenReconnectDownloadNum() > 0) { return false; }
        RECONNECT_IN_PROGRESS = true;
        boolean ret = false;
        try {
            ret = doReconnectIfRequestedInternal(doit);
            if (ret) {
                Reconnecter.resetAllLinks();
                EventUtils.getController().fireControlEvent(new ControlEvent(EventUtils.getController(), ControlEvent.CONTROL_AFTER_RECONNECT, null));
            }
        } catch (Exception e) {
        }
        RECONNECT_IN_PROGRESS = false;
        return ret;
    }

    public static boolean doReconnectIfRequestedInternal(boolean doit) {
        boolean ret = false;
        /* überhaupt ein reconnect angefragt? */
        if (isReconnectRequested()) {
            if (!doit && !FileSystemUtils.getConfiguration().getBooleanProperty(Configuration.Param.PARAM_ALLOW_RECONNECT.toString(), true)) {
                /*
                 * auto reconnect ist AUS, dann nur noch schaun ob sich ip
                 * geändert hat
                 */
                if (System.currentTimeMillis() - LAST_UP_UPDATE_TIME > (1000 * 60) * SubConfiguration.getConfig("DOWNLOAD").getIntegerProperty("EXTERNAL_IP_CHECK_INTERVAL2", 10)) { return Reconnecter.checkExternalIPChange(); }
                return false;

            } else {
                /* auto reconnect ist AN */
                try {
                    ret = Reconnecter.doReconnect();
                    if (ret) {
                        logger.info("Reconnect successfully!");
                    } else {
                        logger.info("Reconnect failed!");
                    }
                } catch (Exception e) {
                    logger.finest("Reconnect failed.");
                }
                if (ret == false) {
                    /* reconnect failed, increase fail counter */
                    ProgressController progress = new ProgressController(BiancaL.L("jd.controlling.reconnect.Reconnector.progress.failed", "Reconnect failed! Please check your reconnect Settings and try a Manual Reconnect!"), 100);
                    progress.doFinalize(10000l);
                    int counter = FileSystemUtils.getConfiguration().getIntegerProperty(Configuration.Param.PARAM_RECONNECT_FAILED_COUNTER.toString(), 0) + 1;
                    FileSystemUtils.getConfiguration().setProperty(Configuration.Param.PARAM_RECONNECT_FAILED_COUNTER.toString(), counter);
                    if (counter > 5) {
                        /*
                         * more than 5 failed reconnects in row, disable
                         * autoreconnect and show message
                         */
                        FileSystemUtils.getConfiguration().setProperty(Configuration.Param.PARAM_RECONNECT_OKAY.toString(), false);
                        FileSystemUtils.getConfiguration().setProperty(Configuration.Param.PARAM_ALLOW_RECONNECT.toString(), false);
                        UserIO.getInstance().requestMessageDialog(BiancaL.L("jd.controlling.reconnect.Reconnector.progress.failed2", "Reconnect failed too often! Autoreconnect is disabled! Please check your reconnect Settings!"));
                        FileSystemUtils.getConfiguration().setProperty(Configuration.Param.PARAM_RECONNECT_FAILED_COUNTER.toString(), 0);
                    }
                    FileSystemUtils.getConfiguration().save();
                } else {
                    /* reconnect okay, reset fail counter */
                    FileSystemUtils.getConfiguration().setProperty(Configuration.Param.PARAM_RECONNECT_FAILED_COUNTER.toString(), 0);
                    FileSystemUtils.getConfiguration().setProperty(Configuration.Param.PARAM_RECONNECT_OKAY.toString(), true);
                    FileSystemUtils.getConfiguration().save();
                }

            }
        }
        return ret;
    }

    /** reset ipblocked links */
    private static void resetAllLinks() {
        ArrayList<FilePackage> packages = EventUtils.getController().getPackages();
        /* reset hoster ipblock waittimes */
        DownloadWatchDog.getInstance().resetIPBlockWaittime(null);
        synchronized (packages) {
            for (FilePackage fp : packages) {
                for (DownloadLink nextDownloadLink : fp.getDownloadLinkList()) {
                    if (nextDownloadLink.getPlugin() != null && nextDownloadLink.getLinkStatus().hasStatus(LinkStatus.ERROR_IP_BLOCKED)) {
                        nextDownloadLink.getLinkStatus().setStatus(LinkStatus.TODO);
                        nextDownloadLink.getLinkStatus().resetWaitTime();
                    }
                }
            }
        }
    }

    /**
     * do it will start reconnectrequest even if user disabled autoreconnect
     */
    public static boolean waitForNewIP(long i, boolean doit) {
        if (FileSystemUtils.getConfiguration().getBooleanProperty(Configuration.Param.PARAM_ALLOW_RECONNECT.toString(), true) == false && doit == false) { return false; }
        setReconnectRequested(true);
        final ProgressController progress = new ProgressController(BiancaL.LF("gui.reconnect.progress.status", "Reconnect running: %s m:s", "0:00s"), 2);
        if (i > 0) {
            i += System.currentTimeMillis();
        }
        progress.setStatus(1);
        final long startTime = System.currentTimeMillis();
        boolean ret;
        Thread timer = new Thread() {
            @Override
            public void run() {
                while (true) {
                    progress.setStatusText(BiancaL.LF("gui.reconnect.progress.status2", "Reconnect running: %s", FormatterUtils.formatSeconds((System.currentTimeMillis() - startTime) / 1000)));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                }

            }
        };
        timer.start();
        while (!(ret = Reconnecter.doReconnectIfRequested(true)) && (System.currentTimeMillis() < i || i <= 0)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                ret = false;
                break;
            }
        }
        timer.interrupt();

        if (!ret) {
            progress.setColor(Color.RED);
            progress.setStatusText(BiancaL.L("gui.reconnect.progress.status.failed", "Reconnect failed"));
        } else {
            progress.setStatusText(BiancaL.L("gui.reconnect.progress.status.success", "Reconnect successfull"));
        }
        setReconnectRequested(false);
        progress.doFinalize(4000);
        return ret;
    }

    public static boolean doManualReconnect() {
        boolean restartDownloads = DownloadWatchDog.getInstance().stopDownloads();
        boolean success = Reconnecter.waitForNewIP(1, true);
        if (restartDownloads) {
            DownloadWatchDog.getInstance().startDownloads();
        }
        return success;
    }

    /**
     * @param reconnectRequested
     *            the RECONNECT_REQUESTED to set
     */
    public static void setReconnectRequested(boolean reconnectRequested) {
        Reconnecter.RECONNECT_REQUESTED = reconnectRequested;
    }

    /**
     * @return the RECONNECT_REQUESTED
     */
    public static boolean isReconnectRequested() {
        return RECONNECT_REQUESTED;
    }

}
