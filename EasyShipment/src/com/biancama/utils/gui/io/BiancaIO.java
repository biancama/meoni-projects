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

package com.biancama.utils.gui.io;

import java.awt.Component;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;

import com.biancama.log.BiancaLogger;
import com.biancama.parser.Regex;
import com.biancama.utils.HashUtils;

public class BiancaIO {

    /**
     * Das aktuelle Verzeichnis (Laden/Speichern)
     */
    private static File currentDirectory;

    /**
     * Schreibt content in eine Lokale textdatei
     * 
     * @param file
     * @param content
     * @return true/False je nach Erfolg des Schreibvorgangs
     */
    public static boolean writeLocalFile(File file, String content) {
        return writeLocalFile(file, content, false);
    }

    /**
     * Schreibt content in eine Lokale textdatei
     * 
     * @param file
     * @param content
     * @return true/False je nach Erfolg des Schreibvorgangs
     */
    public static boolean writeLocalFile(File file, String content, boolean append) {
        try {
            if (!append && file.isFile() && !file.delete()) {
                System.err.println("Konnte Datei nicht löschen " + file);
                return false;
            }
            if (file.getParent() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!append || !file.isFile()) {
                file.createNewFile();
            }
            BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), "UTF8"));

            f.write(content);
            f.close();
            return true;
        } catch (Exception e) {
            BiancaLogger.exception(e);
            return false;
        }
    }

    public static String validateFileandPathName(String name) {
        if (name == null) { return null; }
        return name.replaceAll("([\\\\|<|>|\\||\"|:|\\*|\\?|/|\\x00])+", "_");
    }

    /**
     * Speichert ein byteArray in ein file.
     * 
     * @param file
     * @param bytearray
     * @return Erfolg true/false
     */
    public static boolean saveToFile(File file, byte[] b) {
        try {
            if (file.isFile()) {
                if (!file.delete()) {
                    System.err.println("Konnte Datei nicht überschreiben " + file);
                    return false;
                }
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file, true));
            output.write(b, 0, b.length);
            output.close();
            return true;
        } catch (Exception e) {
            BiancaLogger.exception(e);
            return false;
        }
    }

    /**
     * Speichert ein Objekt
     * 
     * @param frame
     *            eine Komponente
     * @param objectToSave
     *            Das zu speichernde Objekt
     * @param fileOutput
     *            Das File, in das geschrieben werden soll. Falls das File ein
     *            Verzeichnis ist, wird darunter eine Datei erstellt Falls keins
     *            angegeben wird, soll der Benutzer eine Datei auswählen
     * @param name
     *            Dateiname
     * @param extension
     *            Dateiendung (mit Punkt)
     * @param asXML
     *            Soll das Objekt in eine XML Datei gespeichert werden?
     */
    public static void saveObject(Component frame, Object objectToSave, File fileOutput, String name, String extension, boolean asXML) {

        if (fileOutput != null) {
            fileOutput.getParentFile().mkdirs();
        }

        if (fileOutput == null) {
            BiancaFileFilter fileFilter = new BiancaFileFilter(extension, extension, true);
            JFileChooser fileChooserSave = new JFileChooser();
            fileChooserSave.setFileFilter(fileFilter);
            fileChooserSave.setSelectedFile(new File(((name != null) ? name : "*") + ((extension != null) ? extension : ".*")));
            if (currentDirectory != null) {
                fileChooserSave.setCurrentDirectory(currentDirectory);
            }
            if (fileChooserSave.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                fileOutput = fileChooserSave.getSelectedFile();
                currentDirectory = fileChooserSave.getCurrentDirectory();
            }
        }

        if (fileOutput != null) {
            if (fileOutput.isDirectory()) {
                fileOutput = new File(fileOutput, name + extension);

            }

            BiancaIO.waitOnObject(fileOutput);
            BiancaIO.saveReadObject.add(fileOutput);

            if (fileOutput.exists()) {
                fileOutput.delete();
            }
            try {
                FileOutputStream fos = new FileOutputStream(fileOutput);
                BufferedOutputStream buff = new BufferedOutputStream(fos);
                if (asXML) {
                    XMLEncoder xmlEncoder = new XMLEncoder(buff);
                    xmlEncoder.writeObject(objectToSave);
                    xmlEncoder.close();
                } else {
                    ObjectOutputStream oos = new ObjectOutputStream(buff);
                    oos.writeObject(objectToSave);
                    oos.close();
                }
                buff.close();
                fos.close();
            } catch (FileNotFoundException e) {
                BiancaLogger.exception(e);
            } catch (IOException e) {
                BiancaLogger.exception(e);
            }
            String hashPost = HashUtils.getMD5(fileOutput);
            if (hashPost == null) {
                System.err.println("Schreibfehler: " + fileOutput + " Datei wurde nicht erstellt");
            }
            BiancaIO.saveReadObject.remove(fileOutput);

        } else {
            System.err.println("Schreibfehler: Fileoutput: null");
        }
    }

    public static Vector<File> saveReadObject = new Vector<File>();

    public static void waitOnObject(File file) {
        int c = 0;
        while (saveReadObject.contains(file)) {
            if (c++ > 1000) { return; }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {

                BiancaLogger.exception(e);
            }
        }
    }

    /**
     * Lädt ein Objekt aus einer Datei
     * 
     * @param frame
     *            Eine übergeordnete Komponente
     * @param fileInput
     *            Falls das Objekt aus einer bekannten Datei geladen werden
     *            soll, wird hier die Datei angegeben. Falls nicht, kann der
     *            Benutzer über einen Dialog eine Datei aussuchen
     * @param asXML
     *            Soll das Objekt von einer XML Datei aus geladen werden?
     * @return Das geladene Objekt
     */
    public static Object loadObject(Component frame, File fileInput, boolean asXML) {
        Object objectLoaded = null;
        if (fileInput == null) {
            JFileChooser fileChooserLoad = new JFileChooser();
            if (currentDirectory != null) {
                fileChooserLoad.setCurrentDirectory(currentDirectory);
            }
            if (fileChooserLoad.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                fileInput = fileChooserLoad.getSelectedFile();
                currentDirectory = fileChooserLoad.getCurrentDirectory();
            }
        }
        if (fileInput != null) {

            waitOnObject(fileInput);
            saveReadObject.add(fileInput);

            try {
                FileInputStream fis = new FileInputStream(fileInput);
                BufferedInputStream buff = new BufferedInputStream(fis);
                if (asXML) {
                    XMLDecoder xmlDecoder = new XMLDecoder(new BufferedInputStream(buff));
                    objectLoaded = xmlDecoder.readObject();
                    xmlDecoder.close();
                } else {
                    ObjectInputStream ois = new ObjectInputStream(buff);
                    objectLoaded = ois.readObject();
                    ois.close();
                }
                fis.close();
                buff.close();

                saveReadObject.remove(fileInput);
                return objectLoaded;
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            saveReadObject.remove(fileInput);
        }
        return null;
    }

    /**
     * Returns the contents of the file in a byte array. copied from
     * http://www.exampledepot.com/
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] readFileToByteArray(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) { throw new IOException("Could not completely read file " + file.getName()); }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    /**
     * public static String getLocalFile(File file) Liest file über einen
     * bufferdReader ein und gibt den Inhalt asl String zurück
     * 
     * @param file
     * @return File Content als String
     */
    public static String readFileToString(File file) {
        if (file == null) { return null; }
        if (!file.exists()) { return ""; }
        BufferedReader f;
        try {
            f = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

            String line;
            StringBuffer ret = new StringBuffer();
            String sep = System.getProperty("line.separator");
            while ((line = f.readLine()) != null) {
                ret.append(line + sep);
            }
            f.close();
            return ret.toString();
        } catch (IOException e) {

            BiancaLogger.exception(e);
        }
        return "";
    }

    /**
     * Gibt die Endung einer FIle zurück oder null
     * 
     * @param ret
     * @return
     */
    public static String getFileExtension(File ret) {
        if (ret == null) { return null; }
        return getFileExtension(ret.getAbsolutePath());

    }

    public static String getFileExtension(String str) {
        if (str == null) { return null; }

        int i3 = str.lastIndexOf(".");

        if (i3 > 0) { return str.substring(i3 + 1); }
        return null;
    }

    /**
     * copy one file to another, using channels
     * 
     * @param in
     * @param out
     * @returns boolean whether its succeessfull or not
     */
    public static boolean copyFile(File in, File out) {
        if (!in.exists()) { return false; }
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        boolean success = false;
        try {
            try {
                inChannel = new FileInputStream(in).getChannel();
                outChannel = new FileOutputStream(out).getChannel();
            } catch (Exception e1) {
                return false;
            }
            try {
                // magic number for Windows, 64Mb - 32Kb), we use 16Mb here
                int maxCount = (16 * 1024 * 1024) - (32 * 1024);
                long size = inChannel.size();
                long position = 0;
                while (position < size) {
                    position += inChannel.transferTo(position, maxCount, outChannel);
                }
                success = true;
            } catch (Exception e) {
            }
        } finally {
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (Exception e) {
                }
            }
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (Exception e) {
                }
            }
        }
        return success;
    }

    public static boolean removeDirectoryOrFile(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String element : children) {
                boolean success = BiancaIO.removeDirectoryOrFile(new File(dir, element));
                if (!success) { return false; }
            }
        }

        return dir.delete();
    }

    /**
     * Runs recursive through the dir (directory) and list all files. returns
     * null if dir is a file.
     * 
     * @param dir
     * @return
     */
    public static ArrayList<File> listFiles(File dir) {
        if (!dir.isDirectory()) { return null; }
        ArrayList<File> ret = new ArrayList<File>();

        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                ret.addAll(listFiles(f));
            } else {
                ret.add(f);
            }
        }
        return ret;

    }

    /**
     * removes recursive all files and directories in parentFile if the match
     * pattern
     * 
     * @param parentFile
     * @param string
     */
    public static void removeByPattern(File parentFile, final Pattern pattern) {

        removeRekursive(parentFile, new FileSelector() {

            @Override
            public boolean doIt(File file) {

                return Regex.matches(file.getAbsolutePath(), pattern);
            }

        });

    }

    public static abstract class FileSelector {
        public abstract boolean doIt(File file);
    }

    /**
     * Removes all files rekursivly in file, for which fileSelector.doIt returns
     * true
     * 
     * @param file
     * @param fileSelector
     */
    public static void removeRekursive(File file, FileSelector fileSelector) {
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                removeRekursive(f, fileSelector);
            }
            if (fileSelector.doIt(f)) {
                f.delete();
            }

        }

    }
}
