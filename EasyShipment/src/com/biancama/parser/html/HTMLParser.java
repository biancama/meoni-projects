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

package com.biancama.parser.html;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.biancama.http.Browser;
import com.biancama.http.URLConnectionAdapter;
import com.biancama.log.BiancaLogger;
import com.biancama.parser.Regex;

public class HTMLParser {

    /**
     * Diese Methode sucht nach passwörtern in einem Datensatz
     * 
     * @param data
     * @return
     */
    public static ArrayList<String> findPasswords(String data) {
        if (data == null) { return new ArrayList<String>(); }
        ArrayList<String> ret = new ArrayList<String>();
        data = data.replaceAll("(?s)<!-- .*? -->", "").replaceAll("(?s)<script .*?>.*?</script>", "").replaceAll("(?s)<.*?>", "").replaceAll("Spoiler:", "").replaceAll("(no.{0,2}|kein.{0,8}|ohne.{0,8}|nicht.{0,8})(pw|passwort|password|pass)", "").replaceAll("(pw|passwort|password|pass).{0,12}(nicht|falsch|wrong)", "");

        Pattern pattern = Pattern.compile("(пароль|па�?�?|ps?w|passwort|password|passw?)[\\s][\\s]*?[\"']([[^\\:\"'\\s]][^\"'\\s]*)[\"']?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            String pass = matcher.group(2);
            if (pass.length() > 2 && !pass.matches(".*(rar|zip|jpg|gif|png|html|php|avi|mpg)$") && !ret.contains(pass)) {
                ret.add(pass);
            }
        }
        pattern = Pattern.compile("(пароль|па�?�?|ps?w|passwort|password|passw?)[\\s][\\s]*?([[^\\:\"'\\s]][^\"'\\s]*)[\\s]?", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(data);
        while (matcher.find()) {
            String pass = matcher.group(2);
            if (pass.length() > 4 && !pass.matches(".*(rar|zip|jpg|gif|png|html|php|avi|mpg)$") && !ret.contains(pass)) {
                ret.add(pass);
            }
        }
        pattern = Pattern.compile("(пароль|па�?�?|ps?w|passwort|password|passw?)[\\s]?\\:[\\s]*?[\"']([^\"']+)[\"']?", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(data);
        while (matcher.find()) {
            String pass = matcher.group(2);
            if (pass.length() > 2 && !pass.matches(".*(rar|zip|jpg|gif|png|html|php|avi|mpg)$") && !ret.contains(pass)) {
                ret.add(pass);
            }
        }
        pattern = Pattern.compile("(пароль|па�?�?|ps?w|passwort|password|passw?)[\\s]?\\:[\\s]*?([^\"'\\s]+)[\\s]?", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(data);
        while (matcher.find()) {
            String pass = matcher.group(2);
            if (pass.length() > 2 && !pass.matches(".*(rar|zip|jpg|gif|png|html|php|avi|mpg)$") && !ret.contains(pass)) {
                ret.add(pass);
            }
        }

        return ret;
    }

    /**
     * Diese Methode sucht die vordefinierten input type="hidden" und formatiert
     * sie zu einem poststring z.b. würde bei:
     * 
     * <input type="hidden" name="f" value="f50b0f" /> <input type="hidden"
     * name="h" value="390b4be0182b85b0" /> <input type="hidden" name="b"
     * value="9" />
     * 
     * f=f50b0f&h=390b4be0182b85b0&b=9 ausgegeben werden
     * 
     * @param data
     *            Der zu durchsuchende Text
     * 
     * @return ein String, der als POST Parameter genutzt werden kann und alle
     *         Parameter des Formulars enthält
     */
    public static String getFormInputHidden(String data) {
        return HTMLParser.joinMap(HTMLParser.getInputHiddenFields(data), "=", "&");
    }

    /**
     * Diese Methode sucht die vordefinierten input type="hidden" zwischen
     * startpattern und lastpattern und formatiert sie zu einem poststring z.b.
     * würde bei:
     * 
     * <input type="hidden" name="f" value="f50b0f" /> <input type="hidden"
     * name="h" value="390b4be0182b85b0" /> <input type="hidden" name="b"
     * value="9" />
     * 
     * f=f50b0f&h=390b4be0182b85b0&b=9 rauskommen
     * 
     * @param data
     *            Der zu durchsuchende Text
     * @param startPattern
     *            der Pattern, bei dem die Suche beginnt
     * @param lastPattern
     *            der Pattern, bei dem die Suche endet
     * @return ein String, der als POST Parameter genutzt werden kann und alle
     *         Parameter des Formulars enthält
     */
    public static String getFormInputHidden(String data, String startPattern, String lastPattern) {
        String pat = new Regex(data, startPattern + "(.*?)" + lastPattern).getMatch(0);
        if (pat == null) { return null; }
        return HTMLParser.getFormInputHidden(pat);
    }

    /**
     * Gibt alle links die in data gefunden wurden als Stringliste zurück
     * 
     * @param data
     * @return STringliste
     */
    public static String getHttpLinkList(String data) {
        String[] links = HTMLParser.getHttpLinks(data, null);
        StringBuilder ret = new StringBuilder();
        for (String element : links) {
            ret.append('\"');
            ret.append(element.trim());
            ret.append(new char[] { '"', '\r', '\n' });
        }
        return ret.toString();
    }

    /**
     * Sucht alle Links heraus
     * 
     * @param data
     *            ist der Quelltext einer Html-Datei
     * @param url
     *            der Link von dem der Quelltext stammt (um die base automatisch
     *            zu setzen)
     * @return Linkliste aus data extrahiert
     */
    public static String[] getHttpLinks(String data, String url) {
        data = data.trim();
        String protocolPattern = "(flashget|h.{2,3}|httpviajd|httpsviajd|https|ccf|dlc|ftp|jd|rsdf|jdlist)";
        if (!data.matches(".*<.*>.*")) {
            int c = new Regex(data, "(" + protocolPattern + "://|(?<!://)www\\.)").count();
            if (c == 0) {
                return new String[] {};
            } else if (c == 1 && data.length() < 100 && data.matches("^(" + protocolPattern + "://|www\\.).*")) {
                String link = data.replaceFirst("h.{2,3}://", "http://").replaceFirst("^www\\.", "http://www.").replaceFirst("[<>\"].*", "");
                URLConnectionAdapter con = null;
                try {

                    if (!link.matches(".*\\s.*") || (con = new Browser().openGetConnection(link.replaceFirst("^httpviajd", "http").replaceAll("\\s", "%20"))).isOK()) {
                        if (con != null) {
                            con.disconnect();
                        }
                        return new String[] { link.replaceAll("\\s", "%20") };
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    BiancaLogger.exception(e);
                }
                if (con != null) {
                    con.disconnect();
                }
            }
        }

        url = url == null ? "" : url;
        Matcher m;
        String link;
        String basename = "";
        String host = "";
        LinkedList<String> set = new LinkedList<String>();
        Pattern[] basePattern = new Pattern[] { Pattern.compile("(?s)<[ ]?base[^>]*?href=('|\")(.*?)\\1", Pattern.CASE_INSENSITIVE), Pattern.compile("(?s)<[ ]?base[^>]*?(href)=([^'\"][^\\s]*)", Pattern.CASE_INSENSITIVE) };
        for (Pattern element : basePattern) {
            m = element.matcher(data);
            if (m.find()) {
                url = m.group(2);
                break;
            }
        }
        String pro = "http";
        if (url != null && url.trim().length() > 0) {

            if (url.startsWith("https://")) {
                pro = "https";
            }
            if (url.startsWith("jd://")) {
                pro = "jd";
            }
            if (url.startsWith("rsdf://")) {
                pro = "rsdf";
            }
            if (url.startsWith("ccf://")) {
                pro = "ccf";
            }
            if (url.startsWith("dlc://")) {
                pro = "dlc";
            }
            if (url.startsWith("jdlist://")) {
                pro = "jdlist";
            }
            if (url.startsWith("ftp://")) {
                pro = "ftp";
            }
            if (url.startsWith("flashget://")) {
                pro = "flashget";
            }
            url = url.replace(pro + "://", "");
            int dot = url.lastIndexOf('/');
            if (dot != -1) {
                basename = pro + "://" + url.substring(0, dot + 1);
            } else {
                basename = pro + "://" + url + "/";
            }
            dot = url.indexOf('/');
            if (dot != -1) {
                host = pro + "://" + url.substring(0, dot);
            } else {
                host = pro + "://" + url;
            }
            url = pro + "://" + url;
        } else {
            url = "";
        }

        final class Httppattern {
            public Pattern p;
            public int group;

            public Httppattern(Pattern p, int group) {
                this.p = p;
                this.group = group;
            }
        }
        Httppattern[] linkAndFormPattern = new Httppattern[] { new Httppattern(Pattern.compile("src.*?=.*?['|\"](.*?)['|\"]", Pattern.CASE_INSENSITIVE | Pattern.DOTALL), 1), new Httppattern(Pattern.compile("src.*?=(.*?)[ |>]", Pattern.CASE_INSENSITIVE | Pattern.DOTALL), 1), new Httppattern(Pattern.compile("(<[ ]?a[^>]*?href=|<[ ]?form[^>]*?action=)('|\")(.*?)\\2", Pattern.CASE_INSENSITIVE | Pattern.DOTALL), 3), new Httppattern(Pattern.compile("(<[ ]?a[^>]*?href=|<[ ]?form[^>]*?action=)([^'\"][^\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL), 2), new Httppattern(Pattern.compile("\\[(link|url)\\](.*?)\\[/\\1\\]", Pattern.CASE_INSENSITIVE | Pattern.DOTALL), 2) };
        for (Httppattern element : linkAndFormPattern) {
            m = element.p.matcher(data);
            while (m.find()) {
                link = m.group(element.group);
                link = link.replaceAll("h.{2,3}://", "http://");
                if (!(link.length() > 3 && link.matches("^" + protocolPattern + "://.*")) && link.length() > 0) {
                    if (link.length() > 2 && link.startsWith("www")) {
                        link = pro + "://" + link;
                    }
                    if (link.charAt(0) == '/') {
                        link = host + link;
                    } else if (link.charAt(0) == '#') {
                        link = url + link;
                    } else {
                        link = basename + link;
                    }
                }
                link = link.trim();

                try {
                    new URL(link);
                    if (!set.contains(link)) {
                        set.add(link);
                    }
                } catch (MalformedURLException e) {

                }

            }
        }
        data = data.replaceAll("(?s)<.*?>", "\r\n");
        data = data.replaceAll("(?s)\\[(url|link)\\].*?\\[/(url|link)\\]", "");
        m = Pattern.compile("(" + protocolPattern + "://|www\\.)[^\\s<>'\"]*(((?!\\s" + protocolPattern + "://|\\swww\\.)[^<>'\"]){0,20}([\\?|\\&][^<>'\\s\"]{1,10}\\=[^<>'\\s\"]+|\\.(htm[^<>'\\s\"]*|php|cgi|rar|zip|exe|avi|mpe?g|7z|bz2|doc|jpg|bmp|m4a|mdf|mkv|wav|mp[34]|pdf|wm[^<>'\\s\"]*|xcf|jar|swf|class|cue|bin|dll|cab|png|ico|gif|iso)[^<>'\\s\"]*))?", Pattern.CASE_INSENSITIVE).matcher(data);
        while (m.find()) {
            link = m.group(0);
            link = link.replaceAll("^h.{2,3}://", "http://");
            link = link.replaceFirst("^www\\.", "http://www\\.");
            link = link.trim();
            if (!set.contains(link)) {
                set.add(link);
            }
        }

        return set.toArray(new String[set.size()]);
    }

    /**
     * Gibt alle Hidden fields als hasMap zurück
     * 
     * @param data
     * @return hasmap mit allen hidden fields variablen
     */
    public static HashMap<String, String> getInputHiddenFields(String data) {
        Pattern intput1 = Pattern.compile("(?s)<[ ]?input([^>]*?type=['\"]?hidden['\"]?[^>]*?)[/]?>", Pattern.CASE_INSENSITIVE);
        Pattern intput2 = Pattern.compile("name=['\"]([^'\"]*?)['\"]", Pattern.CASE_INSENSITIVE);
        Pattern intput3 = Pattern.compile("value=['\"]([^'\"]*?)['\"]", Pattern.CASE_INSENSITIVE);
        Pattern intput4 = Pattern.compile("name=([^\\s]*)", Pattern.CASE_INSENSITIVE);
        Pattern intput5 = Pattern.compile("value=([^\\s]*)", Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = intput1.matcher(data);
        Matcher matcher2;
        Matcher matcher3;
        Matcher matcher4;
        Matcher matcher5;
        HashMap<String, String> ret = new HashMap<String, String>();
        boolean iscompl;
        while (matcher1.find()) {
            matcher2 = intput2.matcher(matcher1.group(1) + " ");
            matcher3 = intput3.matcher(matcher1.group(1) + " ");
            matcher4 = intput4.matcher(matcher1.group(1) + " ");
            matcher5 = intput5.matcher(matcher1.group(1) + " ");
            iscompl = false;
            String key, value;
            key = value = null;
            if (matcher2.find()) {
                iscompl = true;
                key = matcher2.group(1);
            } else if (matcher4.find()) {
                iscompl = true;
                key = matcher4.group(1);
            }
            if (matcher3.find() && iscompl) {
                value = matcher3.group(1);
            } else if (matcher5.find() && iscompl) {
                value = matcher5.group(1);
            } else {
                iscompl = false;
            }
            ret.put(key, value);
        }
        return ret;
    }

    /**
     * Ermittelt alle hidden input felder in einem HTML Text und gibt die hidden
     * variables als hashmap zurück es wird dabei nur der text zwischen start
     * dun endpattern ausgewertet
     * 
     * @param data
     * @param startPattern
     * @param lastPattern
     * @return hashmap mit hidden input variablen zwischen startPattern und
     *         endPattern
     */
    public static HashMap<String, String> getInputHiddenFields(String data, String startPattern, String lastPattern) {
        return HTMLParser.getInputHiddenFields(new Regex(data, startPattern + "(.*?)" + lastPattern).getMatch(0));
    }

    /**
     * @author olimex Fügt Map als String mit Trennzeichen zusammen TODO:
     *         auslagern
     * @param map
     *            Map
     * @param delPair
     *            Trennzeichen zwischen Key und Value
     * @param delMap
     *            Trennzeichen zwischen Map-Einträgen
     * @return Key-value pairs
     */
    public static String joinMap(Map<String, String> map, String delPair, String delMap) {
        StringBuilder buffer = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                buffer.append(delMap);
            }
            buffer.append(entry.getKey());
            buffer.append(delPair);
            buffer.append(entry.getValue());
        }
        return buffer.toString();
    }
}
