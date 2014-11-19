package com.biancama;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import net.miginfocom.swing.MigLayout;

import com.biancama.events.BiancaController;
import com.biancama.events.ControlEvent;
import com.biancama.events.ControlListener;
import com.biancama.gui.easyShipment.util.nativeintegration.ScreenDevices;
import com.biancama.gui.swing.GuiRunnable;
import com.biancama.gui.swing.easyShipment.GUIUtils;
import com.biancama.gui.swing.laf.LookAndFeelController;
import com.biancama.utils.EventUtils;
import com.biancama.utils.gui.BiancaTheme;

class SplashProgressImage {

    private final Image image;

    private long startTime = 0;
    private final int dur = 2500;

    public SplashProgressImage(Image i) {
        image = i;
    }

    public Image getImage() {
        return image;
    }

    public float getAlpha() {
        if (this.startTime == 0) {
            this.startTime = System.currentTimeMillis();
        }
        return Math.min((System.currentTimeMillis() - startTime) / (float) dur, 1.0f);
    }

}

public class SplashScreen implements ControlListener {

    public static final int SPLASH_FINISH = 0;
    public static final int SPLASH_PROGRESS = 1;

    private final ImageIcon image;

    private JLabel label;

    private JWindow window;

    private final int x;
    private final int y;

    private JProgressBar progress;

    private String curString = "";

    private GraphicsDevice gd = null;

    public SplashScreen(BiancaController controller) throws IOException, AWTException {

        LookAndFeelController.setUIManager();
        this.image = new ImageIcon(BiancaTheme.I("gui.splash"));

        try {
            Object loc = GUIUtils.getConfig().getProperty("LOCATION_OF_MAINFRAME");
            if (loc != null && loc instanceof Point) {
                Point point = (Point) loc;
                if (point.x < 0) {
                    point.x = 0;
                }
                if (point.y < 0) {
                    point.y = 0;
                }
                gd = ScreenDevices.getGraphicsDeviceforPoint(point);
            } else {
                gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            }
        } catch (Exception e) {
            gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        }
        Rectangle v = gd.getDefaultConfiguration().getBounds();
        int screenWidth = (int) v.getWidth();
        int screenHeight = (int) v.getHeight();
        x = screenWidth / 2 - image.getIconWidth() / 2;
        y = screenHeight / 2 - image.getIconHeight() / 2;

        initGui();

        controller.addControlListener(this);
    }

    private void initGui() {
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        label = new JLabel();
        label.setIcon(image);

        window = new JWindow(gc);

        window.setLayout(new MigLayout("ins 0,wrap 1", "[grow,fill]", "[grow,fill]0[]"));
        window.setAlwaysOnTop(true);
        window.setSize(image.getIconWidth(), image.getIconHeight());
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        window.add(label);
        window.add(progress = new JProgressBar(), "hidemode 3,height 20!");
        progress.setVisible(true);

        progress.setIndeterminate(true);
        window.pack();
        Rectangle b = gc.getBounds();
        window.setLocation(b.x + x, b.y + y);
        window.setVisible(true);

    }

    private void finish() {
        new GuiRunnable<Object>() {
            @Override
            public Object runSave() {

                window.dispose();
                return null;
            }
        }.start();
    }

    private void incProgress() {
        new GuiRunnable<Object>() {
            @Override
            public Object runSave() {
                progress.setStringPainted(true);
                progress.setString(curString);
                return null;
            }
        }.start();
    }

    public void controlEvent(ControlEvent event) {
        if (event.getID() == SPLASH_PROGRESS) {
            if (event.getParameter() != null && event.getParameter() instanceof String) {
                synchronized (curString) {
                    curString = (String) event.getParameter();
                }
            }

            incProgress();
        } else if (event.getID() == ControlEvent.CONTROL_INIT_COMPLETE && event.getSource() instanceof Main) {
            EventUtils.getController().removeControlListener(this);
            finish();
        } else if (event.getID() == SPLASH_FINISH) {
            EventUtils.getController().removeControlListener(this);
            finish();
        }

    }
}