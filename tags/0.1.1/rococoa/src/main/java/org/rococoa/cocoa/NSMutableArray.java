package org.rococoa.cocoa;

import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;

public interface NSMutableArray extends NSObject {

    public static final _Class CLASS = Rococoa.createClass("NSMutableArray", _Class.class);  //$NON-NLS-1$
    public interface _Class extends NSClass {
        NSMutableArray arrayWithCapacity(int numItems);
    }
    
    int count();
    void addObject(NSObject anObject);
    void addObject(String string);
    
    NSObject objectAtIndex(int index);
}
