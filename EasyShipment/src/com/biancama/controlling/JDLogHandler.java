//    jDownloader - Downloadmanager
//    Copyright (C) 2009  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.biancama.controlling;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import com.biancama.events.ControlEvent;
import com.biancama.utils.EventUtils;

public class JDLogHandler extends Handler {

    public static JDLogHandler getHandler() {
        if (HANDLER == null) {
            HANDLER = new JDLogHandler();
        }
        return HANDLER;
    }

    private static JDLogHandler HANDLER = null;
    private final ArrayList<LogRecord> buffer;

    private JDLogHandler() {
        super();
        buffer = new ArrayList<LogRecord>();
    }

    public ArrayList<LogRecord> getBuffer() {
        return buffer;
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void publish(LogRecord logRecord) {
        this.buffer.add(logRecord);
        if (EventUtils.getController() != null) {
            EventUtils.getController().fireControlEvent(ControlEvent.CONTROL_LOG_OCCURED, logRecord);
        }
    }

}
