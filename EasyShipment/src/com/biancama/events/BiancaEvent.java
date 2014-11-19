package com.biancama.events;

public abstract class BiancaEvent {
    private final int ID;
    private final Object source;
    private Object parameter;

    public BiancaEvent(Object source, int ID) {
        this.source = source;
        this.ID = ID;
    }

    public BiancaEvent(Object source, int ID, Object parameter) {
        this(source, ID);
        this.parameter = parameter;
    }

    public int getID() {
        return ID;
    }

    public Object getParameter() {
        return parameter;
    }

    public Object getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "[source:" + source + ", controlID:" + ID + ", parameter:" + parameter + "]";
    }

}
