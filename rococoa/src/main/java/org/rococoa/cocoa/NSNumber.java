/**
 * 
 */
package org.rococoa.cocoa;

import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;


public interface NSNumber extends NSObject {
    public static final _Class CLASS = Rococoa.createClass("NSNumber", _Class.class); //$NON-NLS-1$
    public interface _Class extends NSClass {
        public NSNumber numberWithBool(boolean value);
        public NSNumber numberWithInt(int value);
        public NSNumber numberWithDouble(double e);
        public NSNumber numberWithLong(long value);
    }
    
    public int intValue();
    public long longValue();
    public float floatValue();
    public double doubleValue();
    public int compare(NSNumber another);
}