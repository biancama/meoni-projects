package com.biancama.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Logger;

import com.biancama.events.ControlEvent;
import com.biancama.log.BiancaLogger;

public class Property implements Serializable {
    private static final long serialVersionUID = -6093927038856757256L;
    /**
     * Nullvalue used to remove a key completly.
     */
    public static final Object NULL = new Object();

    protected transient Logger logger = null;

    private HashMap<String, Object> properties;
    private HashMap<String, Integer> propertiesHashes;

    protected transient boolean changes = false;

    public Property() {
        properties = new HashMap<String, Object>();
        propertiesHashes = new HashMap<String, Integer>();

        logger = BiancaLogger.getLogger();
    }

    public Property(String value, Object obj) {
        this();
        setProperty(value, obj);
    }

    /**
     * Returns the saved object casted to the type of the defaultvalue
     * <code>def</code>. So no more casts are necessary.
     * 
     * @param <E>
     *            type of the saved object
     * @param key
     *            key for the saved object
     * @param def
     *            defaultvalue if no object is saved (is used to determine the
     *            type of the saved object)
     * @return the saved object casted to its correct type
     */
    @SuppressWarnings("unchecked")
    public <E> E getGenericProperty(String key, E def) {
        Object r = getProperty(key, def);
        try {
            E ret = (E) r;
            return ret;
        } catch (Exception e) {
            logger.finer("Could not cast " + r.getClass().getSimpleName() + " to " + e.getClass().getSimpleName() + " for key " + key);
            return def;
        }
    }

    /**
     * Gibt einen Boolean zu key zurück. Es wird versuchtden Wert zu einem
     * passendem Wert umzuformen
     * 
     * @param key
     * @return
     */
    public Boolean getBooleanProperty(String key) {
        return getBooleanProperty(key, false);
    }

    public Boolean getBooleanProperty(String key, boolean def) {
        try {
            Object r = getProperty(key, def);
            if (!(r instanceof Boolean)) {
                r = r + "";
                if (((String) r).equals("false")) {
                    r = false;
                } else {
                    r = ((String) r).length() > 0;
                }
            }
            Boolean ret = (Boolean) r;
            return ret;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gibt einen Doublewert zu key zurück. Es wird versuchtden Wert zu einem
     * passendem Wert umzuformen
     * 
     * @param key
     * @return
     */
    public Double getDoubleProperty(String key) {
        return getDoubleProperty(key, -1.0);
    }

    public Double getDoubleProperty(String key, Double def) {
        try {
            Object r = getProperty(key, def);
            if (r instanceof String) {
                r = Double.parseDouble((String) r);
            }
            Double ret = (Double) r;
            return ret;
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Gibt einen Integerwert zu key zurück. Es wird versucht, den Wert zu
     * einem passendem Integer umzuformen
     * 
     * @param key
     *            Schlüssel des Wertes
     * @return Der Wert
     */
    public int getIntegerProperty(String key) {
        return getIntegerProperty(key, -1);
    }

    public int getIntegerProperty(String key, int def) {
        try {
            Object r = getProperty(key, def);
            if (r instanceof String) {
                r = Integer.parseInt((String) r);
            }
            Integer ret = (Integer) r;
            return ret;
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Gibt die interne Properties HashMap zurück
     */
    public HashMap<String, Object> getProperties() {
        return properties;
    }

    /**
     * Gibt den Wert zu key zurück
     * 
     * @param key
     * @return Value zu key
     */
    public Object getProperty(String key) {
        if (properties == null) {
            properties = new HashMap<String, Object>();
        }

        return properties.get(key);
    }

    /**
     * Gibt den Wert zu key zurück und falls keiner festgelegt ist def
     * 
     * @param key
     * @param def
     * @return value
     */
    public Object getProperty(String key, Object def) {
        if (properties == null) {
            properties = new HashMap<String, Object>();
        }
        if (properties.get(key) == null) {
            setProperty(key, def);
            return def;
        }
        return properties.get(key);
    }

    /**
     * Gibt einen String zu key zurück. Es wird versuchtden Wert zu einem
     * passendem Wert umzuformen
     * 
     * @param key
     * @return
     */
    public String getStringProperty(String key) {
        return getStringProperty(key, null);
    }

    public String getStringProperty(String key, String def) {
        try {
            Object r = getProperty(key, def);
            String ret = (r == null) ? null : r.toString();
            return ret;
        } catch (Exception e) {
            return def;
        }
    }

    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    /**
     * setzt die interne prperties hashMap
     * 
     * @param properties
     */
    public void setProperties(HashMap<String, Object> properties) {
        this.properties = properties;
        propertiesHashes = new HashMap<String, Integer>();
    }

    /**
     * Speichert einen Wert ab.
     * 
     * @param key
     * @param value
     */
    @SuppressWarnings("unchecked")
    public void setProperty(String key, Object value) {

        if (value == NULL) {
            if (properties.containsKey(key)) {
                properties.remove(key);
                propertiesHashes.remove(key);
                EventUtils.getController().fireControlEvent(new ControlEvent(this, ControlEvent.CONTROL_PROPERTY_CHANGED, key));
                this.changes = true;

            }
            return;

        }
        if (properties == null) {
            properties = new HashMap<String, Object>();
        }
        if (propertiesHashes == null) {
            propertiesHashes = new HashMap<String, Integer>();
        }

        Object old = getProperty(key);

        properties.put(key, value);

        Integer oldHash = propertiesHashes.get(key);

        /*
         * check for null to avoid nullpointer due to .toString() method
         */
        propertiesHashes.put(key, (value == null) ? null : value.toString().hashCode());

        if (EventUtils.getController() == null) { return; }
        try {
            if (old == null && value != null) {
                EventUtils.getController().fireControlEvent(new ControlEvent(this, ControlEvent.CONTROL_PROPERTY_CHANGED, key));
                this.changes = true;
                return;
            } else if (value instanceof Comparable) {
                if (((Comparable<Comparable<?>>) value).compareTo((Comparable<?>) old) != 0) {
                    EventUtils.getController().fireControlEvent(new ControlEvent(this, ControlEvent.CONTROL_PROPERTY_CHANGED, key));
                    this.changes = true;
                }
                return;
            } else {
                if (!value.equals(old) || oldHash != value.hashCode()) {
                    EventUtils.getController().fireControlEvent(new ControlEvent(this, ControlEvent.CONTROL_PROPERTY_CHANGED, key));
                    this.changes = true;
                }
                return;
            }
        } catch (Exception e) {
            EventUtils.getController().fireControlEvent(new ControlEvent(this, ControlEvent.CONTROL_PROPERTY_CHANGED, key));
            this.changes = true;
        }
    }

    public boolean hasChanges() {
        return changes;
    }

    /**
     * GIbt die Proprties als String zurück
     * 
     * @return PropertyString
     */
    // @Override
    @Override
    public String toString() {
        if (properties.size() == 0) { return ""; }
        return "Property: " + properties;
    }

}
