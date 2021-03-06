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

package com.biancama.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import com.biancama.http.requests.FormData;
import com.biancama.http.requests.GetRequest;
import com.biancama.http.requests.PostFormDataRequest;
import com.biancama.http.requests.PostRequest;
import com.biancama.http.requests.RequestVariable;
import com.biancama.parser.Regex;
import com.biancama.parser.html.Form;
import com.biancama.parser.html.InputField;
import com.biancama.parser.html.XPath;
import com.biancama.utils.encoding.Encoding;

public class Browser {
    public class BrowserException extends IOException {

        private static final long serialVersionUID = 1509988898224037320L;
        private URLConnectionAdapter connection;
        private Exception e = null;

        public BrowserException(String string) {
            super(string);
        }

        public BrowserException(String string, Exception e) {
            this(string);
            this.e = e;
        }

        public BrowserException(String message, URLConnectionAdapter con) {
            this(message);
            connection = con;
        }

        public BrowserException(String message, URLConnectionAdapter con, Exception e) {
            this(message, con);
            this.e = e;
        }

        public BrowserException closeConnection() {
            if (connection != null && connection.isConnected()) {
                connection.disconnect();
            }
            return this;
        }

        /**
         * Returns the connection adapter that caused the browserexception
         * 
         * @return
         */
        public URLConnectionAdapter getConnection() {
            return connection;
        }

        public Exception getException() {
            return e;
        }

    }

    private static BiancaProxy GLOBAL_PROXY = null;
    private static Logger LOGGER;

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void setLogger(Logger logger) {
        Browser.LOGGER = logger;
    }

    public static void setGlobalProxy(BiancaProxy p) {
        if (LOGGER != null) {
            LOGGER.info("Use global proxy: " + p);
        }
        GLOBAL_PROXY = p;
    }

    public static BiancaProxy getGlobalProxy() {
        return GLOBAL_PROXY;
    }

    private static HashMap<String, Cookies> COOKIES = new HashMap<String, Cookies>();
    private HashMap<String, Cookies> cookies = new HashMap<String, Cookies>();

    private boolean debug = false;

    private static HashMap<URL, Browser> URL_LINK_MAP = new HashMap<URL, Browser>();

    private HashMap<String, String[]> logins = new HashMap<String, String[]>();

    /**
     * Clears all cookies for the given url. URL has to be a valid url
     * 
     * @param url
     */
    public void clearCookies(String url) {
        String host = Browser.getHost(url);
        Iterator<String> it = getCookies().keySet().iterator();
        String check = null;
        while (it.hasNext()) {
            check = it.next();
            if (check.contains(host)) {
                cookies.get(check).clear();
                break;
            }

        }
    }

    public void forwardCookies(Request request) throws MalformedURLException {
        if (request == null) { return; }
        String host = Browser.getHost(request.getUrl());
        Cookies cookies = getCookies().get(host);
        if (cookies == null) { return; }

        for (Cookie cookie : cookies.getCookies()) {
            // Pfade sollten verarbeitet werden...TODO
            if (cookie.isExpired()) {
                continue;
            }
            request.getCookies().add(cookie);
        }
    }

    public void forwardCookies(URLConnectionAdapter con) throws MalformedURLException {
        if (con == null) { return; }
        String host = Browser.getHost(con.getURL().toString());
        Cookies cookies = getCookies().get(host);
        String cs = Request.getCookieString(cookies);
        if (cs != null && cs.trim().length() > 0) {
            con.setRequestProperty("Cookie", cs);
        }
    }

    public String getCookie(String url, String string) throws MalformedURLException {
        String host = Browser.getHost(url);
        Cookies cookies = getCookies(host);
        Cookie cookie = cookies.get(string);
        if (cookie != null) { return cookie.getValue(); }
        return null;
    }

    private HashMap<String, Cookies> getCookies() {
        if (this.cookiesExclusive) { return cookies; }
        return COOKIES;
    }

    public Cookies getCookies(String url) {
        String host = Browser.getHost(url);
        Cookies cookies2 = getCookies().get(host);
        if (cookies2 == null) {
            cookies2 = new Cookies();
            getCookies().put(host, cookies2);
        }
        return cookies2;
    }

    public void setCookie(String url, String key, String value) throws MalformedURLException {
        String host;
        host = Browser.getHost(url);
        Cookies cookies;
        if (!getCookies().containsKey(host) || (cookies = getCookies().get(host)) == null) {
            cookies = new Cookies();
            getCookies().put(host, cookies);
        }
        Cookie cookie = new Cookie();
        cookie.setHost(host);
        cookie.setKey(key);
        cookie.setValue(value);
        cookies.add(cookie);
    }

    /**
     * Returns the host for url. input: http://srv2.bluehost.to/dsdsf ->out
     * bluehost.to
     * 
     * @param url
     * @return
     * @throws MalformedURLException
     */

    public static String getHost(URL url) {
        return getHost(url.getHost());
    }

    public static String getHost(String url) {
        String ret = url;
        try {
            ret = new URL(url + "").getHost();
        } catch (Exception e) {
        }
        int id = 0;
        while ((id = ret.indexOf(".")) != ret.lastIndexOf(".")) {
            ret = ret.substring(id + 1);

        }
        return ret;
    }

    public void updateCookies(Request request) throws MalformedURLException {
        if (request == null) { return; }
        String host = Browser.getHost(request.getUrl());
        Cookies cookies = getCookies().get(host);
        if (cookies == null) {
            cookies = new Cookies();
            getCookies().put(host, cookies);
        }
        cookies.add(request.getCookies());
    }

    private String acceptLanguage = "de, en-gb;q=0.9, en;q=0.8";
    private int connectTimeout = -1;
    private URL currentURL;

    private boolean doRedirects = false;

    private RequestHeader headers;

    private int limit = 1 * 1024 * 1024;

    private int readTimeout = -1;

    private Request request;
    private String customCharset = null;
    private boolean cookiesExclusive = true;
    private BiancaProxy proxy;
    private HashMap<String, Integer> requestIntervalLimitMap;
    private HashMap<String, Long> requestTimeMap;
    private static HashMap<String, Integer> REQUEST_INTERVAL_LIMIT_MAP;
    private static HashMap<String, Long> REQUESTTIME_MAP;
    private static boolean VERBOSE = false;
    private static int TIMEOUT_READ;
    private static int TIMEOUT_CONNECT;
    private static final Authenticator AUTHENTICATOR = new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            Browser br = Browser.getAssignedBrowserInstance(this.getRequestingURL());
            if (br == null) {
                if (LOGGER != null) {
                    LOGGER.warning("Browser Auth Error!");
                }
                return null;
            }
            return br.getPasswordAuthentication(this.getRequestingHost(), this.getRequestingPort());

        }
    };

    public Browser() {

    }

    public String getAcceptLanguage() {
        return acceptLanguage;
    }

    public int getConnectTimeout() {
        return connectTimeout <= 0 ? connectTimeout : TIMEOUT_CONNECT;
    }

    public Form[] getForms() {

        return Form.getForms(this);

    }

    public void setCustomCharset(String charset) {
        this.customCharset = charset;
    }

    /**
     * Returns the first form with an Submitvalue of name
     * 
     * @param name
     * @return
     */
    public Form getFormBySubmitvalue(String name) {
        for (Form f : getForms()) {
            try {
                f.setPreferredSubmit(name);
                return f;
            } catch (IllegalArgumentException e) {

            }
        }
        return null;
    }

    public Form getFormbyProperty(String property, String name) {
        for (Form f : getForms()) {
            if (f.getStringProperty(property) != null && f.getStringProperty(property).equalsIgnoreCase(name)) { return f; }
        }
        return null;
    }

    public RequestHeader getHeaders() {
        if (headers == null) {
            headers = new RequestHeader();
        }
        return headers;
    }

    public String getPage(String string) throws IOException {

        this.openRequestConnection(this.createGetRequest(string));

        return this.loadConnection(null);

    }

    /**
     * Connects a request. and sets the requests as the browsers latest request
     * 
     * @param request
     * @throws IOException
     */
    public void connect(Request request) throws IOException {
        // sets request BEVOR connection. this enhables to find the request in
        // the protocol handlers
        this.request = request;
        try {
            waitForPageAccess(this, request);
        } catch (InterruptedException e) {
            throw new IOException("requestIntervalTime Exception");
        }

        try {

            request.connect();
        } finally {

            assignURLToBrowserInstance(request.getHttpConnection().getURL(), null);
        }

    }

    private static synchronized void waitForPageAccess(Browser browser, Request request) throws InterruptedException {
        try {
            Integer localLimit = null;
            Integer globalLimit = null;
            Long localLastRequest = null;
            Long globalLastRequest = null;
            if (browser.requestIntervalLimitMap != null) {
                localLimit = browser.requestIntervalLimitMap.get(request.getUrl().getHost());
                localLastRequest = browser.requestTimeMap.get(request.getUrl().getHost());
            }
            if (REQUEST_INTERVAL_LIMIT_MAP != null) {
                globalLimit = REQUEST_INTERVAL_LIMIT_MAP.get(request.getUrl().getHost());
                globalLastRequest = REQUESTTIME_MAP.get(request.getUrl().getHost());
            }

            if (localLimit == null && globalLimit == null) { return; }
            if (localLastRequest == null && globalLastRequest == null) { return; }
            if (localLimit != null && localLastRequest == null) { return; }
            if (globalLimit != null && globalLastRequest == null) { return; }

            if (globalLimit == null) {
                globalLimit = 0;
            }
            if (localLimit == null) {
                localLimit = 0;
            }
            if (localLastRequest == null) {
                localLastRequest = System.currentTimeMillis();
            }
            if (globalLastRequest == null) {
                globalLastRequest = System.currentTimeMillis();
            }
            long dif = Math.max(localLimit - (System.currentTimeMillis() - localLastRequest), globalLimit - (System.currentTimeMillis() - globalLastRequest));

            if (dif > 0) {
                // System.out.println("Sleep " + dif + " before connect to " +
                // request.getUrl().getHost());
                Thread.sleep(dif);
                // waitForPageAccess(request);
            }
        } finally {
            if (browser.requestTimeMap != null) {
                browser.requestTimeMap.put(request.getUrl().getHost(), System.currentTimeMillis());
            }

            if (REQUESTTIME_MAP != null) {
                REQUESTTIME_MAP.put(request.getUrl().getHost(), System.currentTimeMillis());
            }
        }
    }

    private static void assignURLToBrowserInstance(URL url, Browser browser) {
        if (browser == null) {
            URL_LINK_MAP.remove(url);
        } else {
            URL_LINK_MAP.put(url, browser);
        }

        // System.out.println("NO LINKED: " + URL_LINK_MAP);
    }

    public static URL reAssignUrlToBrowserInstance(URL url1, URL url2) {
        assignURLToBrowserInstance(url2, getAssignedBrowserInstance(url1));
        URL_LINK_MAP.remove(url1);
        return url2;
    }

    /**
     * Returns the Browserinstance that requestst this url connection
     * 
     * @param port
     * @param host
     */
    public static Browser getAssignedBrowserInstance(URL url) {
        return URL_LINK_MAP.get(url);
    }

    /**
     * Assures that the browser does not download any binary files in textmode
     * 
     * @param request
     * @throws BrowserException
     */
    private void checkContentLengthLimit(Request request) throws BrowserException {
        if (request == null || request.getHttpConnection() == null || request.getHttpConnection().getHeaderField("Content-Length") == null) { return; }
        if (Long.parseLong(request.getHttpConnection().getHeaderField("Content-Length")) > limit) {
            if (LOGGER != null) {
                LOGGER.severe(request.printHeaders());
            }
            throw new BrowserException("Content-length too big", request.getHttpConnection());
        }
    }

    /**
     * Returns the current readtimeout
     * 
     * @return
     */
    public int getReadTimeout() {
        return readTimeout <= 0 ? TIMEOUT_READ : readTimeout;
    }

    /**
     * If automatic redirectfollowing is disabled, you can get the redirect url
     * if there is any.
     * 
     * @return
     */
    public String getRedirectLocation() {
        if (request == null) { return null; }
        return request.getLocation();

    }

    /**
     * Gets the latest request
     * 
     * @return
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Opens a new connection based on a Form
     * 
     * @param form
     * @return
     * @throws Exception
     */
    public URLConnectionAdapter openFormConnection(Form form) throws Exception {

        return this.openRequestConnection(this.createFormRequest(form));
    }

    /**
     * Creates a new Request object based on a form
     * 
     * @param form
     * @return
     * @throws Exception
     */
    public Request createFormRequest(Form form) throws Exception {
        String base = null;

        if (request != null) {
            base = request.getUrl().toString();
        }
        try {
            // find base in source
            String sourceBase = this.getRegex("<base.*?href=\"(.+?)\"").getMatch(0).trim();
            // check if valid url
            new URL(sourceBase);
            base = sourceBase;
        } catch (Throwable e) {

        }

        String action = form.getAction(base);
        switch (form.getMethod()) {

        case GET:

            String varString = form.getPropertyString();
            if (varString != null && !varString.matches("[\\s]*")) {
                if (action.matches(".*\\?.+")) {
                    action += "&";
                } else if (action.matches("[^\\?]*")) {
                    action += "?";
                }
                action += varString;
            }
            return this.createGetRequest(action);

        case POST:
            if (form.getEncoding() == null || !form.getEncoding().toLowerCase().endsWith("form-data")) {

                return this.createPostRequest(action, form.getRequestVariables());
            } else {

                PostFormDataRequest request = (PostFormDataRequest) createPostFormDataRequest(action);
                if (form.getEncoding() != null) {
                    request.setEncodeType(form.getEncoding());
                }

                for (int i = 0; i < form.getInputFields().size(); i++) {
                    InputField entry = form.getInputFields().get(i);

                    if (entry.getValue() == null) {
                        continue;
                    }
                    if (entry.getType() != null && entry.getType().equalsIgnoreCase("image")) {

                        request.addFormData(new FormData(entry.getKey() + ".x", entry.getIntegerProperty("x", (int) (Math.random() * 100)) + ""));
                        request.addFormData(new FormData(entry.getKey() + ".y", entry.getIntegerProperty("y", (int) (Math.random() * 100)) + ""));

                    } else if (entry.getType() != null && entry.getType().equalsIgnoreCase("file")) {
                        request.addFormData(new FormData(entry.getKey(), entry.getFileToPost().getName(), entry.getFileToPost()));

                    } else if (entry.getKey() != null && entry.getValue() != null) {

                        request.addFormData(new FormData(entry.getKey(), entry.getValue()));

                    }
                }

                return request;
            }

        }
        return null;

    }

    /**
     * Opens a new get connection
     * 
     * @param string
     * @return
     * @throws IOException
     */
    public URLConnectionAdapter openGetConnection(String string) throws IOException {
        return openRequestConnection(this.createGetRequest(string));

    }

    /**
     * Opens a connection based on the requets object
     */
    public URLConnectionAdapter openRequestConnection(Request request) throws IOException {
        connect(request);
        if (isDebug()) {
            if (LOGGER != null) {
                LOGGER.finest("\r\n" + request.printHeaders());
            }
        }

        updateCookies(request);
        this.request = request;
        if (this.doRedirects && request.getLocation() != null) {
            if (request.getLocation().toLowerCase().startsWith("ftp://")) { throw new BrowserException("Cannot redirect to FTP"); }
            this.openGetConnection(null);
        } else {
            currentURL = request.getUrl();
        }
        return this.request.getHttpConnection();
    }

    /**
     * Creates a new Getrequest
     */

    public Request createGetRequest(String string) throws IOException {
        string = getURL(string);
        boolean sendref = true;
        if (currentURL == null) {
            sendref = false;
            currentURL = new URL(string);
        }

        GetRequest request = new GetRequest((string));
        request.setCustomCharset(this.customCharset);
        if (selectProxy() != null) {
            request.setProxy(selectProxy());
        }
        // doAuth(request);
        if (connectTimeout > 0) {
            request.setConnectTimeout(connectTimeout);
        }
        if (readTimeout > 0) {
            request.setReadTimeout(readTimeout);
        }
        request.getHeaders().put("Accept-Language", acceptLanguage);
        // request.setFollowRedirects(doRedirects);
        forwardCookies(request);
        if (sendref) {
            request.getHeaders().put("Referer", currentURL.toString());
        }
        if (headers != null) {
            mergeHeaders(request);

        }

        // if (this.doRedirects && request.getLocation() != null) {
        // this.openGetConnection(null);
        // } else {
        //
        // currentURL = new URL(string);
        // }
        // return this.request.getHttpConnection();
        return request;
    }

    private void mergeHeaders(Request request) {
        if (headers.isDominant()) {
            request.getHeaders().clear();
        }
        for (int i = 0; i < headers.size(); i++) {

            if (headers.getValue(i) == null) {
                request.getHeaders().remove(headers.getKey(i));
            } else {
                request.getHeaders().put(headers.getKey(i), headers.getValue(i));
            }
        }

    }

    private BiancaProxy selectProxy() {
        if (proxy != null) {
            if (proxy == BiancaProxy.NO_PROXY) { return null; }
            return proxy;
        }
        return GLOBAL_PROXY;
    }

    public Request createGetRequestRedirectedRequest(Request oldrequest) throws IOException {
        String string = getURL(oldrequest.getLocation());
        boolean sendref = true;
        if (currentURL == null) {
            sendref = false;
            currentURL = new URL(string);
        }

        GetRequest request = new GetRequest((string));
        request.setCustomCharset(this.customCharset);
        if (selectProxy() != null) {
            request.setProxy(selectProxy());
        }
        request.setCookies(oldrequest.getCookies());
        // doAuth(request);
        if (connectTimeout > 0) {
            request.setConnectTimeout(connectTimeout);
        }
        if (readTimeout > 0) {
            request.setReadTimeout(readTimeout);
        }
        request.getHeaders().put("Accept-Language", acceptLanguage);
        // request.setFollowRedirects(doRedirects);
        forwardCookies(request);
        if (sendref) {
            request.getHeaders().put("Referer", currentURL.toString());
        }
        if (headers != null) {
            mergeHeaders(request);
        }

        // if (this.doRedirects && request.getLocation() != null) {
        // this.openGetConnection(null);
        // } else {
        //
        // currentURL = new URL(string);
        // }
        // return this.request.getHttpConnection();
        return request;
    }

    /**
     * TRies to get a fuill url out of string
     * 
     * @throws BrowserException
     */
    public String getURL(String string) throws BrowserException {
        if (string == null) {
            string = this.getRedirectLocation();
        }
        if (string == null) { throw new BrowserException("Null URL"); }
        try {
            new URL(string);
        } catch (Exception e) {
            if (request == null || request.getHttpConnection() == null) { return string; }
            String base = getBase(string);
            if (string.startsWith("/") || string.startsWith("\\")) {
                try {

                    URL bUrl = new URL(base);
                    if (bUrl.getPort() != 80 && bUrl.getPort() > 0) {
                        string = "http://" + new URL(base).getHost() + ":" + bUrl.getPort() + string;
                    } else {
                        string = "http://" + new URL(base).getHost() + string;
                    }

                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
            } else {
                string = base + string;
            }
        }
        return Browser.correctURL(Encoding.urlEncode_light(string));
    }

    private String getBase(String string) {
        if (string == null) { return ""; }
        String base = getRegex("<base\\s*href=\"(.*?)\">").getMatch(0);
        if (base != null) { return base; }
        String path = request.getHttpConnection().getURL().getPath();
        int id;
        if ((id = path.lastIndexOf("/")) >= 0) {
            path = path.substring(0, id);
        }

        // path.substring(path.lastIndexOf("/"))
        if (request.getHttpConnection().getURL().getPort() != 80 && request.getHttpConnection().getURL().getPort() > 0) {
            string = "http://" + request.getHttpConnection().getURL().getHost() + ":" + request.getHttpConnection().getURL().getPort() + path + "/";
        } else {
            string = "http://" + request.getHttpConnection().getURL().getHost() + path + "/";
        }
        return string;
    }

    /**
     * Opens a Post COnnection based on a variable hashmap
     */
    public URLConnectionAdapter openPostConnection(String url, LinkedHashMap<String, String> post) throws IOException {

        return this.openRequestConnection(this.createPostRequest(url, post));

    }

    public Request createPostRequestfromRedirectedRequest(Request oldrequest, String postdata) throws IOException {
        String url = getURL(oldrequest.getLocation());
        boolean sendref = true;
        if (currentURL == null) {
            sendref = false;
            currentURL = new URL(url);
        }
        HashMap<String, String> post = Request.parseQuery(postdata);

        PostRequest request = new PostRequest((url));
        request.setCustomCharset(this.customCharset);
        if (selectProxy() != null) {
            request.setProxy(selectProxy());
        }
        request.setCookies(oldrequest.getCookies());
        // doAuth(request);
        request.getHeaders().put("Accept-Language", acceptLanguage);
        // request.setFollowRedirects(doRedirects);
        if (connectTimeout > 0) {
            request.setConnectTimeout(connectTimeout);
        }
        if (readTimeout > 0) {
            request.setReadTimeout(readTimeout);
        }
        forwardCookies(request);
        if (sendref) {
            request.getHeaders().put("Referer", currentURL.toString());
        }
        if (post != null) {
            request.addAll(post);
        }
        if (headers != null) {
            mergeHeaders(request);
        }
        return request;

    }

    public Request createPostFormDataRequest(String url) throws IOException {
        url = getURL(url);
        boolean sendref = true;
        if (currentURL == null) {
            sendref = false;
            currentURL = new URL(url);
        }

        PostFormDataRequest request = new PostFormDataRequest((url));
        request.setCustomCharset(this.customCharset);
        if (selectProxy() != null) {
            request.setProxy(selectProxy());
        }

        request.getHeaders().put("Accept-Language", acceptLanguage);

        if (connectTimeout > 0) {
            request.setConnectTimeout(connectTimeout);
        }
        if (readTimeout > 0) {
            request.setReadTimeout(readTimeout);
        }
        forwardCookies(request);
        if (sendref) {
            request.getHeaders().put("Referer", currentURL.toString());
        }

        if (headers != null) {
            mergeHeaders(request);
        }
        return request;
    }

    /**
     * Creates a new POstrequest based on a variable hashmap
     */
    public Request createPostRequest(String url, LinkedHashMap<String, String> post) throws IOException {

        return this.createPostRequest(url, PostRequest.variableMaptoArray(post));
    }

    /**
     * Creates a new postrequest based an an requestVariable Arraylist
     */
    private Request createPostRequest(String url, ArrayList<RequestVariable> post) throws IOException {
        url = getURL(url);
        boolean sendref = true;
        if (currentURL == null) {
            sendref = false;
            currentURL = new URL(url);
        }

        PostRequest request = new PostRequest((url));
        request.setCustomCharset(this.customCharset);
        if (selectProxy() != null) {
            request.setProxy(selectProxy());
        }
        // doAuth(request);
        request.getHeaders().put("Accept-Language", acceptLanguage);
        // request.setFollowRedirects(doRedirects);
        if (connectTimeout > 0) {
            request.setConnectTimeout(connectTimeout);
        }
        if (readTimeout > 0) {
            request.setReadTimeout(readTimeout);
        }
        forwardCookies(request);
        if (sendref) {
            request.getHeaders().put("Referer", currentURL.toString());
        }
        if (post != null) {
            request.addAll(post);

        }
        if (headers != null) {
            mergeHeaders(request);
        }
        return request;
    }

    /**
     * Creates a postrequest based on a querystring
     */
    public Request createPostRequest(String url, String post) throws MalformedURLException, IOException {

        return createPostRequest(url, Request.parseQuery(post));
    }

    /**
     * OPens a new POst connection based on a query string
     */
    public URLConnectionAdapter openPostConnection(String url, String post) throws IOException {

        return openPostConnection(url, Request.parseQuery(post));
    }

    /**
     * loads a new page (post)
     */
    public String postPage(String url, LinkedHashMap<String, String> post) throws IOException {
        openPostConnection(url, post);
        return loadConnection(null);
    }

    /**
     * loads a new page (POST)
     */
    public String postPage(String url, String post) throws IOException {

        return postPage(url, Request.parseQuery(post));
    }

    /**
     * loads a new page (post) the postdata is given by the poststring. it wiull
     * be send as it is
     */
    public String postPageRaw(String url, String post) throws IOException {
        PostRequest request = (PostRequest) this.createPostRequest(url, new ArrayList<RequestVariable>());
        request.setCustomCharset(this.customCharset);
        if (post != null) {
            request.setPostDataString(post);
        }
        this.openRequestConnection(request);
        return this.loadConnection(null);
    }

    public void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setCurrentURL(String string) throws MalformedURLException {

        currentURL = new URL(string);

    }

    public void setFollowRedirects(boolean b) {
        doRedirects = b;

    }

    public boolean isFollowingRedirects() {
        return doRedirects;
    }

    public void setHeaders(RequestHeader h) {
        headers = h;

    }

    public void setLoadLimit(int i) {
        limit = i;

    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getBaseURL() {
        if (request == null) { return null; }

        String base = request.getUrl().toString();
        if (base.matches("http://.*/.*")) {
            return base.substring(0, base.lastIndexOf("/")) + "/";
        } else {
            return base + "/";
        }

    }

    public String submitForm(Form form) throws Exception {

        this.openFormConnection(form);

        checkContentLengthLimit(request);
        return request.read();
    }

    // @Override
    @Override
    public String toString() {
        if (request == null) { return "Browser. no request yet"; }
        return request.toString();
    }

    public Regex getRegex(String string) {
        return new Regex(this, string);
    }

    public Regex getRegex(Pattern compile) {
        return new Regex(this, compile);
    }

    public boolean containsHTML(String regex) {
        return new Regex(this, regex).matches();
    }

    /**
     * Reads the content behind a con and returns them. Note: if con==null, the
     * current request is read. This is usefull for redirects. Note #2: if a
     * connection is loaded, data is not stored in the browser instance.
     * 
     * @param con
     * @return
     * @throws IOException
     */
    public String loadConnection(URLConnectionAdapter con) throws IOException {
        String ret = null;
        try {
            if (con == null) {
                checkContentLengthLimit(request);
                con = request.getHttpConnection();
                ret = request.read();
            } else {
                ret = Request.read(con);
            }
        } catch (BrowserException e) {
            throw e;
        } catch (IOException e) {
            throw new BrowserException(e.getMessage(), con, e).closeConnection();
        }
        if (isVerbose()) {
            if (LOGGER != null) {
                LOGGER.finest("\r\n" + ret + "\r\n");
            }
        }

        return ret;

    }

    public URLConnectionAdapter getHttpConnection() {
        if (request == null) { return null; }
        return request.getHttpConnection();
    }

    /**
     * Lädt über eine URLConnection eine Datei herunter. Zieldatei ist file.
     * 
     * @param file
     * @param con
     * @return Erfolg true/false
     * @throws IOException
     */
    public static void download(File file, URLConnectionAdapter con) throws IOException {

        if (file.isFile()) {
            if (!file.delete()) {
                System.out.println("Konnte Datei nicht löschen " + file);
                throw new IOException("Could not overwrite file: " + file);
            }

        }

        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();

        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file, true));
        BufferedInputStream input;
        if (con.getHeaderField("Content-Encoding") != null && con.getHeaderField("Content-Encoding").equalsIgnoreCase("gzip")) {

            input = new BufferedInputStream(new GZIPInputStream(con.getInputStream()));
        } else {
            input = new BufferedInputStream(con.getInputStream());
        }

        byte[] b = new byte[1024];
        int len;
        while ((len = input.read(b)) != -1) {
            output.write(b, 0, len);
        }
        output.close();
        input.close();

    }

    public void getDownload(File file, String urlString) throws IOException {

        urlString = URLDecoder.decode(urlString, "UTF-8");

        URLConnectionAdapter con = this.openGetConnection(urlString);
        con.setInstanceFollowRedirects(true);
        download(file, con);

    }

    /**
     * Downloads the contents behind con to file. if(con ==null), the latest
     * request is downloaded. Usefull for redirects
     * 
     * @param file
     * @param con
     * @throws IOException
     */
    public void downloadConnection(File file, URLConnectionAdapter con) throws IOException {
        if (con == null) {
            con = request.getHttpConnection();
        }
        con.setInstanceFollowRedirects(true);
        download(file, con);

    }

    /**
     * Downloads url to file.
     * 
     * @param file
     * @param urlString
     * @return Erfolg true/false
     * @throws IOException
     */
    public static void download(File file, String url) throws IOException {

        new Browser().getDownload(file, url);

    }

    public Form getForm(int i) {
        Form[] forms = getForms();
        if (forms.length <= i) { return null; }
        return forms[i];
    }

    public String getHost() {
        if (request == null) { return null; }
        return request.getUrl().getHost();

    }

    public Browser cloneBrowser() {
        Browser br = new Browser();
        br.requestIntervalLimitMap = this.requestIntervalLimitMap;
        br.requestTimeMap = this.requestTimeMap;
        br.acceptLanguage = acceptLanguage;
        br.connectTimeout = connectTimeout;
        br.currentURL = currentURL;
        br.doRedirects = doRedirects;
        br.setCustomCharset(this.customCharset);
        br.getHeaders().putAll(getHeaders());
        br.limit = limit;
        br.readTimeout = readTimeout;
        br.request = request;
        br.cookies = cookies;
        br.logins = new HashMap<String, String[]>();
        br.logins.putAll(logins);
        br.cookiesExclusive = cookiesExclusive;
        br.debug = debug;
        br.proxy = proxy;
        return br;
    }

    public Form[] getForms(String downloadURL) throws IOException {
        this.getPage(downloadURL);
        return this.getForms();

    }

    public URLConnectionAdapter openFormConnection(int i) throws Exception {
        return openFormConnection(getForm(i));

    }

    public String getMatch(String string) {
        return getRegex(string).getMatch(0);

    }

    public String getURL() {
        if (request == null) { return null; }
        return request.getUrl().toString();

    }

    public void setCookiesExclusive(boolean b) {
        if (cookiesExclusive == b) { return; }
        this.cookiesExclusive = b;
        if (b) {
            this.cookies.clear();
            for (Entry<String, Cookies> next : COOKIES.entrySet()) {
                Cookies tmp;
                cookies.put(next.getKey(), tmp = new Cookies());
                tmp.add(next.getValue());
            }
        } else {
            this.cookies.clear();
        }
    }

    public boolean isCookiesExclusive() {
        return cookiesExclusive;
    }

    public String followConnection() throws IOException {
        String ret = null;

        if (request.getHtmlCode() != null) {
            if (LOGGER != null) {
                LOGGER.warning("Request has already been read");
            }
            return null;
        }
        checkContentLengthLimit(request);

        ret = request.read();

        return ret;

    }

    public boolean isDebug() {
        return debug || VERBOSE;
    }

    public void setDebug(boolean debug) {
        String caller = (Thread.currentThread().getContextClassLoader().getResource("jd") + "");

        if (!caller.matches("jar\\:.*\\.jar\\!.*")) {
            this.debug = debug;
        }
    }

    public void setAuth(String domain, String user, String pass) {
        domain = domain.trim();
        if (domain.indexOf(":") <= 0) {
            domain += ":80";
        }
        logins.put(domain, new String[] { user, pass });
    }

    public String[] getAuth(String domain) {
        domain = domain.trim();
        if (domain.indexOf(":") <= 0) {
            domain += ":80";
        }
        String[] ret = logins.get(domain);
        if (ret == null) {
            // see proxy auth
            if (selectProxy() != null) {
                if ((selectProxy().getHost() + ":" + selectProxy().getPort()).equalsIgnoreCase(domain)) {
                    ret = new String[] { selectProxy().getUser(), selectProxy().getPass() };
                }
            }
        }

        return ret;
    }

    public String submitForm(String formname) throws Exception {
        return this.submitForm(getFormBySubmitvalue(formname));

    }

    public String getPage(URL url) throws IOException {

        return getPage(url + "");

    }

    public void setRequest(Request request) throws MalformedURLException {
        if (request == null) { return; }
        updateCookies(request);
        this.request = request;

        currentURL = request.getUrl();

    }

    public Request createRequest(Form form) throws Exception {
        return createFormRequest(form);
    }

    public Request createRequest(String downloadURL) throws Exception {
        return createGetRequest(downloadURL);
    }

    public String getXPathElement(String xPath) {
        return new XPath(this.toString(), xPath, false).getFirstMatch();

    }

    public void setProxy(BiancaProxy proxy) {
        if (debug) {
            if (LOGGER != null) {
                LOGGER.info("Use local proxy: " + proxy);
            }
        }
        if (proxy == null) {
            System.err.println("Browser:No proxy");
            this.proxy = null;
            return;
        }
        // System.err.println("Browser: "+proxy);
        // this.setAuth(proxy.getHost() + ":" + proxy.getPort(),
        // proxy.getUser(), proxy.getPass());
        this.proxy = proxy;
    }

    public BiancaProxy getProxy() {
        return proxy;
    }

    /**
     * Zeigt debuginformationen auch im Hauptprogramm an
     * 
     * @param b
     */
    public void forceDebug(boolean b) {
        this.debug = b;

    }

    /**
     * Returns the Password Authentication if there are auth set for the given
     * url
     * 
     * @param url
     * @param port
     * @param host
     * @return
     */
    public PasswordAuthentication getPasswordAuthentication(String host, int port) {
        if (port <= 0) {
            port = 80;
        }
        String[] auth = this.getAuth(host + ":" + port);
        if (auth == null) { return null; }
        if (LOGGER != null) {
            LOGGER.finest("Use Authentication for: " + host + ":" + port + ": " + auth[0] + " - " + auth[1]);
        }
        return new PasswordAuthentication(auth[0], auth[1].toCharArray());
    }

    public static void init() {
        Authenticator.setDefault(AUTHENTICATOR);
        CookieHandler.setDefault(null);
        XTrustProvider.install();

    }

    public void setRequestIntervalLimit(String host, int i) {
        if (this.requestIntervalLimitMap == null) {
            this.requestTimeMap = new HashMap<String, Long>();
            this.requestIntervalLimitMap = new HashMap<String, Integer>();
        }
        requestIntervalLimitMap.put(host, i);

    }

    public static synchronized void setRequestIntervalLimitGlobal(String host, int i) {
        if (REQUEST_INTERVAL_LIMIT_MAP == null) {
            REQUEST_INTERVAL_LIMIT_MAP = new HashMap<String, Integer>();
            REQUESTTIME_MAP = new HashMap<String, Long>();
        }
        REQUEST_INTERVAL_LIMIT_MAP.put(host, i);
    }

    public static boolean isVerbose() {
        return VERBOSE;
    }

    public static void setVerbose(boolean b) {
        VERBOSE = b;

    }

    /**
     * Sets the global readtimeout in ms
     * 
     * @param integerProperty
     */
    public static void setGlobalReadTimeout(int valueMS) {
        TIMEOUT_READ = valueMS;

    }

    public static int getGlobalReadTimeout() {
        return TIMEOUT_READ;
    }

    /**
     * Sets the global connect timeout
     * 
     * @param valueMS
     */
    public static void setGlobalConnectTimeout(int valueMS) {
        TIMEOUT_CONNECT = valueMS;

    }

    public static int getGlobalConnectTimeout() {
        return TIMEOUT_CONNECT;

    }

    /**
     * Returns the first form that has an inputfiled with name key
     * 
     * @param key
     * @return
     */
    public Form getFormbyKey(String key) {
        for (Form f : getForms()) {
            if (f.hasInputFieldByName(key)) { return f; }
        }
        return null;
    }

    /**
     * Returns a corrected url, where multiple / and ../. are removed
     * 
     * @param url
     * @return
     */
    public static String correctURL(String url) {
        /* check if we need to correct url */
        int begin;
        begin = url.indexOf("://");
        if (url == null || ((begin > 0) && !url.substring(begin).contains("//")) || !url.contains("./")) { return url; }
        String ret = url;
        String end = null;
        String tmp = null;
        boolean endisslash = false;
        if (url.startsWith("http://")) {
            begin = 8;
        } else if (url.startsWith("https://")) {
            begin = 9;
        } else {
            begin = 0;
        }
        int first = url.indexOf("/", begin);
        if (first < 0) { return ret; }
        ret = url.substring(0, first);
        int endp = url.indexOf("?", first);
        if (endp > 0) {
            end = url.substring(endp);
            tmp = url.substring(first, endp);
        } else {
            tmp = url.substring(first);
        }
        /* is the end of url a / */
        endisslash = tmp.endsWith("/");

        /* filter multiple / */
        tmp = tmp.replaceAll("/+", "/");

        /* filter .. and . */
        String parts[] = tmp.split("/");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equalsIgnoreCase(".")) {
                parts[i] = "";
            } else if (parts[i].equalsIgnoreCase("..")) {
                if (i > 0) {
                    int j = i - 1;
                    while (true && j > 0) {
                        if (parts[j].length() > 0) {
                            parts[j] = "";
                            break;
                        }
                        j--;
                    }
                }
                parts[i] = "";
            }
        }
        tmp = "";
        for (String part : parts) {
            if (part.length() > 0) {
                tmp = tmp + "/" + part;
            }
        }
        if (endisslash) {
            tmp = tmp + "/";
        }
        ret = ret + tmp + (end != null ? end : "");
        return ret;
    }
}
