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
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSNumber;
import org.rococoa.test.RococoaTestCase;

import com.sun.jna.FromNativeConverter;
import com.sun.jna.NativeLong;
import com.sun.jna.ToNativeConverter;

@SuppressWarnings({ "nls", "unchecked", "cast" })
public class NSObjectTypeConverterTest extends RococoaTestCase {
    
    private static Class<? extends Number> primitiveTypeOfID = 
        (Class<? extends Number>) new ID().nativeType();
        
    @Test public void convertsNSObjectAsArgumentToID() {	
	ToNativeConverter converter = new NSObjectTypeConverter(NSObject.class);
	    // We treat all NSObject's equally in toNative, see RococoaTypeMapper
        assertEquals(primitiveTypeOfID, converter.nativeType());
        
	NSObject nsObject = Rococoa.create("NSObject", NSObject.class);

        Number nativeValue = (Number) converter.toNative(nsObject, null);
        assertEquals(primitiveTypeOfID, nativeValue.getClass());
        assertEquals(nsObject.id().longValue(), nativeValue.longValue());
    }
    
    @Test public void convertsNullAsArgumentToNull() {
        // Not entirely sure about this, maybe 0 would be better than null, 
        // but JNA seems to interpret it properly
	ToNativeConverter converter = new NSObjectTypeConverter(NSObject.class);
        assertEquals(null, converter.toNative(null, null));
    }
    
    @Test public void convertsReturnedIDToNSObjectSubclass() {
        FromNativeConverter converter = new NSObjectTypeConverter(NSNumber.class);
            // returning is based on declared type, see RococoaTypeMapper

	NSNumber number = Rococoa.create("NSNumber", NSNumber.class, "numberWithInt:", 45);

        // We can cope with 64 bits on 64 and 32
        Number nativeValue = new Long(number.id().longValue());
        NSNumber converted = (NSNumber) converter.fromNative(nativeValue, null);
        assertEquals(converted.id(), number.id());
        assertEquals(45, converted.intValue());        

        // We must cope with 32 bits on 32-bit
        if (NativeLong.SIZE == 4) {
            nativeValue = new Integer(number.id().intValue());
            converted = (NSNumber) converter.fromNative(nativeValue, null);
            assertEquals(45, converted.intValue());        
        }    
    }
    
    @Test public void convertsReturnedNilToNull() {
	// Again I'm not sure that this is desirable, but it is what happens.
        FromNativeConverter converter = new NSObjectTypeConverter(NSNumber.class);
        Number nativeValue = new Long(0);
        assertNull(converter.fromNative(nativeValue, null));
    }
    
    @Test public void returnedNSObjectIsNormallyRetained() {
        FromNativeConverter converter = new NSObjectTypeConverter(NSNumber.class);

	NSNumber number = Rococoa.create("NSNumber", NSNumber.class, "numberWithInt:", 45);
	assertRetainCount(2, number); // one for the pool, one for Java

        NSNumber converted = (NSNumber) converter.fromNative(new Long(number.id().longValue()), null);
	assertRetainCount(3, converted); // now we have another Java alias
	assertRetainCount(3, number);
    }
    
    @Test public void returnedNSObjectIsNotRetainedIfMethodImpliesWeOwnIt() {
	// This is difficult to unit test, as we cannot create FunctionResultContext's.
	// Instead we step back look at the results through Foundation.send, which
	// routes through the converter.
	ID idClass = Foundation.getClass("NSObject");
	assertRetainCount(1, Foundation.send(idClass, "alloc", NSObject.class)); // not in the pool, so just one for Java
    } 
    

}
