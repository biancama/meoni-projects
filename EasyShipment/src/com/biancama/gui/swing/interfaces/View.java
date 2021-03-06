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

package com.biancama.gui.swing.interfaces;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.miginfocom.swing.MigLayout;

import com.biancama.gui.swing.SwingGui;
import com.biancama.utils.gui.BiancaTheme;

/**
 * A view is an abstract class for a contentpanel in JDGui
 * 
 * @author Coalado
 * 
 */
public abstract class View extends SwitchPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 8661526331504317690L;
    public static final int ICON_SIZE = 16;
    public static final Border ORG_BORDER = BorderFactory.createEmptyBorder();

    private final JPanel rightPane;
    protected JScrollPane sidebar;
    private SideBarPanel sidebarContent;
    private SwitchPanel content;
    private JPanel topContent;
    private JPanel bottomContent;
    private SwitchPanel infoPanel;
    private SwitchPanel defaultInfoPanel;
    @SuppressWarnings("unused")
    private JPanel toolbar;
    private final Border orgSidebarBorder;

    public View() {
        SwingGui.checkEDT();
        this.setLayout(new MigLayout("ins 0", "[]0[grow,fill]", "[grow,fill]"));

        add(sidebar = new JScrollPane(), "width 200!,hidemode 1,gapright 3");
        Color line;

        // MetalLookAndFeel.getControlDarkShadow();
        // MetalLookAndFeel.getControlHighlight() ;
        line = MetalLookAndFeel.getControl();

        orgSidebarBorder = sidebar.getBorder();
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, line));
        sidebar.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sidebar.setVisible(false);
        rightPane = new JPanel(new MigLayout("ins 0", "[grow,fill]", "[grow,fill]"));
        add(rightPane);
        add(topContent = new JPanel(new MigLayout("ins 0", "[grow,fill]", "[]")), "gapbottom 3,dock NORTH,hidemode 3");
        topContent.setVisible(false);
        add(bottomContent = new JPanel(new MigLayout("ins 0 0 0 0", "[grow,fill]", "[]")), "dock SOUTH,hidemode 3");
        bottomContent.setVisible(false);
    }

    /**
     * Serts the sidebar's border View.ORG_BORDER --> the LAF original Border
     * 
     * @param b
     */
    public void setSidebarBorder(Border b) {
        if (ORG_BORDER == b) {
            sidebar.setBorder(orgSidebarBorder);

        } else {
            sidebar.setBorder(b);
        }

    }

    /**
     * Sets the default infopanel
     * 
     * @param panel
     */
    protected void setDefaultInfoPanel(DroppedPanel panel) {

        this.defaultInfoPanel = panel;
        if (this.getInfoPanel() == null) {
            setInfoPanel(panel);
        }
    }

    /**
     * SOUTH CONTENT sets the south infopanel. if set to null, the default info
     * panel is shown. of this is null, too the info area is hidden
     * 
     * @param infoPanel
     */
    public void setInfoPanel(SwitchPanel info) {
        SwingGui.checkEDT();
        if (info == null) {
            info = defaultInfoPanel;
        }
        if (infoPanel == info) { return; }

        if (info == null) {
            bottomContent.setVisible(false);
        } else {
            bottomContent.setVisible(true);
            bottomContent.removeAll();
            bottomContent.add(info);
        }
        if (infoPanel != null && isShown()) {
            infoPanel.setHidden();
        }
        revalidate();
        this.infoPanel = info;
        if (this.infoPanel != null && isShown()) {
            this.infoPanel.setShown();
        }
    }

    public SwitchPanel getInfoPanel() {
        return infoPanel;
    }

    /**
     * TOPCONTENT Sets the views toolbar. null removes the toolbar
     * 
     * @param toolbar
     */
    protected void setToolBar(JPanel toolbar) {
        SwingGui.checkEDT();
        if (toolbar == null) {
            topContent.setVisible(false);
        } else {
            topContent.setVisible(true);
            topContent.removeAll();
            topContent.add(toolbar);
        }
        this.toolbar = toolbar;
        revalidate();
    }

    /**
     * CENTER-MAIN-CONTENT Sets the left side main content bar
     * 
     * @param right
     */
    public synchronized void setContent(SwitchPanel right) {
        SwingGui.checkEDT();
        boolean found = false;
        for (Component c : rightPane.getComponents()) {
            c.setVisible(false);
            if (c == right) {
                found = true;
            }
        }

        if (right != null) {
            right.setVisible(true);
            if (!found) {
                rightPane.add(right, "hidemode 3");
            }
        }
        if (this.content != null && isShown()) {
            this.content.setHidden();
        }
        this.content = right;
        this.revalidate();
        if (this.content != null && isShown()) {
            this.content.setShown();
        }
    }

    public SwitchPanel getContent() {
        return content;
    }

    /**
     * SIDEBAR WEST CONTENT sets the left sidebar
     * 
     * @param left
     */
    public void setSideBar(SideBarPanel left) {
        SwingGui.checkEDT();
        if (left == sidebarContent) { return; }
        if (left == null) {
            sidebar.setVisible(false);
        } else {
            sidebar.setVisible(true);
            sidebar.setViewportView(left);
        }

        if (sidebarContent != null && isShown()) {
            sidebarContent.setHidden();
        }

        this.sidebarContent = left;
        if (isShown()) {
            left.setShown();
        }
    }

    public SideBarPanel getSidebar() {
        return sidebarContent;
    }

    /**
     * returns the Tab tooltip
     * 
     * @return
     */
    abstract public String getTooltip();

    /**
     * Returns the tab title
     * 
     * @return
     */
    abstract public String getTitle();

    /**
     * returns the tab icon
     * 
     * @return
     */
    abstract public Icon getIcon();

    /**
     * Returns the defaulticon
     * 
     * @return
     */
    public static Icon getDefaultIcon() {
        return BiancaTheme.II("gui.images.add_package", 16, 16);
    }

}
