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

package com.biancama.gui.swing.dialog;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.biancama.gui.UserIO;
import com.biancama.gui.swing.components.BiancaTextArea;
import com.biancama.utils.gui.BiancaTheme;

public class TextAreaDialog extends AbstractDialog {

    private static final long serialVersionUID = 5129590048597691591L;

    private final String message;

    private final String def;

    private BiancaTextArea txtArea;

    public TextAreaDialog(String title, String message, String def) {
        super(UserIO.NO_COUNTDOWN, title, BiancaTheme.II("gui.images.config.tip", 32, 32), null, null);
        this.message = message;
        this.def = def;
        init();
    }

    @Override
    public JComponent contentInit() {
        JPanel panel = new JPanel(new MigLayout("ins 0, wrap 1", "[grow, fill]", "[]5[]"));
        panel.add(new JLabel(message));
        panel.add(txtArea = new BiancaTextArea(def), "h 100!");
        return panel;
    }

    public String getResult() {
        return txtArea.getText();
    }

}
