package com.biancama.events;

import it.sauronsoftware.junique.JUnique;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.biancama.Main;
import com.biancama.config.Configuration;
import com.biancama.config.SubConfiguration;
import com.biancama.config.database.DatabaseConnector;
import com.biancama.controlling.DownloadWatchDog;
import com.biancama.controlling.ProgressController;
import com.biancama.controlling.ProgressControllerEvent;
import com.biancama.controlling.ProgressControllerListener;
import com.biancama.gui.UserIO;
import com.biancama.gui.easyShipment.plugins.CPluginWrapper;
import com.biancama.log.BiancaLogger;
import com.biancama.plugins.DownloadLink;
import com.biancama.plugins.FilePackage;
import com.biancama.plugins.LinkStatus;
import com.biancama.plugins.PluginsC;
import com.biancama.utils.DatabaseUtils;
import com.biancama.utils.EventUtils;
import com.biancama.utils.FileSystemUtils;
import com.biancama.utils.FlagsUtils;
import com.biancama.utils.locale.BiancaL;

public class BiancaController implements ControlListener {

    private final Logger logger = BiancaLogger.getLogger();
    // init statut variable
    public static final int INIT_STATUS_COMPLETE = 0;
    private final int initStatus = -1;
    private boolean alreadyAutostart = false;

    /**
     * queues to listener an event
     */
    private transient final ArrayList<ControlListener> controlListener = new ArrayList<ControlListener>();
    private transient final ArrayList<ControlListener> removeList = new ArrayList<ControlListener>();

    /**
     * queue of event awt
     */
    private final ArrayList<ControlEvent> eventQueue = new ArrayList<ControlEvent>();
    private static ArrayList<String> delayMap = new ArrayList<String>();

    private EventSender eventSender = null;
    private static BiancaController INSTANCE;

    public static synchronized BiancaController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BiancaController();
        }
        return INSTANCE;
    }

    private static final Object SHUTDOWNLOCK = new Object();

    private class EventSender extends Thread {

        protected static final long MAX_EVENT_TIME = 10000;
        private ControlListener currentListener;
        private ControlEvent event;
        private long eventStart = 0;
        public boolean waitFlag = true;
        private final Thread watchDog;

        public EventSender() {
            super("EventSender");
            watchDog = new Thread("EventSenderWatchDog") {
                @Override
                public void run() {
                    while (true) {
                        if (eventStart > 0 && System.currentTimeMillis() - eventStart > MAX_EVENT_TIME) {
                            logger.finer("WATCHDOG: Execution Limit reached");
                            logger.finer("ControlListener: " + currentListener);
                            logger.finer("Event: " + event);
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            BiancaLogger.exception(e);
                            return;
                        }
                    }
                }

            };
            watchDog.start();
        }

        @Override
        public void run() {
            while (true) {
                synchronized (this) {
                    while (waitFlag) {
                        try {
                            wait();
                        } catch (Exception e) {
                            BiancaLogger.exception(e);
                        }
                    }
                }
                try {
                    synchronized (eventQueue) {
                        if (eventQueue.size() > 0) {
                            event = eventQueue.remove(0);
                        } else {
                            eventStart = 0;
                            waitFlag = true;
                            // JDUtilities.getLogger().severe("PAUSE");
                        }
                    }
                    if (event == null || waitFlag) {
                        continue;
                    }
                    eventStart = System.currentTimeMillis();
                    currentListener = BiancaController.this;
                    try {
                        controlEvent(event);
                    } catch (Exception e) {
                        BiancaLogger.exception(e);
                    }
                    eventStart = 0;
                    synchronized (controlListener) {
                        if (controlListener.size() > 0) {
                            for (ControlListener cl : controlListener) {
                                eventStart = System.currentTimeMillis();
                                try {
                                    cl.controlEvent(event);
                                } catch (Exception e) {
                                    BiancaLogger.exception(e);
                                }
                                eventStart = 0;
                            }
                        }
                        synchronized (removeList) {
                            controlListener.removeAll(removeList);
                            removeList.clear();
                        }
                    }
                    // JDUtilities.getLogger().severe("THREAD2");

                } catch (Exception e) {
                    BiancaLogger.exception(e);
                    eventStart = 0;
                }
            }

        }

    }

    private BiancaController() {
        eventSender = getEventSender();
        EventUtils.setController(this);
    }

    public void controlEvent(ControlEvent event) {
        // TODO Auto-generated method stub

    }

    public void addControlListener(ControlListener listener) {
        if (listener == null) { throw new NullPointerException(); }
        synchronized (controlListener) {
            synchronized (removeList) {
                if (removeList.contains(listener)) {
                    removeList.remove(listener);
                }
            }
            if (!controlListener.contains(listener)) {
                controlListener.add(listener);
            }
        }
    }

    /**
     * quickmode to choose between normal shutdown or quick one
     * 
     * quickmode: no events are thrown
     * 
     * (eg shutdown by os)
     * 
     * we maybe dont have enough time to wait for all addons/plugins to finish,
     * saving the database is the most important thing to do
     * 
     * @param quickmode
     */
    public void prepareShutdown(boolean quickmode) {
        synchronized (SHUTDOWNLOCK) {
            if (DatabaseConnector.isDatabaseShutdown()) { return; }
            logger.info("Stop all running downloads");
            if (!quickmode) {
                logger.info("Call Exit event");
                fireControlEventDirect(new ControlEvent(this, ControlEvent.CONTROL_SYSTEM_EXIT, this));
            }
            if (!quickmode) {
                logger.info("Wait for delayExit");
                waitDelayExit();
            }
            logger.info("Shutdown Database");
            DatabaseUtils.getDatabaseConnector().shutdownDatabase();
            logger.info("Release JUnique Lock");
            try {
                /*
                 * try catch errors in case when lock has not been aquired (eg
                 * firewall prevent junique server creation)
                 */
                JUnique.releaseLock(Main.getInstanceID());
            } catch (Exception e) {
            }
            fireControlEventDirect(new ControlEvent(this, ControlEvent.CONTROL_SYSTEM_SHUTDOWN_PREPARED, this));
        }
    }

    private void waitDelayExit() {
        long maxdelay = 10000;
        while (maxdelay > 0) {
            if (delayMap.size() <= 0) { return; }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
            maxdelay -= 200;
        }
        logger.severe("Unable to satisfy all delayExit requests! " + delayMap);
    }

    private EventSender getEventSender() {
        if (this.eventSender != null && this.eventSender.isAlive()) { return this.eventSender; }
        EventSender th = new EventSender();
        th.start();
        return th;
    }

    public void fireControlEvent(ControlEvent controlEvent) {
        if (controlEvent == null) { return; }
        try {
            synchronized (eventQueue) {
                eventQueue.add(controlEvent);
                synchronized (eventSender) {
                    if (eventSender.waitFlag) {
                        eventSender.waitFlag = false;
                        eventSender.notify();
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public void fireControlEventDirect(ControlEvent controlEvent) {
        if (controlEvent == null) { return; }
        try {
            synchronized (controlListener) {
                synchronized (removeList) {
                    controlListener.removeAll(removeList);
                    removeList.clear();
                }
                if (controlListener.size() > 0) {
                    for (ControlListener cl : controlListener) {
                        try {
                            cl.controlEvent(controlEvent);
                        } catch (Exception e) {
                            BiancaLogger.exception(e);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public void fireControlEvent(int controlID, Object param) {
        ControlEvent c = new ControlEvent(this, controlID, param);
        fireControlEvent(c);
    }

    public synchronized void removeControlListener(ControlListener listener) {
        synchronized (removeList) {
            if (!removeList.contains(listener)) {
                removeList.add(listener);
            }
        }
    }

    // setters -----------------------------------------

    public int getInitStatus() {
        return initStatus;
    }

    public void exit() {
        new Thread(new Runnable() {
            public void run() {
                prepareShutdown(false);
                System.exit(0);
            }
        }).start();
    }

    public void syncDatabase() {
        if (DatabaseConnector.isDatabaseShutdown()) { return; }
        logger.info("Sync Downloadlist");
    }

    public static String requestDelayExit(String name) {
        if (name == null) {
            name = "unknown";
        }
        synchronized (delayMap) {
            String id = "ID: " + name + " TIME: " + System.currentTimeMillis();
            while (delayMap.contains(id)) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
                id = "ID: " + name + " TIME: " + System.currentTimeMillis();
            }
            delayMap.add(id);
            return id;
        }
    }

    public static void releaseDelayExit(String id) {
        synchronized (delayMap) {
            if (!delayMap.remove(id)) {
                BiancaLogger.getLogger().severe(id + " not found in delayMap!");
            }
        }

    }

    public void loadContainerFile(final File file) {
        loadContainerFile(file, false, false);
    }

    /**
     * Hiermit wird eine Containerdatei geöffnet. Dazu wird zuerst ein
     * passendes Plugin gesucht und danach alle DownloadLinks interpretiert
     * 
     * @param file
     *            Die Containerdatei
     */
    public void loadContainerFile(final File file, final boolean hideGrabber, final boolean autostart) {
        System.out.println("load container");
        new Thread() {
            @Override
            public void run() {
                ArrayList<CPluginWrapper> pluginsForContainer = CPluginWrapper.getCWrapper();
                ArrayList<DownloadLink> downloadLinks = new ArrayList<DownloadLink>();
                CPluginWrapper wrapper;
                ProgressController progress = new ProgressController("Containerloader", pluginsForContainer.size());
                logger.info("load Container: " + file);
                for (int i = 0; i < pluginsForContainer.size(); i++) {
                    wrapper = pluginsForContainer.get(i);
                    progress.setStatusText("Containerplugin: " + wrapper.getHost());
                    if (wrapper.canHandle(file.getName())) {
                        // es muss jeweils eine neue plugininstanz erzeugt
                        // werden
                        PluginsC pContainer = (PluginsC) wrapper.getNewPluginInstance();
                        try {
                            progress.setSource(pContainer);
                            pContainer.initContainer(file.getAbsolutePath());
                            ArrayList<DownloadLink> links = pContainer.getContainedDownloadlinks();
                            if (links == null || links.size() == 0) {
                                logger.severe("Container Decryption failed (1)");
                            } else {
                                downloadLinks = links;
                                break;
                            }
                        } catch (Exception e) {
                            BiancaLogger.exception(e);
                        }
                    }
                    progress.increase(1);
                }
                progress.setStatusText(downloadLinks.size() + " links found");
                if (downloadLinks.size() > 0) {
                    if (SubConfiguration.getConfig("GUI").getBooleanProperty(Configuration.Param.SHOW_CONTAINER_ONLOAD_OVERVIEW.toString(), false)) {
                        String html = "<style>p { font-size:9px;margin:1px; padding:0px;}div {font-family:Geneva, Arial, Helvetica, sans-serif; width:400px;background-color:#ffffff; padding:2px;}h1 { vertical-align:top; text-align:left;font-size:10px; margin:0px; display:block;font-weight:bold; padding:0px;}</style><div> <div align='center'> <p><img src='http://jdownloader.org/img/%s.gif'> </p> </div> <h1>%s</h1><hr> <table width='100%%' border='0' cellspacing='5'> <tr> <td><p>%s</p></td> <td style='width:100%%'><p>%s</p></td> </tr> <tr> <td><p>%s</p></td> <td style='width:100%%'><p>%s</p></td> </tr> <tr> <td><p>%s</p></td> <td style='width:100%%'><p>%s</p></td> </tr> <tr> <td><p>%s</p></td> <td style='width:100%%'><p>%s</p></td> </tr> </table> </div>";
                        String app;
                        String uploader;
                        if (downloadLinks.get(0).getFilePackage().getProperty("header", null) != null) {
                            HashMap<String, String> header = downloadLinks.get(0).getFilePackage().getGenericProperty("header", new HashMap<String, String>());
                            uploader = header.get("tribute");
                            app = header.get("generator.app") + " v." + header.get("generator.version") + " (" + header.get("generator.url") + ")";
                        } else {
                            app = "n.A.";
                            uploader = "n.A";
                        }
                        String comment = downloadLinks.get(0).getFilePackage().getComment();
                        String password = downloadLinks.get(0).getFilePackage().getPassword();
                        FlagsUtils.hasAllFlags(UserIO.getInstance().requestConfirmDialog(UserIO.NO_COUNTDOWN | UserIO.STYLE_HTML, BiancaL.L("container.message.title", "DownloadLinkContainer loaded"), String.format(html, FileSystemUtils.getFileExtension(file).toLowerCase(), BiancaL.L("container.message.title", "DownloadLinkContainer loaded"), BiancaL.L("container.message.uploaded", "Brought to you by"), uploader, BiancaL.L("container.message.created", "Created with"), app, BiancaL.L("container.message.comment", "Comment"), comment, BiancaL.L("container.message.password", "Password"), password)), UserIO.RETURN_OK);

                    }

                }
                progress.doFinalize();
            }
        }.start();
    }

    public int getForbiddenReconnectDownloadNum() {
        boolean allowinterrupt = SubConfiguration.getConfig("DOWNLOAD").getBooleanProperty("PARAM_DOWNLOAD_AUTORESUME_ON_RECONNECT", true);

        int ret = 0;
        ArrayList<DownloadLink> links = DownloadWatchDog.getInstance().getRunningDownloads();
        for (DownloadLink link : links) {
            if (link.getLinkStatus().hasStatus(LinkStatus.DOWNLOADINTERFACE_IN_PROGRESS)) {
                if (!(link.getTransferStatus().supportsResume() && allowinterrupt)) {
                    ret++;
                }
            }
        }
        return ret;
    }

    public ArrayList<FilePackage> getPackages() {
        return EventUtils.getDownloadController().getPackages();
    }

    public boolean isContainerFile(File file) {
        ArrayList<CPluginWrapper> pluginsForContainer = CPluginWrapper.getCWrapper();
        CPluginWrapper pContainer;
        for (int i = 0; i < pluginsForContainer.size(); i++) {
            pContainer = pluginsForContainer.get(i);
            if (pContainer.canHandle(file.getName())) { return true; }
        }
        return false;
    }

    public synchronized void autostartDownloadsonStartup() {
        if (alreadyAutostart == true) { return; }
        alreadyAutostart = true;
        new Thread() {
            @Override
            public void run() {
                this.setName("Autostart counter");
                final ProgressController pc = new ProgressController(BiancaL.L("gui.autostart", "Autostart downloads in few seconds..."));
                pc.getBroadcaster().addListener(new ProgressControllerListener() {
                    public void onProgressControllerEvent(ProgressControllerEvent event) {
                        pc.setStatusText("Autostart aborted!");
                    }
                });
                pc.doFinalize(10 * 1000l);
                while (!pc.isFinished()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                if (!pc.isAbort()) {
                    DownloadWatchDog.getInstance().startDownloads();
                }
            }
        }.start();
    }

}
