package com.biancama.gui.easyShipment.views.sidebars.configuration;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.biancama.gui.easyShipment.panels.ConfigPanelGeneral;
import com.biancama.gui.easyShipment.plugins.OptionalPluginWrapper;
import com.biancama.gui.swing.GuiRunnable;
import com.biancama.gui.swing.SingletonPanel;
import com.biancama.gui.swing.interfaces.SwitchPanel;
import com.biancama.utils.FileSystemUtils;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.gui.panels.ConfigPanel;
import com.biancama.utils.locale.BiancaL;

public class ConfigTreeModel implements TreeModel {
    private static final String JDL_PREFIX = "com.biancama.gui.easyShipment.views.ConfigTreeModel.";

    private final TreeEntry root;
    /** Listeners. */
    protected EventListenerList listenerList = new EventListenerList();
    private TreeEntry addons;

    private TreeEntry plugins;

    public ConfigTreeModel() {
        this.root = new TreeEntry(BiancaL.L(JDL_PREFIX + "CONFIGURATION.title", "Settings"));

        TreeEntry basics, modules, hoster, dl;

        root.add(basics = new TreeEntry(BiancaL.L(JDL_PREFIX + "basics.title", "Basics")).setIcon("gui.images.config.home"));

        basics.add(new TreeEntry(ConfigPanelGeneral.class, ConfigPanelGeneral.getTitle()).setIcon("gui.images.config.home"));

    }

    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     * 
     * @see #removeTreeModelListener
     * @param l
     *            the listener to add
     */
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    /**
     * Removes a listener previously added with <B>addTreeModelListener()</B>.
     * 
     * @see #addTreeModelListener
     * @param l
     *            the listener to remove
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    public Object getChild(Object parent, int index) {

        return ((TreeEntry) parent).get(index);
    }

    public int getChildCount(Object parent) {

        return ((TreeEntry) parent).size();
    }

    public int getIndexOfChild(Object parent, Object child) {

        return ((TreeEntry) parent).indexOf(child);
    }

    public Object getRoot() {

        return root;
    }

    public boolean isLeaf(Object node) {

        return ((TreeEntry) node).size() == 0;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    static class TreeEntry {

        private Class<? extends SwitchPanel> clazz;
        private String title;
        private ImageIcon icon;
        private String iconKey;

        public Class<? extends SwitchPanel> getClazz() {
            return clazz;
        }

        public TreeEntry setIcon(String string) {
            iconKey = string;
            icon = BiancaTheme.II(string, 20, 20);
            return this;
        }

        public String getIconKey() {
            return iconKey;
        }

        public void setIconKey(String iconKey) {
            this.iconKey = iconKey;
        }

        public void setClazz(Class<? extends SwitchPanel> clazz) {
            this.clazz = clazz;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public ImageIcon getIcon() {
            return icon;
        }

        public TreeEntry setIcon(ImageIcon icon) {
            this.icon = icon;
            return this;
        }

        public String getTooltip() {
            return tooltip;
        }

        public void setTooltip(String tooltip) {
            this.tooltip = tooltip;
        }

        private String tooltip;
        private final ArrayList<TreeEntry> entries;
        private SingletonPanel panel;
        private static final HashMap<Class<? extends SwitchPanel>, TreeEntry> PANELS = new HashMap<Class<? extends SwitchPanel>, TreeEntry>();

        /**
         * Returns the TreeEntry to a class if it has been added using the
         * public static TreeEntry getTreeByClass(Class<? extends SwitchPanel>
         * cl) constructor
         * 
         * @param cl
         * @return
         */
        public static TreeEntry getTreeByClass(Class<?> cl) {
            return PANELS.get(cl);
        }

        public TreeEntry(final Class<? extends SwitchPanel> class1, String l) {
            this.clazz = class1;

            if (class1 != null) {
                panel = new SingletonPanel(class1, FileSystemUtils.getConfiguration());
                // init this panel in an extra thread..
                new Thread() {
                    @Override
                    public void run() {
                        new GuiRunnable<Object>() {
                            @Override
                            public Object runSave() {
                                panel.getPanel();
                                return null;
                            }

                        }.start();

                    }
                }.start();
            }
            this.title = l;
            this.entries = new ArrayList<TreeEntry>();
            PANELS.put(class1, this);
        }

        public SingletonPanel getPanel() {
            return panel;
        }

        public void setPanel(SingletonPanel panel) {
            this.panel = panel;
        }

        public ArrayList<TreeEntry> getEntries() {
            return entries;
        }

        public int indexOf(Object child) {
            return entries.indexOf(child);
        }

        public int size() {
            return entries.size();
        }

        public Object get(int index) {

            return entries.get(index);
        }

        public void add(TreeEntry treeEntry) {
            entries.add(treeEntry);
        }

        public TreeEntry(String l) {
            this((Class<? extends SwitchPanel>) null, l);
        }

        /**
         * Adds a configpanel
         * 
         * @param panel
         * @param host
         */
        public TreeEntry(ConfigPanel panel, String host) {
            this.panel = new SingletonPanel(panel);
            this.title = host;
            this.entries = new ArrayList<TreeEntry>();
        }
    }

    private void fireTreeStructureChanged(TreePath path) {

        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {

                if (e == null) {
                    e = new TreeModelEvent(this, path);
                }
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
            }
        }
    }

    private void initExtensions(TreeEntry addons2) {
        for (final OptionalPluginWrapper plg : OptionalPluginWrapper.getOptionalWrapper()) {
            if (!plg.isLoaded() || !plg.isEnabled() || plg.getPlugin().getConfig().getEntries().size() == 0) {
                continue;
            }

            addons2.add(new TreeEntry(AddonConfig.getInstance(plg.getPlugin().getConfig(), plg.getHost(), ""), plg.getHost()).setIcon(plg.getPlugin().getIconKey()));
        }

    }

    /**
     * Is called to update The Addons subtree after changes
     * 
     * @return
     */
    public TreePath updateAddons() {
        addons.getEntries().clear();
        initExtensions(addons);
        TreePath path = new TreePath(new Object[] { getRoot(), plugins, addons });
        fireTreeStructureChanged(path);
        return path;
    }
}
