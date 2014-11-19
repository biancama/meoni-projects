package com.biancama.utils;


public class JavaUtils {
    private JavaUtils() {
    }

    public static Double getJavaVersion() {
        String version = System.getProperty("java.version");
        int majorVersion = FormatterUtils.filterInt(version.substring(0, version.indexOf(".")));
        int subversion = FormatterUtils.filterInt(version.substring(version.indexOf(".") + 1));
        return Double.parseDouble(majorVersion + "." + subversion);
    }

}
