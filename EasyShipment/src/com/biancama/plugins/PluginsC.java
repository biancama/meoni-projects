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
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.biancama.HostPluginWrapper;
import com.biancama.controlling.DistributeData;
import com.biancama.controlling.ProgressController;
import com.biancama.events.ControlEvent;
import com.biancama.gui.UserIO;
import com.biancama.gui.easyShipment.plugins.PluginWrapper;
import com.biancama.gui.swing.actions.MenuAction;
import com.biancama.log.BiancaLogger;
import com.biancama.parser.Regex;
import com.biancama.utils.EventUtils;
import com.biancama.utils.FileSystemUtils;
import com.biancama.utils.FlagsUtils;
import com.biancama.utils.HashUtils;
import com.biancama.utils.URLUtils;
import com.biancama.utils.WebUpdate;
import com.biancama.utils.locale.BiancaL;

/**
 * Dies ist die Oberklasse für alle Plugins, die Containerdateien nutzen
 * können
 * 
 * @author astaldo/JD-Team
 */

public abstract class PluginsC extends Plugin {

    public PluginsC(PluginWrapper wrapper) {
        super(wrapper);
        // TODO Auto-generated constructor stub
    }

    private static HashMap<String, ArrayList<DownloadLink>> CONTAINER = new HashMap<String, ArrayList<DownloadLink>>();

    private static HashMap<String, ArrayList<String>> CONTAINERLINKS = new HashMap<String, ArrayList<String>>();

    private static HashMap<String, PluginsC> PLUGINS = new HashMap<String, PluginsC>();

    private static final int STATUS_NOTEXTRACTED = 0;

    private static final int STATUS_ERROR_EXTRACTING = 1;

    protected ArrayList<DownloadLink> cls = new ArrayList<DownloadLink>();

    private ContainerStatus containerStatus = null;

    protected ArrayList<String> dlU;

    protected String md5;
    protected byte[] k;
    protected ProgressController progress;

    private final int status = STATUS_NOTEXTRACTED;

    public abstract ContainerStatus callDecryption(File file);

    // @Override
    @Override
    public synchronized boolean canHandle(String data) {
        if (data == null) { return false; }
        String match = new Regex(data, this.getSupportedLinks()).getMatch(-1);

        return match != null && match.equalsIgnoreCase(data);
    }

    public String createContainerString(ArrayList<DownloadLink> downloadLinks) {
        return null;
    }

    // @Override
    @Override
    public ArrayList<MenuAction> createMenuitems() {
        return null;
    }

    /**
     * geht die containedLinks liste durch und decrypted alle links die darin
     * sind.
     */
    private void decryptLinkProtectorLinks() {
        ArrayList<DownloadLink> tmpDlink = new ArrayList<DownloadLink>();
        ArrayList<String> tmpURL = new ArrayList<String>();

        int i = 0;
        int c = 0;

        progress.addToMax(dlU.size());
        for (String string : dlU) {
            progress.increase(1);
            progress.setStatusText(BiancaL.LF("plugins.container.decrypt", "Decrypt link %s", i));

            DistributeData distributeData = new DistributeData(string);
            ArrayList<DownloadLink> links = distributeData.findLinks();

            DownloadLink srcLink = cls.get(i);
            Iterator<DownloadLink> it = links.iterator();
            progress.addToMax(links.size());

            while (it.hasNext()) {
                progress.increase(1);
                DownloadLink next = it.next();
                tmpDlink.add(next);
                tmpURL.add(next.getDownloadURL());

                next.setContainerFile(srcLink.getContainerFile());
                next.setContainerIndex(c++);
                next.setName(srcLink.getName());

                if (next.getDownloadSize() < 10) {
                    next.setDownloadSize(srcLink.getDownloadSize());
                }

                next.getSourcePluginPasswordList().addAll(srcLink.getSourcePluginPasswordList());
                String comment = "";
                if (srcLink.getSourcePluginComment() != null) {
                    comment += srcLink.getSourcePluginComment();
                }
                if (next.getSourcePluginComment() != null) {
                    if (comment.length() == 0) {
                        comment += "->" + next.getSourcePluginComment();
                    } else {
                        comment += next.getSourcePluginComment();
                    }
                }
                next.setSourcePluginComment(comment);
                next.setLoadedPluginForContainer(this);
                next.setFilePackage(srcLink.getFilePackage());
                next.setUrlDownload(null);
                next.setLinkType(DownloadLink.LINKTYPE_CONTAINER);

            }
            i++;
        }
        cls = tmpDlink;
        dlU = tmpURL;
        // logger.info("downloadLinksURL: "+downloadLinksURL);
    }

    /**
     * Erstellt eine Kopie des Containers im Homedir.
     */
    public synchronized void doDecryption(String parameter) {
        logger.info("DO STEP");
        String file = parameter;
        if (status == STATUS_ERROR_EXTRACTING) {
            logger.severe("Expired JD Version. Could not extract links");
            return;
        }
        if (file == null) {
            logger.severe("Containerfile == null");
            return;
        }
        File f = URLUtils.getResourceFile(file);
        if (md5 == null) {
            md5 = HashUtils.getMD5(f);
        }

        String extension = FileSystemUtils.getFileExtension(f);
        if (f.exists()) {
            File res = URLUtils.getResourceFile("container/" + md5 + "." + extension, true);
            if (!res.exists()) {
                FileSystemUtils.copyFile(f, res);
            }
            if (!res.exists()) {
                logger.severe("Could not copy file to homedir");
            }
            containerStatus = callDecryption(res);
        }
        return;
    }

    public abstract String[] encrypt(String plain);

    /**
     * Diese Methode liefert eine URL zurück, von der aus der Download
     * gestartet werden kann
     * 
     * @param downloadLink
     *            Der DownloadLink, dessen URL zurückgegeben werden soll
     * @return Die URL als String
     */
    public synchronized String extractDownloadURL(DownloadLink downloadLink) {
        // logger.info("EXTRACT " + downloadLink);
        if (dlU == null) {
            initContainer(downloadLink.getContainerFile(), downloadLink.getGenericProperty("k", new byte[] {}));
        }
        checkWorkaround(downloadLink);
        if (dlU == null || dlU.size() <= downloadLink.getContainerIndex()) { return null; }
        downloadLink.setProperty("k", k);
        return dlU.get(downloadLink.getContainerIndex());
    }

    /**
     * workaround and cortrection of a dlc bug
     * 
     * @param downloadLink
     */
    private void checkWorkaround(DownloadLink downloadLink) {
        ArrayList<DownloadLink> links = EventUtils.getDownloadController().getAllDownloadLinks();
        ArrayList<DownloadLink> failed = new ArrayList<DownloadLink>();
        int biggestIndex = 0;
        for (DownloadLink l : links) {
            if (l.getContainerFile() != null && l.getContainerFile().equalsIgnoreCase(downloadLink.getContainerFile())) {
                failed.add(l);
                biggestIndex = Math.max(biggestIndex, l.getContainerIndex());
            }
        }

        if (biggestIndex >= dlU.size()) {
            ArrayList<DownloadLink> rename = new ArrayList<DownloadLink>();
            System.err.println("DLC missmatch found");
            String ren = "";
            for (DownloadLink l : failed) {
                if (new File(l.getFileOutput()).exists() && l.getLinkStatus().hasStatus(LinkStatus.FINISHED)) {
                    rename.add(l);
                    ren += l.getFileOutput() + "<br>";
                }
            }
            if (FlagsUtils.hasAllFlags(UserIO.getInstance().requestConfirmDialog(UserIO.NO_COUNTDOWN | UserIO.STYLE_HTML, "DLC Missmatch", "<b>JD discovered an error while downloading DLC links.</b> <br>The following files may have errors:<br>" + ren + "<br><u> Do you want JD to try to correct them?</u>"), UserIO.RETURN_OK)) {
                int ffailed = 0;
                ren = "";
                for (DownloadLink l : rename) {
                    String name = l.getName();
                    String filename = new File(l.getFileOutput()).getName();
                    l.setUrlDownload(dlU.get(l.getContainerIndex() / 2));
                    if (l.isAvailable()) {

                        String newName = l.getName();

                        if (!name.equals(newName)) {
                            if (FlagsUtils.hasAllFlags(UserIO.getInstance().requestConfirmDialog(UserIO.NO_COUNTDOWN | UserIO.STYLE_HTML, "Rename file", "<b>Filename missmatch</b> <br>This file seems to have the wrong name:" + filename + "<br><u> Rename it to " + newName + "?</u>"), UserIO.RETURN_OK)) {
                                File newFile = new File(new File(l.getFileOutput()).getParent() + "/restore/" + newName);
                                newFile.mkdirs();
                                if (newFile.exists()) {
                                    ffailed++;
                                    ren += l.getFileOutput() + " -> RENAME TO " + newFile + " FAILED<br>";
                                } else {

                                    if (new File(l.getFileOutput()).renameTo(newFile)) {
                                        ren += l.getFileOutput() + " -> " + newFile + "<br>";
                                    } else {
                                        ren += l.getFileOutput() + " -> RENAME TO " + newFile + " FAILED<br>";
                                    }
                                }

                            }

                        }

                    }
                    l.setUrlDownload(null);
                }
                FlagsUtils.hasAllFlags(UserIO.getInstance().requestConfirmDialog(UserIO.NO_COUNTDOWN | UserIO.STYLE_HTML, "DLC Correction", "<b>Correction result:</b> <br>" + ren + ""), UserIO.RETURN_OK);

                ren = null;
            }
            for (DownloadLink l : failed) {
                l.setContainerIndex(l.getContainerIndex() / 2);
            }
        }

    }

    /**
     * Findet anhand des Hostnamens ein passendes Plugiln
     * 
     * @param data
     *            Hostname
     * @return Das gefundene Plugin oder null
     */
    protected PluginForHost findHostPlugin(String data) {
        for (HostPluginWrapper pHost : HostPluginWrapper.getHostWrapper()) {
            if (pHost.canHandle(data)) { return pHost.getPlugin(); }
        }
        return null;
    }

    /**
     * Liefert alle in der Containerdatei enthaltenen Dateien als DownloadLinks
     * zurück.
     * 
     * @param filename
     *            Die Containerdatei
     * @return Ein ArrayList mit DownloadLinks
     */
    public ArrayList<DownloadLink> getContainedDownloadlinks() {
        return cls == null ? new ArrayList<DownloadLink>() : cls;
    }

    /**
     * Gibt das passende plugin für diesen container zurück. falls schon eins
     * exestiert wird dieses zurückgegeben.
     * 
     * @param containerFile
     * @return
     */
    public PluginsC getPlugin(String containerFile) {
        if (PLUGINS.containsKey(containerFile)) { return PLUGINS.get(containerFile); }
        try {
            PluginsC newPlugin = this.getClass().newInstance();
            PLUGINS.put(containerFile, newPlugin);
            return newPlugin;
        } catch (InstantiationException e) {
            BiancaLogger.exception(e);
        } catch (IllegalAccessException e) {
            BiancaLogger.exception(e);
        }
        return null;
    }

    public synchronized void initContainer(String filename, byte[] bs) {

        File rel = URLUtils.getResourceFile(filename);
        File ab = new File(filename);
        String md;

        if (!rel.exists() && ab.exists()) {
            String extension = FileSystemUtils.getFileExtension(ab);
            md = HashUtils.getMD5(ab);
            File newFile = URLUtils.getResourceFile("container/" + md + "." + extension, true);
            if (!newFile.exists()) {
                FileSystemUtils.copyFile(ab, newFile);
            }
            filename = "container/" + md + "." + extension;
        }

        if (filename == null) { return; }
        if (CONTAINER.containsKey(filename) && CONTAINER.get(filename) != null && CONTAINER.get(filename).size() > 0) {
            logger.info("Cached " + filename);
            cls = CONTAINER.get(filename);
            if (cls != null) {
                Iterator<DownloadLink> it = cls.iterator();
                while (it.hasNext()) {
                    it.next().setLinkType(DownloadLink.LINKTYPE_CONTAINER);
                }
            }

            dlU = CONTAINERLINKS.get(filename);
            return;
        }

        if (cls == null || cls.size() == 0) {
            logger.info("Init Container");
            fireControlEvent(ControlEvent.CONTROL_PLUGIN_ACTIVE, this);
            if (progress != null) {
                progress.doFinalize();
            }
            progress = new ProgressController(BiancaL.L("plugins.container.open", "Open Container"), 10);
            progress.increase(1);
            if (bs != null) {
                k = bs;
            }
            doDecryption(filename);
            progress.increase(1);

            logger.info(filename + " Parse");
            if (cls != null && dlU != null) {
                progress.setStatusText(BiancaL.LF("plugins.container.found", "Prozess %s links", cls.size()));
                decryptLinkProtectorLinks();
                progress.setStatusText(BiancaL.LF("plugins.container.exit", "Finished. Found %s links", cls.size()));
                Iterator<DownloadLink> it = cls.iterator();
                while (it.hasNext()) {
                    it.next().setLinkType(DownloadLink.LINKTYPE_CONTAINER);
                }
                progress.increase(1);
            }
            if (cls == null || cls.size() == 0) {
                CONTAINER.put(filename, null);
                CONTAINERLINKS.put(filename, null);
            } else {
                CONTAINER.put(filename, cls);
                CONTAINERLINKS.put(filename, dlU);
            }
            if (this.containerStatus == null) {
                progress.setColor(Color.RED);
                progress.setStatusText(BiancaL.LF("plugins.container.exit.error", "Container error: %s", "Container not found!"));
                progress.doFinalize(500);
            } else if (!this.containerStatus.hasStatus(ContainerStatus.STATUS_FINISHED)) {
                progress.setColor(Color.RED);
                progress.setStatusText(BiancaL.LF("plugins.container.exit.error", "Container error: %s", containerStatus.getStatusText()));
                progress.doFinalize(5000);
                WebUpdate.doUpdateCheck(false);
            } else {
                progress.doFinalize();
            }
            fireControlEvent(ControlEvent.CONTROL_PLUGIN_INACTIVE, this);

        }
    }

    public void initContainer(String absolutePath) {
        this.initContainer(absolutePath, null);

    }

}
