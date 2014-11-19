package com.biancama.gui.easyShipment.menus;

import com.biancama.gui.easyShipment.menus.actions.BackupAction;
import com.biancama.gui.menu.BiancaStartMenu;

public class SaveMenu extends BiancaStartMenu {
    private static final long serialVersionUID = -153884445300435027L;

    public SaveMenu() {
        super("gui.menu.save", "gui.images.save");
        this.add(new BackupAction());

    }
}
