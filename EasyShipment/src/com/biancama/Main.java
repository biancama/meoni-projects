package com.biancama;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import it.sauronsoftware.junique.MessageHandler;

import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.biancama.controlling.DynamicPluginInterface;
import com.biancama.controlling.interaction.Interaction;
import com.biancama.events.BiancaController;
import com.biancama.events.ControlEvent;
import com.biancama.gui.UserIO;
import com.biancama.gui.swing.GuiRunnable;
import com.biancama.gui.swing.components.linkbutton.BiancaLink;
import com.biancama.gui.swing.easyShipment.EasyShipmentVersion;
import com.biancama.gui.swing.easyShipment.GUIUtils;
import com.biancama.gui.swing.easyShipment.utils.EasyShipmentGuiConstants;
import com.biancama.gui.swing.userio.UserIOGui;
import com.biancama.log.BiancaLogger;
import com.biancama.update.FileUpdate;
import com.biancama.update.WebUpdater;
import com.biancama.utils.ApplicationUtils;
import com.biancama.utils.EventUtils;
import com.biancama.utils.FileSystemUtils;
import com.biancama.utils.JavaUtils;
import com.biancama.utils.OSDetector;
import com.biancama.utils.OutdatedParser;
import com.biancama.utils.URLUtils;
import com.biancama.utils.WebUpdate;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.locale.BiancaL;

public class Main {
    private static Logger LOGGER;
    private static String instanceID = Main.class.getName();

    public static String getInstanceID() {
        return instanceID;
    }

    private static boolean instanceStarted = false;

    public static boolean returnedfromUpdate() {
        return EasyShipmentInitFlags.SWITCH_RETURNED_FROM_UPDATE;
    }

    public static void main(String args[]) {

        System.setProperty("file.encoding", "UTF-8");
        OSDetector.setOSString(System.getProperty("os.name"));
        // System.setProperty("os.name", "Windows Vista m.a.c");
        System.setProperty("sun.swing.enableImprovedDragGesture", "true");
        LOGGER = BiancaLogger.getLogger();
        LOGGER.info("Start Easy Shipment");
        UserIO.setInstance(UserIOGui.getInstance());
        preInitChecks();
        ApplicationUtils.setAppArgs(args);

        try {
            JUnique.acquireLock(instanceID, new MessageHandler() {
                private int counter = -1;
                private Vector<String> params = new Vector<String>();

                public String handle(String message) {
                    if (counter == -2) { return null; }
                    if (counter == -1) {
                        try {
                            counter = Integer.parseInt(message.trim());
                        } catch (Exception e) {
                            counter = -2;
                            return null;
                        }
                        if (counter == -1) {
                            counter = -2;/* Abort */
                        }
                    } else {
                        params.add(message);
                        counter--;
                        if (counter == 0) {
                            String[] args = params.toArray(new String[params.size()]);
                            counter = -1;
                            params = new Vector<String>();
                        }
                    }
                    return null;
                }
            });
            instanceStarted = true;
        } catch (AlreadyLockedException e) {
            LOGGER.info("existing Eas instance found!");
            instanceStarted = false;
        } catch (Exception e) {
            BiancaLogger.exception(e);
            LOGGER.severe("Instance Handling not possible!");
            instanceStarted = true;
        }
        if (instanceStarted || EasyShipmentInitFlags.SWITCH_NEW_INSTANCE) {
            
            BiancaTheme.setTheme("default");
            if (EasyShipmentInitFlags.SHOW_SPLASH) {
                if (GUIUtils.getConfig().getBooleanProperty(EasyShipmentGuiConstants.PARAM_SHOW_SPLASH.toString(), true)) {
                    LOGGER.info("init Splash");
                    new GuiRunnable<Object>() {
                        @Override
                        public Object runSave() {
                            try {
                                new SplashScreen(BiancaController.getInstance());
                            } catch (Exception e) {
                                BiancaLogger.exception(e);
                            }
                            return null;
                        }

                    }.waitForEDT();
                }
            }

            Interaction.deleteInteractions();

            start(args);
        } else {
            if (args.length > 0) {
                LOGGER.info("Send parameters to existing jD instance and exit");
                JUnique.sendMessage(instanceID, "" + args.length);
                for (String arg : args) {
                    JUnique.sendMessage(instanceID, arg);
                }
            } else {
                LOGGER.info("There is already a running jD instance");
                JUnique.sendMessage(instanceID, "1");
                JUnique.sendMessage(instanceID, "--focus");
            }
            System.exit(0);
        }

    }

    private static void start(final String[] args) {
        if (!EasyShipmentInitFlags.STOP && !EasyShipmentInitFlags.ENOUGH_MEMORY) {
            ApplicationUtils.restartApplicationAndWait();
            return;
        }
        if (!EasyShipmentInitFlags.STOP) {
            final Main main = new Main();
            EventQueue.invokeLater(new Runnable() {
                public void run() {

                    main.go();
                    for (String p : args) {
                        LOGGER.finest("Param: " + p);
                    }
                    ParameterManager.processParameters(args);
                }
            });
        }
    }

    private static void preInitChecks() {
        heapCheck();
        javaCheck();
    }

    private static void heapCheck() {
        EasyShipmentInitFlags.ENOUGH_MEMORY = !(Runtime.getRuntime().maxMemory() < 100000000);
        if (!EasyShipmentInitFlags.ENOUGH_MEMORY) {
            EasyShipmentInitFlags.SHOW_SPLASH = false;
            LOGGER.warning("Heapcheck: Not enough heap. use: java -Xmx512m -jar EasyShipment.jar");
        }
    }

    /**
     * Checks if the user uses a correct java version
     */
    private static void javaCheck() {

        if (JavaUtils.getJavaVersion() < 1.6) {
            int returnValue = UserIO.getInstance().requestConfirmDialog(UserIO.DONT_SHOW_AGAIN | UserIO.NO_CANCEL_OPTION, BiancaL.LF("gui.javacheck.newerjavaavailable.title", "Outdated Javaversion found: %s!", JavaUtils.getJavaVersion()), BiancaL.L("gui.javacheck.newerjavaavailable.msg", "Although JDownloader runs on your javaversion, we advise to install the latest java updates. \r\nJDownloader will run more stable, faster, and will look better. \r\n\r\nVisit http://jdownloader.org/download."), BiancaTheme.II("gui.images.warning", 32, 32), null, null);
            if ((returnValue & UserIO.RETURN_DONT_SHOW_AGAIN) == 0) {
                try {
                    BiancaLink.openURL("http://jdownloader.org/download/index?updatejava=1");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void go() {
        final EasyShipmentInit init = new EasyShipmentInit();
        final BiancaController controller = BiancaController.getInstance();
        // JDUtilities.getController().fireControlEvent(new ControlEvent(this,
        // SplashScreen.SPLASH_PROGRESS, "This is JD :)"));
        init.init();
        LOGGER.info(new Date() + "");
        LOGGER.info("init Configuration");

        if (init.loadConfiguration() == null) {

            UserIO.getInstance().requestMessageDialog("JDownloader cannot create the config files. Make sure, that JD_HOME/config/ exists and is writeable");
        }
        if (EasyShipmentInitFlags.SWITCH_DEBUG) {
            LOGGER.info("DEBUG MODE ACTIVATED");
            LOGGER.setLevel(Level.ALL);
        } else {
            BiancaLogger.removeConsoleHandler();
        }

        if (!OutdatedParser.parseFile(URLUtils.getResourceFile("outdated.dat"))) {
            LOGGER.severe("COULD NOT DELETE OUTDATED FILES.RESTART REQUIRED");
            int answer = UserIO.getInstance().requestConfirmDialog(0, BiancaL.L("jd.Main.removerestart.title", "Updater"), BiancaL.L("jd.Main.removerestart.message", "Could not remove outdated libraries. Restart recommended!"), null, BiancaL.L("jd.Main.removerestart.ok", "Restart now!"), BiancaL.L("jd.Main.removerestart.cancel", "Continue"));
            if (UserIO.isOK(answer)) {
                ApplicationUtils.restartApplication(true);
                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }

            }
        }

        LOGGER.info("init Controller");

        LOGGER.info("init Webupdate");
        EventUtils.getController().fireControlEvent(new ControlEvent(this, SplashScreen.SPLASH_PROGRESS, BiancaL.L("gui.splash.progress.webupdate", "Check updates")));

        LOGGER.info("init plugins");
        EventUtils.getController().fireControlEvent(new ControlEvent(this, SplashScreen.SPLASH_PROGRESS, BiancaL.L("gui.splash.progress.initplugins", "Init plugins")));

        init.initPlugins();

        Locale.setDefault(Locale.ENGLISH);

        LOGGER.info("init downloadqueue");
        EventUtils.getController().fireControlEvent(new ControlEvent(this, SplashScreen.SPLASH_PROGRESS, BiancaL.L("gui.splash.progress.controller", "Start controller")));
        init.initControllers();
        LOGGER.info("init gui");
        EventUtils.getController().fireControlEvent(new ControlEvent(this, SplashScreen.SPLASH_PROGRESS, BiancaL.L("gui.splash.progress.paintgui", "Paint user interface")));

        new GuiRunnable<Object>() {
            @Override
            public Object runSave() {
                init.initGUI(controller);
                return null;
            }
        }.waitForEDT();

        LOGGER.info("Initialisation finished");

        HashMap<String, String> head = new HashMap<String, String>();
        head.put("rev", EasyShipmentVersion.getRevision());
        FileSystemUtils.getConfiguration().setProperty("head", head);

        Properties pr = System.getProperties();
        TreeSet<Object> propKeys = new TreeSet<Object>(pr.keySet());

        for (Object it : propKeys) {
            String key = it.toString();
            LOGGER.finer(key + "=" + pr.get(key));
        }

        LOGGER.info("Revision: " + EasyShipmentVersion.getRevision());
        LOGGER.finer("Runtype: " + FileSystemUtils.getRunType());

        init.checkUpdate();
        EventUtils.getController().fireControlEvent(new ControlEvent(this, ControlEvent.CONTROL_INIT_COMPLETE, null));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            BiancaLogger.exception(e);
        }

        /*
         * Keeps the home working directory for developers up2date
         */
        LOGGER.info("update start");

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    WebUpdate.doUpdateCheck(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        try {
            loadDynamics();
        } catch (Exception e1) {
            BiancaLogger.exception(Level.FINEST, e1);
        }
        WebUpdate.dynamicPluginsFinished();

        LOGGER.info("update end");
    }

    public static void loadDynamics() throws Exception {
        ArrayList<String> classes = new ArrayList<String>();
        URLClassLoader classLoader = new URLClassLoader(new URL[] { FileSystemUtils.getHomeDirectoryFromEnvironment().toURI().toURL(), URLUtils.getResourceFile("java").toURI().toURL() }, Thread.currentThread().getContextClassLoader());
        if (FileSystemUtils.getRunType() == FileSystemUtils.RunType.LOCAL) {
            /* dynamics aus eclipse heraus laden */

            Enumeration<URL> resources = classLoader.getResources("jd/dynamics/");
            ArrayList<String> dynamics = new ArrayList<String>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.toURI().getPath() != null) {
                    String[] files = new File(resource.toURI().getPath()).list();
                    if (files != null) {
                        for (String file : files) {
                            dynamics.add(new File(file).getName());
                        }
                    }
                }
            }
            if (dynamics.size() == 0) { return; }
            for (String dynamic : dynamics) {
                if (!dynamic.contains("$") && !classes.contains("/jd/dynamics/" + dynamic) && !dynamic.equalsIgnoreCase("DynamicPluginInterface.class")) {
                    System.out.println("Plugins: " + dynamic);
                    classes.add("/jd/dynamics/" + dynamic);
                }
            }
        } else {

            /* dynamics in der public laden */
            BiancaLogger.getLogger().finest("Run dynamics");
            if (WebUpdater.getPluginList() == null) { return; }
            for (Entry<String, FileUpdate> entry : WebUpdater.PLUGIN_LIST.entrySet()) {
                System.out.println("Plugins: " + entry.getKey());
                if (entry.getKey().startsWith("/jd/dynamics/") && !entry.getKey().contains("DynamicPluginInterface")) {
                    BiancaLogger.getLogger().finest("Found dynamic: " + entry.getKey());
                    if (!entry.getValue().equals()) {

                        if (!new WebUpdater().updateUpdatefile(entry.getValue())) {
                            BiancaLogger.getLogger().warning("Could not update " + entry.getValue());
                            continue;
                        } else {
                            BiancaLogger.getLogger().finest("Update OK!");
                        }
                    }
                    if (!entry.getKey().contains("$") && !classes.contains(entry.getKey())) {
                        classes.add(entry.getKey());
                    }
                }
            }
        }
        for (String clazz : classes) {
            try {
                Class<?> plgClass;
                BiancaLogger.getLogger().finest("Init Dynamic " + clazz);
                plgClass = classLoader.loadClass(clazz.replace("/", ".").replace(".class", "").substring(1));
                if (plgClass == null) {
                    BiancaLogger.getLogger().info("Could not load " + clazz);
                    continue;
                }
                if (plgClass == DynamicPluginInterface.class) {
                    continue;
                }
                Constructor<?> con = plgClass.getConstructor(new Class[] {});
                DynamicPluginInterface dplg = (DynamicPluginInterface) con.newInstance(new Object[] {});
                dplg.execute();
            } catch (Exception e) {
                BiancaLogger.exception(Level.FINER, e);
            }
        }
    }

}
