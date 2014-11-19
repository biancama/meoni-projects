package com.biancama.gui.swing.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class ActionController {
    public static final String JDL_PREFIX = "jd.gui.swing.jdgui.actions.ActionController.";
    private static ArrayList<ToolBarAction> TOOLBAR_ACTION_LIST = new ArrayList<ToolBarAction>();

    public static void register(ToolBarAction action) {
        synchronized (TOOLBAR_ACTION_LIST) {
            if (TOOLBAR_ACTION_LIST.contains(action)) { return; }
            for (ToolBarAction act : TOOLBAR_ACTION_LIST) {
                if (act.getID().equalsIgnoreCase(action.getID())) { return; }
            }
            TOOLBAR_ACTION_LIST.add(action);
        }
    }

    /**
     * Defines all possible actions
     */
    public static void initActions() {

        new ToolBarAction("toolbar.separator", "-") {
            private static final long serialVersionUID = -4628452328096482738L;

            @Override
            public void onAction(ActionEvent e) {
            }

            @Override
            public void initDefaults() {
                setType(ToolBarAction.Types.SEPARATOR);
            }

            @Override
            public void init() {
            }

        };

    }

    /**
     * Returns the action for the givven key
     * 
     * @param keyid
     * @return
     */
    public static ToolBarAction getToolBarAction(String keyid) {
        synchronized (TOOLBAR_ACTION_LIST) {
            for (ToolBarAction a : TOOLBAR_ACTION_LIST) {
                if (a.getID().equals(keyid)) { return a; }
            }
            return null;
        }
    }

    /**
     * returns a fresh copy of all toolbaractions
     * 
     * @return
     */
    public static ArrayList<ToolBarAction> getActions() {
        ArrayList<ToolBarAction> ret = new ArrayList<ToolBarAction>();
        synchronized (TOOLBAR_ACTION_LIST) {
            ret.addAll(TOOLBAR_ACTION_LIST);
        }
        return ret;

    }

}
