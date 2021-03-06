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

package com.biancama.gui.swing.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import net.miginfocom.swing.MigLayout;

import com.biancama.utils.gui.BiancaScreen;
import com.biancama.utils.locale.BiancaL;

public class ProgressDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = -1749561448228487759L;

    private JButton btnNotOK;

    private JButton btnOK;

    private final JLabel lblMessage;

    private final JProgressBar progress;

    private Thread thread;

    public ProgressDialog(JFrame owner, String message, Thread ob, boolean ok, boolean cancel) {
        super(owner);
        setModal(true);
        setLayout(new MigLayout("wrap 1", "[center]"));
        setAlwaysOnTop(true);
        setTitle(BiancaL.L("gui.dialogs.progress.title", "Fortschritt...bitte warten"));
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        thread = ob;
        lblMessage = new JLabel(message);
        progress = new JProgressBar();

        this.add(lblMessage, "growx");
        this.add(progress, "growx");
        if (ok) {
            btnOK = new JButton(BiancaL.L("gui.btn_ok", "OK"));
            btnOK.addActionListener(this);
            getRootPane().setDefaultButton(btnOK);
            this.add(btnOK, cancel ? "split 2" : "");
        }
        if (cancel) {
            btnNotOK = new JButton(BiancaL.L("gui.btn_cancel", "Abbrechen"));
            btnNotOK.addActionListener(this);
            this.add(btnNotOK);
        }
        setLocation(BiancaScreen.getCenterOfComponent(owner, this));

        pack();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnOK) {
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }
            dispose();
        } else if (e.getSource() == btnNotOK) {
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }
            dispose();
        }
    }

    public void setMaximum(int value) {
        progress.setMaximum(value);
    }

    public void setString(String txt) {
        progress.setString(txt);
    }

    public void setStringPainted(boolean b) {
        progress.setStringPainted(b);
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void setValue(int value) {
        progress.setValue(value);
    }

}
