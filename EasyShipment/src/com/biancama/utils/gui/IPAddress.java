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

package com.biancama.utils.gui;

import java.util.regex.Pattern;

import com.biancama.config.Configuration;
import com.biancama.config.SubConfiguration;
import com.biancama.log.BiancaLogger;

public class IPAddress {
    public static final String IP_PATTERN = "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";

    /**
     * Überprüft ob eine IP gültig ist. das verwendete Pattern aknn in der
     * config editiert werden.
     * 
     * @param ip
     * @return
     */
    public static boolean validateIP(String ip) {
        if (ip == null) { return false; }
        try {
            return Pattern.compile(SubConfiguration.getConfig("DOWNLOAD").getStringProperty(Configuration.Param.PARAM_GLOBAL_IP_MASK.toString(), IP_PATTERN)).matcher(ip.trim()).matches();
        } catch (Exception e) {
            BiancaLogger.getLogger().severe("Could not validate IP! " + e);
        }
        return true;
    }

}
