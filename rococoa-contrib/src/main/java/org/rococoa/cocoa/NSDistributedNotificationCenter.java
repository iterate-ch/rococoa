/*
 * Copyright 2007, 2008, 2009 Duncan McGregor
 *
 * This file is part of Rococoa, a library to allow Java to talk to Cocoa.
 *
 * Rococoa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rococoa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Rococoa.  If not, see <http://www.gnu.org/licenses/>.
 */

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

