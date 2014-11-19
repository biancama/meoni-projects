package com.biancama.gui.easyShipment.views;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.Icon;

import com.biancama.controlling.JDLogHandler;
import com.biancama.events.ControlEvent;
import com.biancama.events.ControlListener;
import com.biancama.gui.panels.LogInfoPanel;
import com.biancama.gui.swing.views.ClosableView;
import com.biancama.utils.EventUtils;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.locale.BiancaL;

public class LogView extends ClosableView implements ControlListener {

    private static final long serialVersionUID = -4440872942373187410L;

    /**
     * DO NOT MOVE THIS CONSTANT. IT's important to have it in this file for the
     * LFE to parse JDL Keys correct
     */
    private static final String IDENT_PREFIX = "jd.gui.swing.jdgui.views.logview.";

    private static LogView INSTANCE = null;

    private LogInfoPanel lip;

    /**
     * Logview Singleton
     * 
     * @return
     */
    public static LogView getLogView() {
        if (INSTANCE == null) {
            INSTANCE = new LogView();
        }
        return INSTANCE;
    }

    /**
     * @see #getLogView()
     */
    private LogView() {
        super();
        LogPane lp;
        this.setContent(lp = new LogPane());
        this.setDefaultInfoPanel(lip = new LogInfoPanel());
        lip.addActionListener(lp);
        init();
    }

    @Override
    public Icon getIcon() {
        return BiancaTheme.II("gui.images.taskpanes.log", ICON_SIZE, ICON_SIZE);
    }

    @Override
    public String getTitle() {
        return BiancaL.L(IDENT_PREFIX + "tab.title", "Log");
    }

    @Override
    public String getTooltip() {
        return BiancaL.L(IDENT_PREFIX + "tab.tooltip", "See or Upload the Log");
    }

    @Override
    protected void onHide() {
        EventUtils.getController().removeControlListener(this);
    }

    @Override
    protected void onShow() {
        EventUtils.getController().addControlListener(this);
        int severe = 0;
        int warning = 0;
        int exceptions = 0;
        int http = 0;
        ArrayList<LogRecord> buff = JDLogHandler.getHandler().getBuffer();

        for (LogRecord r : buff) {
            if (r.getMessage() != null && r.getMessage().contains("exception")) {
                exceptions++;
            } else if (r.getLevel() == Level.SEVERE) {
                severe++;
            } else if (r.getLevel() == Level.WARNING) {
                warning++;
            } else if (r.getMessage() != null && r.getMessage().contains("--Request--")) {
                http++;
            }
        }
        lip.setSevereCount(severe);
        lip.setWarningCount(warning);
        lip.setExceptionCount(exceptions);
        lip.setHttpCount(http);
        lip.update();
    }

    public void controlEvent(ControlEvent event) {
        if (event.getID() == ControlEvent.CONTROL_LOG_OCCURED) {
            LogRecord r = (LogRecord) event.getParameter();
            if (r.getMessage() != null && r.getMessage().contains("exception")) {
                lip.setExceptionCount(lip.getExceptionCount() + 1);
                lip.update();
            } else if (r.getLevel() == Level.SEVERE) {
                lip.setSevereCount(lip.getSevereCount() + 1);
                lip.update();
            } else if (r.getLevel() == Level.WARNING) {
                lip.setWarningCount(lip.getWarningCount() + 1);
                lip.update();
            } else if (r.getMessage() != null && r.getMessage().contains("--Request--")) {
                lip.setHttpCount(lip.getHttpCount() + 1);
                lip.update();
            }
        }
    }

}
