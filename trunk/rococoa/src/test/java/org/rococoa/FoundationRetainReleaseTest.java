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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.rococoa.cocoa.NSAutoreleasePool;

@SuppressWarnings("nls")
public class FoundationRetainReleaseTest extends RococoaTestCase {
    
    @Test public void test() {
        ID idOfString = Foundation.cfString("Hello world");
        assertEquals(1, Foundation.cfGetRetainCount(idOfString));

        assertEquals(idOfString, Foundation.cfRetain(idOfString));
        assertEquals(2, Foundation.cfGetRetainCount(idOfString));

        Foundation.cfRelease(idOfString);
        assertEquals(1, Foundation.cfGetRetainCount(idOfString));

        Foundation.cfRelease(idOfString);
        // causes count to go to 0 and dispose will happen
    }    
    
    @Test public void testAutorelease() {
        NSAutoreleasePool pool = NSAutoreleasePool.new_();
        
        ID idOfString = Foundation.cfString("Hello world");
        assertEquals(1, Foundation.cfGetRetainCount(idOfString));
        
        Foundation.sendReturnsVoid(idOfString, "autorelease");
        assertEquals(1, Foundation.cfGetRetainCount(idOfString));
        
        Foundation.sendReturnsVoid(idOfString, "retain");
        assertEquals(2, Foundation.cfGetRetainCount(idOfString));
        
        pool.release();
        assertEquals(1, Foundation.cfGetRetainCount(idOfString));

        Foundation.cfRelease(idOfString);
        // causes count to go to 0 and dispose will happen
    }
    
    @Test public void testInitedObject() {
        NSAutoreleasePool pool = NSAutoreleasePool.new_();

        ID idOfClass = Foundation.getClass("NSString");
        ID idOfString = Foundation.sendReturnsID(idOfClass, "alloc");
        idOfString = Foundation.sendReturnsID(idOfString, "initWithCString:", "Hello world");
        assertEquals(1, Foundation.cfGetRetainCount(idOfString));

        // show that it wasn't in the pool
        pool.release();
        assertEquals(1, Foundation.cfGetRetainCount(idOfString));
        Foundation.cfRelease(idOfString);
    }
    
}
