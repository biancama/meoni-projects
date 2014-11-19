package com.biancama.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.biancama.log.BiancaLogger;
import com.biancama.utils.DatabaseUtils;
import com.biancama.utils.Property;

public class SubConfiguration extends Property implements Serializable {

    private static final long serialVersionUID = 7803718581558607222L;
    transient private static boolean SUBCONFIG_LOCK = false;
    protected String name;
    transient private ArrayList<ConfigurationListener> listener = null;
    transient private static HashMap<String, SubConfiguration> SUB_CONFIGS = new HashMap<String, SubConfiguration>();

    /**
     * adds a configurationlistener to this subconfig. A configurationlistener
     * gets informed before, AND after each save process.
     * 
     * @param listener
     */
    public void addConfigurationListener(ConfigurationListener listener) {
        if (this.listener == null) {
            this.listener = new ArrayList<ConfigurationListener>();
        }
        this.removeConfigurationListener(listener);
        this.listener.add(listener);

    }

    private void fireEventPreSave() {
        if (listener == null) { return; }
        for (ConfigurationListener listener : this.listener) {
            listener.onPreSave(this);
        }

    }

    private void fireEventPostSave() {
        if (listener == null) { return; }
        for (ConfigurationListener listener : this.listener) {
            listener.onPostSave(this);
        }

    }

    public void removeConfigurationListener(ConfigurationListener listener) {
        if (listener == null) { return; }
        this.listener.remove(listener);

    }

    public SubConfiguration() {

    }

    @SuppressWarnings("unchecked")
    public SubConfiguration(String name) {

        this.name = name;

        Object props = DatabaseUtils.getDatabaseConnector().getData(name);
        if (props != null) {
            this.setProperties((HashMap<String, Object>) props);

        }

    }

    public void save() {
        this.fireEventPreSave();
        DatabaseUtils.getDatabaseConnector().saveConfiguration(name, this.getProperties());
        this.fireEventPostSave();
        changes = false;
    }

    @Override
    public String toString() {
        return name;
    }

    public synchronized static SubConfiguration getConfig(String name) {
        if (SUBCONFIG_LOCK) {

            BiancaLogger.exception(new Exception("Static Database init error!!"));
        }
        SUBCONFIG_LOCK = true;
        try {

            if (SUB_CONFIGS.containsKey(name)) { return SUB_CONFIGS.get(name); }

            SubConfiguration cfg = new SubConfiguration(name);

            SUB_CONFIGS.put(name, cfg);
            cfg.save();
            return cfg;

        } finally {
            SubConfiguration.SUBCONFIG_LOCK = false;
        }

    }

    /**
     * Gets a Subconfiguration for this class
     * 
     * @param object
     * @return
     */
    public static SubConfiguration getConfig(Object object) {

        return getConfig(object.getClass().getSimpleName());
    }

}
