package com.biancama.config;

import java.io.Serializable;

import com.biancama.utils.DatabaseUtils;

public class Configuration extends SubConfiguration implements Serializable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2709887320616014389L;
    public static final String NAME = "EasyShipmentConfig";

    public enum Param {
        INTERACTIONS, PARAM_DOWNLOAD_DIRECTORY, PARAM_CURRENT_BROWSE_PATH, PARAM_DOWNLOAD_MAX_SPEED, PARAM_DOWNLOAD_MAX_SIMULTAN, JAC_SHOW_TIMEOUT, PARAM_WEBUPDATE_AUTO_RESTART, PARAM_UPDATE_VERSION, PARAM_WEBUPDATE_AUTO_SHOW_CHANGELOG, PARAM_DOWNLOAD_READ_TIMEOUT, PARAM_DOWNLOAD_CONNECT_TIMEOUT, USE_PROXY, PROXY_HOST, PROXY_USER, PROXY_PASS, USE_SOCKS, PROXY_PASS_SOCKS, SOCKS_HOST, SOCKS_PORT, SHOW_CONTAINER_ONLOAD_OVERVIEW, PARAM_LOGGER_LEVEL, PARAM_FINISHED_DOWNLOADS_ACTION, PARAM_RELOADCONTAINER, PARAM_FILE_EXISTS, PARAM_DOWNLOAD_MAX_SIMULTAN_PER_HOST, PARAM_DOWNLOAD_PAUSE_SPEED, PARAM_ALLOW_RECONNECT, PARAM_GLOBAL_IP_DISABLE, PARAM_GLOBAL_IP_CHECK_SITE, PARAM_GLOBAL_IP_PATTERN, PARAM_GLOBAL_IP_BALANCE, PARAM_RECONNECT_FAILED_COUNTER, PARAM_RECONNECT_OKAY, PARAM_GLOBAL_IP_MASK, PARAM_HTTPSEND_REQUESTS_CLR, PARAM_HTTPSEND_REQUESTS, PARAM_HTTPSEND_USER, PARAM_HTTPSEND_PASS, PARAM_HTTPSEND_IP, PARAM_WEBUPDATE_DISABLE, PARAM_USE_GLOBAL_PREMIUM,
        PARAM_PDF_DIRECTORY
    }

    @Override
    public String toString() {
        return Configuration.NAME;
    }

    @Override
    public void save() {
        DatabaseUtils.getDatabaseConnector().saveConfiguration(Configuration.NAME, this);
        changes = false;
    }
}
