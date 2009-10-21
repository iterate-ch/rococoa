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
 
package org.rococoa.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.rococoa.ObjCObject;
import org.rococoa.ObjCObjectByReference;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.cocoa.foundation.NSNumber;
import org.rococoa.test.RococoaTestCase;

@SuppressWarnings("nls")
public class RococoaObjCObjectByReferenceTest extends RococoaTestCase {
    private interface TestShunt extends ObjCObject {
        void testNSNumberByReference_with(ObjCObjectByReference reference, int value);
    };
    
    @Test public void test() {
        NSAutoreleasePool pool = NSAutoreleasePool.new_();
        TestShunt shunt = Rococoa.create("TestShunt", TestShunt.class);
        ObjCObjectByReference reference = new ObjCObjectByReference();
        shunt.testNSNumberByReference_with(reference, 42);
        NSNumber value = reference.getValueAs(NSNumber.class);
        assertEquals(42, value.intValue());
                
        // we better have retained the result by the time it gets back
        assertEquals(3, value.retainCount());
        pool.drain();
        assertEquals(2, value.retainCount());
    }

}
