package com.biancama.utils.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

public class BiancaScreen {
    private BiancaScreen() {
    }

    public static Point getCenterOfComponent(Component parent, Component child) {
        Point center;
        if (parent == null || !parent.isShowing()) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int width = screenSize.width;
            int height = screenSize.height;
            center = new Point(width / 2, height / 2);
        } else {
            center = parent.getLocationOnScreen();
            center.x += parent.getWidth() / 2;
            center.y += parent.getHeight() / 2;
        }
        // Dann Auszurichtende Komponente in die Berechnung einflieÃŸen lassen
        center.x -= child.getWidth() / 2;
        center.y -= child.getHeight() / 2;
        return center;
    }

    public static Point getDockBottomRight(Component child) {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Point((int) (screenSize.getWidth() - child.getWidth() - 20), (int) (screenSize.getHeight() - child.getHeight() - 60));

    }

}
