package org.rococoa.cocoa;

import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;


public interface NSNotification extends NSObject {
    public static final _Class CLASS = Rococoa.createClass("NSNotification",  _Class.class); //$NON-NLS-1$
    public interface _Class extends NSClass {
        NSNotification notificationWithName_object(String notificationName, NSObject object);
    }
  
}
