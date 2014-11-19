package com.biancama.gui.easyShipment;

import com.biancama.gui.easyShipment.menus.actions.AboutAction;
import com.biancama.gui.easyShipment.menus.actions.KnowledgeAction;
import com.biancama.gui.easyShipment.menus.actions.LatestChangesAction;
import com.biancama.gui.easyShipment.menus.actions.LogAction;
import com.biancama.gui.menu.BiancaStartMenu;

public class AboutMenu extends BiancaStartMenu {
    private static final long serialVersionUID = 1899581616146592295L;

    public AboutMenu() {
        super("gui.menu.about", "gui.images.help");

        this.add(new LogAction());
        this.addSeparator();
        this.add(new LatestChangesAction());
        this.add(new KnowledgeAction());
        this.addSeparator();
        this.add(new AboutAction());

    }
}
