package com.biancama.utils.gui.panels;

import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import com.biancama.config.ConfigContainer;
import com.biancama.config.ConfigEntry;
import com.biancama.config.ConfigGroup;
import com.biancama.config.SubConfiguration;
import com.biancama.config.ConfigEntry.PropertyType;
import com.biancama.gui.UserIO;
import com.biancama.gui.easyShipment.EasyShipmentGui;
import com.biancama.gui.swing.Factory;
import com.biancama.gui.swing.interfaces.SwitchPanel;
import com.biancama.gui.swing.settings.GUIConfigEntry;
import com.biancama.log.BiancaLogger;
import com.biancama.utils.ApplicationUtils;
import com.biancama.utils.FlagsUtils;
import com.biancama.utils.locale.BiancaL;

public class ConfigPanel extends SwitchPanel {

    private static final long serialVersionUID = 3383448498625377495L;

    protected ArrayList<GUIConfigEntry> entries = new ArrayList<GUIConfigEntry>();

    protected Logger logger = BiancaLogger.getLogger();

    protected JPanel panel;

    private ConfigGroup currentGroup;

    private JPanel header;

    public ConfigPanel() {
        this.setLayout(new MigLayout("ins 0 0 0 0", "[fill,grow]", "[fill,grow]"));
        panel = new JPanel();
        panel.setLayout(new MigLayout("ins 0 10 10 10,wrap 2", "[fill,grow 10]10[fill,grow]"));
    }

    /**
     * Constructor to display a ConfigPanel with contents of container
     * 
     * @param container
     */
    public ConfigPanel(ConfigContainer container) {
        this();
        for (ConfigEntry cfgEntry : container.getEntries()) {
            GUIConfigEntry ce = new GUIConfigEntry(cfgEntry);
            if (ce != null) {
                addGUIConfigEntry(ce);
            }
        }

        this.load();
        this.add(panel);
    }

    public String getBreadcrum() {
        return "";
    }

    public static String getTitle() {
        return "NOTITLE";
    }

    public void addGUIConfigEntry(GUIConfigEntry entry, JPanel panel) {
        ConfigGroup group = entry.getConfigEntry().getGroup();

        if (group == null) {

            if (currentGroup != null) {
                panel.add(new JSeparator(), "spanx,gapbottom 15,gaptop 15");
                // groupMenu = null;
                // Regression!!!???
                currentGroup = null;
            }

            if (entry.getDecoration() != null) {

                switch (entry.getConfigEntry().getType()) {

                case ConfigContainer.TYPE_TEXTAREA:
                case ConfigContainer.TYPE_LISTCONTROLLED:
                    panel.add(entry.getDecoration(), "spany " + entry.getInput().length + ",spanx, gapright " + getGapRight());

                    break;
                case ConfigContainer.TYPE_CONTAINER:
                    /**
                     * TODO . handly different containers
                     */
                    break;

                default:
                    panel.add(entry.getDecoration(), "spany " + Math.max(1, entry.getInput().length) + (entry.getInput().length == 0 ? ",spanx" : ""));

                }
            }

            int i = 0;
            for (JComponent c : entry.getInput()) {
                i++;
                switch (entry.getConfigEntry().getType()) {

                case ConfigContainer.TYPE_BUTTON:
                    panel.add(c, entry.getDecoration() == null ? "spanx,gapright " + getGapRight() : "width n:n:160,gapright " + getGapRight());

                    break;
                case ConfigContainer.TYPE_TEXTAREA:
                case ConfigContainer.TYPE_LISTCONTROLLED:
                    panel.add(new JScrollPane(c), "spanx,gapright " + getGapRight() + ",growy,pushy");

                    break;
                case ConfigContainer.TYPE_CONTAINER:
                    /**
                     * TODO . handly different containers
                     */
                    break;

                default:
                    panel.add(c, entry.getDecoration() == null ? "spanx,gapright " + getGapRight() : "gapright " + getGapRight());
                    break;
                }

            }
            entries.add(entry);
            currentGroup = null;
            return;
        } else {

            if (currentGroup != group) {

                panel.add(header = Factory.createHeader(group), "spanx,hidemode 3");
                header.setVisible(false);

                currentGroup = group;
            }
            if (entry.getDecoration() != null) {
                switch (entry.getConfigEntry().getType()) {

                case ConfigContainer.TYPE_TEXTAREA:
                case ConfigContainer.TYPE_LISTCONTROLLED:
                    panel.add(entry.getDecoration(), "gapleft " + getGapLeft() + ",spany " + entry.getInput().length + ",spanx");
                    break;
                default:
                    panel.add(entry.getDecoration(), "gapleft " + getGapLeft() + ",spany " + entry.getInput().length + (entry.getInput().length == 0 ? ",spanx" : ""));
                }
            }
            int i = 0;
            for (JComponent c : entry.getInput()) {
                i++;
                switch (entry.getConfigEntry().getType()) {

                case ConfigContainer.TYPE_BUTTON:
                    panel.add(c, entry.getDecoration() == null ? "spanx,gapright " + this.getGapRight() + ",gapleft " + this.getGapLeft() : "width n:n:160,gapright " + this.getGapRight());
                    header.setVisible(true);
                    break;
                case ConfigContainer.TYPE_TEXTAREA:
                    panel.add(new JScrollPane(c), "spanx,gapright " + getGapRight() + ",growy,pushy,gapleft " + getGapLeft());
                    header.setVisible(true);
                    break;
                default:
                    panel.add(c, entry.getDecoration() == null ? "spanx,gapright " + this.getGapRight() + ",gapleft " + this.getGapLeft() : "gapright " + this.getGapRight());
                    header.setVisible(true);
                    break;
                }

            }
        }
        entries.add(entry);

    }

    private String getGapLeft() {
        return "35";
    }

    private String getGapRight() {
        return "20";
    }

    public void addGUIConfigEntry(GUIConfigEntry entry) {
        addGUIConfigEntry(entry, panel);
    }

    /**
     * Should be overwritten to initialise the contentpanel.
     */
    public void initPanel() {
    }

    public final void load() {
        this.loadConfigEntries();
        this.loadSpecial();
    }

    private final void loadConfigEntries() {
        for (GUIConfigEntry akt : entries) {
            akt.load();
        }
    }

    public final void save() {
        this.saveSpecial();
        this.saveConfigEntries();
    }

    /**
     * Should be overwritten to do special loading.
     */
    protected void loadSpecial() {
    }

    /**
     * Should be overwritten to do special saving.
     */
    protected void saveSpecial() {
    }

    @Override
    public void onShow() {
        load();
    }

    @Override
    public void onHide() {

        PropertyType changes = hasChanges();
        this.save();
        if (changes == PropertyType.NEEDS_RESTART) {
            if (!EasyShipmentGui.getInstance().isExitRequested()) {
                int answer = UserIO.getInstance().requestConfirmDialog(0, BiancaL.L("jd.gui.swing.jdgui.settings.ConfigPanel.restartquestion.title", "Restart required!"), BiancaL.L("jd.gui.swing.jdgui.settings.ConfigPanel.restartquestion", "This option needs a JDownloader restart."), null, BiancaL.L("jd.gui.swing.jdgui.settings.ConfigPanel.restartquestion.ok", "Restart NOW!"), null);

                if (FlagsUtils.hasSomeFlags(answer, UserIO.RETURN_DONT_SHOW_AGAIN | UserIO.RETURN_OK)) {
                    ApplicationUtils.restartApplication(false);
                }
            }
        }
    }

    public PropertyType hasChanges() {
        PropertyType ret = PropertyType.NONE;
        Object old;
        synchronized (entries) {
            for (GUIConfigEntry akt : entries) {
                if (akt.getConfigEntry().getPropertyInstance() != null && akt.getConfigEntry().getPropertyName() != null) {
                    if (akt.getConfigEntry().hasChanges()) {
                        ret = ret.getMax(PropertyType.NORMAL);
                    }
                    old = akt.getConfigEntry().getPropertyInstance().getProperty(akt.getConfigEntry().getPropertyName());
                    if (old == null && akt.getText() != null) {
                        ret = ret.getMax(akt.getConfigEntry().getPropertyType());
                    } else if (!old.equals(akt.getText())) {
                        ret = ret.getMax(akt.getConfigEntry().getPropertyType());
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Saves the configentries in THIS panel.
     */
    private final void saveConfigEntries() {
        ArrayList<SubConfiguration> subs = new ArrayList<SubConfiguration>();
        for (GUIConfigEntry akt : entries) {
            if (akt.getConfigEntry().getPropertyInstance() instanceof SubConfiguration && !subs.contains(akt.getConfigEntry().getPropertyInstance())) {
                subs.add((SubConfiguration) akt.getConfigEntry().getPropertyInstance());
            }
            akt.save();
        }

        for (SubConfiguration subConfiguration : subs) {
            subConfiguration.save();
        }
    }

}
