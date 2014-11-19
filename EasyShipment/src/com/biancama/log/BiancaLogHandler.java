package com.biancama.log;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import com.biancama.events.ControlEvent;
import com.biancama.utils.EventUtils;

public class BiancaLogHandler extends Handler {

    private static BiancaLogHandler HANDLER = null;
    private final ArrayList<LogRecord> buffer;

    private BiancaLogHandler() {
        super();
        buffer = new ArrayList<LogRecord>();
    }

    public synchronized static BiancaLogHandler getHandler() {
        if (HANDLER == null) {
            HANDLER = new BiancaLogHandler();
        }
        return HANDLER;
    }

    public ArrayList<LogRecord> getBuffer() {
        return buffer;
    }

    @Override
    public void close() throws SecurityException {
        if (buffer != null) {
            buffer.clear();
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void publish(LogRecord record) {
        this.buffer.add(record);
        if (EventUtils.getController() != null) {
            EventUtils.getController().fireControlEvent(ControlEvent.CONTROL_LOG_OCCURED, record);
        }
    }

}
