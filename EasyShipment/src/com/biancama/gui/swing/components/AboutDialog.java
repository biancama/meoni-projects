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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.biancama.config.SubConfiguration;
import com.biancama.gui.UserIO;
import com.biancama.gui.swing.Factory;
import com.biancama.gui.swing.components.linkbutton.BiancaLink;
import com.biancama.gui.swing.dialog.AbstractDialog;
import com.biancama.gui.swing.easyShipment.EasyShipmentVersion;
import com.biancama.update.WebUpdater;
import com.biancama.utils.FileSystemUtils;
import com.biancama.utils.URLUtils;
import com.biancama.utils.gui.BiancaImage;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.locale.BiancaL;

public class AboutDialog extends AbstractDialog {

    private static final String JDL_PREFIX = "jd.gui.swing.components.AboutDialog.";

    public AboutDialog() {
        super(UserIO.NO_COUNTDOWN | UserIO.NO_OK_OPTION | UserIO.NO_CANCEL_OPTION, BiancaL.L(JDL_PREFIX + "title", "About JDownloader"), null, null, null);

        init();
    }

    private static final long serialVersionUID = -7647771640756844691L;

    @Override
    public JComponent contentInit() {
        JPanel cp = new JPanel(new MigLayout("ins 10 10 0 10, wrap 2"));

        cp.add(new JLabel(BiancaImage.getImageIcon("logo/jd_logo_128_128")), "spany 4, gapright 10");

        JLabel lbl;
        cp.add(lbl = new JLabel(BiancaL.L(JDL_PREFIX + "name", "JDownloader")), "gaptop 15");
        lbl.setFont(lbl.getFont().deriveFont(lbl.getFont().getSize() * 2.0f));
        String branch = SubConfiguration.getConfig("WEBUPDATE").getStringProperty(WebUpdater.BRANCHINUSE, null);
        if (branch == null) {
            branch = "";
        } else {
            branch = "-" + branch + "- ";
        }
        cp.add(new JLabel(branch + BiancaL.LF(JDL_PREFIX + "version", "Version %s", EasyShipmentVersion.JD_VERSION + EasyShipmentVersion.getRevision())));

        cp.add(new JLabel("© AppWork UG (haftungsbeschränkt) 2007-2009"), "gaptop 5");

        cp.add(new JLabel("Synthetica License Registration Number (#289416475)"), "gaptop 15");

        JButton btn;
        cp.add(btn = Factory.createButton(BiancaL.L(JDL_PREFIX + "license", "Show license"), BiancaTheme.II("gui.images.premium", 16, 16), new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String license = FileSystemUtils.readFileToString(URLUtils.getResourceFile("licenses/jdownloader.license"));
                UserIO.getInstance().requestConfirmDialog(UserIO.NO_CANCEL_OPTION | UserIO.STYLE_LARGE | UserIO.NO_ICON, BiancaL.L(JDL_PREFIX + "license.title", "JDownloader License"), license, null, null, null);
            }

        }), "gaptop 15, spanx, split 4");
        btn.setBorder(null);

        try {
            cp.add(new BiancaLink(BiancaL.L(JDL_PREFIX + "homepage", "Homepage"), BiancaTheme.II("gui.images.config.host", 16, 16), new URL("http://www.jdownloader.org/home?lng=en")), "gapleft 10");
            cp.add(new BiancaLink(BiancaL.L(JDL_PREFIX + "forum", "Support board"), BiancaTheme.II("gui.images.list", 16, 16), new URL("http://board.jdownloader.org")), "gapleft 10");
            cp.add(new BiancaLink(BiancaL.L(JDL_PREFIX + "contributers", "Contributers"), BiancaTheme.II("gui.images.accounts", 16, 16), new URL("http://jdownloader.org/knowledge/wiki/contributers")), "gapleft 10");
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

        return cp;
    }

    @Override
    protected void packed() {
        this.remove(countDownLabel);
        this.pack();
        this.setDefaultCloseOperation(AbstractDialog.DISPOSE_ON_CLOSE);
    }

}
