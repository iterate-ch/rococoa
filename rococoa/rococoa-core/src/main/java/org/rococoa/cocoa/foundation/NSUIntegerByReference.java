package org.rococoa.cocoa.foundation;

import org.rococoa.ID;

import com.sun.jna.FromNativeContext;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;
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
