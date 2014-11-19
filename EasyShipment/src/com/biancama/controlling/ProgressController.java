package com.biancama.controlling;

import java.awt.Color;

import javax.swing.Icon;

import com.biancama.events.BiancaBroadcaster;
import com.biancama.events.ControlEvent;
import com.biancama.events.MessageEvent;
import com.biancama.events.MessageListener;
import com.biancama.utils.EventUtils;

class ProgressControllerBroadcaster extends BiancaBroadcaster<ProgressControllerListener, ProgressControllerEvent> {

    @Override
    protected void fireEvent(ProgressControllerListener listener, ProgressControllerEvent event) {
        listener.onProgressControllerEvent(event);

    }

}

/**
 * Diese Klasse kann dazu verwendet werden einen Fortschritt in der GUI
 * anzuzeigen. Sie bildet dabei die schnittstelle zwischen Interactionen,
 * plugins etc und der GUI
 * 
 * @author JD-Team
 */
public class ProgressController implements MessageListener {

    private static int idCounter = 0;
    private long currentValue;
    private boolean finished;
    private boolean finalizing = false;

    private final int id;
    private boolean indeterminate = false;
    private long max;

    private Object source;
    private String statusText;
    private Color progresscolor;

    private Icon icon = null;

    private transient ProgressControllerBroadcaster broadcaster = new ProgressControllerBroadcaster();
    private boolean abort = false;

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public ProgressController(String name) {
        this(name, 100l);
    }

    public boolean isInterruptable() {
        return getBroadcaster().hasListener();
    }

    public ProgressController(String name, long max) {
        id = idCounter++;
        this.max = max;
        statusText = name;
        currentValue = 0;
        finished = false;
        progresscolor = null;
        fireChanges();
    }

    public synchronized BiancaBroadcaster<ProgressControllerListener, ProgressControllerEvent> getBroadcaster() {
        if (broadcaster == null) {
            broadcaster = new ProgressControllerBroadcaster();
        }
        return this.broadcaster;
    }

    public void setColor(Color color) {
        progresscolor = color;
        fireChanges();
    }

    public Color getColor() {
        return progresscolor;
    }

    public void addToMax(long length) {
        setRange(max + length);
    }

    public void decrease(long i) {
        setStatus(currentValue - i);
    }

    public void doFinalize() {
        if (finalizing) { return; }
        finished = true;
        currentValue = max;
        EventUtils.getController().fireControlEvent(new ControlEvent(this, ControlEvent.CONTROL_ON_PROGRESS, source));
    }

    public boolean isFinalizing() {
        return this.finalizing;
    }

    public void doFinalize(final long waittimer) {
        if (finalizing) { return; }
        finalizing = true;
        final ProgressController instance = this;
        new Thread() {
            @Override
            public void run() {
                long timer = waittimer;
                instance.setRange(timer);
                while (timer > 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    timer -= 1000;
                    instance.increase(1000l);
                }
                finished = true;
                currentValue = max;
                if (EventUtils.getController() != null) {
                    EventUtils.getController().fireControlEvent(new ControlEvent(instance, ControlEvent.CONTROL_ON_PROGRESS, source));
                }
            }
        }.start();
    }

    public void fireChanges() {
        if (!isFinished()) {
            if (EventUtils.getController() != null) {
                EventUtils.getController().fireControlEvent(new ControlEvent(this, ControlEvent.CONTROL_ON_PROGRESS, source));
            }
        }
    }

    public int getID() {
        return id;
    }

    public long getMax() {
        return max;
    }

    public int getPercent() {
        if (Math.min(currentValue, max) <= 0) { return 0; }
        return (int) (10000 * currentValue / Math.max(1, Math.max(currentValue, max)));
    }

    public Object getSource() {
        return source;
    }

    public String getStatusText() {
        return statusText;
    }

    public long getValue() {
        return currentValue;
    }

    public synchronized void increase(long i) {
        setStatus(currentValue + i);
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isAbort() {
        return abort;
    }

    public void setRange(long max) {
        this.max = max;
        setStatus(currentValue);
    }

    public void setSource(Object src) {
        source = src;
        fireChanges();
    }

    public void setStatus(long value) {
        if (value < 0) {
            value = 0;
        }
        if (value > max) {
            value = max;
        }
        currentValue = value;
        fireChanges();
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
        fireChanges();
    }

    @Override
    public String toString() {
        return "ProgressController " + id;
    }

    public void fireCancelAction() {
        abort = true;
        getBroadcaster().fireEvent(new ProgressControllerEvent(this, ProgressControllerEvent.CANCEL));
    }

    /**
     * @param indeterminate
     *            the indeterminate to set
     */
    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
        fireChanges();
    }

    /**
     * @return the indeterminate
     */
    public boolean isIndeterminate() {
        return indeterminate;
    }

    public void onMessage(MessageEvent event) {
        this.setStatusText(event.getMessage());
    }
}
