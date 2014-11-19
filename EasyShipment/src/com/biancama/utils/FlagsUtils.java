package com.biancama.utils;

public class FlagsUtils {
    /**
     * checks wether status has all following flags
     * 
     * @param status
     * @param flags
     * @return
     */
    public static boolean hasAllFlags(int status, int... flags) {
        for (int i : flags) {
            if ((status & i) == 0) { return false; }
        }
        return true;
    }

    public static boolean hasNoFlags(int status, int... flags) {
        for (int i : flags) {
            if ((status & i) != 0) { return false; }
        }
        return true;
    }

    public static boolean hasSomeFlags(int status, int... flags) {
        for (int i : flags) {
            if ((status & i) != 0) { return true; }
        }
        return false;
    }

    /**
     * &-Operation returns only bits which are set in both integers
     * 
     * @param curState
     * @param filtermask
     * @return curState&filtermask
     */
    public static int filterFlags(int curState, int filtermask) {
        return curState & filtermask;
    }

}
