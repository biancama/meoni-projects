package com.biancama.events;

public class MessageEvent extends BiancaEvent {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageEvent(Object source, int ID, String parameter) {
        super(source, ID);
        this.message = parameter;
        // TODO Auto-generated constructor stub
    }

}
