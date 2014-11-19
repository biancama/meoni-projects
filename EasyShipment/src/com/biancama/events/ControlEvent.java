package com.biancama.events;

import java.awt.AWTEvent;
import java.awt.Event;

public class ControlEvent extends AWTEvent {
    public static final int CONTROL_LOG_OCCURED = 29;
    public static final int CONTROL_PROPERTY_CHANGED = 27;
    public static final int CONTROL_SYSTEM_EXIT = 26;
    public static final int CONTROL_SYSTEM_SHUTDOWN_PREPARED = 261;
    public static final int CONTROL_INTERACTION_CALL = 28;
    public final static int CONTROL_PLUGIN_ACTIVE = 5;
    public final static int CONTROL_PLUGIN_INACTIVE = 4;
    public static final int CONTROL_INIT_COMPLETE = 30;
    public static final int CONTROL_ON_PROGRESS = 24;
    public static final int CONTROL_DOWNLOAD_START = 13;
    public static final int CONTROL_ALL_DOWNLOADS_FINISHED = 1;
    public static final int CONTROL_DOWNLOAD_STOP = 6;
    public static final int CONTROL_JDPROPERTY_CHANGED = 27;
    public static final int CONTROL_BEFORE_RECONNECT = 2;
    public static final int CONTROL_AFTER_RECONNECT = 3;
    /**
     * Event identification
     */
    protected int id;
    /**
     * optional parameter
     */
    private Object parameter;

    public ControlEvent(Event event) {
        super(event);
    }

    public ControlEvent(Object source, int id) {
        this(source, id, null);
    }

    public ControlEvent(Object source, int id, Object parameter) {
        super(source, id);
        this.id = id;
        this.parameter = parameter;
    }

    public Object getParameter() {
        return parameter;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String toString() {
        return "[source:" + source + ", controlID:" + id + ", parameter:" + parameter + "]";
    }
}
