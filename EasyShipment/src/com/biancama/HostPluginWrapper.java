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

package com.biancama;

import java.util.ArrayList;

import javax.swing.ImageIcon;

import com.biancama.config.container.BiancaLabelContainer;
import com.biancama.gui.easyShipment.plugins.PluginWrapper;
import com.biancama.log.BiancaLogger;
import com.biancama.plugins.PluginForHost;
import com.biancama.utils.FormatterUtils;

public class HostPluginWrapper extends PluginWrapper implements BiancaLabelContainer {
    private static final ArrayList<HostPluginWrapper> HOST_WRAPPER = new ArrayList<HostPluginWrapper>();
    private static boolean uninitialized = true;
    public static Object LOCK = new Object();

    public static ArrayList<HostPluginWrapper> getHostWrapper() {
        synchronized (LOCK) {
            if (uninitialized) {
                try {
                    EasyShipmentInit.loadPluginForHost();
                } catch (Throwable e) {
                    BiancaLogger.exception(e);
                }
                uninitialized = false;
            }
            return HOST_WRAPPER;
        }
    }

    private static final String AGB_CHECKED = "AGB_CHECKED";
    private String revision = "idle";

    public HostPluginWrapper(String host, String classNamePrefix, String className, String patternSupported, int flags, String revision) {
        super(host, classNamePrefix, className, patternSupported, flags);
        this.revision = FormatterUtils.getRevision(revision);
        synchronized (LOCK) {
            HOST_WRAPPER.add(this);
        }
    }

    public HostPluginWrapper(String host, String simpleName, String pattern, int i, String revision) {
        this(host, "jd.plugins.hoster.", simpleName, pattern, i, revision);

    }

    @Override
    public String getVersion() {
        return revision;
    }

    @Override
    public PluginForHost getPlugin() {
        return (PluginForHost) super.getPlugin();
    }

    public boolean isAGBChecked() {
        return super.getPluginConfig().getBooleanProperty(AGB_CHECKED, false);
    }

    public void setAGBChecked(Boolean value) {
        super.getPluginConfig().setProperty(AGB_CHECKED, value);
        super.getPluginConfig().save();
    }

    public boolean isPremiumEnabled() {
        return this.isLoaded() && this.getPlugin().isPremiumEnabled();
    }

    @Override
    public int compareTo(PluginWrapper pw) {
        if (!(pw instanceof HostPluginWrapper)) { return super.compareTo(pw); }

        HostPluginWrapper plg = (HostPluginWrapper) pw;
        if (this.isLoaded() && plg.isLoaded()) {
            if (this.isPremiumEnabled() && plg.isPremiumEnabled()) { return this.getHost().compareToIgnoreCase(plg.getHost()); }
            if (this.isPremiumEnabled() && !plg.isPremiumEnabled()) { return -1; }
            if (!this.isPremiumEnabled() && plg.isPremiumEnabled()) { return 1; }
        }
        if (this.isLoaded() && !plg.isLoaded()) {
            if (this.isPremiumEnabled()) { return -1; }
        }
        if (!this.isLoaded() && !!plg.isLoaded()) {
            if (plg.isPremiumEnabled()) { return 1; }
        }
        return this.getHost().compareToIgnoreCase(plg.getHost());
    }

    @Override
    public String toString() {
        return getHost();
    }

    public static boolean hasPlugin(String s) {
        for (HostPluginWrapper w : getHostWrapper()) {
            if (w.canHandle(s)) { return true; }
        }
        return false;
    }

    public ImageIcon getIcon() {
        return getPlugin().getHosterIcon();
    }

    public String getLabel() {
        return toString();
    }

}
