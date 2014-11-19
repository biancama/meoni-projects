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

package com.biancama.plugins;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.biancama.config.ConfigGroup;
import com.biancama.config.SubConfiguration;
import com.biancama.gui.UserIF;
import com.biancama.gui.easyShipment.plugins.PluginWrapper;
import com.biancama.gui.swing.actions.ActionController;
import com.biancama.gui.swing.actions.MenuAction;
import com.biancama.gui.swing.actions.ToolBarAction.Types;
import com.biancama.gui.swing.components.linkbutton.BiancaLink;
import com.biancama.http.Browser;
import com.biancama.log.BiancaLogger;
import com.biancama.parser.Regex;
import com.biancama.plugins.DownloadLink.AvailableStatus;
import com.biancama.utils.FormatterUtils;
import com.biancama.utils.URLUtils;
import com.biancama.utils.encoding.Encoding;
import com.biancama.utils.gui.BiancaImage;
import com.biancama.utils.locale.BiancaL;

/**
 * Dies ist die Oberklasse für alle Plugins, die von einem Anbieter Dateien
 * herunterladen können
 * 
 * @author astaldo
 */
public abstract class PluginForHost extends Plugin {

    public PluginForHost(PluginWrapper wrapper) {
        super(wrapper);
        config.setIcon(getHosterIcon());
    }

    @Override
    public String getVersion() {
        return wrapper.getVersion();
    }

    private static final String AGB_CHECKED = "AGB_CHECKED";
    private static final String CONFIGNAME = "pluginsForHost";
    private static int currentConnections = 0;

    public static final String PARAM_MAX_RETRIES = "MAX_RETRIES";
    protected DownloadInterface dl = null;
    private int maxConnections = 50;

    private static HashMap<String, Long> LAST_CONNECTION_TIME = new HashMap<String, Long>();
    private static HashMap<String, Long> LAST_STARTED_TIME = new HashMap<String, Long>();
    private Long WAIT_BETWEEN_STARTS = 0L;

    private boolean enablePremium = false;

    private boolean accountWithoutUsername = false;

    private String premiumurl = null;

    private ImageIcon hosterIcon;
    private MenuAction premiumAction;

    public boolean checkLinks(DownloadLink[] urls) {
        return false;
    }

    @Override
    public void clean() {
        dl = null;
        super.clean();
    }

    protected int waitForFreeConnection(DownloadLink downloadLink) throws InterruptedException {
        int free;
        while ((free = getMaxConnections() - getCurrentConnections()) <= 0) {
            Thread.sleep(1000);
            downloadLink.getLinkStatus().setStatusText(BiancaL.LF("download.system.waitForconnection", "Cur. %s/%s connections...waiting", getCurrentConnections() + "", getMaxConnections() + ""));
            downloadLink.requestGuiUpdate();
        }
        return free;
    }

    protected void setBrowserExclusive() {
        br.setCookiesExclusive(true);
        br.clearCookies(getHost());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getID() == 1) {
            UserIF.getInstance().requestPanel(UserIF.Panels.CONFIGPANEL, config);

            return;
        }
        if (e.getID() == 2) {

            UserIF.getInstance().requestPanel(UserIF.Panels.PREMIUMCONFIG, null);
            ActionController.getToolBarAction("action.premiumview.addacc").actionPerformed(new ActionEvent(this, 0, "addaccount"));
            return;
        }

        if (e.getID() == 3) {

            UserIF.getInstance().requestPanel(UserIF.Panels.PREMIUMCONFIG, null);

            try {
                BiancaLink.openURL(getBuyPremiumUrl());
            } catch (Exception ex) {
            }

            return;
        }
    }

    public boolean getAccountwithoutUsername() {
        return accountWithoutUsername;
    }

    public void setAccountwithoutUsername(boolean b) {
        accountWithoutUsername = b;
    }

    @Override
    public ArrayList<MenuAction> createMenuitems() {

        if (!enablePremium) { return null; }
        ArrayList<MenuAction> menuList = new ArrayList<MenuAction>();
        MenuAction m;

        if (config != null && config.getEntries().size() > 0) {
            m = new MenuAction("plugins.configs", 1);
            m.setActionListener(this);
            menuList.add(m);
            menuList.add(new MenuAction(Types.SEPARATOR));
        }

        if (config != null) {
            config.setGroup(new ConfigGroup(getHost(), getHosterIcon()));
        }

        menuList.add(m = new MenuAction("plugins.menu.noaccounts", 2));
        m.setActionListener(this);
        menuList.add(m = new MenuAction("plugins.menu.buyaccount", 3));
        m.setActionListener(this);

        return menuList;

    }

    public abstract String getAGBLink();

    protected void enablePremium() {
        enablePremium(null);
    }

    protected void enablePremium(String url) {
        premiumurl = url;
        enablePremium = true;
    }

    public static synchronized int getCurrentConnections() {
        return currentConnections;
    }

    /**
     * Hier werden Treffer für Downloadlinks dieses Anbieters in diesem Text
     * gesucht. Gefundene Links werden dann in einem ArrayList zurückgeliefert
     * 
     * @param data
     *            Ein Text mit beliebig vielen Downloadlinks dieses Anbieters
     * @return Ein ArrayList mit den gefundenen Downloadlinks
     */
    public ArrayList<DownloadLink> getDownloadLinks(String data, FilePackage fp) {

        ArrayList<DownloadLink> links = null;
        String[] hits = new Regex(data, getSupportedLinks()).getColumn(-1);
        if (hits != null && hits.length > 0) {
            links = new ArrayList<DownloadLink>();
            for (String file : hits) {
                while (file.charAt(0) == '"') {
                    file = file.substring(1);
                }
                while (file.charAt(file.length() - 1) == '"') {
                    file = file.substring(0, file.length() - 1);
                }

                try {
                    // Zwecks Multidownload braucht jeder Link seine eigene
                    // Plugininstanz
                    PluginForHost plg = (PluginForHost) wrapper.getNewPluginInstance();
                    DownloadLink link = new DownloadLink(plg, file.substring(file.lastIndexOf("/") + 1, file.length()), getHost(), file, true);
                    links.add(link);
                    if (fp != null) {
                        link.setFilePackage(fp);
                    }

                } catch (IllegalArgumentException e) {
                    BiancaLogger.exception(e);
                } catch (SecurityException e) {
                    BiancaLogger.exception(e);
                }
            }
        }
        return links;
    }

    /** überschreiben falls die downloadurl erst rekonstruiert werden muss */
    public void correctDownloadLink(DownloadLink link) throws Exception {
    }

    /**
     * Holt Informationen zu einem Link. z.B. dateigröße, Dateiname,
     * verfügbarkeit etc.
     * 
     * @param parameter
     * @return true/false je nach dem ob die Datei noch online ist (verfügbar)
     * @throws IOException
     */
    public abstract AvailableStatus requestFileInformation(DownloadLink parameter) throws Exception;

    /**
     * Gibt einen String mit den Dateiinformationen zurück. Die Defaultfunktion
     * gibt nur den dateinamen zurück. Allerdings Sollte diese Funktion
     * überschrieben werden. So kann ein Plugin zusatzinfos zu seinen Links
     * anzeigen (Nach dem aufruf von getFileInformation()
     * 
     * @param downloadLink
     * @return
     */
    public String getFileInformationString(DownloadLink downloadLink) {
        return downloadLink.getName() + " (" + FormatterUtils.formatReadable(downloadLink.getDownloadSize()) + ")";
    }

    public synchronized int getFreeConnections() {
        return Math.max(1, getMaxConnections() - currentConnections);
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public int getMaxRetries() {
        return SubConfiguration.getConfig("DOWNLOAD").getIntegerProperty(PARAM_MAX_RETRIES, 3);
    }

    public int getMaxSimultanFreeDownloadNum() {
        return 1;
    }

    public int getMaxSimultanPremiumDownloadNum() {
        return -1;
    }

    public synchronized long getLastTimeStarted() {
        if (!LAST_STARTED_TIME.containsKey(getHost())) { return 0; }
        return Math.max(0, (LAST_STARTED_TIME.get(getHost())));
    }

    public synchronized void putLastTimeStarted(long time) {
        LAST_STARTED_TIME.put(getHost(), time);
    }

    public synchronized long getLastConnectionTime() {
        if (!LAST_CONNECTION_TIME.containsKey(getHost())) { return 0; }
        return Math.max(0, (LAST_CONNECTION_TIME.get(getHost())));
    }

    public synchronized void putLastConnectionTime(long time) {
        LAST_CONNECTION_TIME.put(getHost(), time);
    }

    public abstract void handleFree(DownloadLink link) throws Exception;

    public void handle(DownloadLink downloadLink) throws Exception {
        downloadLink.getTransferStatus().usePremium(false);
        downloadLink.getTransferStatus().setResumeSupport(false);
        try {
            while (waitForNextStartAllowed(downloadLink)) {
            }
        } catch (InterruptedException e) {
            return;
        }
        putLastTimeStarted(System.currentTimeMillis());
        if (!isAGBChecked()) {
            logger.severe("AGB not signed : " + this.getWrapper().getID());
            downloadLink.getLinkStatus().addStatus(LinkStatus.ERROR_AGB_NOT_SIGNED);
            return;
        }

        try {
            handleFree(downloadLink);
            if (dl != null && dl.getConnection() != null) {
                try {
                    dl.getConnection().disconnect();
                } catch (Exception e) {
                }
            }
        } catch (PluginException e) {
            e.fillLinkStatus(downloadLink.getLinkStatus());
            if (e.getLinkStatus() == LinkStatus.ERROR_PLUGIN_DEFECT) {
                logger.info(BiancaLogger.getStackTrace(e));
            }
            logger.info(downloadLink.getLinkStatus().getLongErrorMessage());
        }

        return;
    }

    public boolean isAGBChecked() {
        if (!getPluginConfig().hasProperty(AGB_CHECKED)) {
            // this is just so complicated to preserv compatibility
            getPluginConfig().setProperty(AGB_CHECKED, SubConfiguration.getConfig(CONFIGNAME).getBooleanProperty("AGBS_CHECKED_" + getPluginID(), false) || SubConfiguration.getConfig(CONFIGNAME).getBooleanProperty("AGB_CHECKED_" + getHost(), false));
            getPluginConfig().save();
        }
        return getPluginConfig().getBooleanProperty(AGB_CHECKED, false);
    }

    /**
     * Stellt das Plugin in den Ausgangszustand zurück (variablen intialisieren
     * etc)
     */
    public abstract void reset();

    public abstract void resetDownloadlink(DownloadLink link);

    public void resetPluginGlobals() {
    }

    public void setAGBChecked(boolean value) {
        getPluginConfig().setProperty(AGB_CHECKED, value);
        getPluginConfig().save();
    }

    public static synchronized void setCurrentConnections(int CurrentConnections) {
        currentConnections = CurrentConnections;
    }

    public int getTimegapBetweenConnections() {
        return 50;
    }

    public void setStartIntervall(long interval) {
        WAIT_BETWEEN_STARTS = interval;
    }

    public boolean waitForNextStartAllowed(DownloadLink downloadLink) throws InterruptedException {
        long time = Math.max(0, WAIT_BETWEEN_STARTS - (System.currentTimeMillis() - getLastTimeStarted()));
        if (time > 0) {
            try {
                sleep(time, downloadLink);
            } catch (PluginException e) {

                // downloadLink.getLinkStatus().setStatusText(null);
                throw new InterruptedException();
            }
            // downloadLink.getLinkStatus().setStatusText(null);
            return true;
        } else {
            // downloadLink.getLinkStatus().setStatusText(null);
            return false;
        }
    }

    public boolean waitForNextConnectionAllowed() throws InterruptedException {
        long time = Math.max(0, getTimegapBetweenConnections() - (System.currentTimeMillis() - getLastConnectionTime()));
        if (time > 0) {
            Thread.sleep(time);
            return true;
        } else {
            return false;
        }
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public void sleep(long i, DownloadLink downloadLink) throws PluginException {
        sleep(i, downloadLink, "");
    }

    public void sleep(long i, DownloadLink downloadLink, String message) throws PluginException {
        try {
            while (i > 0 && downloadLink.getDownloadLinkController() != null && !downloadLink.getDownloadLinkController().isAborted()) {
                i -= 1000;
                downloadLink.getLinkStatus().setStatusText(message + BiancaL.LF("gui.download.waittime_status2", "Wait %s", FormatterUtils.formatSeconds(i / 1000)));
                downloadLink.requestGuiUpdate();
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            throw new PluginException(LinkStatus.TODO);
        }
        downloadLink.getLinkStatus().setStatusText(null);
    }

    public boolean isAborted(DownloadLink downloadLink) {
        return (downloadLink.getDownloadLinkController() != null && downloadLink.getDownloadLinkController().isAborted());
    }

    public Browser getBrowser() {
        return br;
    }

    public void setDownloadInterface(DownloadInterface dl2) {
        this.dl = dl2;
    }

    /**
     * Gibt die Url zurück, unter welcher ein PremiumAccount gekauft werden
     * kann
     * 
     * @return
     */
    public String getBuyPremiumUrl() {
        if (premiumurl != null) { return "http://jdownloader.org/r.php?u=" + Encoding.urlEncode(premiumurl); }
        return premiumurl;
    }

    public boolean isPremiumEnabled() {
        return enablePremium;
    }

    /**
     * returns hosterspecific infos. for example the downloadserver
     * 
     * @return
     */
    public String getSessionInfo() {
        return "";
    }

    public ImageIcon getHosterIcon() {
        if (hosterIcon == null) {
            hosterIcon = initHosterIcon();
        }
        return hosterIcon;
    }

    private final ImageIcon initHosterIcon() {
        Image image = BiancaImage.getImage("hosterlogos/" + getHost());
        if (image == null) {
            image = createDefaultIcon();
        }
        if (image != null) { return new ImageIcon(image); }
        return null;
    }

    private final String cleanString(String host) {
        return host.replaceAll("[a-z0-9\\-\\.]", "");
    }

    /**
     * Creates a dummyHosterIcon
     */
    private final Image createDefaultIcon() {
        int w = 16;
        int h = 16;
        int size = 9;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        final BufferedImage image = gc.createCompatibleImage(w, h, Transparency.BITMASK);
        Graphics2D g = image.createGraphics();
        String host = getHost();
        String dummy = cleanString(host);
        if (dummy.length() < 2) {
            dummy = cleanString(getClass().getSimpleName());
        }
        if (dummy.length() < 2) {
            dummy = host.toUpperCase();
        }
        if (dummy.length() > 2) {
            dummy = dummy.substring(0, 2);
        }
        g.setFont(new Font("Arial", Font.BOLD, size));
        int ww = g.getFontMetrics().stringWidth(dummy);
        // g.setColor(Color.BLACK);
        // g.drawRect(0, 0, w - 1, h - 1);

        g.setColor(Color.WHITE);
        g.fillRect(1, 1, w - 2, h - 2);
        g.setColor(Color.BLACK);
        g.drawString(dummy, (w - ww) / 2, 2 + size);

        g.dispose();
        try {
            File imageFile = URLUtils.getResourceFile("jd/img/hosterlogos/" + getHost() + ".png", true);
            ImageIO.write(image, "png", imageFile);
        } catch (Exception e) {
            BiancaLogger.exception(e);
        }
        return image;
    }

}
