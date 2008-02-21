package org.rococoa.cocoa;

import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;

public interface NSArray extends NSObject {
    
    public static final _Class CLASS = Rococoa.createClass("NSArray", _Class.class);  //$NON-NLS-1$
    public interface _Class extends NSClass {
        NSArray arrayWithObjects(NSObject...objects);
    }
    
    int count();

    NSObject objectAtIndex(int zeroOffsetIndex);
    
}
