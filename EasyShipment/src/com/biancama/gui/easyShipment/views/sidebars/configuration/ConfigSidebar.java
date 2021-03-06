package com.biancama.gui.easyShipment.views.sidebars.configuration;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import net.miginfocom.swing.MigLayout;

import com.biancama.gui.easyShipment.views.ConfigurationView;
import com.biancama.gui.easyShipment.views.sidebars.configuration.ConfigTreeModel.TreeEntry;
import com.biancama.gui.swing.GuiRunnable;
import com.biancama.gui.swing.easyShipment.GUIUtils;
import com.biancama.gui.swing.interfaces.SideBarPanel;
import com.biancama.gui.swing.laf.LookAndFeelController;

public class ConfigSidebar extends SideBarPanel {

    private static final long serialVersionUID = 6456662020047832983L;
    private static final String PROPERTY_LAST_PANEL = "LAST_PANEL";
    private static ConfigSidebar INSTANCE = null;
    private JTree tree;
    private final ConfigurationView view;

    /**
     * CReate a singlton instance of the config sidebar
     * 
     * @param configurationView
     * @return
     */
    public static ConfigSidebar getInstance(ConfigurationView configurationView) {
        if (INSTANCE == null && configurationView != null) {
            INSTANCE = new ConfigSidebar(configurationView);
        }
        return INSTANCE;
    }

    private ConfigSidebar(ConfigurationView configurationView) {
        this.view = configurationView;
        this.setLayout(new MigLayout("ins 0", "[grow,fill]", "[grow,fill]"));

        this.add(tree = new JTree(new ConfigTreeModel()) {
            private static final long serialVersionUID = -5018817191000357595L;

            /**
             * workaround a synthetica layout bug with doubleclick
             */
            @Override
            public void processMouseEvent(MouseEvent m) {
                if (m.getClickCount() > 1) { return; }
                super.processMouseEvent(m);
            }

            @Override
            public void processKeyEvent(KeyEvent m) {
                if (LookAndFeelController.isSynthetica()) { return; }
                super.processKeyEvent(m);
            }

        });

        tree.setCellRenderer(new TreeRenderer());
        tree.setOpaque(false);
        tree.setRootVisible(false);
        tree.setRowHeight(24);
        tree.setExpandsSelectedPaths(true);
        tree.setBackground(null);
        //        
        // It seems that people do not dint configentries like
        // "Languageselection" because it is hidden in a expandable treentry.
        // No this entry gets selected if the tree expands. This should help
        // people finding what they are looking for.
        tree.addTreeExpansionListener(new TreeExpansionListener() {

            public void treeCollapsed(TreeExpansionEvent event) {
            }

            public void treeExpanded(TreeExpansionEvent event) {
                if (tree.getSelectionPath() == null) { return; }
                TreeEntry entry = (TreeEntry) event.getPath().getLastPathComponent();
                tree.setSelectionPath(event.getPath());
                if (entry.getPanel() != null) {
                    view.setContent(entry.getPanel().getPanel());
                }

            }
        });
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

            private TreeEntry entry;

            public void valueChanged(TreeSelectionEvent e) {
                new GuiRunnable<Object>() {
                    @Override
                    public Object runSave() {
                        if (tree.getSelectionPath() == null) { return null; }
                        entry = (TreeEntry) tree.getSelectionPath().getLastPathComponent();
                        tree.expandPath(tree.getSelectionPath());
                        if (entry.getPanel() != null) {
                            view.setContent(entry.getPanel().getPanel());
                        }
                        return null;
                    }
                }.start();
            }

        });
        TreePath rootPath = new TreePath(tree.getModel().getRoot());
        tree.expandPath(rootPath);
        TreeEntry node = (TreeEntry) rootPath.getLastPathComponent();
        for (TreeEntry n : node.getEntries()) {
            TreePath path = rootPath.pathByAddingChild(n);
            tree.expandPath(path);
        }

        tree.setSelectionRow(1);
        String lastPanel = GUIUtils.getConfig().getStringProperty(PROPERTY_LAST_PANEL, null);
        if (lastPanel != null) {
            try {
                Class<?> lastPanelClass = Class.forName(lastPanel);
                this.setSelectedTreeEntry(lastPanelClass);
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void expandAll(JTree tree, boolean expand) {
        TreeEntry root = (TreeEntry) tree.getModel().getRoot();
        expandAll(tree, new TreePath(root), expand);
    }

    public void expandAll(JTree tree, TreePath parent, boolean expand) {
        TreeEntry node = (TreeEntry) parent.getLastPathComponent();
        if (node.size() >= 0) {
            for (TreeEntry n : node.getEntries()) {
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    @Override
    protected void onHide() {
        /* getPanel is null in case the user selected a rootnode */
        if (((TreeEntry) tree.getLastSelectedPathComponent()).getPanel() == null) { return; }
        if (((TreeEntry) tree.getLastSelectedPathComponent()).getPanel().getPanel() == null) { return; }
        GUIUtils.getConfig().setProperty(PROPERTY_LAST_PANEL, ((TreeEntry) tree.getLastSelectedPathComponent()).getPanel().getPanel().getClass().getName());
        GUIUtils.getConfig().save();
    }

    @Override
    protected void onShow() {
    }

    public void setSelectedTreeEntry(Class<?> class1) {
        TreeEntry root = (TreeEntry) tree.getModel().getRoot();
        TreeEntry child = TreeEntry.getTreeByClass(class1);
        if (child == null) { return; }
        TreePath path = getEntry(new TreePath(root), child);
        if (path != null) {
            tree.setSelectionPath(path);
            tree.setSelectionPath(path);
        }
    }

    private TreePath getEntry(TreePath parent, TreeEntry treeEntry) {
        TreeEntry node = (TreeEntry) parent.getLastPathComponent();
        if (node == treeEntry) { return parent; }
        if (node.size() >= 0) {
            for (TreeEntry n : node.getEntries()) {
                TreePath path = parent.pathByAddingChild(n);

                TreePath res = getEntry(path, treeEntry);
                if (res != null) { return res; }
            }
        }

        return null;
    }

    /**
     * Updates the Addon subtree
     */
    public void updateAddons() {
        TreePath path = ((ConfigTreeModel) tree.getModel()).updateAddons();
        expandAll(tree, path, true);
    }

}
