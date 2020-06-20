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
import org.rococoa.cocoa.foundation.NSObject;

/** NSOperation from Cocoa.
 *
 */
public abstract class NSOperation extends NSObject {
    public static final _Class CLASS = Rococoa.createClass(NSOperation.class.getSimpleName(), _Class.class); //$NON-NLS-1$
    public interface _Class extends ObjCClass {
        public NSOperation alloc();
    }

    public abstract NSOperation init();
    public abstract void start();
    public abstract void main();

    public abstract void cancel();
    public abstract void waitUntilFinished();

    public abstract boolean isCancelled();
    public abstract boolean isExecuting();
    public abstract boolean isFinished();
    public abstract boolean isConcurrent();
    public abstract boolean isReady();
}
