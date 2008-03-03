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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a CFString, interchangable with NSString, passed by pointer.
 * 
 * This subclasses ID to make it easy to pass where an NSString is required.
 * 
 * @author duncan
 *
 */
public class CFString extends ID {

    private static Logger logging = LoggerFactory.getLogger("Rococoa");

    private String valueForDebug;
    private final boolean createdWithDefaultCtor;
    
    // required by jna during marshalling
    public CFString() {
        createdWithDefaultCtor = true;        
    };

    public CFString(String string) {
        super(Foundation.cfString(string).intValue());
        createdWithDefaultCtor = false;        
        if (logging.isTraceEnabled())
            valueForDebug = string;
        if (logging.isTraceEnabled())
            logging.trace("created {} retainCount is now {}", this, Foundation.cfGetRetainCount(this));
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (isNull()) {
            if (!createdWithDefaultCtor) {
                logging.warn("attempt to finalize null CFString {}", this);
            }
        } else {
            logging.trace("releasing {}", this);
            Foundation.cfRelease(this);
            if (logging.isTraceEnabled())
                logging.trace("{} retainCount is now {}", this, Foundation.cfGetRetainCount(this));
        }
        super.finalize();
    }
    
    @Override
    public String toString() {
        if (valueForDebug == null)
            return super.toString();
        return String.format("[CFString 0x%x = %s]", intValue(), valueForDebug); //$NON-NLS-1$
    }
}
