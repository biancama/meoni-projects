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

package com.biancama.gui.swing.components;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.biancama.gui.swing.GuiRunnable;
import com.biancama.utils.FileSystemUtils;

/**
 * Ein Wrapper um JFileChooser
 * 
 * @author JD-Team
 */
public class BiancaFileChooser extends JFileChooser {

    private static final long serialVersionUID = 3315263822025280362L;
    private final String fcID;
    public static int ImagesOnly = 3;

    public BiancaFileChooser() {
        this(null);
    }

    /**
     * Über die id kann eine ID für den filechooser ausgewählt werden . JD
     * Fielchooser merkt sich für diese id den zuletzt verwendeten Pfad
     * 
     * @param id
     */
    public BiancaFileChooser(String id) {
        super();
        fcID = id;
        setCurrentDirectory(FileSystemUtils.getCurrentWorkingDirectory(fcID));
    }

    // @Override
    @Override
    public File getSelectedFile() {
        File ret = super.getSelectedFile();

        if (ret == null) { return null; }
        if (ret.isDirectory()) {
            FileSystemUtils.setCurrentWorkingDirectory(ret, fcID);
        } else {
            FileSystemUtils.setCurrentWorkingDirectory(ret.getParentFile(), fcID);
        }
        return ret;
    }

    // @Override
    @Override
    public File[] getSelectedFiles() {
        File[] ret = super.getSelectedFiles();

        if (ret == null || ret.length == 0) { return ret; }
        if (ret[0].isDirectory()) {
            FileSystemUtils.setCurrentWorkingDirectory(ret[0], fcID);
        } else {
            FileSystemUtils.setCurrentWorkingDirectory(ret[0].getParentFile(), fcID);
        }
        return ret;
    }

    @Override
    public void setFileSelectionMode(int mode) {
        if (mode == ImagesOnly) {
            setFileFilter(new FileFilter() {

                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) { return true; }
                    String nt = f.getName().toLowerCase();
                    if (nt.endsWith(".jpg") || nt.endsWith(".png") || nt.endsWith(".gif") || nt.endsWith(".jpeg") || nt.endsWith(".bmp")) { return true; }
                    return false;
                }

                @Override
                public String getDescription() {
                    return null;
                }
            });
            mode = FILES_ONLY;
        }
        super.setFileSelectionMode(mode);
    }

    public static File getFile() {
        return getFile(FILES_ONLY);
    }

    public static File getFile(final int mode) {
        return new GuiRunnable<File>() {
            @Override
            public File runSave() {
                BiancaFileChooser fc = new BiancaFileChooser();
                fc.setVisible(true);
                fc.setFileSelectionMode(mode);
                fc.showOpenDialog(null);
                File ret = fc.getSelectedFile();
                return ret;
            }
        }.getReturnValue();
    }
}