package com.biancama.gui.easyShipment.menus.actions;

import java.awt.event.ActionEvent;

import com.biancama.gui.easyShipment.views.LogView;
import com.biancama.gui.swing.SwingGui;
import com.biancama.gui.swing.actions.ToolBarAction;

public class LogAction extends ToolBarAction {

    private static final long serialVersionUID = -353145605693194634L;

    public LogAction() {
        super("action.log", "gui.images.taskpanes.log");
    }

    @Override
    public void onAction(ActionEvent e) {

        LogView view = LogView.getLogView();
        System.out.println(view.isShown());

        SwingGui.getInstance().setContent(view);
    }

    @Override
    public void init() {
        this.setSelected(false);
    }

    @Override
    public void initDefaults() {
    }

}
