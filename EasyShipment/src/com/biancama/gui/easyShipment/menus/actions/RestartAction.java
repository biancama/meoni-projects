package com.biancama.gui.easyShipment.menus.actions;

import java.awt.event.ActionEvent;

import com.biancama.gui.UserIO;
import com.biancama.gui.swing.actions.ToolBarAction;
import com.biancama.utils.ApplicationUtils;
import com.biancama.utils.FlagsUtils;
import com.biancama.utils.locale.BiancaL;

public class RestartAction extends ToolBarAction {

    private static final long serialVersionUID = 1333126351380171619L;

    public RestartAction() {
        super("action.restart", "gui.images.restart");
    }

    @Override
    public void onAction(ActionEvent e) {
        if (FlagsUtils.hasSomeFlags(UserIO.getInstance().requestConfirmDialog(0, BiancaL.L("sys.ask.rlyrestart", "Wollen Sie jDownloader wirklich neustarten?")), UserIO.RETURN_OK, UserIO.RETURN_DONT_SHOW_AGAIN)) {
            ApplicationUtils.restartApplication(false);
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void initDefaults() {
    }
}
