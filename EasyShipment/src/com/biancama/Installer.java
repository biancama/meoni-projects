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

package com.biancama;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import com.biancama.config.Configuration;
import com.biancama.config.SubConfiguration;
import com.biancama.gui.UserIO;
import com.biancama.gui.easyShipment.util.nativeintegration.LocalBrowser;
import com.biancama.gui.swing.Factory;
import com.biancama.gui.swing.GuiRunnable;
import com.biancama.gui.swing.components.BrowseFile;
import com.biancama.gui.swing.dialog.AbstractDialog;
import com.biancama.gui.swing.dialog.ContainerDialog;
import com.biancama.log.BiancaLogger;
import com.biancama.utils.FileSystemUtils;
import com.biancama.utils.FlagsUtils;
import com.biancama.utils.OSDetector;
import com.biancama.utils.URLUtils;
import com.biancama.utils.gui.BiancaImage;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.locale.BiancaL;
import com.biancama.utils.locale.BiancaLocale;

/**
 * Der Installer erscheint nur beim ersten mal Starten der Webstartversion und
 * beim neuinstallieren der webstartversion der User kann Basiceinstellungen
 * festlegen
 * 
 * @author JD-Team
 */
public class Installer {


        private static final long serialVersionUID = 8764525546298642601L;

        private boolean aborted = false;

        private String countryCode;

        private File dlFolder = null;

        private BrowseFile br;
        private BrowseFile brPDF;

        // private boolean error;

        private String languageCode;

        public Installer() {
            countryCode = BiancaL.getCountryCodeByIP();
            if (countryCode != null) languageCode = countryCode.toLowerCase();

            SubConfiguration.getConfig(BiancaL.CONFIG).setProperty(BiancaL.LOCALE_PARAM_ID, null);

            showConfig();

            if (FileSystemUtils.getConfiguration().getStringProperty(Configuration.Param.PARAM_DOWNLOAD_DIRECTORY.toString()) == null) {
                BiancaLogger.getLogger().severe("downloaddir not set");
                this.aborted = true;
                return;
            }
            AbstractDialog.setDefaultDimension(new Dimension(550, 400));

           // askInstallFlashgot();
            // JDFileReg.registerFileExts();
            FileSystemUtils.getConfiguration().save();

            if (OSDetector.isWindows()) {
                String lng = BiancaL.getCountryCodeByIP();
                if (lng.equalsIgnoreCase("de") || lng.equalsIgnoreCase("us")) {
                    new GuiRunnable<Object>() {

                        @Override
                        public Object runSave() {
                            new KikinDialog();
                            return null;
                        }

                    }.waitForEDT();
                }
            }

            AbstractDialog.setDefaultDimension(null);
        }

        public static void askInstallFlashgot() {
            final SubConfiguration config = SubConfiguration.getConfig("FLASHGOT");
            if (config.getBooleanProperty("ASKED_TO_INSTALL_FLASHGOT", false)) return;
            int answer = new GuiRunnable<Integer>() {

                @Override
                public Integer runSave() {
                    JPanel c = new JPanel(new MigLayout("ins 10,wrap 1", "[grow,fill]", "[][][grow,fill]"));

                    JLabel lbl = new JLabel(BiancaL.L("installer.gui.message", "After Installation, JDownloader will update to the latest version."));

                    c.add(lbl, "pushx,growx,split 2");

                    Font f = lbl.getFont();
                    f = f.deriveFont(f.getStyle() ^ Font.BOLD);

                    lbl.setFont(f);
                    c.add(new JLabel(BiancaImage.getScaledImageIcon(BiancaImage.getImage("logo/jd_logo_54_54"), 32, 32)), "alignx right");
                    c.add(new JSeparator(), "pushx,growx,gapbottom 5");

                    c.add(lbl = new JLabel(BiancaL.L("installer.firefox.message", "Do you want to integrate JDownloader to Firefox?")), "growy,pushy");
                    c.add(lbl = new JLabel(BiancaImage.getImageIcon("flashgot_logo")), "growy,pushy");
                    c.add(lbl = new JLabel(BiancaL.L("installer.firefox.message.flashgot", "This installs the famous FlashGot Extension (flashgot.net).")), "growy,pushy");

                    lbl.setVerticalAlignment(SwingConstants.TOP);
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);

                    return new ContainerDialog(UserIO.NO_COUNTDOWN, BiancaL.L("installer.firefox.title", "Install firefox integration?"), c, null, null, null) {
                        private static final long serialVersionUID = -7983868276841947499L;

                        @Override
                        protected void packed() {
                            this.setSize(550, 400);
                        }

                        @Override
                        protected void setReturnValue(boolean b) {
                            config.setProperty("ASKED_TO_INSTALL_FLASHGOT", true);
                            config.save();
                        }
                    }.getReturnValue();
                }

            }.getReturnValue();
            if (FlagsUtils.hasAllFlags(answer, UserIO.RETURN_OK)) installFirefoxAddon();
        }

        private void showConfig() {
            new GuiRunnable<Object>() {

                private ContainerDialog dialog;

                @Override
                public Object runSave() {
                    String def = null;
                    if (languageCode != null) {
                        for (BiancaLocale id : BiancaL.getLocaleIDs()) {
                            if (id.getCountryCode() != null && id.getCountryCode().equalsIgnoreCase(languageCode)) {
                                def = languageCode;
                                break;
                            }
                        }
                        if (def == null) {
                            for (BiancaLocale id : BiancaL.getLocaleIDs()) {
                                if (id.getLanguageCode().equalsIgnoreCase(languageCode)) {
                                    def = languageCode;
                                    break;
                                }
                            }
                        }
                    }
                    if (def == null) def = "en";
                    BiancaLocale sel = SubConfiguration.getConfig(BiancaL.CONFIG).getGenericProperty(BiancaL.LOCALE_PARAM_ID, BiancaL.getInstance(def));

                    BiancaL.setLocale(sel);

                    JPanel p = getInstallerPanel();
                    JPanel content = new JPanel(new MigLayout("ins 0,wrap 1", "[grow,fill]"));
                    p.add(content);
                    content.add(Factory.createHeader(BiancaL.L("gui.config.gui.language", "Language"), BiancaTheme.II("gui.splash.languages", 24, 24)), "growx,pushx");
                    final JList list;
                    content.add(new JScrollPane(list = new JList(new AbstractListModel() {
                        private static final long serialVersionUID = -7645376943352687975L;
                        private ArrayList<BiancaLocale> ids;

                        private ArrayList<BiancaLocale> getIds() {
                            if (ids == null) ids = BiancaL.getLocaleIDs();

                            return ids;
                        }

                        public Object getElementAt(int index) {
                            return getIds().get(index);
                        }

                        public int getSize() {
                            return getIds().size();
                        }

                    })), "growx,pushx,gapleft 40,gapright 10");
                    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    // if (error) list.setEnabled(false);

                    list.setSelectedValue(sel, true);
                    list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                        public void valueChanged(ListSelectionEvent e) {
                            BiancaL.setConfigLocale((BiancaLocale) list.getSelectedValue());
                            BiancaL.setLocale(BiancaL.getConfigLocale());
                            SubConfiguration.getConfig(BiancaL.CONFIG).save();
                            dlFolder = br.getCurrentPath();
                            dialog.dispose();
                            showConfig();
                        }

                    });
                    content.add(Factory.createHeader(BiancaL.L("gui.config.general.downloaddirectory", "Saving directory"), BiancaTheme.II("gui.images.taskpanes.download", 24, 24)), " growx,pushx,gaptop 10");

                    content.add(br = new BrowseFile(), "growx,pushx,gapleft 40,gapright 10");
                    br.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    // if (error) br.setEnabled(false);
                    content.add(new JSeparator(), "growx,pushx,gaptop 5");
                    if (dlFolder != null) {
                        br.setCurrentPath(dlFolder);
                    } else {
                        if (OSDetector.isMac()) {
                            br.setCurrentPath(new File(System.getProperty("user.home") + "/EasyShipemnt"));
                        } else if (OSDetector.isWindows() && new File(System.getProperty("user.home") + "/EasyShipemnt").exists()) {
                            br.setCurrentPath(new File(System.getProperty("user.home") + "/EasyShipemnt"));
                        } else if (OSDetector.isWindows() && new File(System.getProperty("user.home") + "/EasyShipemnt").exists()) {
                            br.setCurrentPath(new File(System.getProperty("user.home") + "/EasyShipemnt"));
                        } else {
                            br.setCurrentPath(URLUtils.getResourceFile("EasyShipemnt"));
                        }
                    }
                        // Add the pdf downloader
                   content.add(Factory.createHeader(BiancaL.L("gui.config.general.downloadpdfdirectory", "Saving Document directory"), BiancaTheme.II("gui.images.taskpanes.download", 24, 24)), " growx,pushx,gaptop 10");

                    content.add(brPDF = new BrowseFile(), "growx,pushx,gapleft 40,gapright 10");
                    brPDF.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    // if (error) br.setEnabled(false);
                    content.add(new JSeparator(), "growx,pushx,gaptop 5");
                    if (dlFolder != null) {
                        brPDF.setCurrentPath(dlFolder);
                    } else {
                        if (OSDetector.isMac()) {
                            brPDF.setCurrentPath(new File(System.getProperty("user.home") + "/EasyShipemnt"));
                        } else if (OSDetector.isWindows() && new File(System.getProperty("user.home") + "/EasyShipemnt").exists()) {
                            brPDF.setCurrentPath(new File(System.getProperty("user.home") + "/EasyShipemnt"));
                        } else if (OSDetector.isWindows() && new File(System.getProperty("user.home") + "/EasyShipemnt").exists()) {
                            brPDF.setCurrentPath(new File(System.getProperty("user.home") + "/EasyShipemnt"));
                        } else {
                            brPDF.setCurrentPath(URLUtils.getResourceFile("EasyShipemnt"));
                        }
                    }
                    new ContainerDialog(UserIO.NO_COUNTDOWN, BiancaL.L("installer.gui.title", "JDownloader Installation"), p, BiancaImage.getImage("logo/jd_logo_54_54"), null, null) {
                        private static final long serialVersionUID = 4685519683324833575L;

                        @Override
                        protected void packed() {
                            dialog = this;
                            this.setSize(550, 400);
                            this.setAlwaysOnTop(true);
                        }

                        @Override
                        protected void setReturnValue(boolean b) {
                            super.setReturnValue(b);
                            if (b) {
                                FileSystemUtils.getConfiguration().setProperty(Configuration.Param.PARAM_DOWNLOAD_DIRECTORY.toString(), br.getCurrentPath());
                                FileSystemUtils.getConfiguration().setProperty(Configuration.Param.PARAM_PDF_DIRECTORY.toString(), brPDF.getCurrentPath());
                            } else {
                                FileSystemUtils.getConfiguration().setProperty(Configuration.Param.PARAM_DOWNLOAD_DIRECTORY.toString(), null);
                                FileSystemUtils.getConfiguration().setProperty(Configuration.Param.PARAM_PDF_DIRECTORY.toString(), null);
                            }
                        }
                    };

                    
                    
                    return null;
                }

            }.waitForEDT();
        }

        public static void installFirefoxAddon() {
            File file = URLUtils.getResourceFile("tools/flashgot.xpi");

            LocalBrowser.openinFirefox(file.getAbsolutePath());
        }

        public JPanel getInstallerPanel() {
            JPanel c = new JPanel(new MigLayout("ins 10,wrap 1", "[grow,fill]", "[][grow,fill]"));

            JLabel lbl = new JLabel(BiancaL.L("installer.gui.message", "After Installation, JDownloader will update to the latest version."));

            if (OSDetector.getOSID() == OSDetector.OS.OS_WINDOWS_VISTA || OSDetector.getOSID() == OSDetector.OS.OS_WINDOWS_7) {
                String dir = URLUtils.getResourceFile("downloads").getParent().substring(3).toLowerCase();

                if (!URLUtils.getResourceFile("uninstall.exe").exists()&&(dir.startsWith("programme\\") || dir.startsWith("program files\\"))) {
                    lbl.setText(BiancaL.LF("installer.vistaDir.warning", "Warning! JD is installed in %s. This causes errors.", URLUtils.getResourceFile("downloads")));
                    lbl.setForeground(Color.RED);
                    lbl.setBackground(Color.RED);
                }
                if (!URLUtils.getResourceFile("tools/tinyupdate.jar").canWrite()) {
                    lbl.setText(BiancaL.LF("installer.nowriteDir.warning", "Warning! JD cannot write to %s. Check rights!", URLUtils.getResourceFile("downloads")));
                    lbl.setForeground(Color.RED);
                    lbl.setBackground(Color.RED);
                }
            }
            c.add(lbl, "pushx,growx,split 2");

            Font f = lbl.getFont();
            f = f.deriveFont(f.getStyle() ^ Font.BOLD);

            lbl.setFont(f);
            try {
                c.add(new JLabel(BiancaImage.getScaledImageIcon(BiancaImage.getImage("logo/jd_logo_54_54"), 32, 32)), "alignx right");
            } catch (Exception e) {
                System.err.println("DEVELOPER WARNING! Please copy trunk/ressourcen/jd  to home/.jd_home/jd");
            }
            // c.add(new JSeparator(), "pushx,growx,gapbottom 5");
            return c;
        }

        public boolean isAborted() {
            return aborted;
        }

}
