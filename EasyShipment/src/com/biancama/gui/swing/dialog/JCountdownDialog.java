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

package com.biancama.gui.swing.dialog;

import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.biancama.gui.swing.GuiRunnable;
import com.biancama.gui.swing.interfaces.BiancaMouseAdapter;
import com.biancama.utils.FormatterUtils;
import com.biancama.utils.JavaUtils;
import com.biancama.utils.gui.BiancaTheme;
import com.biancama.utils.locale.BiancaL;

public abstract class JCountdownDialog extends JDialog {

    private static final long serialVersionUID = 8114522313158766965L;

    protected Thread countdownThread;
    protected int countdown;

    protected JLabel countDownLabel;

    public JCountdownDialog(JFrame currentgui) {
        super(currentgui);

        initCountdown();
    }

    protected void initCountdown() {
        this.countDownLabel = new JLabel("no countdown");
        countDownLabel.setIcon(BiancaTheme.II("gui.images.cancel", 16, 16));
        countDownLabel.setToolTipText(BiancaL.L("gui.dialog.countdown.tooltip", "This dialog closes after a certain time. Click here to stop the countdown"));
        countDownLabel.addMouseListener(new BiancaMouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                interrupt();
                countDownLabel.removeMouseListener(this);
            }

        });
        setTitle(BiancaL.L("gui.captchaWindow.askForInput", "Please enter..."));
    }

    public void interrupt() {
        if (countdownThread != null) {
            countdownThread.interrupt();
            countdownThread = null;
            countDownLabel.setEnabled(false);
        }
    }

    protected abstract void onCountdown();

    protected void countdown(int time) {
        this.countdown = time;
        countdownThread = new Thread() {

            // @Override
            @Override
            public void run() {

                while (!isVisible()) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                int c = countdown;

                while (--c >= 0) {
                    if (!isVisible()) { return; }
                    if (countdownThread == null) { return; }
                    final String left = FormatterUtils.formatSeconds(c);

                    new GuiRunnable<Object>() {

                        // @Override
                        @Override
                        public Object runSave() {
                            countDownLabel.setText(left);
                            return null;
                        }

                    }.start();
                    // if (c <= 3)
                    // JDSounds.P("sound.captcha.onCaptchaInputEmergency");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                    if (countdown < 0) { return; }
                    if (!isVisible()) { return; }

                }
                if (countdown < 0) { return; }
                if (!this.isInterrupted()) {
                    onCountdown();
                }

            }

        };

        countdownThread.start();

    }

    /**
     * Wrapper für Java 1.5 (Mac User)
     */
    @Override
    public void setIconImage(Image image) {
        if (JavaUtils.getJavaVersion() >= 1.6) {
            super.setIconImage(image);
        }
    }

    /**
     * Wrapper für Java 1.5 (Mac User)
     */
    @Override
    public void setIconImages(List<? extends Image> icons) {
        if (JavaUtils.getJavaVersion() >= 1.6) {
            super.setIconImages(icons);
        }
    }

}
