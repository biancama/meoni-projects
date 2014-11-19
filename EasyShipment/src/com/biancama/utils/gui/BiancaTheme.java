package com.biancama.utils.gui;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import com.biancama.log.BiancaLogger;
import com.biancama.parser.Regex;
import com.biancama.utils.FileSystemUtils;
import com.biancama.utils.URLUtils;
import com.biancama.utils.encoding.Encoding;
import com.biancama.utils.gui.file.BiancaFileFilter;

public class BiancaTheme {
    private static HashMap<String, String> data = new HashMap<String, String>();

    private static HashMap<String, String> defaultData;

    private static Logger logger = BiancaLogger.getLogger();

    public static String THEME_DIR = "easyShipment/themes/";

    private static String currentTheme;

    public static ArrayList<String> getThemeIDs() {
        File dir = URLUtils.getResourceFile(THEME_DIR);
        if (!dir.exists()) { return null; }
        File[] files = dir.listFiles(new BiancaFileFilter(null, ".icl", false));
        ArrayList<String> ret = new ArrayList<String>();

        for (File element : files) {
            ret.add(element.getName().split("\\.")[0]);
        }
        return ret;
    }

    public static String getThemeValue(String key, String def) {
        if (data == null || defaultData == null) {
            logger.severe("Use setTheme() first!");
            setTheme("default");
        }

        if (data.containsKey(key)) { return Encoding.UTF8Decode(data.get(key)); }
        logger.warning("Key not found: " + key + " (" + def + ")");

        if (defaultData.containsKey(key)) {
            def = Encoding.UTF8Decode(defaultData.get(key));
            logger.finest("Use default Value: " + def);
        }
        if (def == null) {
            def = key;
        }
        data.put(key, def);

        return def;

    }

    /**
     * Gibt eine Farbe zum key zurück
     * 
     * @param key
     * @return
     */
    public static Color C(String key, String def) {
        return new Color(Integer.parseInt(V(key, def), 16));
    }

    public static Color C(String key, String def, int alpha) {

        String hex = V(key, def);
        return new Color(Integer.parseInt(hex.substring(0, 2), 16), Integer.parseInt(hex.substring(2, 4), 16), Integer.parseInt(hex.substring(4), 16), alpha);
    }

    /**
     * Gibt ein Image zum key zurück
     * 
     * @param key
     * @return
     */
    public static Image I(String key) {
        return BiancaImage.getImage(V(key));
    }

    /**
     * Gibt ein skaliertes Image zurück
     * 
     * @param key
     * @param width
     * @param height
     * @return
     */
    public static Image I(String key, int width, int height) {
        return BiancaImage.getImage(V(key)).getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    /**
     * Gibt ein icon zum key zurück
     * 
     * @param key
     * @return
     */
    public static ImageIcon II(String key) {
        return II(key, 32, 32);

    }

    /**
     * Gibt ein skaliertes ImageIcon zurück
     * 
     * @param key
     * @param width
     * @param height
     * @return
     */
    public static ImageIcon II(String key, int width, int height) {
        try {
            return new ImageIcon(getImage(V(key), width, height));
        } catch (Exception e) {
            logger.severe("image not found: " + key + "(" + V(key) + "_" + width + "_" + height);
            BiancaLogger.exception(e);
        }
        return null;
    }

    public static Image getImage(String string, int width, int height) {
        BufferedImage img = BiancaImage.getImage(string + "_" + width + "_" + height);
        if (img != null) { return img; }
        try {

            return BiancaImage.getScaledImage(BiancaImage.getImage(string), width, height);
        } catch (Exception e) {
            logger.severe("Could not find image: " + string);
        }
        return null;
    }

    public static String getTheme() {
        if (currentTheme == null) { return "default"; }
        return currentTheme;
    }

    public static void setTheme(String themeID) {
        File file = URLUtils.getResourceFile(THEME_DIR + themeID + ".icl");

        if (!file.exists()) {

            logger.severe("Theme " + themeID + " not installed, switch to default theme");
            themeID = "default";
            // return;

        }
        currentTheme = themeID;
        data = new HashMap<String, String>();
        String str = FileSystemUtils.readFileToString(file);
        String[] lines = Regex.getLines(str);
        for (String element : lines) {
            int split = element.indexOf("=");
            if (split <= 0 || element.startsWith("#")) {
                continue;
            }
            String key = element.substring(0, split).trim();
            String value = element.substring(split + 1).trim();
            if (data.containsKey(key)) {
                logger.severe("Dupe found: " + key);
            } else {
                data.put(key, value);
            }

        }
        if (themeID.equals("default")) {
            defaultData = data;
        }
        if (defaultData == null) {
            defaultData = new HashMap<String, String>();
            file = URLUtils.getResourceFile(THEME_DIR + "default.icl");

            if (!file.exists()) {
                logger.severe("Theme default not installed");
                return;
            }
            data = new HashMap<String, String>();
            str = FileSystemUtils.readFileToString(file);
            lines = Regex.getLines(str);
            for (String element : lines) {
                int split = element.indexOf("=");
                if (split <= 0 || element.startsWith("#")) {
                    continue;
                }
                String key = element.substring(0, split).trim();
                String value = element.substring(split + 1).trim();
                if (data.containsKey(key)) {
                    logger.finer("Dupe found: " + key);
                } else {
                    data.put(key, value);
                }

            }

        }

    }

    /**
     * Gibt einen Theme String zum Key zurück
     * 
     * @param key
     * @return
     */
    public static String V(String key) {
        return getThemeValue(key, null);
    }

    /**
     * Gibt einen Theme String zum Key zurück
     * 
     * @param key
     * @return
     */
    public static String V(String key, String def) {
        return getThemeValue(key, def);
    }

}
