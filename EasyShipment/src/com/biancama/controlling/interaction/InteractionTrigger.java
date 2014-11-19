package com.biancama.controlling.interaction;

import java.io.Serializable;
import java.util.Vector;

public class InteractionTrigger implements Serializable {
    /**
     * Vector with al triggers already created
     */
    private static Vector<InteractionTrigger> events = new Vector<InteractionTrigger>();

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8656898503474841842L;

    /**
     * Get all interaration triggers
     * 
     * @return
     */
    public static InteractionTrigger[] getAllTrigger() {
        return events.toArray(new InteractionTrigger[events.size()]);
    }

    /**
     * Description
     */
    private final String description;
    /**
     * EventiD
     */
    private final int eventID;
    /**
     * Trigger Name
     */
    private final String name;

    /**
     * Creates a new trigger. NOTE: When you are instantiating the Trigger
     * immediately written to a vector and therefore NEVER! of GarbageCollector
     * recorded. One should therefore in the normal program No new trigger more
     * instantiating
     * 
     * @param id
     * @param name
     * @param description
     */
    public InteractionTrigger(int id, String name, String description) {
        this.eventID = id;
        this.name = name;
        this.description = description;

        events.add(this);
    }

    /**
     * Id
     * 
     * @return
     */
    public int getID() {
        return eventID;
    }

    public String getName() {
        return name;
    }

    /**
     * Get Description
     * 
     * @return
     */
    public String getDescription() {
        return description;
    }

    // @Override
    @Override
    public String toString() {
        return name + " (" + description + ")";
    }

}
