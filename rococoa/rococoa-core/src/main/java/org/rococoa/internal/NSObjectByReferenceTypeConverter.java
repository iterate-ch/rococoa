/**
 * 
 */
package org.rococoa.internal;

import org.rococoa.IDByReference;
import org.rococoa.NSObject;

import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

/**
 * Work in progress. I think that this needs a bit of JNA help to get off the ground.
 */
class NSObjectByReferenceTypeConverter<T extends NSObject> implements TypeConverter {
    

    public Object fromNative(Object nativeValue, FromNativeContext context) {
	throw new UnsupportedOperationException();
    }

    public Class<?> nativeType() {
	return IDByReference.class;
    }

    public Object toNative(Object value, ToNativeContext context) {
	if (value == null)
	    return null;
	return new IDByReference();
    }
    
    
//    // x'd until ObjectByReferenceConverter installed
//    public void xtestPassNSObjectByReference() {
//        // currently only out, not in-out
//        NSObjectByReference reference = new NSObjectByReference();
//        ToNativeConverter toNative = typeMapper.getToNativeConverter(reference.getClass());
//        // argument passing is based on actual type
//
//        assertEquals(IDByReference.class, toNative.nativeType());
//
//        IDByReference nativeValue = (IDByReference) toNative.toNative(reference, null);
//        assertEquals(0, nativeValue.getValue().intValue());
//        
//        // called code will set id
//        //NSNumber number = NSNumber.CLASS.numberWithInt(42);
//        
//        // TODO - can't make this work without jna support
//        nativeValue.getPointer().setInt(number.id().intValue(), 0);
//        
//        // which our reference should see
//        
//
//        assertEquals(null, toNative.toNative(null, null));
//
//    }

}