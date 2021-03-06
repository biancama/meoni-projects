//    jDownloader - Downloadmanager
//    Copyright (C) 2008  JD-Team support@jdownloader.org
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

package com.biancama.plugins;

import java.util.regex.Pattern;

import com.biancama.events.BiancaController;
import com.biancama.events.ControlEvent;
import com.biancama.events.ControlListener;
import com.biancama.gui.easyShipment.plugins.OptionalPluginWrapper;
import com.biancama.gui.easyShipment.plugins.PluginWrapper;
import com.biancama.log.BiancaLogger;

public abstract class PluginOptional extends Plugin implements ControlListener {

    public PluginOptional(PluginWrapper wrapper) {
        super(wrapper);
    }

    public static final int ADDON_INTERFACE_VERSION = 5;

    public void controlEvent(ControlEvent event) {

        // Deaktiviert das PLugin beim beenden
        if (event.getID() == ControlEvent.CONTROL_SYSTEM_EXIT) {
            final String id = BiancaController.requestDelayExit(((OptionalPluginWrapper) wrapper).getID());
            try {
                onExit();
            } catch (Exception e) {
                BiancaLogger.exception(e);
            }
            BiancaController.releaseDelayExit(id);
        }

    }

    @Override
    public String getHost() {
        return this.getWrapper().getHost();
    }

    /**
     * should be overridden by addons with gui
     * 
     * @param b
     */
    public void setGuiEnable(boolean b) {
    }

    public String getIconKey() {
        return "gui.images.config.home";
    }

    // @Override
    @Override
    public Pattern getSupportedLinks() {
        return null;
    }

    public abstract boolean initAddon();

    public abstract void onExit();

    public Object interact(String command, Object parameter) {
        return null;
    }

    @Override
    public String getVersion() {
        return ((OptionalPluginWrapper) this.getWrapper()).getVersion();
    }

    /**
     * Shoudl ALWAYS return a lifetime id String.
     * 
     * @return
     */
    public String getID() {
        return getHost();
    }
}
