//    jDownloader - Downloadmanager
//    Copyright (C) 2008  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.biancama.controlling;

/**
 * Diese Klasse kann einen Laufenden durschschnitt erstellen
 * 
 * @author JD-Team
 * 
 */
public class SpeedMeter {
    private static final int capacity = 2;

    private int c = 0;
    private int lastSpeed = 0;
    private long[] bytes = new long[capacity];
    private long[] times = new long[capacity];
    private Object lock = new Object();

    // private Logger logger;
    /**
     * KOnstruktor dem die Zeit übergeben werden kann über die der durchschnitt
     * eführt wird
     * 
     * @param average
     */
    public SpeedMeter() {
        // logger=JDUtilities.getLogger();
        for (int i = 0; i < capacity; i++) {
            bytes[i] = 0;
            times[i] = 1;
        }
    }

    /**
     * Fügt einen weiteren wert hinzu
     * 
     * @param value
     */
    public void addSpeedValue(long value, long deltaTime) {
        synchronized (lock) {
            bytes[c] = value;
            times[c] = deltaTime;
            c++;
            if (c == capacity) {
                c = 0;
            }
        }
    }

    /**
     * Gibt die durschnittsgeschwindigkeit des letzten intervals zurück
     * 
     * @return speed
     */

    public int getSpeed() {
        synchronized (lock) {
            long totalValue = 0;
            long totalTime = 0;
            int i = 0;
            while (i < capacity) {
                if (bytes[i] == -1) break;
                totalValue += bytes[i];
                totalTime += times[i];
                i++;
            }
            if (totalTime > 0) {
                lastSpeed = (int) (totalValue / totalTime) * 1024;
            }            
            return lastSpeed;
            
        }
    }

}
