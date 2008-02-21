package org.rococoa;

import com.sun.jna.FromNativeContext;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public class IDByReference extends ByReference {

    public IDByReference() {
        this(new ID(0));
    }
    
    public IDByReference(ID value) {
        super(4);
        setValue(value);
    }
    
    public void setValue(ID value) {
        getPointer().setInt(0, value.intValue());
    }
    
    public ID getValue() {
        return new ID(getPointer().getInt(0));
    }
    
    public Object fromNative(Object nativeValue, FromNativeContext context) {
        return super.fromNative(nativeValue, context);
    }
    
    /** Convert this object to its native type (a {@link Pointer}). */
    public Object toNative() {
        return super.toNative();
    }
}

