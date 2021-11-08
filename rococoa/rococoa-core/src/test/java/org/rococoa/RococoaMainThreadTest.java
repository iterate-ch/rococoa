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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.rococoa.test.RococoaTestCase;

public class RococoaMainThreadTest extends RococoaTestCase {
    private interface TestShunt extends ObjCObject {
        boolean isMainThread();
    };
    
    private @RunOnMainThread interface TestShuntOnMainThread extends TestShunt {};
    
    @Test public void testNotMainThreadAtClassLevel() {
        TestShunt testShunt = Rococoa.create("TestShunt", TestShunt.class);
        assertFalse(testShunt.isMainThread());
    }

    @Test public void testOnMainThreadAtClassLevel() {
        TestShunt testShunt = Rococoa.create("TestShunt", TestShuntOnMainThread.class);
        assertTrue(testShunt.isMainThread());

    }

    @Test public void testNotMainThreadAtMethodLevel() {
        TestShunt testShunt = Rococoa.create("TestShunt", TestShuntUnAnnotatedMethod.class);
        assertFalse(testShunt.isMainThread());
    }

    @Test public void testOnMainThreadAtMethodLevel() {
        TestShunt testShunt = Rococoa.create("TestShunt", TestShuntAnnotatedMethod.class);
        assertTrue(testShunt.isMainThread());

    }

    private interface TestShuntAnnotatedMethod extends TestShunt {
        @RunOnMainThread
        boolean isMainThread();
    }

    private interface TestShuntUnAnnotatedMethod extends TestShunt {
        boolean isMainThread();
    }
}
