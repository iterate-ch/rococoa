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

import org.rococoa.cocoa.NSNumber;

import com.sun.jna.FromNativeConverter;
import com.sun.jna.ToNativeConverter;
import com.sun.jna.TypeMapper;

@SuppressWarnings("nls")
public class RococoaTypeMapperTest extends NSTestCase {
    
    private TypeMapper typeMapper = new RococoaTypeMapper();
    
    public void testConvertNSObjectAsArgumentToID() {
        NSNumber fortyTwo = NSNumber.CLASS.numberWithInt(45);
        ToNativeConverter toNative = typeMapper.getToNativeConverter(fortyTwo.getClass());
            // argument passing is based on actual type
        
        assertEquals(Integer.class, toNative.nativeType());
        
        Integer nativeValue = (Integer) toNative.toNative(fortyTwo, null);
        assertEquals(fortyTwo.id().intValue(), nativeValue.intValue());

        assertEquals(null, toNative.toNative(null, null));
    }
    
    public void testConvertReturnIDToNSObject() {
        NSNumber fortyTwo = NSNumber.CLASS.numberWithInt(45);
        
        FromNativeConverter fromNative = typeMapper.getFromNativeConverter(NSNumber.class);
            // returning is based on declared type
        
        assertEquals(Integer.class, fromNative.nativeType());

        assertEquals(2, fortyTwo.retainCount()); // one for the pool, one for java
        Integer nativeValue = new Integer(fortyTwo.id().intValue());
        NSNumber converted = (NSNumber) fromNative.fromNative(nativeValue, null);
        assertEquals(45, converted.intValue());        
        assertEquals(3, fortyTwo.retainCount()); // one more now we have another java

        assertEquals(null, fromNative.fromNative(null, null));
    }
    
    public void testConvertStringAsArgumentToIDofCFString() {
        ToNativeConverter toNative = typeMapper.getToNativeConverter("Hello".getClass());
            // argument passing is based on actual type
        
        assertEquals(Integer.class, toNative.nativeType());
        
        Integer nativeValue = (Integer) toNative.toNative("Hello", null);
        assertEquals("Hello", Foundation.toString(new ID(nativeValue)));

        assertEquals(null, toNative.toNative(null, null));
    }
    
    public void testConvertReturnIDToString() {
        ID helloID = Foundation.cfString("Hello");
        
        FromNativeConverter fromNative = typeMapper.getFromNativeConverter(String.class);
            // returning is based on declared type

        assertEquals(Integer.class, fromNative.nativeType());
        
        String converted = (String) fromNative.fromNative(new Integer(helloID.intValue()), null);
        assertEquals("Hello", converted);

        assertEquals(null, fromNative.fromNative(null, null));
    }
    
    public void testPassNSObjectByReference() {
        // currently only out, not in-out
        NSObjectByReference reference = new NSObjectByReference();
        ToNativeConverter toNative = typeMapper.getToNativeConverter(reference.getClass());
        // argument passing is based on actual type

        assertEquals(IDByReference.class, toNative.nativeType());

        IDByReference nativeValue = (IDByReference) toNative.toNative(reference, null);
        assertEquals(0, nativeValue.getValue().intValue());
        
        // called code will set id
        NSNumber number = NSNumber.CLASS.numberWithInt(42);
        
        // TODO - can't make this work without jna support
//        nativeValue.getPointer().setInt(number.id().intValue(), 0);
        
        // which our reference should see
        

//        assertEquals(null, toNative.toNative(null, null));

    }

}
