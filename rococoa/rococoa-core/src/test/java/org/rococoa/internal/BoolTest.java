/*
 * Copyright 2007-2010 Duncan McGregor
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

import org.junit.Test;
import org.rococoa.ObjCObject;
import org.rococoa.Rococoa;
import org.rococoa.test.RococoaTestCase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class BoolTest extends RococoaTestCase {

    private interface TestShunt extends ObjCObject {
        boolean valueIsYES(boolean a);

        boolean valueIsNO(boolean a);
    }

    private TestShunt testShunt = Rococoa.create("TestShunt", TestShunt.class);

    @Test
    public void test() {
        assertTrue(testShunt.valueIsYES(true));
        assertFalse(testShunt.valueIsYES(false));
        assertTrue(testShunt.valueIsNO(false));
        assertFalse(testShunt.valueIsNO(true));
    }

}
