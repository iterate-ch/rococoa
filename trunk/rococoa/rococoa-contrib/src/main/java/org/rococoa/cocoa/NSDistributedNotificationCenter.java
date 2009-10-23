package org.rococoa.cocoa;

import org.rococoa.ObjCClass;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSDictionary;
import org.rococoa.cocoa.foundation.NSObject;

/**
 * NSDistributedNotificationCenter
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSDistributedNotificationCenter.java,v 1.0 Mar 26, 2009 5:47:51 PM haraldk Exp$
 */
public abstract class NSDistributedNotificationCenter extends NSObject {
    public static final _Class CLASS = Rococoa.createClass("NSDistributedNotificationCenter", _Class.class);  //$NON-NLS-1$

    public interface _Class extends ObjCClass {
         NSDistributedNotificationCenter defaultCenter();
     }

    public static NSDistributedNotificationCenter defaultCenter() {
        return CLASS.defaultCenter();
    }

    abstract void postNotificationName_object_userInfo_deliverImmediately(String notificationName, String notificationSender, NSDictionary userInfo, boolean deliverImmediately);

    public final void postNotification(String notificationName, String notificationSender, NSDictionary userInfo, boolean deliverImmediately) {
        postNotificationName_object_userInfo_deliverImmediately(notificationName, notificationSender, userInfo, deliverImmediately);
    }
}

