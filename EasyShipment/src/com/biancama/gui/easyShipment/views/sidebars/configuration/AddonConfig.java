//    jDownloader - Downloadmanager
//    Copyright (C) 2009  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.biancama.gui.easyShipment.views.sidebars.configuration;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.biancama.config.ConfigContainer;
import com.biancama.config.ConfigEntry;
import com.biancama.utils.gui.panels.ConfigPanel;
import com.biancama.utils.locale.BiancaL;

public class AddonConfig extends ConfigPanel {

    private static final long serialVersionUID = 5561326475681668634L;

    private static HashMap<String, AddonConfig> MAP;

    private final ConfigContainer container;

    private final String name;

    private AddonConfig(ConfigContainer container, String name) {
        super();
        this.container = container;
        this.name = name;
        initPanel();
        load();
    }

    @Override
    public String getBreadcrum() {
        return BiancaL.L(this.getClass().getName() + ".breadcrum", this.getClass().getSimpleName()) + " - " + name;
    }

    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void initPanel() {
        panel = new ConfigPanel(container);
        ArrayList<ConfigEntry> cont = new ArrayList<ConfigEntry>();
        for (ConfigEntry cfgEntry : container.getEntries()) {
            if (cfgEntry.getType() == ConfigContainer.TYPE_CONTAINER) {
                cont.add(cfgEntry);
            }
        }

        final JTabbedPane tabbed = new JTabbedPane();

        tabbed.addChangeListener(new ChangeListener() {

            private ConfigPanel latestSelection;

            public void stateChanged(ChangeEvent e) {

                try {
                    ConfigPanel comp = (ConfigPanel) tabbed.getSelectedComponent();
                    if (comp == latestSelection) { return; }
                    if (latestSelection != null) {
                        latestSelection.setHidden();
                    }
                    latestSelection = comp;
                    if (comp != null) {
                        comp.setShown();
                    }
                    revalidate();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

            }

        });

        tabbed.setOpaque(false);
        tabbed.add(getBreadcrum(), panel);
        for (ConfigEntry c : cont) {
            ConfigPanel p = new ConfigPanel(c.getContainer());
            tabbed.add(c.getContainer().getTitle(), p);
            tabbed.setIconAt(tabbed.getTabCount() - 1, c.getContainer().getIcon());
        }

        this.add(tabbed);

    }

    /**
     * Caches panels...
     * 
     * @param container2
     * @param name2
     * @return
     */
    public synchronized static AddonConfig getInstance(ConfigContainer container2, String name2, String ext) {
        if (MAP == null) {
            MAP = new HashMap<String, AddonConfig>();
        }

        AddonConfig p = MAP.get(container2 + "_" + name2 + ext);
        if (p != null) { return p; }

        MAP.put(container2 + "_" + name2 + ext, p = new AddonConfig(container2, name2));
        return p;
    }

}
