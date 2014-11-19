package com.biancama.utils;

import java.io.File;
import java.util.logging.Logger;

import com.biancama.config.database.DatabaseConnector;
import com.biancama.gui.UserIO;
import com.biancama.log.BiancaLogger;

public class DatabaseUtils {
    private static DatabaseConnector DB_CONNECT = null;

    public synchronized static DatabaseConnector getDatabaseConnector() {

        if (DB_CONNECT == null) {

            try {
                DB_CONNECT = new DatabaseConnector();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                BiancaLogger.exception(e);
                String configpath = FileSystemUtils.getHomeDirectoryFromEnvironment().getAbsolutePath() + "/config/";
                Logger logger = BiancaLogger.getLogger();
                if (e.getMessage().equals("Database broken!")) {
                    logger.severe("Database broken! Creating fresh Database");

                    if (!new File(configpath + "database.script").delete() || !new File(configpath + "database.properties").delete()) {
                        logger.severe("Could not delete broken Database");
                        UserIO.getInstance().requestMessageDialog("Could not delete broken database. Please remove the ES_HOME/config directory and restart EasyShipment");

                    }
                }

                try {
                    DB_CONNECT = new DatabaseConnector();
                } catch (Exception e1) {
                    BiancaLogger.exception(e1);
                    UserIO.getInstance().requestMessageDialog("Could not create database. Please remove the JD_HOME/config directory and restart JD");

                    System.exit(1);
                }
            }

        }
        return DB_CONNECT;

    }

}
