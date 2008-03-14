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
 
package org.rococoa.quicktime;

import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.NSTestCase;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * We had issues with jna crashing with nested structs.
 * 
 * @author duncan
 *
 */
@SuppressWarnings("nls")
public class QTTimeRangeTest extends NSTestCase {

    private interface StructLibrary extends Library {
        int passQTTimeRangeByValue(QTTimeRange r);
    }
    
    private StructLibrary instance = (StructLibrary) Native.loadLibrary("rococoa", StructLibrary.class);    
    
    public void testPassByValueAsMethodCall() {
        ID testID = Foundation.createInstance(Foundation.nsClass("TestShunt"));        
        QTTimeRange range = new QTTimeRange(new QTTime(25, 1000, 0), new QTTime(50, 1000,10));
        int result = Foundation.send(testID, "testPassQTTimeRangeByValue:", int.class, 
                range);
        assertEquals(75, result);
    }
    
    public void testPassByValueAsFunctionCall() {
        QTTimeRange range = new QTTimeRange(new QTTime(25, 1000, 0), new QTTime(50, 1000,10));
        assertEquals(75, instance.passQTTimeRangeByValue(range));
    }


    
}
