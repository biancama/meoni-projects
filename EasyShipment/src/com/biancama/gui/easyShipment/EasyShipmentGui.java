package com.biancama.gui.easyShipment;

import com.biancama.config.ConfigContainer;
import com.biancama.controlling.DownloadController;
import com.biancama.controlling.DownloadWatchDog;
import com.biancama.controlling.LinkCheck;
import com.biancama.events.BiancaController;
import com.biancama.events.ControlEvent;
import com.biancama.gui.UIConstants;
import com.biancama.gui.UserIO;
import com.biancama.gui.easyShipment.components.EasyShipmentStatusBar;
import com.biancama.gui.easyShipment.components.toolbar.MainToolBar;
import com.biancama.gui.easyShipment.components.toolbar.ToolBar;
import com.biancama.gui.easyShipment.menus.SaveMenu;
import com.biancama.gui.easyShipment.menus.ShipmentMenu;
import com.biancama.gui.easyShipment.menus.actions.ExitAction;
import com.biancama.gui.easyShipment.menus.actions.RestartAction;
import com.biancama.gui.easyShipment.plugins.OptionalPluginWrapper;
import com.biancama.gui.easyShipment.views.ConfigurationView;
import com.biancama.gui.easyShipment.views.shipment.ShipmentView;
import com.biancama.gui.easyShipment.views.sidebars.configuration.AddonConfig;
import com.biancama.gui.menu.BiancaStartMenu;
import com.biancama.gui.swing.GuiRunnable;
import com.biancama.gui.swing.MainTabbedPane;
import com.biancama.gui.swing.SwingGui;
import com.biancama.gui.swing.TabProgress;
import com.biancama.gui.swing.components.Balloon;
import com.biancama.gui.swing.components.BiancaCollapser;
import com.biancama.gui.swing.easyShipment.GUIUtils;
import com.biancama.gui.swing.easyShipment.utils.EasyShipmentGuiConstants;
import com.biancama.gui.swing.interfaces.SwitchPanel;
import com.biancama.gui.swing.interfaces.TabbedPanelView;
import com.biancama.gui.swing.interfaces.View;
import com.biancama.log.BiancaLogger;
import com.biancama.plugins.DownloadLink;
import com.biancama.plugins.FilePackage;
import com.biancama.utils.ApplicationUtils;
import com.biancama.utils.EventUtils;
import com.biancama.utils.FlagsUtils;
import com.biancama.utils.JavaUtils;
import com.biancama.utils.OSDetector;
import com.biancama.utils.gui.BiancaImage;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.locale.BiancaL;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class EasyShipmentGui extends SwingGui {
    private static final long serialVersionUID = 1048792964102830601L;
    private static EasyShipmentGui INSTANCE;
    private JMenuBar menuBar;
    private EasyShipmentStatusBar statusBar;

    private MainTabbedPane mainTabbedPane;
    private TabProgress multiProgressBar;
    private ConfigurationView configurationView;
    private ShipmentView shipmentView;

    private MainToolBar toolBar;
    private JPanel waitingPane;
    private boolean exitRequested = false;

    private EasyShipmentGui() {
        super("");
        // Important for unittests
        mainFrame.setName("MAINFRAME");

        initDefaults();
        initComponents();

        setWindowIcon();
        setWindowTitle(ApplicationUtils.getTitle());
        layoutComponents();

        mainFrame.pack();

        initLocationAndDimension();
        mainFrame.setVisible(true);

    }

    @Override
    public void displayMiniWarning(String shortWarn, String longWarn) {
        /*
         * TODO: mal durch ein einheitliches notification system ersetzen,
         * welches an das eventsystem gekoppelt ist
         */
        Balloon.show(shortWarn, BiancaTheme.II("gui.images.warning", 32, 32), longWarn);
    }

    /**
     * restores the dimension and location to the window
     */
    private void initLocationAndDimension() {
        Dimension dim = GUIUtils.getLastDimension(mainFrame, null);
        if (dim == null) {
            dim = new Dimension(800, 600);
        }
        mainFrame.setPreferredSize(dim);
        mainFrame.setSize(dim);
        mainFrame.setMinimumSize(new Dimension(400, 100));
        mainFrame.setLocation(GUIUtils.getLastLocation(null, null, mainFrame));
        mainFrame.setExtendedState(GUIUtils.getConfig().getIntegerProperty("MAXIMIZED_STATE_OF_" + mainFrame.getName(), JFrame.NORMAL));


    }

    private void initComponents() {
        menuBar = createMenuBar();
        statusBar = new EasyShipmentStatusBar();
        waitingPane = new JPanel();
        waitingPane.setOpaque(false);
        mainTabbedPane = MainTabbedPane.getInstance();
        multiProgressBar = new TabProgress();
        toolBar = MainToolBar.getInstance();
        toolBar.registerAccelerators(this);
        shipmentView = ShipmentView.getInstance();
        mainTabbedPane.addTab(shipmentView);
        configurationView = new ConfigurationView();

        mainTabbedPane.addTab(configurationView);
        toolBar.setList(GUIUtils.getConfig().getGenericProperty("TOOLBAR", ToolBar.DEFAULT_LIST).toArray(new String[] {}));

    }

    private void layoutComponents() {
        JPanel contentPane;
        mainFrame.setContentPane(contentPane = new JPanel());
        MigLayout mainLayout = new MigLayout("ins 0 0 0 0,wrap 1", "[grow,fill]", "[grow,fill]0[shrink]");
        contentPane.setLayout(mainLayout);
        mainFrame.setJMenuBar(menuBar);
        mainFrame.add(toolBar, "dock NORTH");

        contentPane.add(mainTabbedPane);
        contentPane.add(multiProgressBar, "hidemode 3");
        contentPane.add(statusBar, "dock SOUTH");

    }

    private void initDefaults() {

        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(this);

        ToolTipManager.sharedInstance().setReshowDelay(0);

    }

    public void setWindowTitle(final String msg) {
        new GuiRunnable<Object>() {
            @Override
            public Object runSave() {
                mainFrame.setTitle(msg);
                return null;
            }
        }.start();
    }

    /**
     * Sets the Windows Icons. lot's of lafs have problems resizing the icon. so
     * we set different sizes. for 1.5 it is only possible to use
     * {@link JFrame#setIconImage(Image)}
     */
    private void setWindowIcon() {
        if (JavaUtils.getJavaVersion() >= 1.6) {
            ArrayList<Image> list = new ArrayList<Image>();
            list.add(BiancaImage.getImage("logo/logo_14_14"));
            list.add(BiancaImage.getImage("logo/logo_15_15"));
            list.add(BiancaImage.getImage("logo/logo_16_16"));
            list.add(BiancaImage.getImage("logo/logo_17_17"));
            list.add(BiancaImage.getImage("logo/logo_18_18"));
            list.add(BiancaImage.getImage("logo/logo_19_19"));
            list.add(BiancaImage.getImage("logo/logo_20_20"));
            list.add(BiancaImage.getImage("logo/jd_logo_64_64"));
            mainFrame.setIconImages(list);
        } else {
            mainFrame.setIconImage(BiancaImage.getImage("logo/logo_17_17"));
        }
    }

    private JMenuBar createMenuBar() {
        JMenuBar ret = new JMenuBar();

        JMenu file = new JMenu(BiancaL.L("com.biancama.gui.easyShipment.menubar.filemenu", "Shipment"));

        file.add(new ShipmentMenu());
      //  file.add(new FindMenu());        
        
        file.add(new SaveMenu());
        file.addSeparator();

        file.add(new RestartAction());
        file.add(new ExitAction());
        ret.add(file);
        BiancaStartMenu m;
        ret.add(m = new AboutMenu());
        m.setIcon(null);

        return ret;
    }

    /**
     * Factorymethode. Erzeugt eine INstanc der Gui oder gibt eine bereits
     * existierende zurück
     * 
     * @return
     */
    public static EasyShipmentGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GuiRunnable<EasyShipmentGui>() {
                @Override
                public EasyShipmentGui runSave() {

                    return new EasyShipmentGui();
                }

            }.getReturnValue();
        }
        return INSTANCE;
    }

    @Override
    public void setFrameStatus(int id) {
        switch (id) {
        case UIConstants.WINDOW_STATUS_MAXIMIZED:
            mainFrame.setState(JFrame.MAXIMIZED_BOTH);
            break;
        case UIConstants.WINDOW_STATUS_MINIMIZED:
            mainFrame.setState(JFrame.ICONIFIED);
            break;
        case UIConstants.WINDOW_STATUS_NORMAL:
            mainFrame.setState(JFrame.NORMAL);
            mainFrame.setVisible(true);
            break;
        case UIConstants.WINDOW_STATUS_FOREGROUND:
            mainFrame.setState(JFrame.NORMAL);
            mainFrame.setFocusableWindowState(false);
            mainFrame.setVisible(true);
            mainFrame.toFront();
            mainFrame.setFocusableWindowState(true);
            break;
        }
    }

    public void controlEvent(ControlEvent event) {
        switch (event.getID()) {
        case ControlEvent.CONTROL_INIT_COMPLETE:
            BiancaLogger.getLogger().info("Init complete");
            new GuiRunnable<Object>() {
                @Override
                public Object runSave() {
                    mainFrame.setEnabled(true);
                    return null;
                }
            }.start();

            break;
        case ControlEvent.CONTROL_SYSTEM_EXIT:
            this.exitRequested = true;
            final String id = BiancaController.requestDelayExit("JDGUI");
            new GuiRunnable<Object>() {
                @Override
                public Object runSave() {
                    mainTabbedPane.onClose();
                    GUIUtils.saveLastLocation(getMainFrame(), null);
                    GUIUtils.saveLastDimension(getMainFrame(), null);
                    GUIUtils.getConfig().save();
                    BiancaController.releaseDelayExit(id);
                    getMainFrame().setVisible(false);
                    getMainFrame().dispose();
                    return null;
                }
            }.start();

            break;

        }
    }

    /**
     * returns true, if the user requested the app to close
     * 
     * @return
     */
    public boolean isExitRequested() {
        return exitRequested;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (e.getComponent() == getMainFrame()) {
            /* dont close/exit if trayicon minimizing is enabled */
            OptionalPluginWrapper addon = ApplicationUtils.getOptionalPlugin("trayicon");
            if (addon != null && addon.isEnabled()) {
                if ((Boolean) addon.getPlugin().interact("enabled", null) == true) {
                    UserIO.getInstance().requestConfirmDialog(UserIO.DONT_SHOW_AGAIN | UserIO.NO_COUNTDOWN | UserIO.NO_CANCEL_OPTION, BiancaL.L("sys.warning.noclose", "JDownloader will be minimized to tray!"));
                    return;
                }
            }
            /*
             * without trayicon also dont close/exit for macos
             */
            if (OSDetector.isMac()) {
                new GuiRunnable<Object>() {
                    @Override
                    public Object runSave() {
                        /* set visible state */
                        getMainFrame().setVisible(false);
                        return null;
                    }
                }.start();
                return;
            }
            closeWindow();
        }
    }

    @Override
    public void closeWindow() {
        if (FlagsUtils.hasSomeFlags(UserIO.getInstance().requestConfirmDialog(UserIO.DONT_SHOW_AGAIN | UserIO.NO_COUNTDOWN | UserIO.DONT_SHOW_AGAIN_IGNORES_CANCEL, BiancaL.L("sys.ask.rlyclose", "Wollen Sie jDownloader wirklich schließen?")), UserIO.RETURN_OK)) {
            EventUtils.getController().exit();
        }
    }

    @Override
    public void setWaiting(final boolean b) {
        internalSetWaiting(b);
    }

    protected void internalSetWaiting(final boolean b) {
        new GuiRunnable<Object>() {
            @Override
            public Object runSave() {
                getMainFrame().setGlassPane(waitingPane);
                waitingPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                getMainFrame().getGlassPane().setVisible(b);
                return null;
            }
        }.waitForEDT();
    }

    @Override
    public void setContent(SwitchPanel tabbedPanel) {

        View view;
        if (tabbedPanel instanceof View) {
            view = (View) tabbedPanel;
        } else {
            view = new TabbedPanelView(tabbedPanel);
        }

        if (!mainTabbedPane.contains(view)) {
            mainTabbedPane.addTab(view);
        }
        mainTabbedPane.setSelectedComponent(view);
    }

    public MainTabbedPane getMainTabbedPane() {
        return this.mainTabbedPane;
    }

    @Override
    public void requestPanel(final Panels panel, final Object param) {
        new GuiRunnable<Object>() {
            @Override
            public Object runSave() {
                switch (panel) {

                case CONFIGPANEL:
                    if (param instanceof ConfigContainer) {
                        if (((ConfigContainer) param).getEntries().size() == 0) { return null; }
                        showConfigPanel((ConfigContainer) param);
                    }
                    break;
                default:
                    mainTabbedPane.setSelectedComponent(configurationView);
                }
                return null;
            }
        }.start();
    }

    /**
     * Converts a ConfigContainer to a Configpanel and displays it
     * 
     * @param container
     */
    protected void showConfigPanel(final ConfigContainer container) {

        String name = "";
        if (container.getTitle() != null) {
            name = container.getTitle();
        }
        if (name == null && container.getGroup() != null && container.getGroup().getName() != null) {
            name = container.getGroup().getName();
        }

        ImageIcon icon = null;
        if (container.getIcon() != null) {
            icon = container.getIcon();
        } else if (container.getGroup() != null && container.getGroup().getIcon() != null) {
            icon = container.getGroup().getIcon();
        }

        final SwitchPanel oldPanel = mainTabbedPane.getSelectedView().getInfoPanel();
        AddonConfig p = AddonConfig.getInstance(container, name, "_2");
        BiancaCollapser col = new BiancaCollapser() {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClosed() {
                // Show the old info panel if it isn't a closable JDCollapser
                // (e.g. used for config panels)
                if (oldPanel != null && oldPanel instanceof BiancaCollapser) {
                    mainTabbedPane.getSelectedView().setInfoPanel(null);
                } else {
                    mainTabbedPane.getSelectedView().setInfoPanel(oldPanel);
                }
            }

            @Override
            protected void onHide() {
            }

            @Override
            protected void onShow() {
            }

        };
        col.getContent().add(p.getPanel());
        col.setInfos(name, icon);

        this.mainTabbedPane.getSelectedView().setInfoPanel(col);
    }

    @Override
    public void disposeView(SwitchPanel view) {
        if (view instanceof View) {
            view = mainTabbedPane.getComponentEquals((View) view);
            mainTabbedPane.remove((View) view);
        }
    }

    public void addLinks(final ArrayList<DownloadLink> links, boolean hidegrabber, final boolean autostart) {
        if (links.size() == 0) { return; }
        if (hidegrabber || autostart) {
            new Thread() {
                @Override
                public void run() {
                    /* TODO: hier autopackaging ? */
                    ArrayList<FilePackage> fps = new ArrayList<FilePackage>();
                    FilePackage fp = FilePackage.getInstance();
                    fp.setName("Added " + System.currentTimeMillis());
                    for (DownloadLink link : links) {
                        if (link.getFilePackage() == FilePackage.getDefaultFilePackage()) {
                            fp.add(link);
                            if (!fps.contains(fp)) {
                                fps.add(fp);
                            }
                        } else {
                            if (!fps.contains(link.getFilePackage())) {
                                fps.add(link.getFilePackage());
                            }
                        }
                    }
                    LinkCheck.getLinkChecker().checkLinksandWait(links);
                    if (GUIUtils.getConfig().getBooleanProperty(EasyShipmentGuiConstants.PARAM_INSERT_NEW_LINKS_AT.toString(), false)) {
                        DownloadController.getInstance().addAllAt(fps, 0);
                    } else {
                        DownloadController.getInstance().addAll(fps);
                    }
                    if (autostart) {
                        DownloadWatchDog.getInstance().startDownloads();
                    }
                }
            }.start();
        } else {
            // LinkGrabberPanel.getLinkGrabber().addLinks(links);
            // requestPanel(UserIF.Panels.LINKGRABBER, null);
        }
    }

}
