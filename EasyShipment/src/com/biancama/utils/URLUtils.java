package com.biancama.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import com.biancama.config.jar.BiancaClassLoader;
import com.biancama.log.BiancaLogger;

public class URLUtils {
    public static File getResourceFile(String resource) {
        URL clURL = getResourceURL(resource);
        if (clURL != null) {
            try {
                return new File(clURL.toURI());
            } catch (URISyntaxException e) {
            }
        }
        return null;
    }

    public static File getResourceFile(String resource, boolean mkdirs) {
        URL clURL = getResourceURL(resource);
        if (clURL != null) {
            try {
                File f = new File(clURL.toURI());
                if (mkdirs) {
                    File f2 = f.getParentFile();
                    if (f2 != null && !f2.exists()) {
                        f2.mkdirs();
                    }
                }
                return f;
            } catch (URISyntaxException e) {
            }
        }
        return null;
    }

    public static URL getResourceURL(String resource) {
        BiancaClassLoader cl = FileSystemUtils.getJDClassLoader();
        if (cl == null) {
            System.err.println("Classloader == null");
            return null;
        }
        return cl.getResource(resource);
    }

    public static long getCRC(File file) {

        try {

            CheckedInputStream cis = null;
            // long fileSize = 0;
            try {
                // Computer CRC32 checksum
                cis = new CheckedInputStream(new FileInputStream(file), new CRC32());

                // fileSize = file.length();

            } catch (FileNotFoundException e) {
                BiancaLogger.exception(e);
                return 0;
            }

            byte[] buf = new byte[128];
            while (cis.read(buf) >= 0) {
            }

            long checksum = cis.getChecksum().getValue();
            return checksum;

        } catch (IOException e) {
            BiancaLogger.exception(e);
            return 0;
        }

    }

}
