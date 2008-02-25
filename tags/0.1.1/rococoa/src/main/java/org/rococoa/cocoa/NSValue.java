package org.rococoa.cocoa;

import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;

import com.sun.jna.Structure;

public interface NSValue extends NSObject {
    
    public static final _Class CLASS = Rococoa.createClass("NSValue", _Class.class);  //$NON-NLS-1$
    public interface _Class extends NSClass {        
        NSValue valueWithSize(NSSize size);    
    }
    
    NSSize sizeValue();
    void getValue(Structure p);
    
}
