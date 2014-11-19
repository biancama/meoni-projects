package com.biancama.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

import com.biancama.utils.encoding.Encoding;

public class FormatterUtils {

    private FormatterUtils() {
    };

    /**
     * The format describing an http date.
     */
    private static SimpleDateFormat DATE_FORMAT = null;

    /**
     * Formatiert Sekunden in das zeitformat stunden:minuten:sekunden returns
     * "~" vor values <0
     * 
     * @param eta
     * @return formatierte Zeit
     */
    public static String formatSeconds(long eta) {
        return formatSeconds(eta, true);
    }

    public static String formatSeconds(long eta, boolean showsec) {
        if (eta < 0) { return "~"; }
        long days = eta / (24 * 60 * 60);
        eta -= days * 24 * 60 * 60;
        long hours = eta / (60 * 60);
        eta -= hours * 60 * 60;
        long minutes = eta / 60;
        long seconds = eta - minutes * 60;
        StringBuilder ret = new StringBuilder();
        if (days != 0) {
            ret.append(days).append('d');
        }
        if (hours != 0 || ret.length() != 0) {
            if (ret.length() != 0) {
                ret.append(':');
            }
            ret.append(hours).append('h');
        }
        if (minutes != 0 || ret.length() != 0) {
            if (ret.length() != 0) {
                ret.append(':');
            }
            ret.append(FormatterUtils.fillInteger(minutes, 2, "0")).append('m');
        }
        if (showsec || ret.length() != 0) {
            if (ret.length() != 0) {
                ret.append(':');
            }
            ret.append(FormatterUtils.fillInteger(seconds, 2, "0")).append('s');
        }
        return ret.toString();
    }

    /**
     * FOIrmatiert im format hours:minutes:seconds.ms
     * 
     * @param ms
     */
    public static String formatMilliseconds(long ms) {
        return formatSeconds(ms / 1000) + "." + FormatterUtils.fillInteger(ms % 1000, 3, "0");
    }

    public static String formatFilesize(double value, int size) {
        if (value > 1024 && size < 5) {
            return formatFilesize(value / 1024.0, ++size);
        } else {
            DecimalFormat c = new DecimalFormat("0.00");
            switch (size) {
            case 0:
                return c.format(value) + " B";
            case 1:
                return c.format(value) + " KB";
            case 2:
                return c.format(value) + " MB";
            case 3:
                return c.format(value) + " GB";
            case 4:
                return c.format(value) + " TB";
            }
        }
        return null;
    }

    public static String formatReadable(long value) {
        if (value < 0) {
            value = 0;
        }
        DecimalFormat c = new DecimalFormat("0.00");
        if (value >= (1024 * 1024 * 1024 * 1024l)) { return c.format(value / (1024 * 1024 * 1024 * 1024.0)) + " TB"; }
        if (value >= (1024 * 1024 * 1024l)) { return c.format(value / (1024 * 1024 * 1024.0)) + " GB"; }
        if (value >= (1024 * 1024l)) { return c.format(value / (1024 * 1024.0)) + " MB"; }
        if (value >= 1024l) { return c.format(value / 1024.0) + " KB"; }
        return value + " B";
    }

    public static String fillString(String binaryString, String pre, String post, int length) {
        while (binaryString.length() < length) {
            if (binaryString.length() < length) {
                binaryString = pre + binaryString;
            }
            if (binaryString.length() < length) {
                binaryString = binaryString + post;
            }
        }
        return binaryString;
    }

    /**
     * Hängt an i solange fill vorne an bis die zechenlänge von i gleich num
     * ist
     * 
     * @param i
     * @param num
     * @param fill
     * @return aufgefüllte Zeichenkette
     */
    public static String fillInteger(long i, int num, String fill) {
        String ret = "" + i;
        while (ret.length() < num) {
            ret = fill + ret;
        }
        return ret;
    }

    /**
     * GIbt den Integer der sich in src befindet zurück. alle nicht
     * integerzeichen werden ausgefiltert
     * 
     * @param src
     * @return Integer in src
     */
    public static int filterInt(String src) {
        try {
            return Integer.parseInt(Encoding.filterString(src, "1234567890"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static long filterLong(String src) {
        try {
            return Long.parseLong(Encoding.filterString(src, "1234567890"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Returns a string containing an HTTP-formatted date.
     * 
     * @param time
     *            The date to format (current time in msec).
     * @return HTTP date string representing the given time.
     */
    public static String formatTime(long time) {
        if (DATE_FORMAT == null) {
            DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            DATE_FORMAT.setTimeZone(new SimpleTimeZone(0, "GMT"));
            DATE_FORMAT.setLenient(true);
        }
        return DATE_FORMAT.format(new Date(time)).substring(0, 29);
    }

    /**
     * Extracts the Revision from rev. $Revision: 6506 $
     * 
     * @param rev
     * @return
     */
    public static String getRevision(String rev) {
        try {
            int start = rev.indexOf("Revision: ") + 10;

            return rev.substring(start, rev.indexOf(" ", start + 1));
        } catch (Exception e) {
            return "-1";
        }
    }

    public static String convertExceptionReadable(Exception e) {
        String s = e.getClass().getName().replaceAll("Exception", "");
        s = s.substring(s.lastIndexOf(".") + 1);
        StringBuilder ret = new StringBuilder();
        String letter = null;
        for (int i = 0; i < s.length(); i++) {
            if ((letter = s.substring(i, i + 1)).equals(letter.toUpperCase())) {
                ret.append(' ');
                ret.append(letter);
            } else {
                ret.append(letter);
            }
        }
        String message = e.getLocalizedMessage();
        String rets = ret.toString();
        return message != null ? rets.trim() + ": " + message : rets.trim();

    }

}
