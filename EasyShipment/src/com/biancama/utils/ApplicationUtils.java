package com.biancama.utils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.biancama.HostPluginWrapper;
import com.biancama.config.Configuration;
import com.biancama.gui.easyShipment.plugins.CPluginWrapper;
import com.biancama.gui.easyShipment.plugins.OptionalPluginWrapper;
import com.biancama.log.BiancaLogger;
import com.biancama.plugins.PluginForHost;
import com.biancama.plugins.PluginsC;
import com.biancama.utils.gui.ExecuterUtils;
import com.biancama.utils.gui.io.BiancaIO;
import com.biancama.utils.locale.BiancaL;

public class ApplicationUtils {
    private ApplicationUtils() {
    }

    private static String[] APPLICATION_ARGUMENTS = new String[1];
    public static Configuration CONFIGURATION = null;
    private static String REVISION;
    private static HashMap<String, PluginsC> CONTAINER_PLUGINS = new HashMap<String, PluginsC>();

    public static void setAppArgs(String[] args) {
        APPLICATION_ARGUMENTS = args;
    }

    public static String[] getAppArgs() {
        return APPLICATION_ARGUMENTS;
    }

    public static void restartApplication(final boolean tinybypass) {
        new Thread(new Runnable() {
            public void run() {
                if (EventUtils.getController() != null) {
                    EventUtils.getController().prepareShutdown(false);
                }

                List<String> lst = ManagementFactory.getRuntimeMXBean().getInputArguments();
                ArrayList<String> jargs = new ArrayList<String>();

                boolean xmxset = false;
                boolean xmsset = false;
                boolean useconc = false;
                boolean minheap = false;
                boolean maxheap = false;
                System.out.println("RESTART NOW");
                for (String h : lst) {
                    if (h.contains("Xmx")) {
                        xmxset = true;
                        if (Runtime.getRuntime().maxMemory() < 533000000) {
                            jargs.add("-Xmx512m");
                            continue;
                        }
                    } else if (h.contains("xms")) {
                        xmsset = true;
                    } else if (h.contains("XX:+useconc")) {
                        useconc = true;
                    } else if (h.contains("minheapfree")) {
                        minheap = true;
                    } else if (h.contains("maxheapfree")) {
                        maxheap = true;
                    }
                    jargs.add(h);
                }
                if (!xmxset) {
                    jargs.add("-Xmx512m");
                }
                if (OSDetector.isLinux()) {
                    if (!xmsset) {
                        jargs.add("-Xms64m");
                    }
                    if (!useconc) {
                        jargs.add("-XX:+UseConcMarkSweepGC");
                    }
                    if (!minheap) {
                        jargs.add("-XX:MinHeapFreeRatio=0");
                    }
                    if (!maxheap) {
                        jargs.add("-XX:MaxHeapFreeRatio=0");
                    }
                }
                jargs.add("-jar");
                jargs.add("EasyShipment.jar");

                String[] javaArgs = jargs.toArray(new String[jargs.size()]);
                String[] finalArgs = new String[APPLICATION_ARGUMENTS.length + javaArgs.length];
                System.arraycopy(javaArgs, 0, finalArgs, 0, javaArgs.length);
                System.arraycopy(APPLICATION_ARGUMENTS, 0, finalArgs, javaArgs.length, APPLICATION_ARGUMENTS.length);

                ArrayList<File> restartfiles = BiancaIO.listFiles(URLUtils.getResourceFile("update"));
                String javaPath = new File(new File(System.getProperty("sun.boot.library.path")), "javaw.exe").getAbsolutePath();
                if (restartfiles != null && restartfiles.size() > 0 || tinybypass) {

                    if (OSDetector.isMac()) {
                        BiancaLogger.getLogger().info(ExecuterUtils.runCommand("java", new String[] { "-jar", "tools/tinyupdate.jar", "-restart" }, URLUtils.getResourceFile(".").getAbsolutePath(), 0));
                    } else {

                        if (new File(javaPath).exists()) {
                            BiancaLogger.getLogger().info(ExecuterUtils.runCommand(javaPath, new String[] { "-jar", "tools/tinyupdate.jar", "-restart" }, URLUtils.getResourceFile(".").getAbsolutePath(), 0));

                        } else {
                            BiancaLogger.getLogger().info(ExecuterUtils.runCommand("java", new String[] { "-jar", "tools/tinyupdate.jar", "-restart" }, URLUtils.getResourceFile(".").getAbsolutePath(), 0));

                        }

                    }
                } else {
                    if (OSDetector.isMac()) {
                        BiancaLogger.getLogger().info(ExecuterUtils.runCommand("open", new String[] { "-n", "easyShipment.app" }, URLUtils.getResourceFile(".").getParentFile().getParentFile().getParentFile().getParentFile().getAbsolutePath(), 0));
                    } else {
                        if (new File(javaPath).exists()) {
                            BiancaLogger.getLogger().info(ExecuterUtils.runCommand(javaPath, finalArgs, URLUtils.getResourceFile(".").getAbsolutePath(), 0));
                        } else {
                            BiancaLogger.getLogger().info(ExecuterUtils.runCommand("java", finalArgs, URLUtils.getResourceFile(".").getAbsolutePath(), 0));

                        }
                    }
                }
                System.out.println("EXIT NOW");
                System.exit(0);
            }
        }).start();

    }

    public static void restartApplicationAndWait() {
        restartApplication(false);
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        }
    }

    public static String getTitle() {
        StringBuilder ret = new StringBuilder("Easy Shipment");
        int i = WebUpdate.getWaitingUpdates();
        if (i > 0) {
            ret.append(new char[] { ' ', '(' });
            ret.append(BiancaL.LF("gui.mainframe.title.updatemessage2", "%s Updates available", i));
            ret.append(')');
        }

        return ret.toString();

    }

    public static OptionalPluginWrapper getOptionalPlugin(String id) {
        for (OptionalPluginWrapper wrapper : OptionalPluginWrapper.getOptionalWrapper()) {
            if (wrapper.getID() != null && wrapper.getID().equalsIgnoreCase(id)) { return wrapper; }
        }
        return null;
    }

    public static Configuration getConfiguration() {
        if (CONFIGURATION == null) {
            CONFIGURATION = new Configuration();
        }
        return CONFIGURATION;
    }

    public static void setConfiguration(Configuration configuration) {
        CONFIGURATION = configuration;
    }

    public static String getRevision() {
        if (REVISION != null) { return REVISION; }
        int rev = -1;
        try {
            rev = FormatterUtils.filterInt(FileSystemUtils.readFileToString(URLUtils.getResourceFile("config/version.cfg")));
        } catch (Throwable t) {
            t.printStackTrace();
        }

        int rev2 = Integer.parseInt(FormatterUtils.getRevision("$Revision: 9608 $"));

        double r = Math.max(rev2, rev) / 1000.0;
        return REVISION = new DecimalFormat("0.000").format(r).replace(",", ".");
    }

    public static PluginForHost getNewPluginForHostInstance(String host) {
        for (HostPluginWrapper pHost : HostPluginWrapper.getHostWrapper()) {
            if (pHost.getHost().equals(host.toLowerCase())) { return (PluginForHost) pHost.getNewPluginInstance(); }
        }
        return null;
    }

    public static PluginsC getPluginForContainer(String container, String containerPath) {
        if (containerPath != null && CONTAINER_PLUGINS.containsKey(containerPath)) { return CONTAINER_PLUGINS.get(containerPath); }
        PluginsC ret = null;
        for (CPluginWrapper act : CPluginWrapper.getCWrapper()) {
            if (act.getHost().equalsIgnoreCase(container)) {

                ret = (PluginsC) act.getNewPluginInstance();
                if (containerPath != null) {
                    CONTAINER_PLUGINS.put(containerPath, ret);
                }
                return ret;

            }
        }
        return null;
    }

}
