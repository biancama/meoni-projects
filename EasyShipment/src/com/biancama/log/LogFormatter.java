package com.biancama.log;

import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogFormatter extends SimpleFormatter {
    Date date = new Date();
    DateFormat longTimestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    private final String lineSeparator = System.getProperty("line.separator");
    private final StringBuilder sb = new StringBuilder();

    @Override
    public String format(LogRecord record) {
        /* clear StringBuilder buffer */
        sb.delete(0, sb.capacity());

        // Minimize memory allocations here.
        date.setTime(record.getMillis());

        // text = new StringBuffer();
        // if (formatter == null) {
        // formatter = new MessageFormat(format);
        // }
        // formatter.format(args, text, null);
        String message = formatMessage(record);
        // sb.append(text);
        // sb.append(" - ");
        if (BiancaLogger.getLogger().getLevel() == Level.ALL) {
            sb.append(longTimestamp.format(date));
            sb.append(" - ");
            sb.append(record.getLevel().getName());
            sb.append(" [");
            if (record.getSourceClassName() != null) {
                sb.append(record.getSourceClassName());
            } else {
                sb.append(record.getLoggerName());
            }
            if (record.getSourceMethodName() != null) {
                sb.append("(");
                sb.append(record.getSourceMethodName());
                sb.append(")");
            }
            sb.append("] ");

            sb.append("-> ");
        } else {
            sb.append(longTimestamp.format(date));
            sb.append(" - ");
            if (record.getSourceClassName() != null) {
                sb.append(record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf(".") + 1));
            } else {
                sb.append(record.getLoggerName());
            }
            sb.append("-> ");
        }
        sb.append(message);
        sb.append(lineSeparator);
        if (record.getThrown() != null) {
            sb.append(BiancaLogger.getStackTrace(record.getThrown()));
        }
        return sb.toString();

    }

}
