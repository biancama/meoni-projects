package com.biancama.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.biancama.utils.FormatterUtils;

public class BiancaLogger {
    private BiancaLogger() {
    }

    private static Logger logger = null;
    public static String LOGGER_NAME = "easy_Shipment";
    private static ConsoleHandler console;

    /**
     * If the logger is not already created, it creates a new one and returtn
     * it.
     * 
     * @return The logger
     */
    public static synchronized Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(LOGGER_NAME);
            Formatter formatter = new LogFormatter();
            logger.setUseParentHandlers(false);

            console = new ConsoleHandler();
            console.setLevel(Level.INFO);
            console.setFormatter(formatter);
            logger.addHandler(console);

            logger.setLevel(Level.INFO);
            logger.addHandler(BiancaLogHandler.getHandler());
            BiancaLogHandler.getHandler().setFormatter(formatter);

        }
        return logger;
    }

    public static void timestamp(String msg) {
        getLogger().warning(FormatterUtils.formatMilliseconds(System.currentTimeMillis()) + " : " + msg);
    }

    public static void exception(Throwable e) {
        exception(Level.SEVERE, e);
    }

    public static void removeConsoleHandler() {
        if (console != null) {
            getLogger().removeHandler(console);
        }
        System.err.println("Removed Consolehandler. Start with -debug to see console output");

    }

    public static void addHeader(String string) {
        getLogger().info("\r\n\r\n--------------------------------------" + string + "-----------------------------------");

    }

    public static void exception(Level level, Throwable e) {
        getLogger().log(level, level.getName() + " Exception occurred", e);
    }

    public static void quickLog() {
        System.out.println("Footstep: " + new Exception().getStackTrace()[1]);

    }

    static public void warning(Object o) {
        getLogger().warning(o.toString());

    }

    /**
     * Return stack trace of an exception
     * 
     * @param thrown
     * @return
     */
    public static String getStackTrace(Throwable thrown) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        thrown.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }

    /**
     * Retuns al log entries as string.. filtered with loglevel level
     * 
     * @param all
     * @return
     */
    public static String getLog(Level level) {

        Level tmp = getLogger().getLevel();
        getLogger().setLevel(level);
        try {
            ArrayList<LogRecord> buff = BiancaLogHandler.getHandler().getBuffer();
            StringBuilder sb = new StringBuilder();
            for (LogRecord lr : buff) {

                sb.append(BiancaLogHandler.getHandler().getFormatter().format(lr));
            }

            return sb.toString();
        } finally {
            getLogger().setLevel(tmp);

        }
    }

}
