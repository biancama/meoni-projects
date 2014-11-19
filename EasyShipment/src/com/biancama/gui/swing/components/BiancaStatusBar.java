//    jDownloader - Downloadmanager
//    Copyright (C) 2009  JD-Team support@jdownloader.org
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

package com.biancama.gui.swing.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import com.biancama.config.SubConfiguration;
import com.biancama.events.ControlEvent;
import com.biancama.events.ControlListener;
import com.biancama.utils.EventUtils;
import com.biancama.utils.FormatterUtils;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.locale.BiancaL;

public class BiancaStatusBar extends JPanel implements ChangeListener, ControlListener {

    private static final long serialVersionUID = 3676496738341246846L;

    private JDSpinner spMaxChunks;

    private JDSpinner spMaxDls;

    private JDSpinner spMaxSpeed;

    public BiancaStatusBar() {
        SubConfiguration.getConfig("DOWNLOAD");

        initGUI();
    }

    private class JDSpinner extends JPanel {
        private static final long serialVersionUID = 8892482065686899916L;

        private final JLabel lbl;

        private final JSpinner spn;

        public JDSpinner(String label) {
            super(new MigLayout("ins 0", "[][grow,fill]"));

            lbl = new JLabel(label) {
                private static final long serialVersionUID = 8794670984465489135L;

                @Override
                public Point getToolTipLocation(MouseEvent e) {
                    return new Point(0, -25);
                }
            };
            spn = new JSpinner();
            spn.addChangeListener(BiancaStatusBar.this);

            add(lbl);
            add(spn, "w 70!, h 20!");
        }

        public JSpinner getSpinner() {
            return spn;
        }

        public void setText(String s) {
            lbl.setText(s);
        }

        public void setValue(Integer i) {
            spn.setValue(i);
        }

        public Integer getValue() {
            return (Integer) spn.getValue();
        }

        public void setColor(Color c) {
            lbl.setForeground(c);
            ((DefaultEditor) spn.getEditor()).getTextField().setForeground(c);
        }

        @Override
        public void setToolTipText(String s) {
            lbl.setToolTipText(s);
        }

    }

    private void initGUI() {
        this.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, getBackground().darker()));

        setLayout(new MigLayout("ins 0 0 0 0", "[fill,grow,left][shrink,right][shrink,right][shrink,right][shrink,right][shrink,right]", "[23px!]"));

        EventUtils.getController().addControlListener(this);

        add(spMaxChunks);
        add(spMaxDls);
        add(spMaxSpeed);

    }

    private void colorizeSpinnerSpeed() {
        /* färbt den spinner ein, falls speedbegrenzung aktiv */
        if (spMaxSpeed.getValue() > 0) {
            spMaxSpeed.setColor(BiancaTheme.C("gui.color.statusbar.maxspeedhighlight", "ff0c03"));
        } else {
            spMaxSpeed.setColor(null);
        }
    }

    public void controlEvent(ControlEvent event) {
    }

    /**
     * Setzt die Downloadgeschwindigkeit
     * 
     * @param speed
     *            bytes pro sekunde
     */
    public void setSpeed(int speed) {
        if (speed <= 0) {
            spMaxSpeed.setText(BiancaL.L("gui.statusbar.speed", "Max. Speed"));
        } else {
            spMaxSpeed.setText("(" + FormatterUtils.formatReadable(speed) + "/s)");
        }
    }

    public void setSpinnerSpeed(Integer speed) {
        spMaxSpeed.setValue(speed);
        colorizeSpinnerSpeed();
    }

    public void stateChanged(ChangeEvent e) {
    }

}
