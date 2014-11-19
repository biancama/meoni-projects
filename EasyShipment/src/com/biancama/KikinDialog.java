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

package com.biancama;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import net.miginfocom.swing.MigLayout;

import com.biancama.gui.UserIO;
import com.biancama.gui.swing.components.linkbutton.BiancaLink;
import com.biancama.gui.swing.dialog.AbstractDialog;
import com.biancama.http.Browser;
import com.biancama.utils.ApplicationUtils;
import com.biancama.utils.OSDetector;
import com.biancama.utils.URLUtils;
import com.biancama.utils.gui.BiancaImage;
import com.biancama.utils.gui.ExecuterUtils;
import com.biancama.utils.locale.BiancaL;

public class KikinDialog extends AbstractDialog {

    private JLabel label;

    private JTextPane textFieldAccept;
    /**
     * Radiobutton to accept Kikin Installadtion
     */
    private JRadioButton radioAccept;
    /**
     * Radio button to deny kikin installation
     */
    private JRadioButton radioDeny;

    private JTextPane textFieldDeny;

    private JTextPane textField;

    public KikinDialog() {
        super(UserIO.NO_COUNTDOWN, BiancaL.L("gui.installer.kikin.title", "Kikin Installer"), null, BiancaL.L("gui.installer.kikin.ok", "Continue"), BiancaL.L("gui.installer.kikin.cancel", "Cancel"));

        init();
    }

    /**
* 
*/
    private static final long serialVersionUID = -7647771640756844691L;

    @Override
    public JComponent contentInit() {
        JPanel cp = new JPanel(new MigLayout("ins 0,wrap 1", "[fill,grow]", "[][fill,grow][]"));
        // cp.setLayout(new MigLayout("ins 0,wrap 1,debug", "[fill,grow]"));
        JPanel p = new JPanel(new MigLayout("ins 5,wrap 2"));

        JLabel lbl;
        p.add(lbl = new JLabel(BiancaL.L("gui.installer.kikin.message", "Free! Personalize your search experience")), "alignx left, aligny bottom");
        Font f = lbl.getFont();

        // bold
        lbl.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));

        p.add(new JLabel(BiancaImage.getImageIcon(URLUtils.getResourceFile("tools/Windows/kikin/kikin.png"))), "alignx right,aligny top");
        p.add(new JSeparator(), "spanx,growx,pushx");
        if (BiancaL.getLocale().getLanguageCode().equals("de")) {
            label = new JLabel(BiancaImage.getImageIcon(URLUtils.getResourceFile("tools/Windows/kikin/ins_de.png")));
        } else {
            label = new JLabel(BiancaImage.getImageIcon(URLUtils.getResourceFile("tools/Windows/kikin/ins_en.png")));
        }
        cp.add(p, "growx, pushx");
        cp.add(label, "alignx left,aligny top");
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setVerticalAlignment(SwingConstants.TOP);

        // Create the radio buttons using the actions
        radioAccept = new JRadioButton();
        radioDeny = new JRadioButton();
        radioDeny.setSelected(true);
        // Associate the two buttons with a button group
        ButtonGroup group = new ButtonGroup();
        group.add(radioAccept);
        group.add(radioDeny);

        radioAccept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (radioAccept.isSelected()) {
                    btnOK.setEnabled(true);
                    btnOK.setToolTipText(null);
                } else {
                    btnOK.setEnabled(false);
                    btnOK.setToolTipText(BiancaL.L("gui.installer.kikin.tooltip", "Please read and accept the conditions"));
                }

            }

        });
        textField = new JTextPane();
        textField.setContentType("text/html");

        textField.setBorder(null);

        textField.setOpaque(false);
        textField.putClientProperty("Synthetica.opaque", Boolean.FALSE);
        textField.setText("<style type='text/css'> body {        font-family: Geneva, Arial, Helvetica, sans-serif; font-size:9px;}</style>" + BiancaL.L("gui.installer.kikin.whatis3", "<b>kikin uses your browsing history to give you personalized content from sites you like.   <a href=\"http://jdownloader.org/kikin\">more...</a></b>"));
        textField.setEditable(false);
        HyperlinkListener hyperlinkListener;
        textField.addHyperlinkListener(hyperlinkListener = new HyperlinkListener() {

            public void hyperlinkUpdate(HyperlinkEvent e) {

                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        BiancaLink.openURL(e.getURL());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        ExecuterUtils.runCommand("cmd", new String[] { "/c", "start " + e.getURL() + "" }, null, 0);
                    }
                }

            }

        });
        textFieldAccept = new JTextPane();
        textFieldAccept.setContentType("text/html");

        textFieldAccept.setBorder(null);

        textFieldAccept.setOpaque(false);
        textFieldAccept.putClientProperty("Synthetica.opaque", Boolean.FALSE);
        textFieldAccept.setText("<style type='text/css'> body {        font-family: Geneva, Arial, Helvetica, sans-serif; font-size:9px;}</style>" + BiancaL.L("gui.installer.kikin.agree3", "<span>Yes, I would like to install kikin. I agree to the <a href=\"http://www.kikin.com/terms\">Terms of Service</a> and <a href=\"http://www.kikin.com/privacy\">Privacy Policy</a></span>"));
        textFieldAccept.setEditable(false);

        textFieldAccept.addHyperlinkListener(hyperlinkListener);

        textFieldDeny = new JTextPane();
        textFieldDeny.setContentType("text/html");

        textFieldDeny.setBorder(null);

        textFieldDeny.setOpaque(false);
        textFieldDeny.putClientProperty("Synthetica.opaque", Boolean.FALSE);
        textFieldDeny.setText("<style type='text/css'> body {        font-family: Geneva, Arial, Helvetica, sans-serif; font-size:9px;}</style>" + BiancaL.L("gui.installer.kikin.deny3", "<span>No, thanks</span>"));
        textFieldDeny.setEditable(false);
        textFieldDeny.addHyperlinkListener(hyperlinkListener);
        JPanel pp = new JPanel(new MigLayout("ins 0,wrap 2", "[shrink][grow,fill]", "[]"));

        pp.add(textField, "spanx");
        pp.add(radioAccept, "aligny ");
        pp.add(textFieldAccept, "aligny bottom,gapbottom 4");
        pp.add(radioDeny, "aligny bottom");
        pp.add(textFieldDeny, "aligny bottom,gapbottom 4");
        pp.add(new JSeparator(), "spanx,growx,pushx");
        cp.add(pp, "growx,pushx");
        // btnOK.setEnabled(false);

        btnOK.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (radioAccept.isSelected()) {
                    // KikinDialog.this.setVisible(false);
                    File file = URLUtils.getResourceFile("tools/Windows/kikin/kikin_installer.exe");
                    // Executer exec = new Executer(file.getAbsolutePath());
                    // exec.setWaitTimeout(1000000);
                    // exec.start();
                    // exec.waitTimeout();
                    // if (exec.getException() != null) {
                    System.out.println("Install " + file.getAbsolutePath());
                    ExecuterUtils.runCommand("cmd", new String[] { "/c", "start  " + file.getName() + "" }, file.getParent(), 10 * 60000);
                    // }
                    try {
                        new Browser().getPage("http://service.jdownloader.org/update/inst.php?k=1&o=" + OSDetector.getOSString() + "&v=" + ApplicationUtils.getRevision());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else {
                    try {
                        new Browser().getPage("http://service.jdownloader.org/update/inst.php?k=0&o=" + OSDetector.getOSString() + "&v=" + ApplicationUtils.getRevision());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

            }

        });

        btnCancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                try {
                    new Browser().getPage("http://service.jdownloader.org/update/inst.php?k=0&o=" + OSDetector.getOSString() + "&v=" + ApplicationUtils.getRevision());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }

        });
        return cp;
    }

    public Integer getReturnID() {
        return super.getReturnValue();
    }

    @Override
    protected void packed() {
        this.setSize(550, 400);
    }

}
