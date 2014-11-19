package com.biancama.gui.swing.components;

import java.awt.Color;
import java.awt.FontMetrics;

import javax.swing.Icon;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.renderer.JRendererLabel;

import com.biancama.utils.gui.BiancaImage;

public class StatusLabel extends JPanel {

    private static final long serialVersionUID = -378709535509849986L;
    public static final int ICONCOUNT = 5;
    private final FontMetrics fontmetrics;
    private JRendererLabel left;
    private String strLeft = "";
    private JRendererLabel[] rights = new JRendererLabel[ICONCOUNT];

    public StatusLabel() {
        super(new MigLayout("ins 0", "[]0[fill,grow,align right]"));
        fontmetrics = getFontMetrics(getFont());
        add(left = new JRendererLabel());
        left.setOpaque(false);
        for (int i = 0; i < ICONCOUNT; i++) {
            add(rights[i] = new JRendererLabel(), "dock east");
            rights[i].setOpaque(false);
        }
        this.setOpaque(true);
    }

    /**
     * clears the icon for left, setIcon AFTER setText
     */
    public void setText(String text, Icon icon) {
        left.setIcon(icon);
        left.setText(text);
        left.setToolTipText(text);
        strLeft = text;
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (left == null) return;
        left.setForeground(fg);
        for (int i = 0; i < rights.length; i++) {
            rights[i].setForeground(fg);
        }
    }

    public void setIcon(int i, Icon icon, String text, String tooltip) {
        if (i < 0 && ICONCOUNT > 0) {
            left.setIcon(icon);
            left.setText(text);
            left.setToolTipText(tooltip);
        } else {
            if (i < 0 || i >= ICONCOUNT) return;
            rights[i].setIcon(icon);
            rights[i].setText(text);
            rights[i].setToolTipText(tooltip);
        }
    }

    @Override
    public void setEnabled(boolean b) {
        if (left == null) return;

        left.setDisabledIcon(BiancaImage.getDisabledIcon(left.getIcon()));
        left.setEnabled(b);
        for (int i = 0; i < ICONCOUNT; i++) {
            rights[i].setDisabledIcon(BiancaImage.getDisabledIcon(rights[i].getIcon()));
            rights[i].setEnabled(b);
        }
    }

    /**
     * Remember, that its always the same panel instance. so we have to reset to
     * defaults before each cellrenderer call.
     */
    public void clearIcons(int counter) {
        for (int i = counter; i < ICONCOUNT; i++) {
            rights[i].setIcon(null);
            rights[i].setText(null);
            rights[i].setToolTipText(null);
        }
    }

    @Override
    public String getToolTipText() {
        StringBuilder sb = new StringBuilder();
        if (left.getToolTipText() != null) {
            sb.append(left.getToolTipText());
        }
        for (int i = rights.length - 1; i >= 0; --i) {
            if (rights[i].getToolTipText() != null) {
                if (sb.length() > 0) sb.append(" | ");
                sb.append(rights[i].getToolTipText());
            }
        }
        if (sb.length() > 0) return sb.toString();
        return null;
    }

    public void setWidth(int iconcount, int width) {
        String s = strLeft;
        int w = (iconcount + (left.getIcon() == null ? 0 : 1)) * 16 + left.getIconTextGap() + 5;
        while (s.length() > 3 && fontmetrics.stringWidth(s) + w >= width) {
            s = s.replaceAll("....$", "...");
        }
        left.setText(s);
    }
}
