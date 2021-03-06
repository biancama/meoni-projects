package com.biancama.config.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.biancama.config.SubConfiguration;
import com.biancama.log.BiancaLogger;
import com.biancama.utils.FileSystemUtils;
import com.biancama.utils.URLUtils;

public class DatabaseConnector implements Serializable {

    private static final long serialVersionUID = 8074213660382482620L;

    private static Logger logger = BiancaLogger.getLogger();

    private static String configpath = FileSystemUtils.getHomeDirectoryFromEnvironment().getAbsolutePath() + "/config/";

    private final HashMap<String, Object> dbdata = new HashMap<String, Object>();

    public static final Object LOCK = new Object();

    private static boolean dbshutdown = false;

    private static Connection con = null;

    static {

        try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * return if Database is still open
     * 
     * @return
     */
    public static boolean isDatabaseShutdown() {
        return dbshutdown;
    }

    /**
     * Constructor
     * 
     * @throws Exception
     */
    public DatabaseConnector() throws SQLException {

        if (con != null) { return; }
        logger.finer("Loading database");
        if (new File(configpath + "database.script").exists()) {
            if (!checkDatabaseHeader()) { throw new SQLException("Database broken!");

            }
        }

        con = DriverManager.getConnection("jdbc:hsqldb:file:" + configpath + "database;shutdown=true", "sa", "");

        con.setAutoCommit(true);
        con.createStatement().executeUpdate("SET LOGSIZE 1");

        if (!new File(configpath + "database.script").exists()) {
            logger.finer("No CONFIGURATION database found. Creating new one.");

            con.createStatement().executeUpdate("CREATE TABLE config (name VARCHAR(256), obj OTHER)");
            con.createStatement().executeUpdate("CREATE TABLE links (name VARCHAR(256), obj OTHER)");

            PreparedStatement pst = con.prepareStatement("INSERT INTO config VALUES (?,?)");
            logger.finer("Starting database wrapper");

            for (String tmppath : new File(configpath).list()) {
                try {
                    if (tmppath.endsWith(".cfg")) {
                        logger.finest("Wrapping " + tmppath);

                        Object props = FileSystemUtils.loadObject(null, URLUtils.getResourceFile("config/" + tmppath), false);

                        if (props != null) {
                            pst.setString(1, tmppath.split(".cfg")[0]);
                            pst.setObject(2, props);
                            pst.execute();
                        }
                    }
                } catch (Exception e) {
                    BiancaLogger.exception(e);
                }
            }
        }

    }

    /**
     * Checks the database of inconsistency
     * 
     * @throws IOException
     */
    private boolean checkDatabaseHeader() {
        logger.finer("Checking database");
        File f = new File(configpath + "database.script");
        if (!f.exists()) { return true; }
        boolean databaseok = true;

        FileInputStream fis = null;
        BufferedReader in = null;
        try {
            fis = new FileInputStream(f);
            in = new BufferedReader(new InputStreamReader(fis));
            String line = "";
            int counter = 0;

            main: while (counter < 7) {
                line = in.readLine();
                if (line == null) {
                    databaseok = false;
                    break main;
                }
                switch (counter) {
                case 0:
                    if (!line.equals("CREATE SCHEMA PUBLIC AUTHORIZATION DBA")) {
                        databaseok = false;
                    }
                    break;
                case 1:
                    if (!line.equals("CREATE MEMORY TABLE CONFIG(NAME VARCHAR(256),OBJ OBJECT)")) {
                        databaseok = false;
                    }
                    break;
                case 2:
                    if (!line.equals("CREATE MEMORY TABLE LINKS(NAME VARCHAR(256),OBJ OBJECT)")) {
                        databaseok = false;
                    }
                    break;
                case 3:
                    if (!line.equals("CREATE USER SA PASSWORD \"\"")) {
                        databaseok = false;
                    }
                    break;
                case 4:
                    if (!line.equals("GRANT DBA TO SA")) {
                        databaseok = false;
                    }
                    break;
                case 5:
                    if (!line.equals("SET WRITE_DELAY 10")) {
                        databaseok = false;
                    }
                    break;
                case 6:
                    if (!line.equals("SET SCHEMA PUBLIC")) {
                        databaseok = false;
                    }
                    break;
                }
                counter++;
            }

            while (((line = in.readLine()) != null)) {
                if (!line.matches("INSERT INTO .*? VALUES\\('.*?','.*?'\\)")) {
                    databaseok = false;
                    break;
                }
            }

        } catch (FileNotFoundException e) {
            databaseok = false;
        } catch (IOException e) {
            databaseok = false;

        } finally {
            try {
                in.close();

            } catch (IOException e1) {
            }
            try {

                fis.close();
            } catch (IOException e1) {
            }
        }
        return databaseok;
    }

    /**
     * Returns a CONFIGURATION
     */
    public synchronized Object getData(String name) {
        synchronized (LOCK) {
            if (isDatabaseShutdown()) { return null; }
            Object ret = null;
            ret = dbdata.get(name);
            try {
                if (ret == null) {
                    // try to init the table
                    ResultSet rs = con.createStatement().executeQuery("SELECT * FROM config WHERE name = '" + name + "'");
                    if (rs.next()) {
                        ret = rs.getObject(2);
                        dbdata.put(rs.getString(1), ret);
                    }

                }
            } catch (Exception e) {
                BiancaLogger.getLogger().warning("Database not available. Create new one: " + name);
                BiancaLogger.exception(Level.FINEST, e);
            }
            return ret;
        }
    }

    /**
     * Returns all Subconfigurations
     * 
     * @return
     */
    public ArrayList<SubConfiguration> getSubConfigurationKeys() {
        synchronized (LOCK) {
            if (isDatabaseShutdown()) { return null; }
            ArrayList<SubConfiguration> ret = new ArrayList<SubConfiguration>();
            ResultSet rs;
            try {
                rs = con.createStatement().executeQuery("SELECT * FROM config");

                while (rs.next()) {
                    try {
                        SubConfiguration conf = SubConfiguration.getConfig((String) rs.getObject(1));
                        if (conf.getProperties().size() > 0) {
                            ret.add(conf);

                        }
                    } catch (Exception e) {

                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return ret;
        }
    }

    /**
     * Saves a CONFIGURATION into the database
     */
    public void saveConfiguration(String name, Object data) {

        synchronized (LOCK) {
            if (isDatabaseShutdown()) { return; }
            dbdata.put(name, data);
            try {
                ResultSet rs = con.createStatement().executeQuery("SELECT COUNT(name) FROM config WHERE name = '" + name + "'");
                rs.next();
                if (rs.getInt(1) > 0) {
                    PreparedStatement pst = con.prepareStatement("UPDATE config SET obj = ? WHERE name = '" + name + "'");
                    pst.setObject(1, data);
                    pst.execute();
                } else {
                    PreparedStatement pst = con.prepareStatement("INSERT INTO config VALUES (?,?)");
                    pst.setString(1, name);
                    pst.setObject(2, data);
                    pst.execute();
                }

            } catch (Exception e) {
                try {
                    System.out.println("First save " + name);
                    PreparedStatement pst = con.prepareStatement("INSERT INTO config VALUES (?,?)");
                    pst.setString(1, name);
                    pst.setObject(2, data);
                    pst.execute();
                } catch (Exception e2) {
                    BiancaLogger.getLogger().warning("Database save error: " + name);
                    BiancaLogger.exception(Level.FINEST, e2);
                }
            }
        }
    }

    /**
     * Shutdowns the database
     */
    public void shutdownDatabase() {
        synchronized (LOCK) {
            if (isDatabaseShutdown()) { return; }
            try {
                dbshutdown = true;
                con.close();
            } catch (SQLException e) {
                BiancaLogger.exception(e);
            }
        }
    }

    /**
     * Returns the saved linklist
     */
    public Object getLinks() {
        synchronized (LOCK) {
            if (isDatabaseShutdown()) { return null; }
            try {
                ResultSet rs = con.createStatement().executeQuery("SELECT * FROM links");
                rs.next();
                return rs.getObject(2);
            } catch (Exception e) {
                BiancaLogger.exception(Level.FINEST, e);
                BiancaLogger.getLogger().warning("Database not available. Create new one: links");
            }
            return null;
        }
    }

    /**
     * Saves the linklist into the database
     */
    public void saveLinks(Object obj) {
        synchronized (LOCK) {
            if (isDatabaseShutdown()) { return; }
            try {
                if (getLinks() == null) {
                    PreparedStatement pst = con.prepareStatement("INSERT INTO links VALUES (?,?)");
                    pst.setString(1, "links");
                    pst.setObject(2, obj);
                    pst.execute();
                } else {
                    PreparedStatement pst = con.prepareStatement("UPDATE links SET obj=? WHERE name='links'");
                    pst.setObject(1, obj);
                    pst.execute();
                }
            } catch (Exception e) {
                BiancaLogger.exception(e);
            }
        }
    }

    /**
     * Returns the connection to the database
     */
    public Connection getDatabaseConnection() {
        return con;
    }
}
