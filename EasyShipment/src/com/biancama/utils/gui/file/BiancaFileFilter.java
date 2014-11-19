package com.biancama.utils.gui.file;

import java.io.File;
import java.io.FileFilter;

public class BiancaFileFilter extends javax.swing.filechooser.FileFilter implements FileFilter {
    /**
     * Accept directories in the filter
     */
    private boolean acceptDirectories = true;

    /**
     * Description of the filter
     */
    private String description;

    /**
     * extension of the filter
     */
    private String[] extension = null;

    /**
     * Constructor of File filter
     * 
     * @param description
     *            Description of the file filter
     * @param extension
     *            extension of file included in the filter, separated by |
     * @param acceptDirectories
     *            Directories must be encluded or not
     */
    public BiancaFileFilter(String description, String extension, boolean acceptDirectories) {
        if (description != null) {
            this.description = description;
        } else {
            this.description = "Container files";
        }
        this.extension = extension.split("\\|");
        this.acceptDirectories = acceptDirectories;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) { return acceptDirectories; }
        for (String element : extension) {
            if (f.getName().toLowerCase().endsWith(element.toLowerCase())) { return true; }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

}
