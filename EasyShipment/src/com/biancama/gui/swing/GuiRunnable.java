package com.biancama.gui.swing;

import javax.swing.SwingUtilities;

import com.biancama.log.BiancaLogger;

public abstract class GuiRunnable<T> implements Runnable {

    public GuiRunnable() {

    }

    private static final long serialVersionUID = 7777074589566807490L;

    private T returnValue;
    private Object lock = new Object();

    private boolean started = false;
    private boolean done = false;

    public boolean isStarted() {
        return started;
    }

    private void setStarted(boolean started) {
        this.started = started;
    }

    /**
     * If this method mis calls, the thread waits until THD EDT has innvoked the
     * runnable.. it ensures that the return value is available
     * 
     * @return
     */
    public T getReturnValue() {
        waitForEDT();
        return returnValue;
    }

    /**
     * This method waits until the EDT has invoked the runnable. If the Runnable
     * is not started yet.. the start method gets called
     */
    public void waitForEDT() {
        if (done) { return; }
        if (!isStarted()) {
            start();
        }
        if (!SwingUtilities.isEventDispatchThread()) {
            if (lock != null) {
                synchronized (lock) {
                    try {
                        if (lock != null) {
                            lock.wait();
                        }
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
        done = true;
    }

    public void run() {
        try {
            this.returnValue = this.runSave();
        } catch (Exception e) {
            System.err.println("Error in Gui Runnable: " +e);
            BiancaLogger.exception(e);
        }
        synchronized (lock) {
            lock.notify();
            lock = null;
        }
    }

    public abstract T runSave();

    /**
     * Starts the Runnable and adds it to the ETD
     */
    public void start() {
        setStarted(true);

        if (SwingUtilities.isEventDispatchThread()) {
            run();
        } else {
            SwingUtilities.invokeLater(this);
        }
    }

}
