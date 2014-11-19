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
//     along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.biancama.gui.swing.laf;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.FontUIResource;

import com.biancama.EasyShipmentInitFlags;
import com.biancama.config.SubConfiguration;
import com.biancama.gui.swing.easyShipment.GUIUtils;
import com.biancama.gui.swing.easyShipment.utils.EasyShipmentGuiConstants;
import com.biancama.log.BiancaLogger;
import com.biancama.utils.FileSystemUtils;
import com.biancama.utils.HexUtils;
import com.biancama.utils.JavaUtils;
import com.biancama.utils.OSDetector;
import com.biancama.utils.crypt.BiancaCrypt;
import com.biancama.utils.locale.BiancaL;
import com.biancama.utils.locale.BiancaLEvent;
import com.biancama.utils.locale.BiancaLListener;

public class LookAndFeelController {

    /**
     *Config parameter to store the users laf selection
     */
    public static final String PARAM_PLAF = "PLAF5";
    public static final String DEFAULT_PREFIX = "LAF_CFG";
    private static boolean uiInitated = false;

    /**
     * Collects all supported LAFs for the current system
     * 
     * @return
     */
    public static LookAndFeelWrapper[] getSupportedLookAndFeels() {
        LookAndFeelInfo[] lafis = UIManager.getInstalledLookAndFeels();

        ArrayList<LookAndFeelWrapper> ret = new ArrayList<LookAndFeelWrapper>();
        for (LookAndFeelInfo lafi : lafis) {
            String clname = lafi.getClassName();

            if (clname.contains("Substance") && JavaUtils.getJavaVersion() >= 1.6) {
                ret.add(new LookAndFeelWrapper(lafi).setName(lafi.getName().replaceAll("([A-Z0-9]\\d*)", " $0").trim()));
            } else if (clname.contains("Synthetica")) {
                ret.add(new LookAndFeelWrapper(lafi).setName(lafi.getName().replaceAll("([A-Z0-9]\\d*)", " $0").trim()));
            } else if (clname.contains("goodie")) {
                LookAndFeelWrapper lafm = new LookAndFeelWrapper(lafi);
                lafm.setName(lafi.getName());
                ret.add(lafm);
            } else if (clname.startsWith("apple.laf")) {
                LookAndFeelWrapper lafm = new LookAndFeelWrapper(lafi);
                lafm.setName("Apple Aqua");
                ret.add(lafm);
            } else if (clname.endsWith("WindowsLookAndFeel")) {
                LookAndFeelWrapper lafm = new LookAndFeelWrapper(lafi);
                lafm.setName("Windows Style");
                ret.add(lafm);
            } else if (clname.endsWith("MetalLookAndFeel") && OSDetector.isLinux()) {
                LookAndFeelWrapper lafm = new LookAndFeelWrapper(lafi);
                lafm.setName("Light(Metal)");
                ret.add(lafm);
            } else if (clname.endsWith("GTKLookAndFeel") && OSDetector.isLinux()) {
                LookAndFeelWrapper lafm = new LookAndFeelWrapper(lafi);
                lafm.setName("Light(GTK)");
                ret.add(lafm);
            } else if (clname.startsWith("com.jtattoo")) {
                LookAndFeelWrapper lafm = new LookAndFeelWrapper(lafi);
                lafm.setName(lafi.getName());
                ret.add(lafm);
            } else if (EasyShipmentInitFlags.SWITCH_DEBUG) {
                LookAndFeelWrapper lafm = new LookAndFeelWrapper(lafi);
                lafm.setName(lafi.getName() + " [Debug]");
                ret.add(lafm);
            }

        }
        return ret.toArray(new LookAndFeelWrapper[] {});
    }

    /**
     * Returns the configured LAF and makes sure that this LAF is supported by
     * the system
     * 
     * @return
     */
    public static LookAndFeelWrapper getPlaf() {
        LookAndFeelWrapper ret = getPlaf0();
        for (LookAndFeelWrapper laf : getSupportedLookAndFeels()) {
            if (ret.getClassName().equals(laf.getClassName())) { return ret; }
        }

        return getDefaultLAFM();

    }

    public static LookAndFeelWrapper getPlaf0() {
        SubConfiguration config = GUIUtils.getConfig();
        Object plaf = config.getProperty(PARAM_PLAF, null);
        if (plaf == null) { return getDefaultLAFM(); }
        if (plaf instanceof LookAndFeelWrapper) {
            if (((LookAndFeelWrapper) plaf).getName() != null) {
                return (LookAndFeelWrapper) plaf;
            } else {
                plaf = getDefaultLAFM();
                config.setProperty(PARAM_PLAF, plaf);
                config.save();
                return (LookAndFeelWrapper) plaf;
            }
        } else if (plaf instanceof String) {
            for (LookAndFeelInfo lafi : UIManager.getInstalledLookAndFeels()) {
                if (lafi.getName().equals(plaf) || lafi.getName().equals("Substance" + plaf)) {
                    plaf = new LookAndFeelWrapper(lafi);
                    config.setProperty(PARAM_PLAF, plaf);
                    config.save();
                    return (LookAndFeelWrapper) plaf;
                }
            }
        }
        return getDefaultLAFM();
    }

    /**
     * Returns the default Look And Feel... may be os dependend
     * 
     * @return
     */
    private static LookAndFeelWrapper getDefaultLAFM() {
        return new LookAndFeelWrapper("de.javasoft.plaf.synthetica.SyntheticaStandardLookAndFeel");
    }

    /**
     * setups the correct Look and Feel
     */
    public static void setUIManager() {
        if (uiInitated) { return; }

        uiInitated = true;

        install();
        try {

            BiancaLogger.getLogger().info("Use Look & Feel: " + getPlaf().getClassName());

            preSetup(getPlaf().getClassName());

            UIManager.put("ClassLoader", FileSystemUtils.getJDClassLoader());
            String laf = getPlaf().getClassName();
            if (laf.contains("Synthetica")) {

                // Sets the Synthetica Look and feel and avoids errors if the
                // synth laf is not loaded (no imports)
                try {

                    Class<?> slaf = Class.forName("de.javasoft.plaf.synthetica.SyntheticaLookAndFeel");

                    Method method = slaf.getMethod("setLookAndFeel", new Class[] { String.class, boolean.class, boolean.class });
                    method.invoke(null, new Object[] { laf, false, false });
                    // disable extended filechooser. jd cares itself for setting
                    // the latestlocation
                    slaf.getMethod("setExtendedFileChooserEnabled", new Class[] { boolean.class }).invoke(null, false);

                } catch (InvocationTargetException e) {

                    // ON some systems (turkish) sntheticy throws bugs when
                    // inited for the SPlashscreen. this workaroudn disables the
                    // spashscreen and
                    // this the synthetica lafs work
                    BiancaLogger.exception(e);
                    try {
                        UIManager.setLookAndFeel(getPlaf().getClassName());
                    } catch (Exception e2) {
                        GUIUtils.getConfig().setProperty(EasyShipmentGuiConstants.PARAM_SHOW_SPLASH.toString(), false);
                        GUIUtils.getConfig().save();
                        BiancaLogger.warning("Disabled Splashscreen cause it cases LAF errors");
                        BiancaLogger.exception(e2);
                        uiInitated = false;
                        return;
                    }
                }

                // SyntheticaLookAndFeel#setLookAndFeel(String className),
            } else {
                UIManager.setLookAndFeel(getPlaf().getClassName());
            }

            UIManager.put("ClassLoader", FileSystemUtils.getJDClassLoader());

            // UIManager.setLookAndFeel(new SyntheticaStandardLookAndFeel());

            // overwrite defaults
            SubConfiguration cfg = SubConfiguration.getConfig(DEFAULT_PREFIX + "." + LookAndFeelController.getPlaf().getClassName());

            postSetup(getPlaf().getClassName());

            for (Entry<String, Object> next : cfg.getProperties().entrySet()) {
                BiancaLogger.getLogger().info("Use special LAF Property: " + next.getKey() + " = " + next.getValue());
                UIManager.put(next.getKey(), next.getValue());
            }

        } catch (Throwable e) {

            BiancaLogger.exception(e);
        }

        BiancaL.getBroadcaster().addListener(new BiancaLListener() {

            @Override
            public void onBiancaLEvent(BiancaLEvent event) {
                if (event.getID() == BiancaLEvent.SET_NEW_LOCALE) {

                    if (BiancaL.getSettings().get("font") != null) {
                        if (isSynthetica()) {
                            String font = BiancaL.getSettings().get("font");
                            int fontsize = 12;
                            try {
                                fontsize = Integer.parseInt(BiancaL.getSettings().get("fontsize"));
                            } catch (Exception e) {
                            }

                            try {
                                Class.forName("de.javasoft.plaf.synthetica.SyntheticaLookAndFeel").getMethod("setFont", new Class[] { String.class, int.class }).invoke(null, new Object[] { font, fontsize });

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    }

                }

            }

        });

    }

    /**
     * INstalls all Look and feels founmd in libs/laf/
     */
    private static void install() {

        for (File file : FileSystemUtils.getJDClassLoader().getLafs()) {
            try {
                JarInputStream jarFile = new JarInputStream(new FileInputStream(file));
                JarEntry e;
                String cl;
                ArrayList<String> names = new ArrayList<String>();
                ArrayList<String> classes = new ArrayList<String>();
                while ((e = jarFile.getNextJarEntry()) != null) {
                    if (!e.getName().endsWith(".class") || e.getName().contains("$")) {
                        continue;
                    }
                    cl = e.getName().replace("/", ".");
                    cl = cl.substring(0, cl.length() - 6);
                    if (!cl.toLowerCase().endsWith("lookandfeel")) {
                        continue;
                    }
                    Class<?> clazz = FileSystemUtils.getJDClassLoader().loadClass(cl);
                    try {

                        if (LookAndFeel.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {

                            String name = clazz.getSimpleName().replace("LookAndFeel", "");
                            names.add(name);
                            classes.add(cl);

                        }
                    } catch (Throwable t) {

                        t.printStackTrace();
                    }

                }
                // first collect all. Of the jar contaisn errors, an exception
                // gets thrown and no laf is added (e.gh. substance for 1.5
                for (int i = 0; i < names.size(); i++) {
                    UIManager.installLookAndFeel(names.get(i), classes.get(i));
                }
            } catch (Throwable e) {
                BiancaLogger.exception(e);
            }
        }

    }

    /**
     * Executes laf dependend commands AFTER setting the laf
     * 
     * @param className
     */
    private static void postSetup(String className) {

        int fontsize = GUIUtils.getConfig().getIntegerProperty(EasyShipmentGuiConstants.PARAM_GENERAL_FONT_SIZE.toString(), 100);

        if (isSynthetica()) {
            try {

                String font = "" + Class.forName("de.javasoft.plaf.synthetica.SyntheticaLookAndFeel").getMethod("getFontName", new Class[] {}).invoke(null, new Object[] {});
                int fonts = (Integer) Class.forName("de.javasoft.plaf.synthetica.SyntheticaLookAndFeel").getMethod("getFontSize", new Class[] {}).invoke(null, new Object[] {});

                Class.forName("de.javasoft.plaf.synthetica.SyntheticaLookAndFeel").getMethod("setFont", new Class[] { String.class, int.class }).invoke(null, new Object[] { font, (fonts * fontsize) / 100 });

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {

            for (Enumeration<Object> e = UIManager.getDefaults().keys(); e.hasMoreElements();) {
                Object key = e.nextElement();
                Object value = UIManager.get(key);

                if (value instanceof Font) {
                    Font f = (Font) value;

                    UIManager.put(key, new FontUIResource(f.getName(), f.getStyle(), (f.getSize() * fontsize) / 100));
                }
            }
        }

        //
        // JTattooUtils.setJTattooRootPane(this);

    }

    /**
     * Executes LAF dependend commands BEFORE initializing the LAF
     */
    private static void preSetup(String className) {
        Boolean windowDeco = GUIUtils.getConfig().getBooleanProperty(EasyShipmentGuiConstants.DECORATION_ENABLED.toString(), true);
        UIManager.put("Synthetica.window.decoration", windowDeco);
        JFrame.setDefaultLookAndFeelDecorated(windowDeco);
        JDialog.setDefaultLookAndFeelDecorated(windowDeco);
        /*
         * NOTE: This Licensee Information may only be used by AppWork UG. If
         * you like to create derived creation based on this sourcecode, you
         * have to remove this license key. Instead you may use the FREE Version
         * of synthetica found on javasoft.de
         */
        String[] li = { "Licensee=AppWork UG", "LicenseRegistrationNumber=289416475", "Product=Synthetica", "LicenseType=Small Business License", "ExpireDate=--.--.----", "MaxVersion=2.999.999" };
        UIManager.put("Synthetica.license.info", li);
        UIManager.put("Synthetica.license.key", BiancaCrypt.decrypt(HexUtils.getByteArray("4a94286634a203ada63b87c54662227252490d6f10e421b7239c610138c53e4c51fc7c0a2a8a18a0a2c0a40191b1186f"), new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 11, 12, 13, 14, 15, 16 }));

        // if
        // (className.equalsIgnoreCase("de.javasoft.plaf.synthetica.SyntheticaStandardLookAndFeel"))
        // {
        // UIManager.put("Synthetica.window.decoration", false);
        // }
    }

    /**
     * Returns if currently a substance look and feel is selected. Not very
     * fast.. do not use this in often used methods
     * 
     * @return
     */
    public static boolean isSubstance() {
        return UIManager.getLookAndFeel().getName().toLowerCase().contains("substance");
    }

    public static boolean isSynthetica() {
        return UIManager.getLookAndFeel().getName().toLowerCase().contains("synthetica");
    }

}
