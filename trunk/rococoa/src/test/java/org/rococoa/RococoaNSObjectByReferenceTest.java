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
import org.rococoa.cocoa.NSNumber;

@SuppressWarnings("nls")
public class RococoaNSObjectByReferenceTest extends RococoaTestCase {
    private interface TestShunt extends NSObject {
        void testNSNumberByReference_with(NSObjectByReference reference, int value);
    };
    
    public void test() {
        NSAutoreleasePool pool = NSAutoreleasePool.new_();
        TestShunt shunt = Rococoa.create("TestShunt", TestShunt.class);
        NSObjectByReference reference = new NSObjectByReference();
        shunt.testNSNumberByReference_with(reference, 42);
        NSNumber value = reference.getValueAs(NSNumber.class);
        assertEquals(42, value.intValue());
                
        // we better have retained the result by the time it gets back
        assertEquals(3, value.retainCount());
        pool.release();
        assertEquals(2, value.retainCount());
    }

}
