package org.rococoa.cocoa;

import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;

public interface NSData extends NSObject {
    public static final _Class CLASS = Rococoa.createClass("NSData", _Class.class);  //$NON-NLS-1$
    public interface _Class extends NSClass {
        NSData dataWithBytes_length(byte[] bytes, int length);
    }
    
    int length();
    void getBytes(byte[] bytes);
    void getBytes_length(byte[] bytes, int length);
    
}
