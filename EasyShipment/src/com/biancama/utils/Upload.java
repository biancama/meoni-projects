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

package com.biancama.utils;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import com.biancama.http.Browser;
import com.biancama.http.requests.FormData;
import com.biancama.http.requests.PostFormDataRequest;
import com.biancama.log.BiancaLogger;
import com.biancama.parser.Regex;
import com.biancama.utils.encoding.Encoding;

public class Upload {
    public static String toJDownloader(String str, String desc) {
        try {
            Browser br = new Browser();
            br.postPage("http://jdownloader.org/pastebin", "upload=1&desc=" + Encoding.urlEncode(desc) + "&log=" + Encoding.urlEncode(str));
            String path = br.getRegex("window.location = \"(.*?)\"").getMatch(0);
            return "http://jdownloader.org" + path;
        } catch (IOException e) {
            BiancaLogger.exception(e);
        }
        return null;

    }

    public static String toPastebinCom(String str, String name) {
        Browser br = new Browser();
        br.setFollowRedirects(false);
        try {
            br.postPage("http://jd_" + HashUtils.getMD5(str) + ".pastebin.com/pastebin.php", "parent_pid=&format=text&code2=" + URLEncoder.encode(str, "UTF-8") + "&poster=" + URLEncoder.encode(name, "UTF-8") + "&paste=Send&expiry=f&email=");
            if (br.getHttpConnection().isOK()) { return br.getRedirectLocation(); }
        } catch (IOException e) {
            BiancaLogger.exception(e);
        }
        return null;
    }

    public static String toRapidshareComPremium(File file, String userid, String pass) {
        try {
            Browser br = new Browser();
            String[] data = br.getPage("http://rapidshare.com/cgi-bin/upload.cgi?intsysdata=1").split("\\,");
            PostFormDataRequest r = (PostFormDataRequest) br.createPostFormDataRequest("http://rs" + data[0].trim() + "l3.rapidshare.com/cgi-bin/upload.cgi");
            r.addFormData(new FormData("toolmode2", "1"));
            r.addFormData(new FormData("filecontent", file.getName(), file));
            r.addFormData(new FormData("freeaccountid", userid));
            r.addFormData(new FormData("password", pass));
            br.openRequestConnection(r);

            String code = r.read();
            System.out.println(code);
            String[] lines = Regex.getLines(code);
            return lines[1];
        } catch (Exception e) {
            BiancaLogger.exception(e);
            return null;
        }

    }

    // public static String toUploadedToPremium(File file, String username,
    // String password) {
    // try {
    // Browser br = new Browser();
    // br.getPage("http://uploaded.to/login");
    // Form form = br.getForm(0);
    //
    // form.put("email", username);
    // form.put("password", password);
    // br.submitForm(form);
    // br.getPage("http://uploaded.to/home");
    // form = br.getForm(0);
    //
    // form.setFileToPost(file, null);
    // form.action = br.getRegex(
    // "document..*?.action = \"(http://.*?.uploaded.to/up\\?upload_id=)\";"
    // ).getMatch(0) + Math.round(10000 * Math.random()) + "0" +
    // Math.round(10000 * Math.random());
    // br.submitForm(form);
    // br.getPage("http://uploaded.to/home");
    // return br.getRegex("http://uploaded.to/\\?id=[A-Za-z0-9]+").getMatch(0);
    // } catch (Exception e) {
    // JDLogger.exception(e);
    // }
    // return "";
    //
    // }
}