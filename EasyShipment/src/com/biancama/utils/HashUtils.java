package com.biancama.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import com.biancama.log.BiancaLogger;

public class HashUtils {
    public static String HASH_TYPE_MD5 = "md5";

    public static String HASH_TYPE_SHA1 = "SHA-1";

    /**
     * Gibt einen Hash vom File zurück
     * 
     * @author JD-Team
     * @param arg
     * @param type
     * @return Hashstring
     */
    public static String getFileHash(File arg, String type) {
        if (arg == null || !arg.exists() || arg.isDirectory()) { return null; }
        try {
            MessageDigest md = MessageDigest.getInstance(type);
            byte[] b = new byte[1024];
            InputStream in = new FileInputStream(arg);
            for (int n = 0; (n = in.read(b)) > -1;) {
                md.update(b, 0, n);

            }
            in.close();
            byte[] digest = md.digest();
            return byteArrayToHex(digest);
        } catch (Exception e) {
            BiancaLogger.exception(e);
            return null;
        }
    }

    /**
     * Gibt einen Hash vom String zurück
     * 
     * @author JD-Team
     * @param arg
     * @param type
     * @return Hashstring
     */
    public static String getStringHash(String arg, String type) {
        if (arg == null) { return null; }
        try {
            MessageDigest md = MessageDigest.getInstance(type);
            byte[] digest = md.digest(arg.getBytes());
            return byteArrayToHex(digest);
        } catch (Exception e) {
            BiancaLogger.exception(e);
            return null;
        }
    }

    public static String byteArrayToHex(byte[] digest) {
        StringBuilder ret = new StringBuilder();
        String tmp;
        for (byte d : digest) {
            tmp = Integer.toHexString(d & 0xFF);
            if (tmp.length() < 2) {
                ret.append('0');
            }
            ret.append(tmp);
        }
        return ret.toString();
    }

    public static String getMD5(String arg) {
        return getStringHash(arg, HASH_TYPE_MD5);
    }

    public static String getMD5(File arg) {
        return getFileHash(arg, HASH_TYPE_MD5);
    }

    public static String getSHA1(String arg) {
        return getStringHash(arg, HASH_TYPE_SHA1);
    }

    public static String getSHA1(File arg) {
        return getFileHash(arg, HASH_TYPE_SHA1);
    }

}
