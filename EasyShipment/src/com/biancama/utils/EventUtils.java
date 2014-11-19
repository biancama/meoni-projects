package com.biancama.utils;

import com.biancama.controlling.DownloadController;
import com.biancama.events.BiancaController;

public class EventUtils {

    private static BiancaController CONTROLLER = null;

    private EventUtils() {
    }

    public static BiancaController getController() {
        return CONTROLLER;
    }

    public static void setController(BiancaController controller) {
        CONTROLLER = controller;
    }

    public static DownloadController getDownloadController() {
        return DownloadController.getInstance();
    }
}
