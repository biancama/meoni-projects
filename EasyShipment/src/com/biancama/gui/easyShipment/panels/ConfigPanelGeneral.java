package com.biancama.gui.easyShipment.panels;

import java.util.logging.Level;

import javax.swing.JTabbedPane;

import com.biancama.config.ConfigContainer;
import com.biancama.config.ConfigEntry;
import com.biancama.config.ConfigGroup;
import com.biancama.config.Configuration;
import com.biancama.config.SubConfiguration;
import com.biancama.gui.swing.settings.GUIConfigEntry;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.gui.panels.ConfigPanel;
import com.biancama.utils.locale.BiancaL;

public class ConfigPanelGeneral extends ConfigPanel {
    private static final String BiancaL_PREFIX = "com.biancama.gui.easyShipment.panels.ConfigPanelGeneral.";

    @Override
    public String getBreadcrum() {
        return BiancaL.L(this.getClass().getName() + ".breadcrum", this.getClass().getSimpleName());
    }

    public static String getTitle() {
        return BiancaL.L(BiancaL_PREFIX + "general.title", "General");
    }

    private static final long serialVersionUID = 3383448498625377495L;

    private final Configuration configuration;

    public ConfigPanelGeneral(Configuration configuration) {
        super();
        this.configuration = configuration;
        initPanel();
        load();
    }

    @Override
    public void initPanel() {
        ConfigEntry conditionEntry;

        ConfigGroup logging = new ConfigGroup(BiancaL.L("gui.config.general.logging", "Logging"), BiancaTheme.II("gui.images.terminal", 32, 32));

        addGUIConfigEntry(new GUIConfigEntry(new ConfigEntry(ConfigContainer.TYPE_COMBOBOX, configuration, Configuration.Param.PARAM_LOGGER_LEVEL.toString(), new Level[] { Level.ALL, Level.INFO, Level.OFF }, BiancaL.L("gui.config.general.loggerLevel", "Level für's Logging")).setDefaultValue(Level.INFO).setGroup(logging)));

        ConfigGroup update = new ConfigGroup(BiancaL.L("gui.config.general.update", "Update"), BiancaTheme.II("gui.splash.update", 32, 32));

        addGUIConfigEntry(new GUIConfigEntry(conditionEntry = new ConfigEntry(ConfigContainer.TYPE_CHECKBOX, SubConfiguration.getConfig("WEBUPDATE"), Configuration.Param.PARAM_WEBUPDATE_DISABLE.toString(), BiancaL.L("gui.config.general.webupdate.disable2", "Do not inform me about important updates")).setDefaultValue(false).setGroup(update)));
        addGUIConfigEntry(new GUIConfigEntry(new ConfigEntry(ConfigContainer.TYPE_CHECKBOX, configuration, Configuration.Param.PARAM_WEBUPDATE_AUTO_RESTART.toString(), BiancaL.L("gui.config.general.webupdate.auto", "automatisch, ohne Nachfrage ausführen")).setDefaultValue(false).setEnabledCondidtion(conditionEntry, false).setGroup(update)));
        addGUIConfigEntry(new GUIConfigEntry(new ConfigEntry(ConfigContainer.TYPE_CHECKBOX, configuration, Configuration.Param.PARAM_WEBUPDATE_AUTO_SHOW_CHANGELOG.toString(), BiancaL.L("gui.config.general.changelog.auto", "Open Changelog after update")).setDefaultValue(true).setGroup(update)));

        JTabbedPane tabbed = new JTabbedPane();

        tabbed.setOpaque(false);
        tabbed.add(getBreadcrum(), panel);

        this.add(tabbed);
    }

    @Override
    protected void saveSpecial() {
        logger.setLevel((Level) configuration.getProperty(Configuration.Param.PARAM_LOGGER_LEVEL.toString()));
    }

}
