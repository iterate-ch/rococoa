/**
 * 
 */
package org.rococoa.cocoa;

import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;


public interface NSDate extends NSObject {
    public static final _Class CLASS = Rococoa.createClass("NSDate",  _Class.class); //$NON-NLS-1$
    public interface _Class extends NSClass {
        public NSDate dateWithTimeIntervalSince1970(double d);
    }

    double timeIntervalSince1970();

    String description();
}