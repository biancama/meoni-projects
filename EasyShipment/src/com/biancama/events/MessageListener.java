package com.biancama.events;


import java.util.EventListener;
/**
 * Listener interface for simple messages.
 * @author thomas
 *
 */
public interface MessageListener extends EventListener{

    void onMessage(MessageEvent event);

}
