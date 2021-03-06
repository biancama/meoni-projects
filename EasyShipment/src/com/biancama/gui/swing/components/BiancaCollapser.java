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

package com.biancama.gui.swing.components;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;

import com.biancama.gui.swing.borders.BiancaBorderFactory;
import com.biancama.gui.swing.interfaces.DroppedPanel;
import com.biancama.gui.swing.interfaces.BiancaMouseAdapter;
import com.biancama.utils.locale.BiancaL;

/**
 * class for an infopanel with close button.
 * 
 * @author Coalado
 */
public abstract class BiancaCollapser extends DroppedPanel {

    private static final long serialVersionUID = 6864885344815243560L;

    protected JMenuBar menubar;
    private JButton closeButton;
    protected JLabel menutitle;

    protected JPanel content;

    protected BiancaCollapser() {
        super();
        this.setLayout(new MigLayout("ins 0 5 0 0,wrap 1", "[fill,grow]", "[fill,grow]"));

        menubar = new JMenuBar();
        menubar.add(menutitle = new JLabel(""));
        menubar.add(Box.createHorizontalGlue());
        menubar.setBorder(BiancaBorderFactory.createInsideShadowBorder(0, 0, 1, 0));
        CloseAction closeAction = new CloseAction();

        Box panel = new Box(1);
        panel.add(closeButton = new JButton(closeAction));
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.addMouseListener(new BiancaMouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setContentAreaFilled(true);
                closeButton.setBorderPainted(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setContentAreaFilled(false);
                closeButton.setBorderPainted(false);
            }
        });
        closeButton.setPreferredSize(new Dimension(closeAction.getWidth(), closeAction.getHeight()));
        closeButton.setToolTipText(BiancaL.LF("jd.gui.swing.components.JDCollapser.closetooltip", "Close %s", ""));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 5));
        menubar.add(panel);

        add(menubar, "dock NORTH,height " + Math.max(closeAction.getHeight() + 3, 18) + "!,gapbottom 2");
        this.content = new JPanel();
        add(content);
        content.setLayout(new MigLayout("ins 0,wrap 1", "[grow,fill]", "[grow,fill]"));
        this.setVisible(true);

    }

    public void setInfos(String name, Icon icon) {
        menutitle.setText(name);
        menutitle.setIcon(icon);

        closeButton.setToolTipText(BiancaL.LF("jd.gui.swing.components.JDCollapser.closetooltip", "Close %s", name));
    }

    public class CloseAction extends AbstractAction {
        private static final long serialVersionUID = -771203720364300914L;
        private final int height;
        private final int width;

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public CloseAction() {
            Icon ic = UIManager.getIcon("InternalFrame.closeIcon");
            this.height = ic.getIconHeight();
            this.width = ic.getIconWidth();
            this.putValue(AbstractAction.SMALL_ICON, ic);
        }

        public void actionPerformed(ActionEvent e) {
            BiancaCollapser.this.onClosed();
        }
    }

    abstract public void onClosed();

    public JPanel getContent() {
        return content;
    }

}
