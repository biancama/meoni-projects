package com.biancama.gui.easyShipment.menus.actions;

import java.awt.event.ActionEvent;

import com.biancama.events.BiancaController;
import com.biancama.events.ControlEvent;
import com.biancama.events.ControlListener;
import com.biancama.gui.UserIO;
import com.biancama.gui.swing.actions.ToolBarAction;
import com.biancama.utils.FlagsUtils;
import com.biancama.utils.URLUtils;
import com.biancama.utils.gui.Executer;
import com.biancama.utils.locale.BiancaL;

public class RestoreAction extends ToolBarAction {

    private static final long serialVersionUID = -1428029294638573437L;

    public RestoreAction() {
        super("action.restore", "gui.images.edit");
    }

    @Override
    public void onAction(ActionEvent e) {
        if (FlagsUtils.hasSomeFlags(UserIO.getInstance().requestConfirmDialog(0, BiancaL.L("sys.ask.rlyrestore", "This will restart JDownloader and do a FULL-Update. Continue?")), UserIO.RETURN_OK, UserIO.RETURN_DONT_SHOW_AGAIN)) {
            final Executer exec = new Executer("java");
            exec.addParameters(new String[] { "-jar", "jdupdate.jar", "-restore" });
            exec.setRunin(URLUtils.getResourceFile(".").getAbsolutePath());
            exec.setWaitTimeout(0);
            BiancaController.getInstance().addControlListener(new ControlListener() {
                public void controlEvent(ControlEvent event) {
                    if (event.getID() == ControlEvent.CONTROL_SYSTEM_SHUTDOWN_PREPARED) {
                        exec.start();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            });
            BiancaController.getInstance().exit();
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void initDefaults() {
    }
}
