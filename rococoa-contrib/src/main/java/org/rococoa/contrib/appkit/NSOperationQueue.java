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
package org.rococoa.contrib.appkit;

import org.rococoa.ObjCClass;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSInteger;
import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.cocoa.foundation.NSUInteger;

/** NSOperationQueue from Cocoa;
 *
 */
public abstract class NSOperationQueue extends NSObject {
    public static final _Class CLASS = Rococoa.createClass(NSOperationQueue.class.getSimpleName(), _Class.class); //$NON-NLS-1$
    public static final int NSOperationQueueDefaultMaxConcurrentOperationCount = -1;
    public interface _Class extends ObjCClass {
        public NSOperationQueue alloc();
        public NSOperationQueue currentQueue();
        public NSOperationQueue mainQueue();
    }

    public abstract NSOperationQueue init();
    public abstract void addOperation(NSOperation operation);
    public abstract void addOperations_waitUntilFinished(NSArray ops, boolean wait);
    public abstract void cancelAllOperations();
    public abstract boolean isSuspended();
    public abstract NSInteger maxConcurrentOperationCount();
    public abstract String name();
    public abstract NSUInteger operationCount();
    public abstract NSArray operations();
    public abstract void setMaxConcurrentOperationCount(NSInteger count);
    public abstract void setName(String name);
    public abstract void setSuspended(boolean suspend);
    public abstract void waitUntilAllOperationsAreFinished();
}
