package com.biancama.utils;

public class OSDetector {
    private OSDetector() {
    }

    private static String OS_STRING;

    public enum OS {
        OS_LINUX_OTHER, OS_MAC_OTHER, OS_WINDOWS_OTHER, OS_WINDOWS_NT, OS_WINDOWS_2000, OS_WINDOWS_XP, OS_WINDOWS_2003, OS_WINDOWS_VISTA, OS_WINDOWS_7
    }

    private static OS OS_ID;

    private static void getOS() {
        String OSFound = getOSString().toLowerCase();
        if (OSFound.indexOf("windows 7") > -1) {
            OS_ID = OS.OS_WINDOWS_7;
        } else if (OSFound.indexOf("windows xp") > -1) {
            OS_ID = OS.OS_WINDOWS_XP;
        } else if (OSFound.indexOf("windows vista") > -1) {
            OS_ID = OS.OS_WINDOWS_VISTA;
        } else if (OSFound.indexOf("windows 2000") > -1) {
            OS_ID = OS.OS_WINDOWS_2000;
        } else if (OSFound.indexOf("windows 2003") > -1) {
            OS_ID = OS.OS_WINDOWS_2003;
        } else if (OSFound.indexOf("nt") > -1) {
            OS_ID = OS.OS_WINDOWS_NT;
        } else if (OSFound.indexOf("windows") > -1) {
            OS_ID = OS.OS_WINDOWS_OTHER;
        } else if (OSFound.indexOf("mac") > -1) {
            OS_ID = OS.OS_MAC_OTHER;
        } else {
            OS_ID = OS.OS_LINUX_OTHER;
        }

    }

    public static OS getOSID() {
        if (OS_ID == null) {
            OSDetector.getOS();
        }
        return OS_ID;
    }

    public static boolean isLinux() {
        OS id = OSDetector.getOSID();
        switch (id) {
        case OS_LINUX_OTHER:
            return true;
        }
        return false;
    }

    public static boolean isMac() {
        OS id = OSDetector.getOSID();
        switch (id) {
        case OS_MAC_OTHER:
            return true;
        }
        return false;
    }

    public static boolean isWindows() {
        OS id = OSDetector.getOSID();
        switch (id) {
        case OS_WINDOWS_XP:
        case OS_WINDOWS_VISTA:
        case OS_WINDOWS_2000:
        case OS_WINDOWS_2003:
        case OS_WINDOWS_NT:
        case OS_WINDOWS_OTHER:
        case OS_WINDOWS_7:
            return true;
        }
        return false;
    }

    /**
     * erkennt gnome.
     */
    public static boolean isGnome() {
        if (!isLinux()) { return false; }
        // gdm session
        String gdmSession = System.getenv("GDMSESSION");
        if (gdmSession != null && gdmSession.toLowerCase().contains("gnome")) { return true; }

        // desktop session
        String desktopSession = System.getenv("DESKTOP_SESSION");
        if (desktopSession != null && desktopSession.toLowerCase().contains("gnome")) { return true; }

        // gnome desktop id
        String gnomeDesktopSessionId = System.getenv("GNOME_DESKTOP_SESSION_ID");
        if (gnomeDesktopSessionId != null && gnomeDesktopSessionId.trim().length() > 0) { return true; }

        return false;
    }

    /**
     * erkennt KDE.
     */
    public static boolean isKDE() {
        if (!isLinux()) { return false; }

        // gdm session
        String gdmSession = System.getenv("GDMSESSION");
        if (gdmSession != null && gdmSession.toLowerCase().contains("kde")) { return true; }

        // desktop session
        String desktopSession = System.getenv("DESKTOP_SESSION");
        if (desktopSession != null && desktopSession.toLowerCase().contains("kde")) { return true; }

        // window manager
        String windowManager = System.getenv("WINDOW_MANAGER");
        if (windowManager != null && windowManager.trim().toLowerCase().endsWith("kde")) { return true; }

        return false;
    }

    public static String getOSString() {
        if (OS_STRING == null) {
            OS_STRING = System.getProperty("os.name");
        }
        return OS_STRING;
    }

    public static void setOSString(String property) {
        OS_STRING = property;
    }

}
