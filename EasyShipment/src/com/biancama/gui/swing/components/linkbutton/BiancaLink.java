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

package com.biancama.gui.swing.components.linkbutton;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.TableCellRenderer;

import com.biancama.config.SubConfiguration;
import com.biancama.events.BiancaBroadcaster;
import com.biancama.events.BiancaEvent;
import com.biancama.gui.easyShipment.util.nativeintegration.LocalBrowser;
import com.biancama.gui.swing.easyShipment.utils.EasyShipmentGuiConstants;
import com.biancama.gui.swing.interfaces.BiancaMouseAdapter;
import com.biancama.log.BiancaLogger;
import com.biancama.parser.Regex;
import com.biancama.utils.gui.Executer;

class JLinkButtonRenderer implements TableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return (Component) value;
    }
}

public class BiancaLink extends JLabel {

    private static final long serialVersionUID = 1L;
    public static final int CLICKED = 0;

    public static BiancaLinkButtonEditor getJLinkButtonEditor() {
        return new BiancaLinkButtonEditor();
    }

    public static JLinkButtonRenderer getJLinkButtonRenderer() {
        return new JLinkButtonRenderer();
    }

    public static void openURL(String url) throws Exception {
        if (url == null) { return; }
        BiancaLink.openURL(new URL(url));
    }

    public static void openURL(URL url) throws Exception {
        if (url == null) { return; }
        SubConfiguration cfg = SubConfiguration.getConfig(EasyShipmentGuiConstants.CONFIG_PARAMETER);
        if (cfg.getBooleanProperty(EasyShipmentGuiConstants.PARAM_CUSTOM_BROWSER_USE.toString(), false)) {
            Executer exec = new Executer(cfg.getStringProperty(EasyShipmentGuiConstants.PARAM_CUSTOM_BROWSER.toString()));
            String params = cfg.getStringProperty(EasyShipmentGuiConstants.PARAM_CUSTOM_BROWSER_PARAM.toString()).replace("%url", url + "");
            exec.addParameters(Regex.getLines(params));
            exec.start();
            exec.setWaitTimeout(1);
            exec.waitTimeout();
            if (exec.getException() != null) { throw exec.getException(); }
        } else {
            String browser = SubConfiguration.getConfig(EasyShipmentGuiConstants.CONFIG_PARAMETER).getStringProperty(EasyShipmentGuiConstants.PARAM_BROWSER.toString(), null);
            LocalBrowser.openURL(browser, url);
        }
    }

    private URL url;
    private transient BiancaBroadcaster<ActionListener, BiancaEvent> broadcaster;

    public BiancaLink() {
        this(null, null, null);
    }

    public BiancaLink(Icon icon) {
        this(null, icon, null);
    }

    public BiancaLink(Icon icon, URL url) {
        this(null, icon, url);
    }

    public BiancaLink(String s) {
        this(s, null, null);
    }

    public BiancaLink(String text, String urlstr) {
        super(text);
        URL url = null;
        try {
            url = new URL(urlstr);
        } catch (Exception e) {
            // e.printStackTrace();
            this.setEnabled(false);
        }
        init(text, url);
    }

    public BiancaLink(String s, URL url) {
        this(s, null, url);
    }

    public BiancaLink(URL url) {
        this(null, null, url);
    }

    public BiancaLink(String text, Icon icon, URL url) {
        super(text);

        this.setIcon(icon);

        init(text, url);
    }

    private void initBroadcaster() {
        this.broadcaster = new BiancaBroadcaster<ActionListener, BiancaEvent>() {

            @Override
            protected void fireEvent(ActionListener listener, BiancaEvent event) {
                listener.actionPerformed(new ActionEvent(BiancaLink.this, BiancaLink.CLICKED, getText()));

            }

        };
    }

    public BiancaBroadcaster<ActionListener, BiancaEvent> getBroadcaster() {
        if (broadcaster == null) {
            initBroadcaster();
        }
        return broadcaster;
    }

    private void init(String text, URL url) {
        if (url == null && text != null) {
            if (text.matches("https?://.*")) {
                try {
                    url = new URL(text);
                } catch (MalformedURLException e1) {

                    BiancaLogger.exception(e1);
                }
            } else if (text.matches("www\\..*?\\..*")) {
                try {
                    url = new URL("http://" + text);
                } catch (MalformedURLException e1) {

                    BiancaLogger.exception(e1);
                }
            }
        }
        if (text == null && url != null) {
            setText(url.toExternalForm());
        }
        setUrl(url);
        this.setBackground(null);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new BiancaMouseAdapter() {

            private Font originalFont;

            @SuppressWarnings("unchecked")
            @Override
            public void mouseEntered(MouseEvent evt) {
                originalFont = getFont();
                if (isEnabled()) {
                    Map attributes = originalFont.getAttributes();
                    attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);

                    setFont(originalFont.deriveFont(attributes));
                }
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                setFont(originalFont);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    if (getUrl() != null) {
                        BiancaLink.openURL(getUrl());
                    }
                    getBroadcaster().fireEvent(null);
                } catch (Exception e1) {
                    BiancaLogger.exception(e1);
                }
            }

        });

    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
        if (url != null) {
            this.setToolTipText(url.toExternalForm());
        }
    }

    public static HyperlinkListener getHyperlinkListener() {
        return new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        BiancaLink.openURL(e.getURL());
                    } catch (Exception e1) {
                        BiancaLogger.exception(e1);
                    }
                }
            }
        };
    }

}