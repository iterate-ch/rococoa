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

import java.util.logging.LogRecord;

/**
 * Print a brief summary of the LogRecord in a human readable format ON ONE LINE, and include details of
 * the thread from which it was logged. Blatant copy of the default 'SimpleFormatter' included with JDK.
 */
public class LevelAndThreadLogFormatter extends LogFormatter
{

    @Override
    protected void appendExtras(LogRecord record, StringBuffer sb) {
        sb.append("\t["); 
        sb.append(record.getLevel().getLocalizedName());
        sb.append("] "); 

        sb.append("{"); 
        sb.append(Thread.currentThread());
        sb.append("} "); 
    }
}