//    jDownloader - Downloadmanager
//    Copyright (C) 2009  JD-Team support@jdownloader.org
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

import com.biancama.events.BiancaEvent;

public class DownloadControllerEvent extends BiancaEvent {
    public DownloadControllerEvent(Object source, int ID) {
        super(source, ID);
    }

    public DownloadControllerEvent(Object source, int ID, Object param) {
        super(source, ID, param);
    }

    /**
     * Wird bei Strukturänderungen der DownloadListe
     */
    public static final int REFRESH_STRUCTURE = 1;

    /* Downloadlink oder ArrayList<DownloadLink> soll aktuallisiert werden */
    public static final int REFRESH_SPECIFIC = 11;

    /* die komplette liste soll aktuallisiert werden */
    public static final int REFRESH_ALL = 12;

    public static final int ADD_FILEPACKAGE = 2;

    public static final int REMOVE_FILPACKAGE = 3;

    public static final int ADD_DOWNLOADLINK = 4;

    public static final int REMOVE_DOWNLOADLINK = 5;

}
