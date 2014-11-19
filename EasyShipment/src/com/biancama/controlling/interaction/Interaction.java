package com.biancama.controlling.interaction;

import java.io.Serializable;
import java.util.logging.Logger;

import com.biancama.config.SubConfiguration;
import com.biancama.log.BiancaLogger;
import com.biancama.utils.Property;

public abstract class Interaction extends Property implements Serializable {

    protected static Logger logger = BiancaLogger.getLogger();

    private transient static final long serialVersionUID = -5609631258725998799L;
    private static final String CONFIG_INTERACTIONS = "EVENTS";

    private static final String PARAM_INTERACTIONS = "INTERACTIONS";

    public Interaction() {
    }

    protected abstract boolean doInteraction(Object arg);

    public abstract String getInteractionName();

    public abstract void initConfig();

    @Override
    public abstract String toString();

    public static void deleteInteractions() {
        SubConfiguration config = SubConfiguration.getConfig(CONFIG_INTERACTIONS);
        if (config.hasProperty(PARAM_INTERACTIONS)) {
            config.setProperty(PARAM_INTERACTIONS, Property.NULL);
            config.save();
            logger.finer("deleted old saved interactions");
        }
    }

}
