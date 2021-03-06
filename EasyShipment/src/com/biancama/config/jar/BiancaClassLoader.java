package com.biancama.config.jar;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import com.biancama.log.BiancaLogger;
import com.biancama.utils.FileSystemUtils;
import com.biancama.utils.gui.file.BiancaFileFilter;

public class BiancaClassLoader extends ClassLoader {
    private final ClassLoader classLoaderParent;
    private Vector<File> jarFile;
    private JarFile[] jars;
    private final Logger logger = BiancaLogger.getLogger();
    private URLClassLoader rootClassLoader;
    private final String rootDir;
    private ArrayList<File> lafs;
    private static byte[] S = new byte[] { (byte) 2, (byte) 1, (byte) 1, (byte) 49, (byte) 11, (byte) 48, (byte) 9, (byte) 6, (byte) 5, (byte) 43, (byte) 14, (byte) 3, (byte) 2, (byte) 26, (byte) 5, (byte) 0, (byte) 48, (byte) 11, (byte) 6, (byte) 9, (byte) 42, (byte) -122, (byte) 72, (byte) -122, (byte) -9, (byte) 13, (byte) 1, (byte) 7, (byte) 1, (byte) -96, (byte) -126, (byte) 2, (byte) -111, (byte) 48, (byte) -126, (byte) 2, (byte) -115, (byte) 48, (byte) -126, (byte) 2, (byte) 75, (byte) 2, (byte) 4, (byte) 70, (byte) -60, (byte) 21, (byte) -27, (byte) 48, (byte) 11, (byte) 6, (byte) 7, (byte) 42, (byte) -122, (byte) 72, (byte) -50, (byte) 56, (byte) 4, (byte) 3, (byte) 5, (byte) 0, (byte) 48, (byte) 44, (byte) 49, (byte) 24, (byte) 48, (byte) 22, (byte) 6, (byte) 3, (byte) 85, (byte) 4, (byte) 10, (byte) 19, (byte) 15, (byte) 103, (byte) 117, (byte) 108, (byte) 108, (byte) 105,
            (byte) 32, (byte) 67, (byte) 111, (byte) 109, (byte) 109, (byte) 117, (byte) 110, (byte) 105, (byte) 116, (byte) 121, (byte) 49, (byte) 16, (byte) 48, (byte) 14, (byte) 6, (byte) 3, (byte) 85, (byte) 4, (byte) 3, (byte) 19, (byte) 7, (byte) 97, (byte) 115, (byte) 116, (byte) 97, (byte) 108, (byte) 100, (byte) 111, (byte) 48, (byte) 30, (byte) 23, (byte) 13, (byte) 48, (byte) 55, (byte) 48, (byte) 56, (byte) 49, (byte) 54, (byte) 48, (byte) 57, (byte) 49, (byte) 54, (byte) 50, (byte) 49, (byte) 90, (byte) 23, (byte) 13, (byte) 48, (byte) 57, (byte) 48, (byte) 56, (byte) 48, (byte) 53, (byte) 48, (byte) 57, (byte) 49, (byte) 54, (byte) 50, (byte) 49, (byte) 90, (byte) 48, (byte) 44, (byte) 49, (byte) 24, (byte) 48, (byte) 22, (byte) 6, (byte) 3, (byte) 85, (byte) 4, (byte) 10, (byte) 19, (byte) 15, (byte) 103, (byte) 117, (byte) 108, (byte) 108, (byte) 105, (byte) 32,
            (byte) 67, (byte) 111, (byte) 109, (byte) 109, (byte) 117, (byte) 110, (byte) 105, (byte) 116, (byte) 121, (byte) 49, (byte) 16, (byte) 48, (byte) 14, (byte) 6, (byte) 3, (byte) 85, (byte) 4, (byte) 3, (byte) 19, (byte) 7, (byte) 97, (byte) 115, (byte) 116, (byte) 97, (byte) 108, (byte) 100, (byte) 111, (byte) 48, (byte) -126, (byte) 1, (byte) -72, (byte) 48, (byte) -126, (byte) 1, (byte) 44, (byte) 6, (byte) 7, (byte) 42, (byte) -122, (byte) 72, (byte) -50, (byte) 56, (byte) 4, (byte) 1, (byte) 48, (byte) -126, (byte) 1, (byte) 31, (byte) 2, (byte) -127, (byte) -127, (byte) 0, (byte) -3, (byte) 127, (byte) 83, (byte) -127, (byte) 29, (byte) 117, (byte) 18, (byte) 41, (byte) 82, (byte) -33, (byte) 74, (byte) -100, (byte) 46, (byte) -20, (byte) -28, (byte) -25, (byte) -10, (byte) 17, (byte) -73, (byte) 82, (byte) 60, (byte) -17, (byte) 68, (byte) 0, (byte) -61, (byte) 30,
            (byte) 63, (byte) -128, (byte) -74, (byte) 81, (byte) 38, (byte) 105, (byte) 69, (byte) 93, (byte) 64, (byte) 34, (byte) 81, (byte) -5, (byte) 89, (byte) 61, (byte) -115, (byte) 88, (byte) -6, (byte) -65, (byte) -59, (byte) -11, (byte) -70, (byte) 48, (byte) -10, (byte) -53, (byte) -101, (byte) 85, (byte) 108, (byte) -41, (byte) -127, (byte) 59, (byte) -128, (byte) 29, (byte) 52, (byte) 111, (byte) -14, (byte) 102, (byte) 96, (byte) -73, (byte) 107, (byte) -103, (byte) 80, (byte) -91, (byte) -92, (byte) -97, (byte) -97, (byte) -24, (byte) 4, (byte) 123, (byte) 16, (byte) 34, (byte) -62, (byte) 79, (byte) -69, (byte) -87, (byte) -41, (byte) -2, (byte) -73, (byte) -58, (byte) 27, (byte) -8, (byte) 59, (byte) 87, (byte) -25, (byte) -58, (byte) -88, (byte) -90, (byte) 21, (byte) 15, (byte) 4, (byte) -5, (byte) -125, (byte) -10, (byte) -45, (byte) -59, (byte) 30, (byte) -61,
            (byte) 2, (byte) 53, (byte) 84, (byte) 19, (byte) 90, (byte) 22, (byte) -111, (byte) 50, (byte) -10, (byte) 117, (byte) -13, (byte) -82, (byte) 43, (byte) 97, (byte) -41, (byte) 42, (byte) -17, (byte) -14, (byte) 34, (byte) 3, (byte) 25, (byte) -99, (byte) -47, (byte) 72, (byte) 1, (byte) -57, (byte) 2, (byte) 21, (byte) 0, (byte) -105, (byte) 96, (byte) 80, (byte) -113, (byte) 21, (byte) 35, (byte) 11, (byte) -52, (byte) -78, (byte) -110, (byte) -71, (byte) -126, (byte) -94, (byte) -21, (byte) -124, (byte) 11, (byte) -16, (byte) 88, (byte) 28, (byte) -11, (byte) 2, (byte) -127, (byte) -127, (byte) 0, (byte) -9, (byte) -31, (byte) -96, (byte) -123, (byte) -42, (byte) -101, (byte) 61, (byte) -34, (byte) -53, (byte) -68, (byte) -85, (byte) 92, (byte) 54, (byte) -72, (byte) 87, (byte) -71, (byte) 121, (byte) -108, (byte) -81, (byte) -69, (byte) -6, (byte) 58, (byte) -22,
            (byte) -126, (byte) -7, (byte) 87, (byte) 76, (byte) 11, (byte) 61, (byte) 7, (byte) -126, (byte) 103, (byte) 81, (byte) 89, (byte) 87, (byte) -114, (byte) -70, (byte) -44, (byte) 89, (byte) 79, (byte) -26, (byte) 113, (byte) 7, (byte) 16, (byte) -127, (byte) -128, (byte) -76, (byte) 73, (byte) 22, (byte) 113, (byte) 35, (byte) -24, (byte) 76, (byte) 40, (byte) 22, (byte) 19, (byte) -73, (byte) -49, (byte) 9, (byte) 50, (byte) -116, (byte) -56, (byte) -90, (byte) -31, (byte) 60, (byte) 22, (byte) 122, (byte) -117, (byte) 84, (byte) 124, (byte) -115, (byte) 40, (byte) -32, (byte) -93, (byte) -82, (byte) 30, (byte) 43, (byte) -77, (byte) -90, (byte) 117, (byte) -111, (byte) 110, (byte) -93, (byte) 127, (byte) 11, (byte) -6, (byte) 33, (byte) 53, (byte) 98, (byte) -15, (byte) -5, (byte) 98, (byte) 122, (byte) 1, (byte) 36, (byte) 59, (byte) -52, (byte) -92, (byte) -15,
            (byte) -66, (byte) -88, (byte) 81, (byte) -112, (byte) -119, (byte) -88, (byte) -125, (byte) -33, (byte) -31, (byte) 90, (byte) -27, (byte) -97, (byte) 6, (byte) -110, (byte) -117, (byte) 102, (byte) 94, (byte) -128, (byte) 123, (byte) 85, (byte) 37, (byte) 100, (byte) 1, (byte) 76, (byte) 59, (byte) -2, (byte) -49, (byte) 73, (byte) 42, (byte) 3, (byte) -127, (byte) -123, (byte) 0, (byte) 2, (byte) -127, (byte) -127, (byte) 0, (byte) -116, (byte) 41, (byte) 78, (byte) 97, (byte) 29, (byte) -39, (byte) -6, (byte) -125, (byte) -99, (byte) -18, (byte) -77, (byte) 120, (byte) 40, (byte) 126, (byte) -104, (byte) 69, (byte) -60, (byte) -33, (byte) 40, (byte) 9, (byte) 11, (byte) -48, (byte) 85, (byte) -62, (byte) -93, (byte) 100, (byte) 38, (byte) 106, (byte) 74, (byte) -116, (byte) 0, (byte) 123, (byte) -11, (byte) -122, (byte) 125, (byte) 127, (byte) 2, (byte) 21, (byte) -77,
            (byte) 18, (byte) -53, (byte) -85, (byte) 48, (byte) 118, (byte) 56, (byte) 80, (byte) -38, (byte) 67, (byte) -56, (byte) -96, (byte) 122, (byte) 66, (byte) 9, (byte) -7, (byte) -124, (byte) -90, (byte) -3, (byte) 115, (byte) 122, (byte) -128, (byte) -32, (byte) -31, (byte) -22, (byte) -12, (byte) 1, (byte) 76, (byte) 102, (byte) -54, (byte) 124, (byte) -112, (byte) 0, (byte) -55, (byte) -11, (byte) -101, (byte) -69, (byte) -19, (byte) 77, (byte) -65, (byte) -86, (byte) 61, (byte) 111, (byte) 119, (byte) -107, (byte) -56, (byte) 110, (byte) -116, (byte) -39, (byte) -9, (byte) 46, (byte) 18, (byte) -106, (byte) -28, (byte) 23, (byte) 113, (byte) -39, (byte) -12, (byte) 41, (byte) 47, (byte) -79, (byte) -72, (byte) 125, (byte) -84, (byte) -120, (byte) -115, (byte) 125, (byte) -95, (byte) 111, (byte) -44, (byte) 125, (byte) -77, (byte) 45, (byte) 10, (byte) -5, (byte) -104,
            (byte) 47, (byte) -41, (byte) -119, (byte) 106, (byte) 97, (byte) -95, (byte) -9, (byte) 80, (byte) -29, (byte) 26, (byte) -111, (byte) -102, (byte) -109, (byte) -116, (byte) 48, (byte) 11, (byte) 6, (byte) 7, (byte) 42, (byte) -122, (byte) 72, (byte) -50, (byte) 56, (byte) 4, (byte) 3, (byte) 5, (byte) 0, (byte) 3, (byte) 47, (byte) 0, (byte) 48, (byte) 44, (byte) 2, (byte) 20, (byte) 31, (byte) 93, (byte) -31, (byte) 109, (byte) -22, (byte) -112, (byte) -62, (byte) 99, (byte) -20, (byte) -5, (byte) 17, (byte) -9, (byte) 123, (byte) 116, (byte) 1, (byte) -78, (byte) 3, (byte) 60, (byte) 122, (byte) 60, (byte) 2, (byte) 20, (byte) 48, (byte) -86, (byte) 15, (byte) 10, (byte) 63, (byte) -70, (byte) -20, (byte) 37, (byte) -16, (byte) -79, (byte) 19, (byte) 58, (byte) 104, (byte) -122, (byte) 26, (byte) -85, (byte) -49, (byte) 63, (byte) 104, (byte) 49, (byte) 49, (byte) -127 };

    public BiancaClassLoader(String rootDir, ClassLoader classLoaderParent) {
        if (rootDir == null) { throw new IllegalArgumentException("Null root directory"); }
        this.rootDir = rootDir;
        this.classLoaderParent = classLoaderParent;
        logger.finest("rootDir:" + rootDir);
        try {
            rootClassLoader = new URLClassLoader(new URL[] { new File(rootDir).toURI().toURL() }, null);
        } catch (MalformedURLException e1) {
            BiancaLogger.exception(e1);
        }

        boolean isWebupdater = false;
        System.out.println(rootDir);
        System.out.println(" " + rootClassLoader.getResource("easyShipment"));
        isWebupdater = (classLoaderParent.getResource("easyShipment") + "").contains("jdupdate.jar") || (classLoaderParent.getResource("easyShipment") + "").contains("webupdater.jar");
        if (!isWebupdater) {
            // Hier werden die JAR Dateien ausgelesen
            Vector<JarFile> jarFiles = new Vector<JarFile>();
            jarFile = new Vector<File>();
            ArrayList<String> names = new ArrayList<String>();
            File[] files = new File(new File(rootDir), "plugins").listFiles(new BiancaFileFilter(null, ".jar", false));
            if (files != null) {

                // jars = new JarFile[files.length];

                for (int i = 0; i < files.length; i++) {

                    try {
                        if (!files[i].getAbsolutePath().endsWith("webupdater.jar") || !files[i].getAbsolutePath().endsWith("jdupdate.jar")) {
                            if (FileSystemUtils.getRunType() == FileSystemUtils.RunType.LOCAL_JARED && !comp(getSig(files[i].getAbsolutePath()))) {
                                logger.severe("Not loaded due to sig violation: " + files[i]);
                                continue;
                            }

                            if (names.contains(files[i].getName())) {
                                logger.severe("Duplicate Jars found: " + files[i].getAbsolutePath());
                            } else {
                                names.add(files[i].getName());
                                logger.finer("Jar file loaded: " + files[i].getAbsolutePath());
                                // jars[i] = new JarFile(files[i]);
                                jarFile.add(files[i]);

                                jarFiles.add(new JarFile(files[i]));
                            }
                        }
                    } catch (Exception e) {
                        BiancaLogger.exception(e);
                    }
                }
            }
            // Hier werden lokale JAR Dateien ausgelesen
            files = new File(rootDir).listFiles(new BiancaFileFilter(null, ".jar", false));
            if (files != null) {
                // jars = new JarFile[files.length];
                for (int i = 0; i < files.length; i++) {

                    try {

                        if (!files[i].getAbsolutePath().endsWith("webupdater.jar") && !files[i].getAbsolutePath().endsWith("jdupdate.jar")) {
                            if (!comp(getSig(files[i].getAbsolutePath()))) {
                                logger.severe("Not loaded due to sig violation: " + files[i]);
                                continue;
                            }
                            if (names.contains(files[i].getName())) {
                                logger.severe("Duplicate Jars found: " + files[i].getAbsolutePath());
                            } else {
                                names.add(files[i].getName());
                                logger.finer("Jar file loaded: " + files[i].getAbsolutePath());
                                jarFile.add(files[i]);
                                jarFiles.add(new JarFile(files[i]));
                            }
                        }

                    } catch (IOException e) {
                        BiancaLogger.exception(e);
                    }
                }
            }

            // und hier folgen die LAFS

            // Hier werden lokale JAR Dateien ausgelesen
            files = new File(rootDir, "libs/laf").listFiles(new BiancaFileFilter(null, ".jar", false));
            lafs = new ArrayList<File>();
            if (files != null) {
                // jars = new JarFile[files.length];
                for (int i = 0; i < files.length; i++) {

                    try {

                        if (!files[i].getAbsolutePath().endsWith("webupdater.jar") && !files[i].getAbsolutePath().endsWith("jdupdate.jar")) {
                            // if (!comp(getSig(files[i].getAbsolutePath()))) {
                            // logger.severe("Not loaded due to sig violation: "
                            // + files[i]);
                            // continue;
                            // }
                            if (names.contains(files[i].getName())) {
                                logger.severe("Duplicate Jars found: " + files[i].getAbsolutePath());
                            } else {
                                names.add(files[i].getName());
                                logger.finer("Look and Feel JAR loaded: " + files[i].getAbsolutePath());
                                jarFile.add(files[i]);
                                lafs.add(files[i]);
                                jarFiles.add(new JarFile(files[i]));
                            }
                        }

                    } catch (IOException e) {
                        BiancaLogger.exception(e);
                    }
                }
            }
            jars = jarFiles.toArray(new JarFile[] {});
        }
    }

    public ArrayList<File> getLafs() {
        return lafs;
    }

    private boolean comp(byte[] sig) {
        if (sig == null) { return false; }
        for (int i = 0; i < S.length; i++) {
            byte a = S[i];
            byte b = sig[i];
            if (a != b) { return false; }
        }
        return true;
    }

    public static byte[] getSig(String jarfile) {
        try {

            JarFile jar = new JarFile(jarfile);
            String dsa = "META-INF/JDOWNLOA.DSA";

            ZipEntry entry = jar.getEntry(dsa);

            byte[] b = new byte[1];
            InputStream in = new BufferedInputStream(jar.getInputStream(entry));
            byte[] res = new byte[692];
            int c = 0;
            int n = 0;
            while ((n = in.read(b)) > -1) {
                if (n > 0) {
                    c++;
                    if (c >= 24 && c < 24 + 692) {
                        res[c - 24] = b[0];
                        // System.out.println(c + ". " + b[0]);
                    }
                }
            }
            return res;
        } catch (Exception e) {
            // jd.controlling.BiancaLogger.getLogger().log(java.util.logging.Level.
            // SEVERE,"Exception occurred",e);
        }
        return null;
    }

    @Override
    protected URL findResource(String name) {
        URL url = rootClassLoader.findResource(name);
        if (url != null) { return url; }

        return super.findResource(name);
    }

    public Vector<File> getJars() {
        Vector<File> ret = new Vector<File>();
        ret.addAll(jarFile);
        return ret;
    }

    public URL getResource(byte[] key) {
        return getResource(new String(key));
    }

    @Override
    public URL getResource(String name) {

        if (jars != null) {
            // An dieser Stelle werden die JAR Dateien überprüft
            JarEntry entry;
            for (JarFile element : jars) {

                if (element != null && (entry = element.getJarEntry(name)) != null) {
                    try {
                        // System.out.println("getResource:" + entry.getName());
                        return new URL(entry.getName());
                    } catch (MalformedURLException e) {
                    }
                }
            }
        }
        URL url = rootClassLoader.getResource(name);
        if (url != null) { return url; }

        url = super.getResource(name);
        if (url != null) { return url; }

        url = classLoaderParent.getResource(name);
        if (url != null) { return url; }

        try {
            // Falls immer noch nichts vorhanden, wird ein neu erzeugtes File
            // Objekt zurückgegeben
            // Ist für das Abspeichern der Captcha notwendig

            return new File(new File(rootDir), name).toURI().toURL();
        } catch (MalformedURLException e) {
        }
        return null;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Vector<URL> urls = new Vector<URL>();

        Enumeration<URL> en = classLoaderParent.getResources(name);

        while (en.hasMoreElements()) {
            URL tmp = en.nextElement();

            urls.add(tmp);
        }
        // if (urls.size() > 0) { return urls.elements(); }
        if (jars != null) {
            JarEntry entry;
            for (JarFile element : jars) {
                // logger.info(jars[i]+" -tttt "+jars[i].entries());
                if (element != null && (entry = element.getJarEntry(name)) != null) {
                    try {
                        // Das sollte nun hoffentlich eine Systemunabhängige
                        // Implementierung sein.

                        String url = new File(element.getName().replace("\\", "/")).toURI().toURL() + "!/" + entry.getName();
                        // // url=url.replace("file:/", "file://");
                        // logger.finer(new URL("jar","",url)+"");
                        // logger.finer("jar:file:/"+jars[i].getName().replace(
                        // "\\",
                        // "/")+"!/"+entry.getName());
                        urls.add(new URL("jar", "", url));
                    } catch (MalformedURLException e) {
                        BiancaLogger.exception(e);
                    }
                }
            }
        }

        logger.info(urls + "");

        return urls.elements();
    }

    /**
     * Lädt die Klasse. Dazu wird zuerst überprüft, ob die Klasse durch einen
     * System-Classloader geladen werden kann. Erst zum Schluß wird versucht,
     * diese Klasse selbst zu laden.
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            try {
                c = findSystemClass(name);
            } catch (Exception e) {
            }
            if (c == null) {
                try {
                    c = classLoaderParent.loadClass(name);
                } catch (Exception e) {
                }
            }
        }
        if (c == null) {
            JarEntry entry = null;
            for (JarFile element : jars) {
                entry = element.getJarEntry(name.replace('.', '/') + ".class");
                if (entry != null) {
                    try {
                        byte data[] = loadClassData(element, entry);
                        System.out.println("Loaded class " + name + " from " + element.getName());
                        c = defineClass(name, data, 0, data.length, getClass().getProtectionDomain());
                        if (c == null) { throw new ClassNotFoundException(name); }
                    } catch (ClassFormatError e) {
                        BiancaLogger.exception(e);
                    } catch (IOException e) {
                        BiancaLogger.exception(e);
                    }
                }
            }
        }
        if (resolve) {
            resolveClass(c);
        }
        return c;
    }

    public String findJar(String name) throws ClassNotFoundException {
        JarEntry entry = null;
        for (JarFile element : jars) {
            entry = element.getJarEntry(name.replace('.', '/') + ".class");
            if (entry != null) { return element.getName(); }
        }
        return null;
    }

    /**
     * Diese Methode lädt eine Klasse aus einer JAR nach
     * 
     * @param jarFile
     *            Die JARDatei
     * @param jarEntry
     *            Die Klasse innerhalb der JAR
     * @return Die eingelesenen Bytes
     * @throws IOException
     */
    private byte[] loadClassData(JarFile jarFile, JarEntry jarEntry) throws IOException {

        byte buff[] = new byte[(int) jarEntry.getSize()];
        DataInputStream dis = new DataInputStream(jarFile.getInputStream(jarEntry));
        dis.readFully(buff);
        dis.close();
        return buff;
    }

}
