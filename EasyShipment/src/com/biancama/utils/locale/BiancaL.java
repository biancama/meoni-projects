package com.biancama.utils.locale;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Map.Entry;

import javax.swing.JComponent;

import com.biancama.config.SubConfiguration;
import com.biancama.events.BiancaBroadcaster;
import com.biancama.http.Browser;
import com.biancama.log.BiancaLogger;
import com.biancama.utils.EditDistance;
import com.biancama.utils.URLUtils;
import com.biancama.utils.encoding.Encoding;
import com.biancama.utils.gui.file.BiancaFileFilter;

public class BiancaL {
    private static final HashMap<String, BiancaLocale> CACHE = new HashMap<String, BiancaLocale>();

    public static final String CONFIG = "LOCALE";

    private static String COUNTRY_CODE = null;

    private static HashMap<Integer, String> DATA = new HashMap<Integer, String>();

    public static boolean DEBUG = false;

    private static HashMap<Integer, String> DEFAULT_DATA = null;

    private static int KEY;

    private static String LANGUAGES_DIR = "easyShipment/languages/";

    public static final String LOCALE_PARAM_ID = "LOCALE4";

    public static final BiancaLocale DEFAULT_LOCALE = BiancaL.getInstance("it");

    private static BiancaBroadcaster<BiancaLListener, BiancaLEvent> BROADCASTER = null;

    private static File LOCALE_FILE;

    private static BiancaLocale LOCALE_ID;

    private static String STATIC_LOCALE;

    private static HashMap<String, String> SETTINGS;

    /**
     * Returns SETTINGS form the current lng file
     * 
     * @return
     */
    public static HashMap<String, String> getSettings() {
        if (SETTINGS == null) {
            SETTINGS = new HashMap<String, String>();
        }
        return SETTINGS;
    }

    public static BiancaBroadcaster<BiancaLListener, BiancaLEvent> getBroadcaster() {
        if (BROADCASTER == null) {
            BROADCASTER = new BiancaBroadcaster<BiancaLListener, BiancaLEvent>() {

                @Override
                protected void fireEvent(BiancaLListener listener, BiancaLEvent event) {
                    listener.onBiancaLEvent(event);

                }

            };

        }
        return BROADCASTER;
    }

    /**
     * returns the correct country code
     * 
     * @return
     */
    public static String getCountryCodeByIP() {
        if (COUNTRY_CODE != null) { return COUNTRY_CODE; }

        if ((COUNTRY_CODE = SubConfiguration.getConfig(BiancaL.CONFIG).getStringProperty("DEFAULTLANGUAGE", null)) != null) { return COUNTRY_CODE; }
        Browser br = new Browser();
        br.setConnectTimeout(10000);
        br.setReadTimeout(10000);
        try {
            COUNTRY_CODE = br.getPage("http://www.jdownloader.org/advert/getLanguage.php?id=" + System.currentTimeMillis() + new Random(System.currentTimeMillis()).nextLong());
            if (!br.getRequest().getHttpConnection().isOK()) {
                COUNTRY_CODE = null;
            } else {
                COUNTRY_CODE = COUNTRY_CODE.trim().toUpperCase();

                SubConfiguration.getConfig(BiancaL.CONFIG).setProperty("DEFAULTLANGUAGE", COUNTRY_CODE);
                SubConfiguration.getConfig(BiancaL.CONFIG).save();
            }
        } catch (Exception e) {
            COUNTRY_CODE = null;
        }
        return COUNTRY_CODE;
    }

    /**
     * Creates a new BiancaLocale instance or uses a cached one
     * 
     * @param lngGeoCode
     * @return
     */
    public static BiancaLocale getInstance(String lngGeoCode) {
        BiancaLocale ret;
        if ((ret = CACHE.get(lngGeoCode)) != null) { return ret; }
        ret = new BiancaLocale(lngGeoCode);
        CACHE.put(lngGeoCode, ret);
        return ret;
    }

    /**
     * Returns an array for the best matching KEY to text
     * 
     * @param text
     * @return
     */
    public static String[] getKeysFor(String text) {
        ArrayList<Integer> bestKeys = new ArrayList<Integer>();
        int bestValue = Integer.MAX_VALUE;
        for (Entry<Integer, String> next : DATA.entrySet()) {
            int dist = EditDistance.getLevenshteinDistance(text, next.getValue());

            if (dist < bestValue) {
                bestKeys.clear();
                bestKeys.add(next.getKey());
                bestValue = dist;
            } else if (bestValue == dist) {
                bestKeys.add(next.getKey());
                bestValue = dist;
            }
        }
        if (bestKeys.size() == 0) { return null; }
        String[] ret = new String[bestKeys.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = hashToKey(bestKeys.get(i));
        }
        return ret;
    }

    public static File getLanguageFile() {
        return LOCALE_FILE;
    }

    /**
     * Gibt den configwert für Locale zurück
     * 
     * @return
     */
    public static BiancaLocale getConfigLocale() {
        return SubConfiguration.getConfig(BiancaL.CONFIG).getGenericProperty(BiancaL.LOCALE_PARAM_ID, BiancaL.DEFAULT_LOCALE);
    }

    /**
     * saves defaultlocal
     */
    public static void setConfigLocale(BiancaLocale l) {
        SubConfiguration.getConfig(BiancaL.CONFIG).setProperty(BiancaL.LOCALE_PARAM_ID, l);
        SubConfiguration.getConfig(BiancaL.CONFIG).save();
    }

    public static BiancaLocale getLocale() {
        return LOCALE_ID;
    }

    public static ArrayList<BiancaLocale> getLocaleIDs() {
        File dir = URLUtils.getResourceFile(LANGUAGES_DIR);
        if (!dir.exists()) { return null; }
        File[] files = dir.listFiles(new BiancaFileFilter(null, ".loc", false));
        ArrayList<BiancaLocale> ret = new ArrayList<BiancaLocale>();
        for (File element : files) {
            if (BiancaGeoCode.parseLanguageCode(element.getName().split("\\.")[0]) == null) {
                element.renameTo(new File(element, ".outdated"));
            } else {
                ret.add(getInstance(element.getName().split("\\.")[0]));
            }
        }
        return ret;
    }

    /**
     * Searches the KEY to a given hashcode. only needed for debug issues
     * 
     * @param hash
     * @return
     */
    private static String hashToKey(Integer hash) {
        BufferedReader f = null;
        try {
            f = new BufferedReader(new InputStreamReader(new FileInputStream(LOCALE_FILE), "UTF8"));
            String line;
            String key;
            while ((line = f.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                int split = line.indexOf("=");
                if (split <= 0) {
                    continue;
                }

                key = line.substring(0, split).trim().toLowerCase();
                if (hash == key.hashCode()) { return key; }

            }

        } catch (Exception e) {
            BiancaLogger.exception(e);
        } finally {
            try {
                if (f != null) {
                    f.close();
                }
            } catch (Exception e1) {
                BiancaLogger.exception(e1);
            }
        }
        return null;
    }

    public static void initLocalisation() {
        JComponent.setDefaultLocale(new Locale(BiancaL.getLocale().getLanguageCode()));
    }

    public static boolean isGerman() {
        String country = System.getProperty("user.country");
        return country != null && country.equalsIgnoreCase("DE");
    }

    public static String L(String key, String def) {
        return BiancaL.getLocaleString(key, def);
    }

    /**
     * Wrapper für String.format(BiancaL.L(..),args)
     * 
     * @param KEY
     * @param def
     * @param args
     * @return
     */
    public static String LF(String key, String def, Object... args) {
        if (DEBUG) { return key; }
        if (args == null || args.length == 0) {
            BiancaLogger.getLogger().severe("FIXME: " + key);
        }
        try {
            return String.format(BiancaL.L(key, def), args);
        } catch (Exception e) {
            BiancaLogger.getLogger().severe("FIXME: " + key);
            return "FIXME: " + key;
        }
    }

    private static void loadDefault() {
        if (DEFAULT_DATA == null) {
            System.err.println("JD have to load the default language, there is an missing entry");
            DEFAULT_DATA = new HashMap<Integer, String>();
            File defaultFile = STATIC_LOCALE == null ? URLUtils.getResourceFile(LANGUAGES_DIR + DEFAULT_LOCALE.getLngGeoCode() + ".loc") : new File(STATIC_LOCALE);
            if (defaultFile.exists()) {
                BiancaL.parseLanguageFile(defaultFile, DEFAULT_DATA);
            } else {
                System.out.println("Could not load the default languagefile: " + defaultFile);
            }
        }
    }

    public static void parseLanguageFile(File file, HashMap<Integer, String> data) {

        BiancaLogger.getLogger().info("parse lng file " + file);
        data.clear();

        if (file == null || !file.exists()) {
            System.out.println("BiancaLocale: " + file + " not found");
            return;
        }

        BufferedReader f;
        try {
            f = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

            String line;
            String key;
            String value;
            while ((line = f.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }

                int split = line.indexOf("=");
                if (split <= 0) {
                    continue;
                }

                key = line.substring(0, split).trim().toLowerCase();
                value = line.substring(split + 1).trim() + (line.endsWith(" ") ? " " : "");
                value = value.replace("\\r", "\r").replace("\\n", "\n");
                if (key.startsWith("!")) {
                    getSettings().put(key.substring(1), value);

                }
                data.put(key.hashCode(), value);
            }
            f.close();
        } catch (IOException e) {
            BiancaLogger.exception(e);
        }

        BiancaLogger.getLogger().info("parse lng file end " + file);
    }

    public static void setLocale(BiancaLocale lID) {
        if (lID == null) { return; }
        LOCALE_ID = lID;
        BiancaLogger.getLogger().info("Loaded language: " + lID);
        LOCALE_FILE = STATIC_LOCALE == null ? URLUtils.getResourceFile(LANGUAGES_DIR + LOCALE_ID.getLngGeoCode() + ".loc") : new File(STATIC_LOCALE);
        if (LOCALE_FILE.exists()) {
            BiancaL.parseLanguageFile(LOCALE_FILE, DATA);
            getBroadcaster().fireEvent(new BiancaLEvent(lID, BiancaLEvent.SET_NEW_LOCALE));
        } else {
            System.out.println("Language " + LOCALE_ID + " not installed");
            return;
        }
    }

    public static String translate(String to, String msg) {
        return BiancaL.translate("auto", to, msg);
    }

    public static String translate(String from, String to, String msg) {
        try {
            LinkedHashMap<String, String> postData = new LinkedHashMap<String, String>();
            postData.put("hl", "de");
            postData.put("text", msg);
            postData.put("sl", from);
            postData.put("tl", to);
            postData.put("ie", "UTF8");

            Browser br = new Browser();
            br.postPage("http://translate.google.com/translate_t", postData);

            return Encoding.UTF8Decode(Encoding.htmlDecode(br.getRegex("<div id\\=result_box dir\\=\"ltr\">(.*?)</div>").getMatch(0)));
        } catch (IOException e) {
            BiancaLogger.exception(e);
            return null;
        }
    }

    /**
     * Use a absolute path to a locale
     * 
     * @param string
     */
    public static void setStaticLocale(String string) {
        STATIC_LOCALE = string;

    }

    public static String getLocaleString(String key2, String def) {
        if (DEBUG) { return key2; }
        if (DATA == null || LOCALE_FILE == null) {
            setLocale(getConfigLocale());
        }
        if (DATA == null) { return "Error in JDL: DATA==null"; }
        KEY = key2.toLowerCase().hashCode();
        if (DATA.containsKey(KEY)) { return DATA.get(KEY); }

        System.out.println("Key not found: " + key2 + " Defaultvalue: " + def);
        if (def == null) {

            def = getDefaultLocaleString(KEY);
            if (def == null) {
                def = key2;
            }
        }

        DATA.put(KEY, def);

        return def;
    }

    /**
     * loads the default translation(english) and returns the string for the
     * givven key
     * 
     * @param key2
     *            stringkey.toLowerCase().hashCode()
     * @return
     */
    public static String getDefaultLocaleString(int key) {
        // DEFAULT_DATA nur im absoluten Notfall laden
        loadDefault();
        if (DEFAULT_DATA.containsKey(key)) { return DEFAULT_DATA.get(key); }
        return null;
    }

}
