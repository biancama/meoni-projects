package com.biancama.gui.easyShipment.menus.actions;

import java.awt.event.ActionEvent;

import com.biancama.gui.swing.SwingGui;
import com.biancama.gui.swing.actions.ToolBarAction;

public class ExitAction extends ToolBarAction {

    private static final long serialVersionUID = -1428029294638573437L;

    public ExitAction() {
        super("action.exit", "gui.images.exit");
    }

    @Override
    public void onAction(ActionEvent e) {
        SwingGui.getInstance().closeWindow();
    }

    @Override
    public void init() {
    }

    @Override
    public void initDefaults() {
    }
}
