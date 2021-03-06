//    jDownloader - Downloadmanager
//    Copyright (C) 2008  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.biancama.gui.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import com.biancama.controlling.ProgressController;
import com.biancama.events.ControlEvent;
import com.biancama.events.ControlListener;
import com.biancama.utils.EventUtils;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.locale.BiancaL;

/**
 * Diese Klasse zeigt alle Fortschritte von momenten aktiven Plugins an.
 * 
 * @author JD-Team
 */
public class TabProgress extends JPanel implements ActionListener, ControlListener, MouseListener {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8537543161116653345L;

    private static final int MAX_BARS = 6;

    // private static final String COLLAPSED = "COLLAPSED";

    /**
     * Hier werden alle Fortschritte der Plugins gespeichert
     */
    private final ArrayList<ProgressController> controllers;

    private final ProgressEntry[] lines;

    private JLabel title;

    private boolean updateInProgress = false;

    private long latestUpdateTIme = 0;

    /**
     * Die Tabelle für die Pluginaktivitäten
     */
    public TabProgress() {
        controllers = new ArrayList<ProgressController>();
        EventUtils.getController().addControlListener(this);
        this.addMouseListener(this);
        this.setVisible(false);
        lines = new ProgressEntry[MAX_BARS];
        this.setLayout(new MigLayout("ins 0,wrap 1", "[fill,grow]"));

        initGUI();
        this.setTitle(BiancaL.LF("gui.progresspane.title", "%s module(s) running", 0));
    }

    private void setTitle(String lf) {
        title.setText(lf);
    }

    private void initGUI() {
        this.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, getBackground().darker()));
        add(title = new JLabel(""), "split 3,gapleft 10,gapbottom 5,gaptop 5");
        title.setIcon(BiancaTheme.II("gui.images.sort", 24, 24));
        title.setIconTextGap(15);
        add(new JSeparator(), "growx,pushx,gapright 15");
        add(new JLabel(BiancaTheme.II("gui.images.config.tip", 16, 16)));
        for (int i = 0; i < MAX_BARS; i++) {
            lines[i] = new ProgressEntry();
        }
    }

    public void actionPerformed(ActionEvent e) {
        setVisible(false);
    }

    private void addController(ProgressController source) {
        synchronized (controllers) {
            if (!controllers.contains(source)) {
                controllers.add(0, source);
            }
        }
    }

    private void removeController(ProgressController source) {
        synchronized (controllers) {
            controllers.remove(source);
        }
    }

    public void controlEvent(ControlEvent event) {
        synchronized (controllers) {
            if (event.getID() == ControlEvent.CONTROL_ON_PROGRESS && event.getSource() instanceof ProgressController) {
                ProgressController source = (ProgressController) event.getSource();
                addController(source);
                if (source.isFinished()) {
                    removeController(source);
                    new GuiRunnable<Object>() {
                        @Override
                        public Object runSave() {
                            update(true);
                            return null;
                        }
                    }.start();
                } else {
                    new GuiRunnable<Object>() {
                        @Override
                        public Object runSave() {
                            update(false);
                            return null;
                        }
                    }.start();
                }
            }
        }
    }

    protected void update(boolean force) {
        if (!force) {
            if (updateInProgress) { return; }
            if ((System.currentTimeMillis() - latestUpdateTIme) < (500)) { return; }
            updateInProgress = true;
        }
        sortControllers();
        synchronized (controllers) {
            for (int i = 0; i < Math.min(controllers.size(), MAX_BARS); i++) {
                if (!lines[i].isAttached()) {
                    this.add(lines[i], "height 20!");
                    // System.out.println("ATTACH " + i);
                    lines[i].setAttached(true);
                } else {
                    // System.out.println("OK " + i);
                }
                lines[i].update(controllers.get(i));

            }
            for (int i = Math.max(0, Math.min(controllers.size(), MAX_BARS)); i < MAX_BARS; i++) {
                if (lines[i].isAttached()) {
                    this.remove(lines[i]);
                    // System.out.println("GONE " + i);

                    lines[i].setAttached(false);
                }

            }
            if (controllers.size() == 0) {
                this.setVisible(false);
            } else {
                this.setVisible(true);
            }
            this.setTitle(BiancaL.LF("gui.progresspane.title", "%s module(s) running", "" + controllers.size()));
            this.revalidate();
            this.repaint();
            if (!force) {
                updateInProgress = false;
            }
            latestUpdateTIme = System.currentTimeMillis();
        }
    }

    /**
     * Sorts the controllers
     */
    private void sortControllers() {
        synchronized (controllers) {
            Collections.sort(controllers, new Comparator<ProgressController>() {

                public int compare(ProgressController o1, ProgressController o2) {
                    if (o1.getPercent() == o2.getPercent()) { return 0; }
                    return o1.getPercent() < o2.getPercent() ? 1 : -1;
                }

            });
            Collections.sort(controllers, new Comparator<ProgressController>() {

                public int compare(ProgressController o1, ProgressController o2) {
                    if (o1.isFinalizing()) { return 1; }
                    return 0;
                }

            });
        }
    }

    private class ProgressEntry extends JPanel implements ActionListener {

        private static final long serialVersionUID = 2676301394570621548L;
        private JLabel label;
        private JProgressBar bar;
        private JButton cancel;
        private boolean attached = false;
        private ProgressController controller = null;
        private final Color bgc;
        private final Color fgc;

        public void setAttached(boolean attached) {
            this.attached = attached;
        }

        public ProgressEntry() {
            this.setLayout(new MigLayout("ins 0", "20![18!]11![grow,fill]5![20!]", "16!"));
            this.add(label = new JLabel());
            this.add(bar = new JProgressBar());
            this.add(cancel = new JButton(BiancaTheme.II("gui.images.cancel", 16, 16)), "width 16!,height 16!");
            bgc = bar.getBackground();
            fgc = bar.getForeground();
            cancel.setBorderPainted(false);
            cancel.setContentAreaFilled(false);
            cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cancel.setOpaque(false);
            cancel.setFocusable(false);
            cancel.addActionListener(this);
            // this.add(new JSeparator(), "span");
        }

        public boolean isAttached() {
            return attached;
        }

        public void update(ProgressController controller) {
            this.controller = controller;
            if (!controller.isInterruptable()) {
                cancel.setIcon(BiancaTheme.II("gui.images.cancel", 16, 16));
                cancel.setEnabled(false);
                cancel.setToolTipText(BiancaL.L("gui.progressbars.cancel.tooltip.disabled", "Not possible to interrupt this module"));
            } else {
                if (controller.isAbort()) {
                    cancel.setIcon(BiancaTheme.II("gui.images.bad", 16, 16));
                    cancel.setEnabled(false);
                    cancel.setToolTipText(BiancaL.L("gui.progressbars.cancel.tooltip.interrupted", "Termination in progress"));
                } else {
                    cancel.setIcon(BiancaTheme.II("gui.images.cancel", 16, 16));
                    cancel.setEnabled(true);
                    cancel.setToolTipText(BiancaL.L("gui.progressbars.cancel.tooltip.enabled", "Interrupt this module"));
                }
            }
            label.setIcon(controller.getIcon() == null ? BiancaTheme.II("gui.images.running", 16, 16) : controller.getIcon());
            label.setToolTipText(BiancaL.L("gui.tooltip.progressicon", "This module is active"));
            if (controller.isIndeterminate()) {
                bar.setIndeterminate(true);
            } else {
                bar.setMaximum(10000);
                bar.setValue(controller.getPercent());
            }
            bar.setStringPainted(true);
            bar.setString(controller.getStatusText());
            if (controller.getColor() != null) {
                bar.setBackground(controller.getColor());
                bar.setForeground(controller.getColor().brighter());
            } else {
                bar.setBackground(bgc);
                bar.setForeground(fgc);
            }
        }

        public void actionPerformed(ActionEvent arg0) {
            if (arg0.getSource() == this.cancel) {
                if (controller != null && !controller.isAbort()) {
                    controller.fireCancelAction();
                }
            }
        }

    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        // System.out.println("Task is :" + this.isCollapsed());
        // SubConfiguration.getConfig("gui").setProperty(TabProgress.COLLAPSED,
        // this.isCollapsed());
        // SubConfiguration.getConfig("gui").save();
    }

}
