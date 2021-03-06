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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.biancama.gui.easyShipment.views.sidebars.configuration.ConfigTreeModel.TreeEntry;
import com.biancama.utils.gui.BiancaTheme;

public class TreeRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = -3927390875702401200L;
    private TreeEntry te;
    private Font orgFont;
    private Font boldFont;
    private final JLabel label;

    public TreeRenderer() {
        label = new JLabel();
        label.setBackground(null);

    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        te = (TreeEntry) value;
        if (orgFont == null && label.getFont() != null) {
            orgFont = label.getFont();
            boldFont = label.getFont().deriveFont(label.getFont().getStyle() ^ Font.BOLD);
        }

        label.setText(te.getTitle());
        if (!sel && te.getIcon() != null) {

            label.setFont(orgFont);

            label.setIcon(BiancaTheme.II(te.getIconKey(), 16, 16));
        } else {
            label.setFont(boldFont);

            label.setIcon(te.getIcon());
        }
        label.setToolTipText(te.getTooltip());

        label.setPreferredSize(new Dimension(200, 20));
        return label;
    }

}
