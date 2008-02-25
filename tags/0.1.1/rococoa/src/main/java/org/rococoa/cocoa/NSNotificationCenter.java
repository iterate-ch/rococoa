package org.rococoa.cocoa;

import org.rococoa.ID;
import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.Selector;


public interface NSNotificationCenter extends NSObject {
    public static final _Class CLASS = Rococoa.createClass("NSNotificationCenter", _Class.class); //$NON-NLS-1$
    public interface _Class extends NSClass {
        public NSNotificationCenter defaultCenter();
    }
    
    void addObserver_selector_name_object(ID notificationObserver,
            Selector notificationSelector,
            String notificationName,
            NSObject notificationSender);

    void postNotification(NSNotification notification);

}
