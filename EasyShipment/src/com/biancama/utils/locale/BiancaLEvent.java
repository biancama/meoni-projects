package com.biancama.utils.locale;

import com.biancama.events.BiancaEvent;

public class BiancaLEvent extends BiancaEvent {
    /**
     * a new languagefile ahas been loaded. maybe their are new setttings, too
     * parameter: JDLocale instance
     */
    public static final int SET_NEW_LOCALE = 1;

    public BiancaLEvent(Object source, int ID) {
        super(source, ID);

    }
}
