package com.biancama.gui.swing;

import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import com.biancama.EasyShipmentConfigConstants;
import com.biancama.config.ConfigGroup;
import com.biancama.gui.swing.components.BiancaUnderlinedText;
import com.biancama.gui.swing.components.linkbutton.BiancaLink;
import com.biancama.log.BiancaLogger;
import com.biancama.utils.gui.BiancaTheme;

public class Factory {
    public static JPanel createHeader(ConfigGroup group) {
        return createHeader(group.getName(), group.getIcon());
    }

    public static JPanel createHeader(String name, ImageIcon icon) {
        JPanel ret = new JPanel(new MigLayout("ins 0", "[]10[grow,fill]3[]"));
        BiancaLink label;
        try {
            ret.add(label = new BiancaLink("<html><u><b>" + name + "</b></u></html>", icon, new URL(EasyShipmentConfigConstants.WIKI_URL + name.replace(" ", "-"))));
            label.setIconTextGap(8);
            label.setBorder(null);
        } catch (MalformedURLException e) {
            BiancaLogger.exception(e);
        }
        ret.add(new JSeparator());
        ret.add(new JLabel(BiancaTheme.II("gui.images.config.tip", 16, 16)));
        ret.setOpaque(false);
        ret.setBackground(null);
        return ret;
    }

    public static JButton createButton(String string, Icon i) {
        return createButton(string, i, null);
    }

    public static JButton createButton(String string, Icon i, ActionListener listener) {
        JButton bt;
        if (i != null) {
            bt = new JButton(string, i);
        } else {
            bt = new JButton(string);
        }

        bt.setContentAreaFilled(false);
        bt.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bt.setFocusPainted(false);
        bt.setBorderPainted(false);
        bt.setHorizontalAlignment(JButton.LEFT);
        bt.setIconTextGap(5);
        if (listener != null) {
            bt.addActionListener(listener);
        }
        bt.addMouseListener(new BiancaUnderlinedText(bt));
        return bt;
    }

}
