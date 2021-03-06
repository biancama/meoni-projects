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

package com.biancama.http.requests;

import java.io.IOException;
import java.net.MalformedURLException;

import com.biancama.http.Browser;
import com.biancama.http.Request;
import com.biancama.http.URLConnectionAdapter;


public class GetRequest extends Request {

    public GetRequest(String url) throws MalformedURLException {
        super(Browser.correctURL(url));

    }

    // @Override
    public void postRequest(URLConnectionAdapter httpConnection) throws IOException {

    }

    // @Override
    public void preRequest(URLConnectionAdapter httpConnection) throws IOException {
        httpConnection.setRequestMethod("GET");

    }

}
