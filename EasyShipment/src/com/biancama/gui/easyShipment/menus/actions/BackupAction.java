package com.biancama.gui.easyShipment.menus.actions;

import java.awt.event.ActionEvent;

import com.biancama.events.BiancaController;
import com.biancama.gui.swing.actions.ThreadedAction;
import com.biancama.gui.swing.components.Balloon;
import com.biancama.update.BiancaUpdateUtils;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.locale.BiancaL;

public class BackupAction extends ThreadedAction {

    private static final long serialVersionUID = 823930266263085474L;

    public BackupAction() {
        super("action.backup", "gui.images.save");
    }

    @Override
    public void init() {
    }

    @Override
    public void initDefaults() {
    }

    @Override
    public void threadedActionPerformed(ActionEvent e) {
        BiancaController.getInstance().syncDatabase();
        BiancaUpdateUtils.backupDataBase();
        Balloon.show(BiancaL.L("gui.balloon.backup.title", "Backup"), BiancaTheme.II("gui.images.save", 32, 32), BiancaL.LF("gui.backup.finished", "Linklist successfully backuped!"));
    }
}
