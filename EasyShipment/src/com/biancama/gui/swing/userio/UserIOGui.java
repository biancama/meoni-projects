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

package com.biancama.gui.swing.userio;

import java.awt.Point;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileFilter;

import com.biancama.gui.UserIO;
import com.biancama.gui.swing.GuiRunnable;
import com.biancama.gui.swing.components.BiancaFileChooser;
import com.biancama.gui.swing.dialog.ClickPositionDialog;
import com.biancama.gui.swing.dialog.ComboDialog;
import com.biancama.gui.swing.dialog.ConfirmDialog;
import com.biancama.gui.swing.dialog.HelpDialog;
import com.biancama.gui.swing.dialog.InputDialog;
import com.biancama.gui.swing.dialog.TextAreaDialog;
import com.biancama.gui.swing.dialog.TwoTextFieldDialog;
import com.biancama.utils.FlagsUtils;
import com.biancama.utils.gui.BiancaTheme;

public class UserIOGui extends UserIO {
    private UserIOGui() {
        super();
    }

    public static UserIO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserIOGui();
        }
        return INSTANCE;
    }

    @Override
    protected Point showClickPositionDialog(final File imagefile, final String title, final String explain) {
        return new GuiRunnable<Point>() {

            @Override
            public Point runSave() {
                return new ClickPositionDialog(imagefile, title, explain).getPoint();
            }

        }.getReturnValue();
    }

    @Override
    protected int showHelpDialog(final int flag, final String title, final String message, final String helpMessage, final String url) {
        return new GuiRunnable<Integer>() {

            @Override
            public Integer runSave() {
                return new HelpDialog(flag, title, message, helpMessage, url).getReturnValue();
            }

        }.getReturnValue();
    }

    @Override
    protected int showConfirmDialog(final int flag, final String title, final String message, final ImageIcon icon, final String okOption, final String cancelOption) {
        if ((flag & UserIO.NO_USER_INTERACTION) > 0) { return 0; }
        return new GuiRunnable<Integer>() {

            @Override
            public Integer runSave() {
                return new ConfirmDialog(flag, title, message, icon, okOption, cancelOption).getReturnID();
            }

        }.getReturnValue();
    }

    @Override
    protected String showInputDialog(final int flag, final String title, final String message, final String defaultMessage, final ImageIcon icon, final String okOption, final String cancelOption) {
        if ((flag & UserIO.NO_USER_INTERACTION) > 0) { return defaultMessage; }
        return new GuiRunnable<String>() {

            @Override
            public String runSave() {
                return new InputDialog(flag, title, message, defaultMessage, icon, okOption, cancelOption).getReturnID();
            }

        }.getReturnValue();
    }

    @Override
    protected String showTextAreaDialog(final String title, final String message, final String def) {
        return new GuiRunnable<String>() {

            @Override
            public String runSave() {
                TextAreaDialog dialog = new TextAreaDialog(title, message, def);
                if (FlagsUtils.hasAllFlags(dialog.getReturnValue(), UserIO.RETURN_OK)) { return dialog.getResult(); }
                return null;
            }

        }.getReturnValue();
    }

    @Override
    protected String[] showTwoTextFieldDialog(final String title, final String messageOne, final String defOne, final String messageTwo, final String defTwo) {
        return new GuiRunnable<String[]>() {

            @Override
            public String[] runSave() {
                TwoTextFieldDialog dialog = new TwoTextFieldDialog(title, messageOne, defOne, messageTwo, defTwo);
                if (FlagsUtils.hasAllFlags(dialog.getReturnValue(), UserIO.RETURN_OK)) { return dialog.getResult(); }
                return null;
            }

        }.getReturnValue();
    }

    @Override
    public ImageIcon getIcon(int iconInfo) {
        switch (iconInfo) {
        case UserIO.ICON_ERROR:
            return BiancaTheme.II("gui.images.stop", 32, 32);
        case UserIO.ICON_WARNING:
            return BiancaTheme.II("gui.images.warning", 32, 32);
        case UserIO.ICON_QUESTION:
            return BiancaTheme.II("gui.images.help", 32, 32);
        default:
            return BiancaTheme.II("gui.images.config.tip", 32, 32);
        }
    }

    @Override
    protected File[] showFileChooser(String id, String title, Integer fileSelectionMode, FileFilter fileFilter, Boolean multiSelection) {
        BiancaFileChooser fc = new BiancaFileChooser(id);
        if (title != null) {
            fc.setDialogTitle(title);
        }
        if (fileSelectionMode != null) {
            fc.setFileSelectionMode(fileSelectionMode);
        }
        if (fileFilter != null) {
            fc.setFileFilter(fileFilter);
        }
        if (multiSelection != null) {
            fc.setMultiSelectionEnabled(multiSelection);
        }
        if (fc.showOpenDialog(DummyFrame.getDialogParent()) == BiancaFileChooser.APPROVE_OPTION) { return fc.getSelectedFiles(); }
        return null;
    }

    @Override
    public int requestComboDialog(final int flag, final String title, final String question, final Object[] options, final int defaultSelection, final ImageIcon icon, final String okText, final String cancelText, final ListCellRenderer renderer) {
        if ((flag & UserIO.NO_USER_INTERACTION) > 0) { return defaultSelection; }
        return new GuiRunnable<Integer>() {

            @Override
            public Integer runSave() {
                return new ComboDialog(flag, title, question, options, defaultSelection, icon, okText, cancelText, renderer).getReturnID();
            }

        }.getReturnValue();
    }

}
