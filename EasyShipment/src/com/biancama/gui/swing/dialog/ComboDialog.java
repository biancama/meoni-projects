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

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;

import net.miginfocom.swing.MigLayout;

import com.biancama.gui.UserIO;

public class ComboDialog extends AbstractDialog {

    private static final long serialVersionUID = 3817208838787228122L;
    private final String message;
    private JTextPane messageArea;
    private JComboBox input;
    private final int defaultAnswer;
    private final Object[] options;
    private final ListCellRenderer renderer;

    public ComboDialog(int flag, String title, String question, Object[] options, int defaultSelection, ImageIcon icon, String okText, String cancelText, ListCellRenderer renderer) {
        super(flag, title, icon, okText, cancelText);
        message = question;
        this.renderer = renderer;
        this.defaultAnswer = defaultSelection;
        this.options = options;
        init();
    }

    @Override
    public JComponent contentInit() {
        JPanel contentpane = new JPanel(new MigLayout("ins 0,wrap 1", "[fill,grow]"));
        messageArea = new JTextPane();
        messageArea.setBorder(null);
        messageArea.setBackground(null);
        messageArea.setOpaque(false);
        messageArea.setText(this.message);
        messageArea.setEditable(false);

        contentpane.add(messageArea);

        input = new JComboBox(options);
        if (renderer != null) {
            input.setRenderer(renderer);
        }
        input.setSelectedIndex(this.defaultAnswer);

        if (AbstractDialog.getDefaultDimension() != null) {
            input.setBounds(0, 0, (int) AbstractDialog.getDefaultDimension().getWidth(), (int) AbstractDialog.getDefaultDimension().getHeight());
            input.setMaximumSize(AbstractDialog.getDefaultDimension());
        } else {
            input.setBounds(0, 0, 450, 600);
            input.setMaximumSize(new Dimension(450, 600));
        }
        contentpane.add(input, "pushy,growy, width n:n:450");

        return contentpane;
    }

    @Override
    protected void packed() {

    }

    public Integer getReturnID() {
        if ((this.getReturnValue() & UserIO.RETURN_OK) == 0) { return -1; }
        return input.getSelectedIndex();
    }

}
