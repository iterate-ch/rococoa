package org.rococoa.internal;

import org.rococoa.Foundation;
import org.rococoa.ID;

import com.sun.jna.FromNativeContext;
import com.sun.jna.NativeMapped;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

/**
 * Converts between java.lang.String and Cocooa id, which it needs to return
 * as Integer or Long depending on platform
 */
class StringTypeConverter implements TypeConverter {
    private static final NativeMapped nativeLongConverter = new ID();

    public Class<?> nativeType() {
        // see NSObjectTypeConverter.nativeType
        return nativeLongConverter.nativeType();
    }

    // Takes an Integer or Long representing id (32 or 64 bit respectively)
    // and returns a java.lang.String
    public String fromNative(Object nativeValue, FromNativeContext context) {
        Number nativeValueAsNumber = (Number) nativeValue;
        if (nativeValueAsNumber == null)
            return null;
        ID id = ID.fromLong(nativeValueAsNumber.longValue());
        if (id.isNull())
            return null;            
        return Foundation.toString(id);
    }

    // Takes java.lang.String and returns value of an id as Integer or Long
    public Object toNative(Object value, ToNativeContext context) {
        if (value == null)
            return null;
        String valueAsString = (String) value;
        ID valueAsID = Foundation.cfString(valueAsString);
        Foundation.sendReturnsID(valueAsID, "autorelease");
        return valueAsID.toNative();
    }
}