package com.biancama.utils;

import com.biancama.gui.UserIO;
import com.biancama.plugins.CryptedLink;
import com.biancama.plugins.DownloadLink;
import com.biancama.plugins.LinkStatus;
import com.biancama.utils.locale.BiancaL;

public class UserIOUtils {
    private UserIOUtils() {
    }

    public static final Object USERIO_LOCK = new Object();

    public static String getUserInput(String message, DownloadLink link) {
        return getUserInput(message, null, link);
    }

    public static String getUserInput(String message, String defaultmessage, DownloadLink link) {
        try {
            link.getLinkStatus().addStatus(LinkStatus.WAITING_USERIO);

            String code = getUserInput(message, defaultmessage);

            return code;
        } finally {
            link.getLinkStatus().removeStatus(LinkStatus.WAITING_USERIO);
        }
    }

    public static String getUserInput(String message, CryptedLink link) {
        return getUserInput(message, null, link);
    }

    public static String getUserInput(String message, String defaultmessage, CryptedLink link) {
        link.getProgressController().setStatusText(BiancaL.L("gui.linkgrabber.waitinguserio", "Waiting for user input"));
        String password = getUserInput(message, defaultmessage);
        link.getProgressController().setStatusText(null);
        return password;
    }

    public static String getUserInput(String message, String defaultmessage) {
        synchronized (USERIO_LOCK) {
            if (message == null) {
                message = BiancaL.L("gui.linkgrabber.password", "Password?");
            }
            if (defaultmessage == null) {
                defaultmessage = "";
            }
            String password = UserIO.getInstance().requestInputDialog(0, message, defaultmessage);
            return password;
        }
    }

}
