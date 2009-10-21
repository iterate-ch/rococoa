package org.rococoa.cocoa.foundation;

import com.sun.jna.ptr.NativeLongByReference;

public class NSUIntegerByReference extends NativeLongByReference {
	
	public NSUIntegerByReference() {
        this(new NSUInteger(0L));
    }
    
    public NSUIntegerByReference(NSUInteger value) {
        super(value);
    }
    
    public void setValue(NSUInteger value) {
        getPointer().setNativeLong(0, value);
    }
    
    public NSUInteger getValue() {
        return new NSUInteger(getPointer().getNativeLong(0));
    }
    
}
