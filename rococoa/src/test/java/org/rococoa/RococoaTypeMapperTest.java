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

import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.IDByReference;
import org.rococoa.NSObjectByReference;
import org.rococoa.cocoa.NSNumber;
import org.rococoa.internal.RococoaTypeMapper;

import com.sun.jna.FromNativeConverter;
import com.sun.jna.NativeLong;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeMapper;

@SuppressWarnings({ "nls", "unchecked", "cast" })
public class RococoaTypeMapperTest extends RococoaTestCase {
    
    private static Class<? extends Number> primitiveTypeOfNativeLong = 
        (Class<? extends Number>) new NativeLong().nativeType();
    
    private TypeMapper typeMapper = new RococoaTypeMapper();
    
    @Override
    protected void setUp() throws Exception {
        typeMapper = new RococoaTypeMapper();
    }
    
    public void testConvertNSObjectAsArgumentToID() {
        NSNumber fortyTwo = NSNumber.CLASS.numberWithInt(45);
        ToNativeConverter toNative = typeMapper.getToNativeConverter(fortyTwo.getClass());
            // argument passing is based on actual type
        
        assertEquals(primitiveTypeOfNativeLong, toNative.nativeType());
        
        Number nativeValue = (Number) toNative.toNative(fortyTwo, null);
        assertEquals(primitiveTypeOfNativeLong, nativeValue.getClass());
        assertEquals(fortyTwo.id().intValue(), nativeValue.intValue());

        assertEquals(null, toNative.toNative(null, null));
    }
    
    public void testConvertReturnIDToNSObject() {
        NSNumber fortyTwo = NSNumber.CLASS.numberWithInt(45);
        
        FromNativeConverter fromNative = typeMapper.getFromNativeConverter(NSNumber.class);
            // returning is based on declared type
        
        assertEquals(primitiveTypeOfNativeLong, fromNative.nativeType());

        int expectedRetainCount = 2; // one for the pool, one for java
        assertEquals(expectedRetainCount, fortyTwo.retainCount()); 

        Number nativeValue;
        NSNumber converted;
        
        // We should cope with Integer on 32-bit
        if (NativeLong.SIZE == 4) {
            nativeValue = new Integer(fortyTwo.id().intValue());
            converted = (NSNumber) fromNative.fromNative(nativeValue, null);
            expectedRetainCount++; // one more now we have another java
            assertEquals(45, converted.intValue());        
            assertEquals(expectedRetainCount, fortyTwo.retainCount()); 
        }

        // and should cope with Long on 64 and 32
        nativeValue = new Long(fortyTwo.id().longValue());
        converted = (NSNumber) fromNative.fromNative(nativeValue, null);
        expectedRetainCount++; // one more now we have another java
        assertEquals(45, converted.intValue());        
        assertEquals(expectedRetainCount, fortyTwo.retainCount());

        assertEquals(null, fromNative.fromNative(null, null));
    }
    
    public void testConvertStringAsArgumentToIDofCFString() {
        ToNativeConverter toNative = typeMapper.getToNativeConverter("Hello".getClass());
            // argument passing is based on actual type
        
        assertEquals(primitiveTypeOfNativeLong, toNative.nativeType());
        
        Number nativeValue = (Number) toNative.toNative("Hello", null);
        assertEquals(primitiveTypeOfNativeLong, nativeValue.getClass());
        assertEquals("Hello", Foundation.toString(ID.fromLong(nativeValue.longValue())));

        assertEquals(null, toNative.toNative(null, null));
    }
    
    public void testConvertReturnIDToString() {
        ID helloID = Foundation.cfString("Hello");
        
        FromNativeConverter fromNative = typeMapper.getFromNativeConverter(String.class);
            // returning is based on declared type

        assertEquals(primitiveTypeOfNativeLong, fromNative.nativeType());
        
        Number nativeValue;
        String converted;
        
        // We should cope with Integer on 32-bit
        if (NativeLong.SIZE == 4) {
            nativeValue = new Integer(helloID.intValue());
            converted = (String) fromNative.fromNative(nativeValue, null);
            assertEquals("Hello", converted);
        }

        // and we should cope with Long
        nativeValue = new Long(helloID.longValue());
        converted = (String) fromNative.fromNative(nativeValue, null);
        assertEquals("Hello", converted);

        assertEquals(null, fromNative.fromNative(null, null));
    }
    
    // x'd until ObjectByReferenceConverter installed
    public void xtestPassNSObjectByReference() {
        // currently only out, not in-out
        NSObjectByReference reference = new NSObjectByReference();
        ToNativeConverter toNative = typeMapper.getToNativeConverter(reference.getClass());
        // argument passing is based on actual type

        assertEquals(IDByReference.class, toNative.nativeType());

        IDByReference nativeValue = (IDByReference) toNative.toNative(reference, null);
        assertEquals(0, nativeValue.getValue().intValue());
        
        // called code will set id
        //NSNumber number = NSNumber.CLASS.numberWithInt(42);
        
        // TODO - can't make this work without jna support
//        nativeValue.getPointer().setInt(number.id().intValue(), 0);
        
        // which our reference should see
        

//        assertEquals(null, toNative.toNative(null, null));

    }

}
