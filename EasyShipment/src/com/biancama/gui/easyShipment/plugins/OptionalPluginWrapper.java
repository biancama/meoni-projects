package com.biancama.gui.easyShipment.plugins;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import com.biancama.config.jar.BiancaClassLoader;
import com.biancama.log.BiancaLogger;
import com.biancama.plugins.OptionalPlugin;
import com.biancama.plugins.PluginOptional;
import com.biancama.utils.FileSystemUtils;
import com.biancama.utils.FormatterUtils;
import com.biancama.utils.JavaUtils;
import com.biancama.utils.locale.BiancaL;

public class OptionalPluginWrapper extends PluginWrapper {

    private static final ArrayList<OptionalPluginWrapper> OPTIONAL_WRAPPER = new ArrayList<OptionalPluginWrapper>();

    public static ArrayList<OptionalPluginWrapper> getOptionalWrapper() {
        return OPTIONAL_WRAPPER;
    }

    private final double version;
    private final String id;
    private final String name;
    private final String revision;
    private final OptionalPlugin annotation;

    public OptionalPluginWrapper(Class<?> c, OptionalPlugin help) {

        super(c.getName(), null, c.getName(), null, 0);
        this.id = help.id();
        revision = FormatterUtils.getRevision(help.rev());
        this.version = help.minJVM();
        this.name = BiancaL.L(c.getName(), c.getSimpleName());
        this.annotation = help;

        try {

            logger.finer("OPTIONAL loaded " + help);
            OPTIONAL_WRAPPER.add(this);

            if (this.isEnabled()) {
                this.getPlugin();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public OptionalPlugin getAnnotation() {
        return annotation;
    }

    public double getJavaVersion() {
        return version;
    }

    @Override
    public String getHost() {
        return name;
    }

    /**
     * returns the addon's version (revision)
     */
    @Override
    public String getVersion() {
        // TODO
        return revision;
    }

    @Override
    public PluginOptional getPlugin() {
        if (!OPTIONAL_WRAPPER.contains(this)) { return null; }
        if (loadedPlugin == null) {
            loadPlugin();
        }
        return (PluginOptional) loadedPlugin;
    }

    private PluginOptional loadPlugin() {
        BiancaClassLoader jdClassLoader = FileSystemUtils.getJDClassLoader();
        Double version = JavaUtils.getJavaVersion();

        if (version < this.version) {
            logger.finer("Plugin " + this.getClassName() + " requires Java Version " + this.version + " your Version is: " + version);
            return null;
        }
        logger.finer("Try to initialize " + this.getClassName());
        try {

            Class<?> plgClass = jdClassLoader.loadClass(this.getClassName());
            if (plgClass == null) {
                logger.info("PLUGIN NOT FOUND!");
                return null;
            }
            Class<?>[] classes = new Class[] { PluginWrapper.class };
            Constructor<?> con = plgClass.getConstructor(classes);

            try {

                this.loadedPlugin = (PluginOptional) con.newInstance(new Object[] { this });
                logger.finer("Successfully loaded " + this.getClassName());
                return (PluginOptional) loadedPlugin;

            } catch (Exception e) {
                BiancaLogger.exception(e);
                logger.severe("Addon " + this.getClassName() + " is outdated and incompatible. Please update(Packagemanager) :" + e.getLocalizedMessage());
            }

        } catch (Exception e) {
            logger.info("Plugin Exception!");
            BiancaLogger.exception(e);
        }
        return null;

    }

    @Override
    public String getID() {
        return id;
    }

    public String getConfigParamKey() {
        return "OPTIONAL_PLUGIN2_" + id;
    }

    @Override
    public int compareTo(PluginWrapper plg) {
        return getHost().toLowerCase().compareTo(plg.getHost().toLowerCase());
    }

    @Override
    public boolean isEnabled() {
        return FileSystemUtils.getConfiguration().getBooleanProperty(getConfigParamKey(), annotation.defaultEnabled());
    }

}
