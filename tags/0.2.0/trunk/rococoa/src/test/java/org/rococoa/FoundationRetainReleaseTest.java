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

import org.rococoa.cocoa.NSAutoreleasePool;


@SuppressWarnings("nls")
public class FoundationRetainReleaseTest extends RococoaTestCase {
    
    public void test() {
        ID string = Foundation.cfString("Hello world");
        assertEquals(1, Foundation.cfGetRetainCount(string));

        Foundation.cfRetain(string);
        assertEquals(2, Foundation.cfGetRetainCount(string));

        Foundation.cfRelease(string);
        assertEquals(1, Foundation.cfGetRetainCount(string));

        Foundation.cfRelease(string);
        // causes count to go to 0 and dispose will happen
    }    
    
    public void testAutorelease() {
        NSAutoreleasePool pool = NSAutoreleasePool.new_();
        
        ID string = Foundation.cfString("Hello world");
        assertEquals(1, Foundation.cfGetRetainCount(string));
        
        Foundation.sendReturnsVoid(string, "autorelease");
        assertEquals(1, Foundation.cfGetRetainCount(string));
        
        Foundation.sendReturnsVoid(string, "retain");
        assertEquals(2, Foundation.cfGetRetainCount(string));
        
        pool.release();
        assertEquals(1, Foundation.cfGetRetainCount(string));

        Foundation.cfRelease(string);
        // causes count to go to 0 and dispose will happen
    }
    
    public void testInitedObject() {
        NSAutoreleasePool pool = NSAutoreleasePool.new_();

        ID clas = Foundation.getClass("NSString");
        ID string = Foundation.sendReturnsID(clas, "alloc");
        string = Foundation.sendReturnsID(string, "initWithCString:", "Hello world");
        assertEquals(1, Foundation.cfGetRetainCount(string));

        // show that it wasn't in the pool
        pool.release();
        assertEquals(1, Foundation.cfGetRetainCount(string));
        Foundation.cfRelease(string);
    }
    
}
