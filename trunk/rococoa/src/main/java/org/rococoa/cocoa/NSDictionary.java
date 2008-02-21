package org.rococoa.cocoa;

import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;

public interface NSDictionary extends NSObject {
    public static final _Class CLASS = Rococoa.createClass("NSDictionary", _Class.class); //$NON-NLS-1$

    public interface _Class extends NSClass {
        NSDictionary dictionaryWithObjects_forKeys(NSArray objects, NSArray keys);
        NSDictionary dictionaryWithObjectsAndKeys(NSObject...objects);
    }
    
    int count();
    
}
