package com.biancama.plugins;

import java.util.EventListener;

public interface DownloadLinkListener extends EventListener {
    public void onDownloadLinkEvent(DownloadLinkEvent event);
}
