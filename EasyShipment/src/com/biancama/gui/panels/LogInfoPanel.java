package com.biancama.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.biancama.gui.swing.Factory;
import com.biancama.gui.swing.GuiRunnable;
import com.biancama.log.BiancaLogger;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.locale.BiancaL;

public class LogInfoPanel extends InfoPanel implements ActionListener {

    private static final long serialVersionUID = -1910950245889164423L;
    private static final String BiancaL_PREFIX = "jd.gui.swing.jdgui.views.info.LogInfoPanel.";

    public static final int ACTION_SAVE = 1;
    public static final int ACTION_UPLOAD = 2;

    private final JButton btnSave;
    private final JButton btnUpload;
    private int severeCount = 0;
    private int warningCount = 0;
    private int httpCount = 0;
    private int exceptionCount = 0;

    public LogInfoPanel() {
        super();
        this.setIcon(BiancaTheme.II("gui.images.taskpanes.log", 32, 32));

        btnSave = Factory.createButton(BiancaL.L(BiancaL_PREFIX + "save", "Save Log As"), BiancaTheme.II("gui.images.save", 16, 16), this);
        btnUpload = Factory.createButton(BiancaL.L(BiancaL_PREFIX + "upload", "Upload Log"), BiancaTheme.II("gui.images.upload", 16, 16), this);
        this.addInfoEntry("", BiancaL.LF(BiancaL_PREFIX + "loglevel", "Log Level %s", BiancaLogger.getLogger().getLevel().getLocalizedName()), 0, 0);
        addComponent(btnSave, 1, 0);
        addComponent(btnUpload, 1, 1);
        this.addInfoEntry(BiancaL.L(BiancaL_PREFIX + "info.severe", "Error(s)"), severeCount + "", 2, 0);
        this.addInfoEntry(BiancaL.L(BiancaL_PREFIX + "info.warning", "Warning(s)"), warningCount + "", 2, 1);

        this.addInfoEntry(BiancaL.L(BiancaL_PREFIX + "info.warninghttp", "HTTP Notify"), httpCount + "", 3, 0);
        this.addInfoEntry(BiancaL.L(BiancaL_PREFIX + "info.exceptions", "Fatal error(s)"), exceptionCount + "", 3, 1);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSave) {
            this.broadcastEvent(new ActionEvent(this, ACTION_SAVE, e.getActionCommand()));
        } else if (e.getSource() == btnUpload) {
            this.broadcastEvent(new ActionEvent(this, ACTION_UPLOAD, e.getActionCommand()));
        }
    }

    /**
     * @param severeCount
     *            the severeCount to set
     */
    public void setSevereCount(int severeCount) {
        this.severeCount = severeCount;
    }

    /**
     * @return the severeCount
     */
    public int getSevereCount() {
        return severeCount;
    }

    /**
     * @param warningCount
     *            the warningCount to set
     */
    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    /**
     * @return the warningCount
     */
    public int getWarningCount() {
        return warningCount;
    }

    /**
     * @param httpCount
     *            the httpCount to set
     */
    public void setHttpCount(int httpCount) {
        this.httpCount = httpCount;
    }

    /**
     * @return the httpCount
     */
    public int getHttpCount() {
        return httpCount;
    }

    /**
     * @param exceptionCount
     *            the exceptionCount to set
     */
    public void setExceptionCount(int exceptionCount) {
        this.exceptionCount = exceptionCount;
    }

    /**
     * @return the exceptionCount
     */
    public int getExceptionCount() {
        return exceptionCount;
    }

    public void update() {
        new GuiRunnable<Object>() {
            @Override
            public Object runSave() {
                updateInfo("", BiancaL.LF(BiancaL_PREFIX + "loglevel", "Log Level %s", BiancaLogger.getLogger().getLevel().getLocalizedName()));
                updateInfo(BiancaL.L(BiancaL_PREFIX + "info.severe", "Error(s)"), severeCount + "");
                updateInfo(BiancaL.L(BiancaL_PREFIX + "info.warning", "Warning(s)"), warningCount + "");
                updateInfo(BiancaL.L(BiancaL_PREFIX + "info.warninghttp", "HTTP Notify"), httpCount + "");
                updateInfo(BiancaL.L(BiancaL_PREFIX + "info.exceptions", "Fatal error(s)"), exceptionCount + "");
                return null;
            }
        }.start();
    }
}
