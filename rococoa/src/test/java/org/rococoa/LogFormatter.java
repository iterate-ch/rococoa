/*
 * Copyright 2007, 2008 Duncan McGregor
 * 
 * This file is part of Rococoa, a library to allow Java to talk to Cocoa.
 * 
 * Rococoa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Rococoa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Rococoa.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package org.rococoa;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Print a brief summary of the LogRecord in a human readable format ON ONE LINE, and include details of
 * the thread from which it was logged. Blatant copy of the default 'SimpleFormatter' included with JDK.
 */
public class LogFormatter extends Formatter
{

    Date dat = new Date();
    private final static String format = "{0,time} ";
    private MessageFormat formatter;

    private Object args[] = new Object[1];

    // Line separator string. This is the value of the line.separator
    // property at the moment that the SimpleFormatter was created.
    @SuppressWarnings("unchecked") // looks like Sun class GetPropertyAction uses raw PrivilegedAction
    private String lineSeparator = (String) java.security.AccessController
            .doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

    /**
     * Format the given LogRecord.
     *
     * @param record
     *            the log record to be formatted.
     * @return a formatted log record
     */
    public synchronized String format(LogRecord record)
    {
        StringBuffer sb = new StringBuffer();
        // Minimize memory allocations here.
        dat.setTime(record.getMillis());
        args[0] = dat;
        StringBuffer text = new StringBuffer();
        if (formatter == null)
        {
            formatter = new MessageFormat(format);
        }
        formatter.format(args, text, null);
        sb.append(text);

        try {
            sb.append(Class.forName(record.getSourceClassName()).getSimpleName());
        } catch (Exception e) {
            sb.append(record.getLoggerName());
        }

        if (record.getSourceMethodName() != null)
        {
            sb.append("."); 
            sb.append(record.getSourceMethodName());
        }

        sb.append(" - "); 
        sb.append(formatMessage(record));
        
        appendExtras(record, sb);
        sb.append(lineSeparator);
        if (record.getThrown() != null)
        {
            try
            {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            }
            catch (Exception ex)
            {
            }
        }
        return sb.toString();
    }

    protected void appendExtras(LogRecord record, StringBuffer sb) {
    }
}