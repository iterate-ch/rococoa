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

import org.junit.Ignore;
import org.junit.Test;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.test.RococoaTestCase;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("nls")
@Ignore("by vavi because of crash")
public class FoundationRetainReleaseTest extends RococoaTestCase {

    @Test public void test() {
        ID idOfString = Foundation.cfString("Hello world");
        assertRetainCount(1, idOfString);

        assertEquals(idOfString, Foundation.cfRetain(idOfString));
        assertRetainCount(2, idOfString);

        Foundation.cfRelease(idOfString);
        assertRetainCount(1, idOfString);

        Foundation.cfRelease(idOfString);
        // causes count to go to 0 and dispose will happen
    }

    @Test public void testAutorelease() {
        NSAutoreleasePool pool = NSAutoreleasePool.new_();

        ID idOfString = Foundation.cfString("Hello world");
        assertRetainCount(1, idOfString);

        Foundation.sendReturnsVoid(idOfString, "autorelease");
        assertRetainCount(1, idOfString); // autorelease does not increase the count

        Foundation.sendReturnsVoid(idOfString, "retain");
        assertRetainCount(2, idOfString);

        pool.drain();
        assertRetainCount(1, idOfString);

        Foundation.cfRelease(idOfString);
        // causes count to go to 0 and dispose will happen
    }

    @Test public void testInitedObject() {
        NSAutoreleasePool pool = NSAutoreleasePool.new_();

        ID idOfClass = Foundation.getClass("NSString");
        ID idOfString = Foundation.sendReturnsID(idOfClass, "alloc");
        idOfString = Foundation.sendReturnsID(idOfString, "initWithCString:", "Hello world");
        assertRetainCount(1, idOfString);

        // show that it wasn't in the pool
        pool.drain();
        assertRetainCount(1, idOfString);
        Foundation.cfRelease(idOfString);
    }

}
