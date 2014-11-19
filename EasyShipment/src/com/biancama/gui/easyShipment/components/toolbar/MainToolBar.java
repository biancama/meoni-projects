package com.biancama.gui.easyShipment.components.toolbar;

import com.biancama.events.BiancaController;
import com.biancama.events.ControlEvent;
import com.biancama.events.ControlListener;
import com.biancama.gui.swing.GuiRunnable;
import com.biancama.gui.swing.actions.ActionController;

public class MainToolBar extends ToolBar implements ControlListener {
    /**
     * 
     */
    private static final long serialVersionUID = 922971719957349497L;
    private static MainToolBar INSTANCE = null;

    public static synchronized MainToolBar getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MainToolBar();
        }
        return INSTANCE;
    }

    private MainToolBar() {
        super();

        INSTANCE = this;
        BiancaController.getInstance().addControlListener(this);
    }

    public void controlEvent(final ControlEvent event) {
        switch (event.getID()) {
        case ControlEvent.CONTROL_DOWNLOAD_START:
        case ControlEvent.CONTROL_DOWNLOAD_STOP:
        case ControlEvent.CONTROL_ALL_DOWNLOADS_FINISHED:
            new GuiRunnable<Object>() {
                @Override
                public Object runSave() {
                    switch (event.getID()) {
                    case ControlEvent.CONTROL_DOWNLOAD_START:
                        ActionController.getToolBarAction("toolbar.control.stopmark").setEnabled(true);
                        break;
                    case ControlEvent.CONTROL_DOWNLOAD_STOP:
                    case ControlEvent.CONTROL_ALL_DOWNLOADS_FINISHED:
                        ActionController.getToolBarAction("toolbar.control.stopmark").setEnabled(false);

                        break;
                    }
                    return null;
                }
            }.start();
        }

    }

    @Override
    public void updateToolbar() {
        super.updateToolbar();

        new GuiRunnable<Object>() {
            @Override
            public Object runSave() {

                return null;
            }
        }.waitForEDT();
    }

}
