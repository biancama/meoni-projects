package com.biancama.utils.locale;

import java.util.EventListener;

public abstract class BiancaLListener implements EventListener {
    abstract public void onBiancaLEvent(BiancaLEvent event);
}
