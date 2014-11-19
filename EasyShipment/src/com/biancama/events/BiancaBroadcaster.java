package com.biancama.events;

import java.util.EventListener;
import java.util.Vector;

import com.biancama.log.BiancaLogger;

public abstract class BiancaBroadcaster<T extends EventListener, TT extends BiancaEvent> {

    transient protected Vector<T> callList = null;

    transient protected Vector<T> removeList = null;

    public BiancaBroadcaster() {
        callList = new Vector<T>();
        removeList = new Vector<T>();
    }

    public void addListener(T listener) {
        if (removeList.contains(listener)) {
            removeList.remove(listener);
        }
        if (!callList.contains(listener)) {
            callList.add(listener);
        }
    }

    public boolean hasListener() {
        return callList.size() > 0;
    }

    public boolean fireEvent(TT event) {
        synchronized (removeList) {
            callList.removeAll(removeList);
            removeList.clear();
        }
        for (int i = callList.size() - 1; i >= 0; i--) {
            try {
                this.fireEvent(callList.get(i), event);
            } catch (Exception e) {
                BiancaLogger.exception(e);
            }
        }
        return false;
    }

    protected abstract void fireEvent(T listener, TT event);

    public void removeListener(T listener) {
        if (!removeList.contains(listener)) {
            removeList.add(listener);
        }
    }

    public Vector<T> getListener() {
        return callList;
    }

    public void addAllListener(Vector<T> listener) {
        for (T l : listener) {
            this.addListener(l);
        }

    }

}
