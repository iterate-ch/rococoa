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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.test.RococoaTestCase;

import com.sun.jna.NativeLong;

@SuppressWarnings({ "nls", "unchecked", "cast" })
public class StringTypeConverterTest extends RococoaTestCase {

    private static Class<? extends Number> primitiveTypeOfID = 
        (Class<? extends Number>) new NativeLong().nativeType();

    private StringTypeConverter converter = new StringTypeConverter();


    @Test public void convertsStringAsArgumentToIDofCFString() {
        assertEquals(primitiveTypeOfID, converter.nativeType());

        Number nativeValue = (Number) converter.toNative("Hello", null);
        assertEquals(primitiveTypeOfID, nativeValue.getClass());
        assertEquals("Hello", Foundation.toString(ID.fromLong(nativeValue.longValue())));
    }

    @Test public void convertsNullAsArgumentToNull() {
        // Not entirely sure about this, maybe 0 would be better than null, 
        // but JNA seems to interpret it properly
        assertEquals(null, converter.toNative(null, null));
    }

    @Test public void convertsReturnedIDToString() {
        ID helloID = Foundation.cfString("Hello"); // just leaks

        // We can cope with 64 bits on 64 and 32
        Number nativeValue = new Long(helloID.longValue());
        String converted = converter.fromNative(nativeValue, null);
        assertEquals("Hello", converted);

        // We must cope with 32 bits on 32-bit
        if (NativeLong.SIZE == 4) {
            nativeValue = new Integer(helloID.intValue());
            converted = converter.fromNative(nativeValue, null);
            assertEquals("Hello", converted);
        }
    }

    @Test public void convertsReturnedNilToNull() {
        Number nativeValue = new Long(0);
        assertNull(converter.fromNative(nativeValue, null));
    }



}
