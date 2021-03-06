//    jDownloader - Downloadmanager
//    Copyright (C) 2009  JD-Team support@jdownloader.org
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

import java.util.logging.Logger;

import com.biancama.config.ConfigContainer;
import com.biancama.config.Configuration;
import com.biancama.config.SubConfiguration;
import com.biancama.controlling.ProgressController;
import com.biancama.gui.UserIF;
import com.biancama.log.BiancaLogger;
import com.biancama.utils.FileSystemUtils;
import com.biancama.utils.gui.IPAddress;
import com.biancama.utils.locale.BiancaL;
import com.biancama.utils.router.IPCheck;

public abstract class ReconnectMethod {

    protected static Logger logger = BiancaLogger.getLogger();

    public static final String PARAM_RECONNECT_TYPE = "RECONNECT_TYPE";

    public static final int LIVEHEADER = 0;
    public static final int EXTERN = 1;
    public static final int BATCH = 2;
    public static final int CLR = 3;
    /* Integer Property: 0=LiveHeader, 1=Extern, 2=Batch, 3=CLR */

    public static final String PARAM_IPCHECKWAITTIME = "RECONNECT_IPCHECKWAITTIME2";

    public static final String PARAM_RETRIES = "RECONNECT_RETRIES2";

    public static final String PARAM_WAITFORIPCHANGE = "RECONNECT_WAITFORIPCHANGE2";

    protected ConfigContainer config = null;

    protected ReconnectMethod() {
    }

    public final boolean doReconnect() {
        if (SubConfiguration.getConfig("DOWNLOAD").getBooleanProperty(Configuration.Param.PARAM_GLOBAL_IP_DISABLE.toString(), false)) {
            /*
             * disabled ipcheck, let run 1 reconnect round and guess it has been
             * successful
             */
            doReconnectInteral(1);
            Reconnecter.setCurrentIP("na");
            return true;
        }
        int maxretries = FileSystemUtils.getConfiguration().getIntegerProperty(PARAM_RETRIES, 5);
        boolean ret = false;
        int retry = 0;
        if (maxretries <= -1) {
            while (true) {
                if ((ret = doReconnectInteral(++retry)) == true) {
                    break;
                }
            }
        } else {
            for (retry = 0; retry <= maxretries; retry++) {
                if ((ret = doReconnectInteral(retry + 1)) == true) {
                    break;
                }
            }
        }
        return ret;
    }

    private String getIP() {
        if (SubConfiguration.getConfig("DOWNLOAD").getBooleanProperty(Configuration.Param.PARAM_GLOBAL_IP_DISABLE.toString(), false)) { return "na"; }
        return IPCheck.getIPAddress();
    }

    public final boolean doReconnectInteral(int retry) {
        ProgressController progress = new ProgressController(this.toString(), 10);
        progress.setStatusText(BiancaL.L("reconnect.progress.1_retries", "Reconnect #") + retry);
        try {
            int waittime = FileSystemUtils.getConfiguration().getIntegerProperty(PARAM_IPCHECKWAITTIME, 5);
            int waitForIp = FileSystemUtils.getConfiguration().getIntegerProperty(PARAM_WAITFORIPCHANGE, 30);

            logger.info("Starting " + this.toString() + " #" + retry);
            String preIp = getIP();

            progress.increase(1);
            progress.setStatusText(BiancaL.L("reconnect.progress.2_oldIP", "Reconnect Old IP:") + preIp);
            if (!runCommands(progress)) {
                progress.doFinalize();
                logger.severe("An error occured while processing the reconnect ... Terminating");
                return false;
            }
            logger.finer("Initial Waittime: " + waittime + " seconds");
            try {
                Thread.sleep(waittime * 1000);
            } catch (InterruptedException e) {
            }
            String afterIP = getIP();
            progress.setStatusText(BiancaL.LF("reconnect.progress.3_ipcheck", "Reconnect New IP: %s / %s", afterIP, preIp));
            long endTime = System.currentTimeMillis() + waitForIp * 1000;
            logger.info("Wait " + waitForIp + " sec for new ip");
            while (System.currentTimeMillis() <= endTime && (afterIP.equals(preIp) || afterIP.equals("na"))) {
                logger.finer("IP before: " + preIp + " after: " + afterIP);
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                }
                afterIP = getIP();
                progress.setStatusText(BiancaL.LF("reconnect.progress.3_ipcheck", "Reconnect New IP: %s / %s", afterIP, preIp));
            }
            if (SubConfiguration.getConfig("DOWNLOAD").getBooleanProperty(Configuration.Param.PARAM_GLOBAL_IP_DISABLE.toString(), false)) {
                /* stop here when ipcheck is disabled */
                Reconnecter.setCurrentIP("na");
                return true;
            }
            logger.finer("IP before: " + preIp + " after: " + afterIP);
            if (afterIP.equals("na") && !afterIP.equals(preIp)) {
                logger.warning("JD could disconnect your router, but could not connect afterwards. Try to rise the option 'Wait until first IP Check'");
                endTime = System.currentTimeMillis() + 120 * 1000;
                while (System.currentTimeMillis() <= endTime && (afterIP.equals(preIp) || afterIP.equals("na"))) {
                    logger.finer("IP before: " + preIp + " after: " + afterIP);
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                    }
                    afterIP = getIP();
                    progress.setStatusText(BiancaL.LF("reconnect.progress.3_ipcheck", "Reconnect New IP: %s / %s", preIp, afterIP));
                }
            }

            if (!afterIP.equals(preIp) && !afterIP.equals("na")) {
                logger.finer("IP before: " + preIp + " after: " + afterIP);
                /* Reconnect scheint erfolgreich gewesen zu sein */
                /* nun IP validieren */
                if (!IPAddress.validateIP(afterIP)) {
                    logger.warning("IP " + afterIP + " was filtered by mask: " + SubConfiguration.getConfig("DOWNLOAD").getStringProperty(Configuration.Param.PARAM_GLOBAL_IP_MASK.toString(), "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).)" + "{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b"));
                    UserIF.getInstance().displayMiniWarning(BiancaL.L("reconnect.ipfiltered.warning.title", "Wrong IP!"), BiancaL.LF("reconnect.ipfiltered.warning.short", "Die IP %s wurde als nicht erlaubt identifiziert", afterIP));
                    Reconnecter.setCurrentIP("na");
                    return false;
                } else {
                    progress.doFinalize();
                    Reconnecter.setCurrentIP(afterIP);
                    return true;
                }
            }
            return false;
        } finally {
            progress.doFinalize();
        }
    }

    protected abstract boolean runCommands(ProgressController progress);

    protected abstract void initConfig();

    public final ConfigContainer getConfig() {
        if (config == null) {
            config = new ConfigContainer();
            initConfig();
        }
        return config;
    }

}
