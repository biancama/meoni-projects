package com.biancama.gui.easyShipment.views;

import javax.swing.Icon;

import com.biancama.gui.easyShipment.views.sidebars.configuration.ConfigSidebar;
import com.biancama.gui.swing.GuiRunnable;
import com.biancama.gui.swing.interfaces.View;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.locale.BiancaL;

public class ConfigurationView extends View {

    private static final long serialVersionUID = -5607304856678049342L;

    /**
     * DO NOT MOVE THIS CONSTANT. IT's important to have it in this file for the
     * LFE to parse JDL Keys correct
     */
    private static final String IDENT_PREFIX = "com.biancama.gui.easyShipment.views.configurationview.";

    public ConfigurationView() {
        super();
        sidebar.setBorder(null);
        // init config with 2 seconds delay to avoid gui locks at startup
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                new GuiRunnable<Object>() {

                    @Override
                    public Object runSave() {
                        setSideBar(ConfigSidebar.getInstance(ConfigurationView.this));
                        return null;
                    }

                }.start();
            }
        }.start();

    }

    @Override
    public Icon getIcon() {
        return BiancaTheme.II("gui.images.taskpanes.configuration", ICON_SIZE, ICON_SIZE);
    }

    @Override
    public String getTitle() {
        return BiancaL.L(IDENT_PREFIX + "tab.title", "Settings");
    }

    @Override
    public String getTooltip() {
        return BiancaL.L(IDENT_PREFIX + "tab.tooltip", "All options and settings for Easy Shipment");
    }

    @Override
    protected void onHide() {

    }

    @Override
    protected void onShow() {

    }

}
