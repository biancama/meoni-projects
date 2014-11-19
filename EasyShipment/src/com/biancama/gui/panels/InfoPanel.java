package com.biancama.gui.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.EventListenerList;

import net.miginfocom.swing.MigLayout;

import com.biancama.gui.swing.SwingGui;
import com.biancama.gui.swing.interfaces.DroppedPanel;

public abstract class InfoPanel extends DroppedPanel {

    private static final long serialVersionUID = 5465564955866972884L;

    protected EventListenerList listenerList;

    private final JLabel iconContainer;

    protected final Color valueColor;
    protected final Color titleColor;
    private final HashMap<String, JComponent> map;

    public InfoPanel() {
        super();
        SwingGui.checkEDT();
        listenerList = new EventListenerList();
        map = new HashMap<String, JComponent>();
        this.setLayout(new MigLayout("ins 5", "[]5[]", "[][]"));
        valueColor = getBackground().darker().darker().darker().darker().darker();
        titleColor = getBackground().darker().darker();
        this.iconContainer = new JLabel();
        add(iconContainer, "spany 2,cell 0 0,gapleft 1");
    }

    /**
     * Adds an <code>ActionListener</code> to the InfoPanel.
     * 
     * @param l
     *            the <code>ActionListener</code> to be added
     */
    public void addActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
        listenerList.add(ActionListener.class, l);
    }

    /**
     * Removes an <code>ActionListener</code> from the InfoPanel.
     * 
     * @param l
     *            the listener to be removed
     */
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);

    }

    public void broadcastEvent(final ActionEvent e) {
        for (ActionListener listener : listenerList.getListeners(ActionListener.class)) {
            listener.actionPerformed(e);
        }
    }

    /**
     * Updates an entry previously added my addInfoEntry. Use as key the
     * previously used title
     * 
     * @param key
     * @param string
     */
    protected void updateInfo(String key, Object value) {
        SwingGui.checkEDT();
        JComponent c = map.get(key);

        if (c != null && c instanceof JLabel) {
            ((JLabel) c).setText(value.toString());
        }
    }

    /**
     * Adds an info entry at x ,y title has to be constant and value may be
     * updated later by using updateInfo(..)
     * 
     * @param title
     * @param value
     * @param x
     * @param y
     */
    protected void addInfoEntry(String title, String value, int x, int y) {
        SwingGui.checkEDT();
        JLabel myValue = new JLabel(value);
        myValue.setForeground(valueColor);
        addComponent(title, myValue, x, y);
    }

    protected void addComponent(JComponent myComponent, int x, int y) {
        SwingGui.checkEDT();
        x *= 2;
        x += 1;
        myComponent.setForeground(valueColor);
        add(myComponent, "gapleft 20,cell " + x + " " + y + ",spanx 2");
        map.put(myComponent.getName(), myComponent);
    }

    protected void addComponent(String title, JComponent myComponent, int x, int y) {
        SwingGui.checkEDT();
        x *= 2;
        x += 1;
        JLabel myTitle = new JLabel((title != null && title.length() > 0) ? title + ":" : "");
        myTitle.setForeground(titleColor);
        myComponent.setForeground(valueColor);
        add(myTitle, "gapleft 20,alignx right,cell " + x + " " + y);
        add(myComponent, "cell " + (x + 1) + " " + y);
        map.put(title, myComponent);
    }

    protected JComponent getComponent(String key) {
        return map.get(key);
    }

    protected void setIcon(ImageIcon ii) {
        iconContainer.setIcon(ii);
    }

    @Override
    public void onHide() {
    }

    @Override
    public void onShow() {
    }

}
