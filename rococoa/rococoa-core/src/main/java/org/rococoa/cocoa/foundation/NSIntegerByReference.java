package org.rococoa.cocoa.foundation;

import com.sun.jna.ptr.NativeLongByReference;

public class NSIntegerByReference extends NativeLongByReference {
	
    public NSIntegerByReference() {
        this(new NSInteger(0L));
    }
    
    public NSIntegerByReference(NSInteger value) {
        super(value);
    }
    
    public void setValue(NSInteger value) {
        getPointer().setNativeLong(0, value);
    }
    
    public NSInteger getValue() {
        return new NSInteger(getPointer().getNativeLong(0));
    }
    
}
