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
import com.biancama.gui.swing.components.BiancaTextField;
import com.biancama.utils.gui.BiancaTheme;

public class TwoTextFieldDialog extends AbstractDialog {

    private static final long serialVersionUID = -7426399217833694784L;

    private final String messageOne;

    private final String defOne;

    private final String messageTwo;

    private final String defTwo;

    private BiancaTextField txtFieldOne;

    private BiancaTextField txtFieldTwo;

    public TwoTextFieldDialog(String title, String messageOne, String defOne, String messageTwo, String defTwo) {
        super(UserIO.NO_COUNTDOWN, title, BiancaTheme.II("gui.images.config.tip", 32, 32), null, null);
        this.messageOne = messageOne;
        this.defOne = defOne;
        this.messageTwo = messageTwo;
        this.defTwo = defTwo;
        init();
    }

    @Override
    public JComponent contentInit() {
        JPanel panel = new JPanel(new MigLayout("ins 0, wrap 1", "[grow, fill]", "[]5[]10[]5[]"));
        panel.add(new JLabel(messageOne));
        panel.add(txtFieldOne = new BiancaTextField(defOne));
        panel.add(new JLabel(messageTwo));
        panel.add(txtFieldTwo = new BiancaTextField(defTwo));
        return panel;
    }

    public String[] getResult() {
        return new String[] { txtFieldOne.getText(), txtFieldTwo.getText() };
    }

}
