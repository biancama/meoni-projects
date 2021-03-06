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

package sun.net.www.protocol.jdps;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import com.biancama.http.BiancaProxy;
import com.biancama.http.Browser;
import com.biancama.http.HTTPsConnection;
import com.biancama.http.Request;

public class Handler extends sun.net.www.protocol.https.Handler {

    public Handler() {
        this(null, -1);
    }

    public Handler(String proxy, int port) {
        this.proxy = proxy;
        this.proxyPort = port;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return openConnection(u, (Proxy) null);
    }

    @Override
    protected URLConnection openConnection(URL u, Proxy p) throws IOException {
        String urlCorrect = u.toString();
        if (urlCorrect.startsWith("jdp")) {
            urlCorrect = "http" + urlCorrect.substring(3);
        }
        if (u.getUserInfo() != null) {
            String[] logins = u.getUserInfo().split(":");
            Browser.getAssignedBrowserInstance(u).setAuth(u.getHost(), logins[0], logins.length > 1 ? logins[1] : "");
        }

        URL nurl = Browser.reAssignUrlToBrowserInstance(u, new URL(urlCorrect));

        URLConnection con = p == null ? nurl.openConnection() : nurl.openConnection(p);
        if (p == null) {
            return new HTTPsConnection(con, (BiancaProxy) null);
        } else {
            Browser br = Browser.getAssignedBrowserInstance(u);
            Request request = br.getRequest();
            BiancaProxy pr = request.getProxy();
            if (pr == null) { throw new IOException("Proxy Mapping failed."); }
            return new HTTPsConnection(con, pr);

        }

    }

}
