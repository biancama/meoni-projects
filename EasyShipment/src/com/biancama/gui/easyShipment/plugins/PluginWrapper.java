package com.biancama.gui.easyShipment.plugins;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.biancama.EasyShipmentInitFlags;
import com.biancama.config.SubConfiguration;
import com.biancama.controlling.DownloadController;
import com.biancama.log.BiancaLogger;
import com.biancama.plugins.Plugin;
import com.biancama.utils.EventUtils;
import com.biancama.utils.FileSystemUtils;
import com.biancama.utils.FlagsUtils;
import com.biancama.utils.URLUtils;
import com.biancama.utils.locale.BiancaL;

public abstract class PluginWrapper implements Comparable<PluginWrapper> {
    /**
     * Usage Flag for {@link jd.PluginWrapper.PluginWrapper(String, String,
     * String, String, int)}<br>
     */
    public static final int LOAD_ON_INIT = 1 << 1;
    /**
     * By default, a plugin pattern must macht a valid URL. Sometimes it is
     * required to match a javascript expression or anything like this. Use this
     * flag to tell the plugin that it should accept invalid URLs
     */
    public static final int PATTERN_ACCEPTS_INVALID_URI = 1 << 2;
    /**
     * By default, plugins can be disabled. But in some cases plugins should not
     * be disabled for controlling reasons. Use this flag to prevent the plugin
     * from disabeling
     */
    public static final int ALWAYS_ENABLED = 1 << 3;
    /**
     * See http://wiki.jdownloader.org/knowledge/wiki/glossary/cnl2 for cnl2
     * details. If a Decrypter uses CNL2, we can think about activating this
     * feature here. JDownloader then will only decrypt indriect or
     * deepencrypted links. Direct links will be opened in th systems
     * defaultbrowser to use CNL
     */
    public static final int CNL_2 = 1 << 4;
    /**
     * Load only if debug flag is set. For internal developer plugins
     */
    public static final int DEBUG_ONLY = 1 << 4;

    /**
     * The Regular expression pattern. This pattern defines which urls can be
     * handeled by this plugin
     */
    private Pattern pattern;
    /**
     * The domain od this plugin, which is the plugin's name, too
     */
    private final String host;
    /**
     * Full qualified classname
     */
    private final String className;
    /**
     * internal logger instance
     */
    protected Logger logger = BiancaLogger.getLogger();
    /**
     * field to cache the plugininstance if it is loaded already
     */
    protected Plugin loadedPlugin = null;
    /**
     * @see PluginWrapper#PATTERN_ACCEPTS_INVALID_URI
     */
    private boolean acceptOnlyURIs = true;
    /**
     * @see PluginWrapper#ALWAYS_ENABLED
     */
    private boolean alwaysenabled = false;
    /**
     * Usage and InitFlags created by <br>{@link PluginWrapper#CNL_2} <br>
     * {@link PluginWrapper#DEBUG_ONLY} <br> {@link PluginWrapper#LOAD_ON_INIT} <br>
     * {@link PluginWrapper#PATTERN_ACCEPTS_INVALID_URI}
     */
    private final int flags;
    /**
     * Static classloader. gets created when the first plugin should be
     * initiated.
     * 
     */
    private static URLClassLoader CL;

    /**
     * Static map where all pluginwrapper register themselves with key =
     * {@link PluginWrapper#className}
     */
    private static final HashMap<String, PluginWrapper> WRAPPER = new HashMap<String, PluginWrapper>();

    /**
     * Creates a new wrapper
     * 
     * @param host
     *            domain of the plugin in lowercase
     * @param classNamePrefix
     *            package or null if classname is already fully qualifdied
     * @param classNameClassname
     *            of the plugin. or fully quialified Classpath
     * @param pattern
     *            {@link jd.PluginWrapper#pattern}
     * @param flags
     *            a integer wich has been created by <br>{@link PluginWrapper#CNL_2} <br>
     *            {@link PluginWrapper#DEBUG_ONLY} <br>
     *            {@link PluginWrapper#LOAD_ON_INIT} <br>
     *            {@link PluginWrapper#PATTERN_ACCEPTS_INVALID_URI}
     */
    public PluginWrapper(String host, String classNamePrefix, String className, String pattern, int flags) {
        String classn = (classNamePrefix == null ? "" : classNamePrefix) + className;
        if (pattern != null) {
            this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        }
        this.host = host.toLowerCase();
        this.className = classn;
        this.flags = flags;
        if (FlagsUtils.hasSomeFlags(flags, LOAD_ON_INIT)) {
            this.getPlugin();
        }
        if (FlagsUtils.hasSomeFlags(flags, ALWAYS_ENABLED)) {
            this.alwaysenabled = true;
        }
        if (FlagsUtils.hasSomeFlags(flags, PATTERN_ACCEPTS_INVALID_URI)) {
            this.acceptOnlyURIs = false;
        }
        if (FlagsUtils.hasNoFlags(flags, DEBUG_ONLY) || EasyShipmentInitFlags.SWITCH_DEBUG) {

            WRAPPER.put(classn, this);
        }
    }

    /**
     * Should always return lifetime id. This means that this id never changes!
     * It is used as config key to save settings
     * 
     * @return
     */
    public String getID() {
        return getHost();
    }

    /**
     * instanciates the wrapped plugin. Tries to update the plugin before
     * loading. the new instance is cached, so the whol update process will only
     * be done once
     * 
     * @return plugin instance
     */
    public synchronized Plugin getPlugin() {
        if (loadedPlugin != null) { return loadedPlugin; }
        try {
            if (CL == null) {
                CL = new URLClassLoader(new URL[] { FileSystemUtils.getHomeDirectoryFromEnvironment().toURI().toURL(), URLUtils.getResourceFile("java").toURI().toURL() }, Thread.currentThread().getContextClassLoader());
            }
            logger.finer("load plugin: " + getClassName());
            Class<?> plgClass;
            try {
                plgClass = CL.loadClass(getClassName());
            } catch (ClassNotFoundException e) {
                // fallback classloader.
                logger.severe("Fallback cloassloader used");
                plgClass = FileSystemUtils.getJDClassLoader().loadClass(getClassName());
            }

            if (plgClass == null) {
                logger.severe("PLUGIN " + this.getClassName() + "NOT FOUND!");
                return null;
            }
            Class<?>[] classes = new Class[] { PluginWrapper.class };
            Constructor<?> con = plgClass.getConstructor(classes);
            classes = null;
            this.loadedPlugin = (Plugin) con.newInstance(new Object[] { this });

            return loadedPlugin;
        } catch (Exception e) {
            logger.severe("Plugin Exception!");
            BiancaLogger.exception(e);
        }
        return null;
    }

    /**
     * Returns the VersionID for this plugin. it may be only available after
     * loading the plugin. BUT to date, revisions are autoset in the annotations
     * and will return the correct version without loading the plugin
     * 
     * @return
     */
    abstract public String getVersion();

    /**
     * 
     * @return "idle" if the plugin has not been loaded or
     *         {@link com.biancama.plugins.Plugin#getCoder()}
     */
    public String getCoder() {
        return loadedPlugin != null ? loadedPlugin.getCoder() : BiancaL.L("plugin.system.notloaded", "idle");
    }

    /**
     * if {@link #alwaysenabled} is enabled this method will return true. Else
     * the plugin config is used. By default, plugins are enabled.
     * 
     * @return
     */
    public boolean isEnabled() {
        return this.alwaysenabled || getPluginConfig().getBooleanProperty("USE_PLUGIN", true);
    }

    /**
     * if {@link #alwaysenabled} is enabled, this method will be ignored, else
     * the plugin can be enabled or disabled here
     * 
     * @param bool
     */
    public void setEnabled(boolean bool) {
        if (this.alwaysenabled) { return; }
        getPluginConfig().setProperty("USE_PLUGIN", bool);
        getPluginConfig().save();
        if (EventUtils.getController() != null) {
            DownloadController.getInstance().fireGlobalUpdate();
        }
    }

    /**
     * 
     * @param data
     *            any stringdata
     * @return true if data contains a match to {@link #pattern}
     */
    public boolean canHandle(String data) {
        if (this.isLoaded()) { return getPlugin().canHandle(data); }
        if (data == null) { return false; }
        Pattern pattern = this.getPattern();
        if (pattern != null) {
            Matcher matcher = pattern.matcher(data);
            if (matcher.find()) { return true; }
        }
        return false;
    }

    /**
     * 
     * @return true if the plugin is already loaded
     * @see #getPlugin()
     */
    public boolean isLoaded() {
        return this.loadedPlugin != null;
    }

    /**
     * Returns the plugins Subconfiguration
     * 
     * @return
     */

    public SubConfiguration getPluginConfig() {
        return SubConfiguration.getConfig(getHost());
    }

    /**
     * Creates a NEW instance of the plugin
     * 
     * @return
     */

    public Plugin getNewPluginInstance() {
        try {
            return getPlugin().getClass().getConstructor(new Class[] { PluginWrapper.class }).newInstance(new Object[] { this });
        } catch (Exception e) {
            BiancaLogger.exception(e);
        }
        return null;
    }

    /**
     * 
     * @return true if the plugin is already {@link #isLoaded() loaded} and
     *         there are {@link jd.config.ConfigEntry}s defined.
     */
    public boolean hasConfig() {
        return isLoaded() && !getPlugin().getConfig().getEntries().isEmpty();
    }

    /**
     * Delegates the compareTo functionality to hostA.compareTo(hostB)
     */
    public int compareTo(PluginWrapper plg) {
        return getHost().toLowerCase().compareTo(plg.getHost().toLowerCase());
    }

    /**
     * The name of the config. This should be unique for the plugin!
     * 
     * @return
     */
    public String getConfigName() {
        return getHost();
    }

    /**
     * Static getter. All pluginwrapper are cached in {@link #WRAPPER}
     * 
     * @param clazz
     *            fully qualified {@link #className}
     * @return
     */
    public static PluginWrapper getWrapper(String clazz) {
        return WRAPPER.get(clazz);
    }

    /**
     * Creates a new instance of the plugin with classname
     * 
     * @param className
     *            ully qualified {@link #className}
     * @return
     */
    public static Plugin getNewInstance(String className) {
        if (!WRAPPER.containsKey(className)) {
            try {
                throw new Exception("plugin " + className + " could not be found");
            } catch (Exception e) {
                BiancaLogger.exception(e);
                return null;
            }
        }
        return WRAPPER.get(className).getNewPluginInstance();
    }

    /**
     * @return the {@link PluginWrapper#pattern}
     * @see PluginWrapper#pattern
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * @return the {@link PluginWrapper#host}
     * @see PluginWrapper#host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the {@link PluginWrapper#alwaysenabled}
     * @see PluginWrapper#alwaysenabled
     */
    public boolean isAlwaysenabled() {
        return alwaysenabled;
    }

    /**
     * @return the {@link PluginWrapper#flags}
     * @see PluginWrapper#flags
     */
    public int getFlags() {
        return flags;
    }

    /**
     * @return the {@link PluginWrapper#className}
     * @see PluginWrapper#className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the {@link PluginWrapper#acceptOnlyURIs}
     * @see PluginWrapper#acceptOnlyURIs
     */
    public boolean isAcceptOnlyURIs() {
        return acceptOnlyURIs;
    }

}
