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
 
package org.rococoa.cocoa;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSMutableArray;
import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.cocoa.foundation.NSString;
import org.rococoa.test.RococoaTestCase;

public class NSMutableArrayTest extends RococoaTestCase {

    @Test public void test() {
        NSMutableArray array = NSMutableArray.CLASS.arrayWithCapacity(3);
        assertEquals(0, array.count());
        array.addObject(NSString.stringWithString("Hello"));
        array.addObject("Goodbye");
        assertEquals(2, array.count());
        assertEquals("(\n    Hello,\n    Goodbye\n)", array.description());
        
        NSObject first = array.objectAtIndex(0);
        assertEquals(NSString.stringWithString("Hello"), first);
        
        NSString firstAsString = Rococoa.cast(first, NSString.class);
        assertEquals("Hello", firstAsString.toString());
        assertEquals("Goodbye", 
                Rococoa.cast(array.objectAtIndex(1), NSString.class).toString());
    }   
}
