/*
 * Copyright 2009 Duncan McGregor
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

import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSInteger;
import org.rococoa.cocoa.foundation.NSUInteger;

/** NSOperationQueue from Cocoa;
 *
 */
public interface NSOperationQueue extends NSObject {
    public static final _Class CLASS = Rococoa.createClass(NSOperationQueue.class.getSimpleName(), _Class.class); //$NON-NLS-1$
    public static final int NSOperationQueueDefaultMaxConcurrentOperationCount = -1;
    public interface _Class extends NSClass {
        public NSOperationQueue alloc();
        public NSOperationQueue currentQueue();
        public NSOperationQueue mainQueue();
    }

    NSOperationQueue init();
    void addOperation(NSOperation operation);
    void addOperations_waitUntilFinished(NSArray ops, boolean wait);
    void cancelAllOperations();
    boolean isSuspended();
    NSInteger maxConcurrentOperationCount();
    String name();
    NSUInteger operationCount();
    NSArray operations();
    void setMaxConcurrentOperationCount(NSInteger count);
    void setName(String name);
    void setSuspended(boolean suspend);
    void waitUntilAllOperationsAreFinished();
}
