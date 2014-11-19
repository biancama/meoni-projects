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

package com.biancama.gui.easyShipment.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;

import net.miginfocom.swing.MigLayout;

import com.biancama.events.ControlEvent;
import com.biancama.events.ControlListener;
import com.biancama.gui.UserIO;
import com.biancama.gui.panels.LogInfoPanel;
import com.biancama.gui.swing.components.BiancaFileChooser;
import com.biancama.gui.swing.components.linkbutton.BiancaLink;
import com.biancama.gui.swing.interfaces.SwitchPanel;
import com.biancama.log.BiancaLogHandler;
import com.biancama.log.BiancaLogger;
import com.biancama.log.LogFormatter;
import com.biancama.utils.EventUtils;
import com.biancama.utils.FlagsUtils;
import com.biancama.utils.Upload;
import com.biancama.utils.encoding.Encoding;
import com.biancama.utils.gui.io.BiancaIO;
import com.biancama.utils.locale.BiancaL;

public class LogPane extends SwitchPanel implements ActionListener, ControlListener {

    private static final long serialVersionUID = -5753733398829409112L;
    private static final Object LOCK = new Object();

    /**
     * JTextField wo der Logger Output eingetragen wird
     */
    private final JTextPane logField;

    public LogPane() {
        this.setName("LOGDIALOG");
        this.setLayout(new MigLayout("ins 3", "[fill,grow]", "[fill,grow]"));

        logField = new JTextPane();
        // logField.setContentType("text/html");
        logField.setEditable(true);
        logField.setAutoscrolls(true);

        add(new JScrollPane(logField));
    }

    public void actionPerformed(ActionEvent e) {

        switch (e.getID()) {

        case LogInfoPanel.ACTION_SAVE:
            BiancaFileChooser fc = new BiancaFileChooser();
            fc.setApproveButtonText(BiancaL.L("gui.btn_save", "Save"));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (fc.showOpenDialog(this) == BiancaFileChooser.APPROVE_OPTION) {
                File ret = fc.getSelectedFile();
                if (ret != null) {
                    String content = toString();
                    BiancaIO.writeLocalFile(ret, content);
                    BiancaLogger.getLogger().info("Log saved to file: " + ret.getAbsolutePath());
                }
            }
            break;
        case LogInfoPanel.ACTION_UPLOAD:
            Level level = BiancaLogger.getLogger().getLevel();

            if (!level.equals(Level.ALL)) {
                int status = UserIO.getInstance().requestHelpDialog(UserIO.NO_COUNTDOWN, BiancaL.L("gui.logdialog.loglevelwarning.title", "Wrong Loglevel for Uploading selected!"), BiancaL.LF("gui.logdialog.loglevelwarning", "The selected loglevel (%s) isn't preferred to upload a log! Please change it to ALL and create a new log!", level.getName()), null, "http://jdownloader.org/knowledge/wiki/support/create-a-jd-log");
                if (FlagsUtils.hasSomeFlags(status, UserIO.RETURN_CANCEL, UserIO.RETURN_COUNTDOWN_TIMEOUT)) { return; }
            }
            String content = null;
            synchronized (LOCK) {
                content = logField.getSelectedText();
                if (content == null || content.length() == 0) {
                    content = Encoding.UTF8Encode(logField.getText());
                }
            }
            if (content == null || content.length() == 0) { return; }

            String name = UserIO.getInstance().requestInputDialog(UserIO.NO_COUNTDOWN, BiancaL.L("userio.input.title", "Please enter!"), BiancaL.L("gui.askName", "Your name?"), null, null, null, null);
            if (name == null) {
                name = "";
            }

            String question = UserIO.getInstance().requestInputDialog(UserIO.NO_COUNTDOWN, BiancaL.L("userio.input.title", "Please enter!"), BiancaL.L("gui.logger.askQuestion", "Please describe your Problem/Bug/Question!"), null, null, null, null);
            if (question == null) {
                question = "";
            }
            append("\r\n\r\n-------------------------------------------------------------\r\n\r\n");
            String url = Upload.toJDownloader(content, name + "\r\n\r\n" + question);
            if (url != null) {
                try {
                    BiancaLink.openURL(url);
                    append(BiancaL.L("gui.logupload.message", "Please send this loglink to your supporter") + "\r\n");
                    append(url);
                } catch (Exception e1) {
                    BiancaLogger.exception(e1);
                }
            } else {
                UserIO.getInstance().requestConfirmDialog(UserIO.DONT_SHOW_AGAIN | UserIO.NO_CANCEL_OPTION, BiancaL.L("sys.warning.loguploadfailed", "Upload of logfile failed!"));
                append(BiancaL.L("gui.logDialog.warning.uploadFailed", "Upload failed"));
            }
            append("\r\n\r\n-------------------------------------------------------------\r\n\r\n");
            break;
        }

    }

    @Override
    public String toString() {
        synchronized (LOCK) {
            String content = logField.getSelectedText();
            if (content == null || content.length() == 0) {
                content = logField.getText();
            }
            return content;
        }
    }

    @Override
    public void onShow() {
        /*
         * enable autoscrolling by setting the caret to the last position
         */
        /**
         * TODO: not synchronized properbly in loop.
         */
        try {

            EventUtils.getController().addControlListener(this);
            ArrayList<LogRecord> buff = new ArrayList<LogRecord>();

            buff.addAll(BiancaLogHandler.getHandler().getBuffer());

            LogFormatter formater = (LogFormatter) BiancaLogHandler.getHandler().getFormatter();
            StringBuilder sb = new StringBuilder();

            // sb.append("<style type=\"text/css\">");
            // sb.append(".warning { background-color:yellow;}");
            // sb.append(".severe { background-color:red;}");
            // sb.append(".exception { background-color:red;}");
            // // sb.append(".normal { background-color:black;}");
            // sb.append("</style>");

            for (LogRecord lr : buff) {
                // if (lr.getLevel().intValue() >=
                // BiancaLogger.getLogger().getLevel().intValue())

                sb.append(format(lr, formater));
            }
            synchronized (LOCK) {
                logField.setText(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String format(LogRecord lr, LogFormatter formater) {
        if (lr.getThrown() != null) {
            return ("EXCEPTION   " + formater.format(lr));
        } else {
            return (formater.format(lr));
        }
    }

    @Override
    public void onHide() {
        EventUtils.getController().removeControlListener(this);
    }

    public void append(String sb) {
        synchronized (LOCK) {
            Document doc = logField.getDocument();

            EditorKit editorkit = logField.getEditorKit();
            Reader r = new StringReader(sb);
            try {
                editorkit.read(r, doc, doc.getEndPosition().getOffset() - 1);
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void controlEvent(ControlEvent event) {
        if (event.getID() == ControlEvent.CONTROL_LOG_OCCURED) {
            append(format((LogRecord) event.getParameter(), (LogFormatter) BiancaLogHandler.getHandler().getFormatter()));
        }
    }

}
