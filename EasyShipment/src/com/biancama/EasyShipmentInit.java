package com.biancama;

import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.biancama.config.Configuration;
import com.biancama.config.SubConfiguration;
import com.biancama.events.BiancaController;
import com.biancama.events.ControlEvent;
import com.biancama.gui.UserIF;
import com.biancama.gui.UserIO;
import com.biancama.gui.easyShipment.EasyShipmentGui;
import com.biancama.gui.easyShipment.events.EDTEventQueue;
import com.biancama.gui.easyShipment.plugins.CPluginWrapper;
import com.biancama.gui.easyShipment.plugins.OptionalPluginWrapper;
import com.biancama.gui.swing.SwingGui;
import com.biancama.gui.swing.components.linkbutton.BiancaLink;
import com.biancama.gui.swing.easyShipment.EasyShipmentVersion;
import com.biancama.gui.swing.easyShipment.GUIUtils;
import com.biancama.gui.swing.easyShipment.utils.EasyShipmentGuiConstants;
import com.biancama.gui.swing.laf.LookAndFeelController;
import com.biancama.http.BiancaProxy;
import com.biancama.http.Browser;
import com.biancama.log.BiancaLogger;
import com.biancama.plugins.DecrypterPlugin;
import com.biancama.plugins.HostPlugin;
import com.biancama.plugins.OptionalPlugin;
import com.biancama.plugins.PluginOptional;
import com.biancama.utils.ApplicationUtils;
import com.biancama.utils.ClassFinder;
import com.biancama.utils.DatabaseUtils;
import com.biancama.utils.EventUtils;
import com.biancama.utils.FileSystemUtils;
import com.biancama.utils.FlagsUtils;
import com.biancama.utils.JavaUtils;
import com.biancama.utils.OSDetector;
import com.biancama.utils.URLUtils;
import com.biancama.utils.encoding.Encoding;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.locale.BiancaL;

public class EasyShipmentInit {

    private static final boolean TEST_INSTALLER = false;

    private static Logger logger = BiancaLogger.getLogger();

    private static ClassLoader CL;

    private boolean installerVisible = false;

    public EasyShipmentInit() {
    }

    public void checkUpdate() {
        if (URLUtils.getResourceFile("webcheck.tmp").exists() && FileSystemUtils.readFileToString(URLUtils.getResourceFile("webcheck.tmp")).indexOf("(Revision" + EasyShipmentVersion.getRevision() + ")") > 0) {
            UserIO.getInstance().requestTextAreaDialog("Error", "Failed Update detected!", "It seems that the previous webupdate failed.\r\nPlease ensure that your java-version is equal- or above 1.5.\r\nMore infos at http://www.syncom.org/projects/jdownloader/wiki/FAQ.\r\n\r\nErrorcode: \r\n" + FileSystemUtils.readFileToString(URLUtils.getResourceFile("webcheck.tmp")));
            URLUtils.getResourceFile("webcheck.tmp").delete();
            FileSystemUtils.getConfiguration().setProperty(Configuration.Param.PARAM_WEBUPDATE_AUTO_RESTART.toString(), false);
        }
        if (FileSystemUtils.getRunType() == FileSystemUtils.RunType.LOCAL_JARED) {
            String old = FileSystemUtils.getConfiguration().getStringProperty(Configuration.Param.PARAM_UPDATE_VERSION.toString(), "");
            if (!old.equals(EasyShipmentVersion.getRevision())) {
                logger.info("Detected that JD just got updated");
                EventUtils.getController().fireControlEvent(new ControlEvent(this, SplashScreen.SPLASH_FINISH));
                int status = UserIO.getInstance().requestHelpDialog(UserIO.NO_CANCEL_OPTION, BiancaL.LF("system.update.message.title", "Updated to version %s", EasyShipmentVersion.getRevision()), BiancaL.L("system.update.message", "Update successfull"), BiancaL.L("system.update.showchangelogv2", "What's new?"), "http://jdownloader.org/changes/index");
                if (FlagsUtils.hasAllFlags(status, UserIO.RETURN_OK) && FileSystemUtils.getConfiguration().getBooleanProperty(Configuration.Param.PARAM_WEBUPDATE_AUTO_SHOW_CHANGELOG.toString(), true)) {
                    try {
                        BiancaLink.openURL("http://jdownloader.org/changes/index");
                    } catch (Exception e) {
                        BiancaLogger.exception(e);
                    }
                }

            }
        }
        submitVersion();
    }

    private void submitVersion() {
        new Thread(new Runnable() {
            public void run() {
                if (FileSystemUtils.getRunType() == FileSystemUtils.RunType.LOCAL_JARED) {
                    String os = "unk";
                    if (OSDetector.isLinux()) {
                        os = "lin";
                    } else if (OSDetector.isMac()) {
                        os = "mac";
                    } else if (OSDetector.isWindows()) {
                        os = "win";
                    }
                    String tz = System.getProperty("user.timezone");
                    if (tz == null) {
                        tz = "unknown";
                    }
                    Browser br = new Browser();
                    br.setConnectTimeout(15000);
                    if (!FileSystemUtils.getConfiguration().getStringProperty(Configuration.Param.PARAM_UPDATE_VERSION.toString(), "").equals(EasyShipmentVersion.getRevision())) {
                        try {
                            String prev = FileSystemUtils.getConfiguration().getStringProperty(Configuration.Param.PARAM_UPDATE_VERSION.toString(), "");
                            if (prev == null || prev.length() < 3) {
                                prev = "0";
                            } else {
                                prev = prev.replaceAll(",|\\.", "");
                            }
                            br.postPage("http://service.jdownloader.org/tools/s.php", "v=" + EasyShipmentVersion.getRevision().replaceAll(",|\\.", "") + "&p=" + prev + "&os=" + os + "&tz=" + Encoding.urlEncode(tz));
                            FileSystemUtils.getConfiguration().setProperty(Configuration.Param.PARAM_UPDATE_VERSION.toString(), EasyShipmentVersion.getRevision());
                            FileSystemUtils.getConfiguration().save();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }).start();
    }

    public void init() {
        initBrowser();

    }

    public void initBrowser() {
        Browser.setLogger(BiancaLogger.getLogger());
        Browser.init();
        /* init default global Timeouts */
        Browser.setGlobalReadTimeout(SubConfiguration.getConfig("DOWNLOAD").getIntegerProperty(Configuration.Param.PARAM_DOWNLOAD_READ_TIMEOUT.toString(), 100000));
        Browser.setGlobalConnectTimeout(SubConfiguration.getConfig("DOWNLOAD").getIntegerProperty(Configuration.Param.PARAM_DOWNLOAD_CONNECT_TIMEOUT.toString(), 100000));

        if (SubConfiguration.getConfig("DOWNLOAD").getBooleanProperty(Configuration.Param.USE_PROXY.toString(), false)) {

            String host = SubConfiguration.getConfig("DOWNLOAD").getStringProperty(Configuration.Param.PROXY_HOST.toString(), "");
            int port = SubConfiguration.getConfig("DOWNLOAD").getIntegerProperty(Configuration.Param.PROXY_HOST.toString(), 8080);
            String user = SubConfiguration.getConfig("DOWNLOAD").getStringProperty(Configuration.Param.PROXY_USER.toString(), "");
            String pass = SubConfiguration.getConfig("DOWNLOAD").getStringProperty(Configuration.Param.PROXY_PASS.toString(), "");
            if (host.trim().equals("")) {
                BiancaLogger.getLogger().warning("Proxy disabled. No host");
                SubConfiguration.getConfig("DOWNLOAD").setProperty(Configuration.Param.USE_PROXY.toString(), false);
                return;
            }

            BiancaProxy pr = new BiancaProxy(Proxy.Type.HTTP, host, port);

            if (user != null && user.trim().length() > 0) {
                pr.setUser(user);
            }
            if (pass != null && pass.trim().length() > 0) {
                pr.setPass(pass);
            }
            Browser.setGlobalProxy(pr);

        }
        if (SubConfiguration.getConfig("DOWNLOAD").getBooleanProperty(Configuration.Param.USE_SOCKS.toString(), false)) {

            String user = SubConfiguration.getConfig("DOWNLOAD").getStringProperty(Configuration.Param.USE_SOCKS.toString(), "");
            String pass = SubConfiguration.getConfig("DOWNLOAD").getStringProperty(Configuration.Param.PROXY_PASS_SOCKS.toString(), "");
            String host = SubConfiguration.getConfig("DOWNLOAD").getStringProperty(Configuration.Param.SOCKS_HOST.toString(), "");
            int port = SubConfiguration.getConfig("DOWNLOAD").getIntegerProperty(Configuration.Param.SOCKS_PORT.toString(), 1080);
            if (host.trim().equals("")) {
                BiancaLogger.getLogger().warning("Socks Proxy disabled. No host");
                SubConfiguration.getConfig("DOWNLOAD").setProperty(Configuration.Param.USE_SOCKS.toString(), false);
                return;
            }
            BiancaProxy pr = new BiancaProxy(Proxy.Type.SOCKS, host, port);

            if (user != null && user.trim().length() > 0) {
                pr.setUser(user);
            }
            if (pass != null && pass.trim().length() > 0) {
                pr.setPass(pass);
            }
            Browser.setGlobalProxy(pr);
        }
        Browser.init();
    }

    public void initControllers() {
    }

    public void initGUI(BiancaController controller) {
        try{
        LookAndFeelController.setUIManager();

        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new EDTEventQueue());
        SwingGui.setInstance(EasyShipmentGui.getInstance());
        UserIF.setInstance(SwingGui.getInstance());
        controller.addControlListener(SwingGui.getInstance());
        } catch (RuntimeException ex){
            BiancaLogger.getLogger().severe("RunTime error in init method: " +ex );
            System.exit(-1);
        }
    }

    public void initPlugins() {
        try {
            movePluginUpdates(URLUtils.getResourceFile("update"));
        } catch (Throwable e) {
            BiancaLogger.exception(e);
        }
        try {
            loadCPlugins();
            loadPluginOptional();
            for (final OptionalPluginWrapper plg : OptionalPluginWrapper.getOptionalWrapper()) {
                if (plg.isLoaded()) {
                    try {
                        if (plg.isEnabled() && !plg.getPlugin().initAddon()) {
                            logger.severe("Error loading Optional Plugin:" + plg.getClassName());
                        }
                    } catch (Throwable e) {
                        logger.severe("Error loading Optional Plugin: " + e.getMessage());
                        BiancaLogger.exception(e);
                    }
                }
            }
        } catch (Throwable e) {
            BiancaLogger.exception(e);
        }
    }

    /**
     * @param resourceFile
     */
    private void movePluginUpdates(File dir) {
        if (!URLUtils.getResourceFile("update").exists()) { return; }
        if (!dir.isDirectory()) { return; }
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                movePluginUpdates(f);
            } else {
                // Create relativ path
                File update = URLUtils.getResourceFile("update");
                File root = update.getParentFile();
                String n = URLUtils.getResourceFile("update").getAbsolutePath();
                n = f.getAbsolutePath().replace(n, "").substring(1);
                File newFile = new File(root, n).getAbsoluteFile();
                logger.info("./update -> real  " + n + " ->" + newFile.getAbsolutePath());
                logger.info("Exists: " + newFile.exists());
                if (!newFile.getParentFile().exists()) {
                    logger.info("Parent Exists: false");

                    if (newFile.getParentFile().mkdirs()) {
                        logger.info("^^CREATED");
                    } else {
                        logger.info("^^CREATION FAILED");
                    }
                }

                newFile.delete();
                f.renameTo(newFile);
                File parent = newFile.getParentFile();

                while (parent.listFiles().length == 0) {
                    parent.delete();
                    parent = parent.getParentFile();
                }
            }
        }
        if (dir.list() != null) {
            if (dir.list().length == 0) {
                dir.delete();
            }
        }

    }

    public boolean installerWasVisible() {
        return installerVisible;
    }

    public Configuration loadConfiguration() {
        Object obj = DatabaseUtils.getDatabaseConnector().getData(Configuration.NAME);

        if (obj == null) {
            logger.finest("Fresh install?");
            // File file = URLUtils.getResourceFile(JDUtilities.CONFIG_PATH);
            // if (file.exists()) {
            // logger.info("Wrapping jdownloader.config");
            // obj = JDIO.loadObject(null, file, Configuration.saveAsXML);
            // logger.finest(obj.getClass().getName());
            // JDUtilities.getDatabaseConnector().saveConfiguration(
            // "jdownloaderconfig",
            // obj);
            // }
        }

        if (!TEST_INSTALLER && obj != null && ((Configuration) obj).getStringProperty(Configuration.Param.PARAM_DOWNLOAD_DIRECTORY.toString()) != null) {

            Configuration configuration = (Configuration) obj;
            ApplicationUtils.setConfiguration(configuration);
            BiancaLogger.getLogger().setLevel(configuration.getGenericProperty(Configuration.Param.PARAM_LOGGER_LEVEL.toString(), Level.WARNING));
            BiancaTheme.setTheme(GUIUtils.getConfig().getStringProperty(EasyShipmentGuiConstants.PARAM_THEME.toString(), "default"));

        } else {

            File cfg = URLUtils.getResourceFile("config");
            if (!cfg.exists()) {

                if (!cfg.mkdirs()) {
                    System.err.println("Could not create configdir");
                    return null;
                }
                if (!cfg.canWrite()) {
                    System.err.println("Cannot write to configdir");
                    return null;
                }
            }
            Configuration configuration = new Configuration();
            ApplicationUtils.setConfiguration(configuration);
            BiancaLogger.getLogger().setLevel(configuration.getGenericProperty(Configuration.Param.PARAM_LOGGER_LEVEL.toString(), Level.WARNING));
            BiancaTheme.setTheme(GUIUtils.getConfig().getStringProperty(EasyShipmentGuiConstants.PARAM_THEME.toString(), "default"));

            DatabaseUtils.getDatabaseConnector().saveConfiguration(Configuration.NAME, ApplicationUtils.getConfiguration());
            installerVisible = true;
            EventUtils.getController().fireControlEvent(new ControlEvent(this, SplashScreen.SPLASH_FINISH));
            /**
             * Workaround to enable JGoodies for MAC oS
             */

            LookAndFeelController.setUIManager();
            Installer inst = new Installer();

            if (!inst.isAborted()) {

                File home = URLUtils.getResourceFile(".");
                if (home.canWrite() && !URLUtils.getResourceFile("noupdate.txt").exists()) {

                    // try {
                    // new WebUpdate().doWebupdate(true);
                    // FileSystemUtils.RunType.getRunType.save();
                    // DatabaseUtils.getDatabaseConnector().shutdownDatabase();
                    // logger.info(ExecuterUtils.runCommand("java", new String[]
                    // { "-jar", "jdupdate.jar", "/restart", "/rt" +
                    // JDUtilities.RUNTYPE_LOCAL_JARED },
                    // home.getAbsolutePath(), 0));
                    // System.exit(0);
                    // } catch (Exception e) {
                    // BiancaLogger.getLogger().log(java.util.logging.Level.SEVERE,
                    // "Exception occurred", e);
                    // // System.exit(0);
                    // }

                }
                if (!home.canWrite()) {
                    logger.severe("INSTALL aborted");

                    UserIO.getInstance().requestMessageDialog(BiancaL.L("installer.error.noWriteRights", "Error. You do not have permissions to write to the dir"));

                    FileSystemUtils.removeDirectoryOrFile(URLUtils.getResourceFile("config"));
                    System.exit(1);
                }

            } else {
                logger.severe("INSTALL abgebrochen2");

                UserIO.getInstance().requestMessageDialog(BiancaL.L("installer.abortInstallation", "Error. User aborted installation."));

                FileSystemUtils.removeDirectoryOrFile(URLUtils.getResourceFile("config"));
                System.exit(0);

            }
        }

        return FileSystemUtils.getConfiguration();
    }

    public void loadCPlugins() {
        try {
            new CPluginWrapper("ccf", "C", ".+\\.ccf");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            new CPluginWrapper("rsdf", "R", ".+\\.rsdf");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            new CPluginWrapper("dlc", "D", ".+\\.dlc");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            new CPluginWrapper("jdc", "J", ".+\\.jdc");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            new CPluginWrapper("metalink", "MetaLink", ".+\\.metalink");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void loadPluginForDecrypt() {
        try {
            for (Class<?> c : ClassFinder.getClasses("jd.plugins.decrypter", getPluginClassLoader())) {
                try {
                    logger.finest("Try to load " + c);
                    if (c != null && c.getAnnotations().length > 0) {
                        DecrypterPlugin help = (DecrypterPlugin) c.getAnnotations()[0];

                        if (help.interfaceVersion() != DecrypterPlugin.INTERFACE_VERSION) {
                            logger.warning("Outdated Plugin found: " + help);
                            continue;
                        }
                        String[] names = help.names();
                        String[] patterns = help.urls();
                        int[] flags = help.flags();

                        // TODO: Change this String to test the changes from
                        // Wordpress/CMS/Redirector/... Decrypters WITHOUT
                        // commiting
                        String dump = "";
                        // See if there are cached annotations
                        if (names.length == 0) {
                            SubConfiguration cfg = SubConfiguration.getConfig("jd.JDInit.loadPluginForDecrypt");
                            names = cfg.getGenericProperty(c.getName() + "_names_" + dump + help.revision(), names);
                            patterns = cfg.getGenericProperty(c.getName() + "_pattern_" + dump + help.revision(), patterns);
                            flags = cfg.getGenericProperty(c.getName() + "_flags_" + dump + help.revision(), flags);
                        }
                        // if not, try to load them from static functions
                        if (names.length == 0) {
                            names = (String[]) c.getMethod("getAnnotationNames", new Class[] {}).invoke(null, new Object[] {});
                            patterns = (String[]) c.getMethod("getAnnotationUrls", new Class[] {}).invoke(null, new Object[] {});
                            flags = (int[]) c.getMethod("getAnnotationFlags", new Class[] {}).invoke(null, new Object[] {});
                            SubConfiguration cfg = SubConfiguration.getConfig("jd.JDInit.loadPluginForDecrypt");
                            cfg.setProperty(c.getName() + "_names_" + help.revision(), names);
                            cfg.setProperty(c.getName() + "_pattern_" + help.revision(), patterns);
                            cfg.setProperty(c.getName() + "_flags_" + help.revision(), flags);
                            cfg.save();
                        }
                        for (int i = 0; i < names.length; i++) {
                            try {
                                new DecryptPluginWrapper(names[i], c.getSimpleName(), patterns[i], flags[i], help.revision());
                            } catch (Throwable e) {
                                BiancaLogger.exception(e);
                            }
                        }
                    }
                } catch (Throwable e) {
                    BiancaLogger.exception(e);
                }
            }
        } catch (Throwable e) {
            BiancaLogger.exception(e);
        }
    }

    /**
     * Returns a classloader to load plugins (class files); Depending on runtype
     * (dev or local jared) a different classoader is used to load plugins
     * either from installdirectory or from rundirectory
     * 
     * @return
     */
    private static ClassLoader getPluginClassLoader() {
        if (CL == null) {
            try {
                if (FileSystemUtils.getRunType() == FileSystemUtils.RunType.LOCAL_JARED) {
                    CL = new URLClassLoader(new URL[] { FileSystemUtils.getHomeDirectoryFromEnvironment().toURI().toURL(), URLUtils.getResourceFile("java").toURI().toURL() }, Thread.currentThread().getContextClassLoader());
                } else {
                    CL = Thread.currentThread().getContextClassLoader();
                }
            } catch (MalformedURLException e) {
                BiancaLogger.exception(e);
            }
        }
        return CL;
    }

    public static void loadPluginForHost() {
        try {
            for (Class<?> c : ClassFinder.getClasses("jd.plugins.hoster", getPluginClassLoader())) {
                try {
                    logger.finest("Try to load " + c);
                    if (c != null && c.getAnnotations().length > 0) {
                        HostPlugin help = (HostPlugin) c.getAnnotations()[0];

                        if (help.interfaceVersion() != HostPlugin.INTERFACE_VERSION) {
                            logger.warning("Outdated Plugin found: " + help);
                            continue;
                        }
                        for (int i = 0; i < help.names().length; i++) {
                            try {
                                new HostPluginWrapper(help.names()[i], c.getSimpleName(), help.urls()[i], help.flags()[i], help.revision());
                            } catch (Throwable e) {
                                BiancaLogger.exception(e);
                            }
                        }
                    }
                } catch (Throwable e) {
                    BiancaLogger.exception(e);
                }
            }
        } catch (Throwable e) {
            BiancaLogger.exception(e);
        }
    }

    public void loadPluginOptional() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            for (Class<?> c : ClassFinder.getClasses("jd.plugins.optional", FileSystemUtils.getJDClassLoader())) {
                try {
                    if (list.contains(c.getName())) {
                        System.out.println("Already loaded:" + c);
                        continue;
                    }
                    if (c.getAnnotations().length > 0) {
                        OptionalPlugin help = (OptionalPlugin) c.getAnnotations()[0];

                        if ((help.windows() && OSDetector.isWindows()) || (help.linux() && OSDetector.isLinux()) || (help.mac() && OSDetector.isMac())) {
                            if (JavaUtils.getJavaVersion() >= help.minJVM() && PluginOptional.ADDON_INTERFACE_VERSION == help.interfaceversion()) {
                                logger.finest("Init PluginWrapper!");
                                new OptionalPluginWrapper(c, help);
                                list.add(c.getName());
                            }
                        }
                    }
                } catch (Throwable e) {
                    BiancaLogger.exception(e);
                }
            }
        } catch (Throwable e) {
            BiancaLogger.exception(e);
        }

    }

}
