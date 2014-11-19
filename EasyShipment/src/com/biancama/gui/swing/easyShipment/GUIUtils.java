package com.biancama.gui.swing.easyShipment;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;

import com.biancama.config.SubConfiguration;
import com.biancama.gui.swing.SwingGui;
import com.biancama.gui.swing.easyShipment.utils.EasyShipmentGuiConstants;
import com.biancama.utils.gui.BiancaScreen;

public class GUIUtils {
    private GUIUtils() {
    }

    public static Dimension getLastDimension(Component child, String key) {
        if (key == null) {
            key = child.getName();
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;
        SubConfiguration cfg = getConfig();
        Object loc = cfg.getProperty("DIMENSION_OF_" + key);
        if (loc != null && loc instanceof Dimension) {
            Dimension dim = (Dimension) loc;
            if (dim.width > width) {
                dim.width = width;
            }
            if (dim.height > height) {
                dim.height = height;
            }

            return dim;
        }

        return null;
    }

    /**
     * Returns the gui subconfiguration
     * 
     * @return
     */
    public static SubConfiguration getConfig() {

        return SubConfiguration.getConfig(EasyShipmentGuiConstants.CONFIG_PARAMETER);
    }

    public static Point getLastLocation(Component parent, String key, Component child) {
        if (key == null) {
            key = child.getName();
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;
        Object loc = getConfig().getProperty("LOCATION_OF_" + key);
        if (loc != null && loc instanceof Point) {
            Point point = (Point) loc;
            if (point.x < 0) {
                point.x = 0;
            }
            if (point.y < 0) {
                point.y = 0;
            }
            if (point.x > width) {
                point.x = width;
            }
            if (point.y > height) {
                point.y = height;
            }

            return point;
        }

        return BiancaScreen.getCenterOfComponent(parent, child);
    }

    public static void restoreWindow(JFrame parent, Component component) {
        if (parent == null) {
            parent = SwingGui.getInstance().getMainFrame();
        }

        component.setLocation(getLastLocation(parent, null, component));
        Dimension dim = getLastDimension(component, null);
        if (dim != null) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            dim.width = Math.min(dim.width, screenSize.width);
            dim.height = Math.min(dim.height, screenSize.height);
            component.setSize(dim);
            if (component instanceof JFrame) {
                ((JFrame) component).setExtendedState(getConfig().getIntegerProperty("MAXIMIZED_STATE_OF_" + component.getName(), JFrame.NORMAL));
            }
        } else {
            component.validate();
        }

    }

    public static void saveLastDimension(Component child, String key) {
        if (getConfig() == null) { return; }
        if (key == null) {
            key = child.getName();
        }

        boolean max = false;
        if (child instanceof JFrame) {
            getConfig().setProperty("MAXIMIZED_STATE_OF_" + key, ((JFrame) child).getExtendedState());
            if (((JFrame) child).getExtendedState() != Frame.NORMAL) {
                max = true;
            }
        }
        // do not save dimension if frame is not in normal state
        if (!max) {
            getConfig().setProperty("DIMENSION_OF_" + key, child.getSize());
        }
        getConfig().save();
    }

    public static void saveLastLocation(Component parent, String key) {
        if (getConfig() == null) { return; }
        if (key == null) {
            key = parent.getName();
        }
        // don not save location if frame is not in normal state
        if (parent instanceof JFrame && ((JFrame) parent).getExtendedState() != Frame.NORMAL) { return; }
        if (parent.isShowing()) {
            getConfig().setProperty("LOCATION_OF_" + key, parent.getLocationOnScreen());
            getConfig().save();
        }

    }

}
