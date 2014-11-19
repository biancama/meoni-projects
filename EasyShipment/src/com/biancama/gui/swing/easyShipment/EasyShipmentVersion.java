package com.biancama.gui.swing.easyShipment;

import java.text.DecimalFormat;

import com.biancama.utils.FormatterUtils;
import com.biancama.utils.URLUtils;
import com.biancama.utils.gui.io.BiancaIO;

public class EasyShipmentVersion {
    private EasyShipmentVersion() {
    }

    public static final String JD_VERSION = "0.";
    private static String REVISION;

    public static String getRevision() {
        if (REVISION != null) { return REVISION; }
        int rev = -1;
        try {
            rev = FormatterUtils.filterInt(BiancaIO.readFileToString(URLUtils.getResourceFile("config/version.cfg")));
        } catch (Throwable t) {
            t.printStackTrace();
        }

        int rev2 = Integer.parseInt(FormatterUtils.getRevision("$Revision: 9608 $"));

        double r = Math.max(rev2, rev) / 1000.0;
        return REVISION = new DecimalFormat("0.000").format(r).replace(",", ".");
    }

}
