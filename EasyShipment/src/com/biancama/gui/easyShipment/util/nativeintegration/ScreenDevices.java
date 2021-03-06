package com.biancama.gui.easyShipment.util.nativeintegration;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.biancama.gui.swing.easyShipment.GUIUtils;

public class ScreenDevices {
    private static ScreenDevices INSTANCE = new ScreenDevices();

    private static DeviceInfo SCREENS[] = new DeviceInfo[0];
    static {
        try {
            ArrayList<DeviceInfo> robint = new ArrayList<DeviceInfo>();
            for (GraphicsDevice dv : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
                try {
                    DeviceInfo tmp = INSTANCE.new DeviceInfo();
                    tmp.robot = new Robot(dv);
                    tmp.dv = dv;
                    tmp.height = dv.getDisplayMode().getHeight();
                    tmp.width = dv.getDisplayMode().getWidth();
                    tmp.posx = dv.getDefaultConfiguration().getBounds().x;
                    tmp.maxx = tmp.posx + tmp.width;
                    tmp.posy = dv.getDefaultConfiguration().getBounds().y;
                    tmp.maxy = tmp.posy + tmp.height;
                    robint.add(tmp);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }
            SCREENS = robint.toArray(new DeviceInfo[robint.size()]);
        } catch (Exception e) {
        }
    }

    public static boolean gotRobots() {
        return SCREENS.length > 0;
    }

    public static GraphicsDevice getGraphicsDeviceforPoint(Point p) {
        synchronized (SCREENS) {
            if (SCREENS.length == 0) { return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(); }
            for (DeviceInfo dv : SCREENS) {
                if (p.x >= dv.posx && p.x <= dv.maxx && p.y >= dv.posy && p.y <= dv.maxy) { return dv.dv; }
            }
            return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        }
    }

    public static BufferedImage getScreenShot(Rectangle r) throws AWTException {
        synchronized (SCREENS) {
            if (SCREENS.length == 0) { return new Robot().createScreenCapture(r); }
            if (GUIUtils.getConfig().getBooleanProperty("XINERAMAWORKAROUND", false)) {
                for (DeviceInfo dv : SCREENS) {
                    if (r.x >= dv.posx && r.x <= dv.maxx && r.y >= dv.posy && r.y <= dv.maxy) {
                        r.x = r.x - dv.posx;
                        r.y = r.y - dv.posy;
                        return dv.robot.createScreenCapture(r);
                    }
                }
                return SCREENS[0].robot.createScreenCapture(r);
            } else {
                return SCREENS[0].robot.createScreenCapture(r);
            }
        }
    }

    class DeviceInfo {
        Robot robot;
        GraphicsDevice dv;
        int width;
        int height;
        int posx;
        int posy;
        int maxx;
        int maxy;
    }

}
