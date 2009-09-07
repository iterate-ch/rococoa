/*
 * Copyright 2009 David Kocher
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

package org.rococoa.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.rococoa.NSObject;
import org.rococoa.cocoa.foundation.NSData;
import org.rococoa.test.RococoaTestCase;

/**
 * @version $Id:$
 */
public class NSObjectInvocationHandlerTest extends RococoaTestCase {

    /**
     * We test for the case when the init method is not able to complete the initialization
     * In such a case,, the init... method could free the receiver and return nil, indicating that
     * the requested object can’t be created.
     * <p/>
     * Therefore, Rococoa should not release such instances.
     * <p/>
     * Example:
     * NSImage *img = [NSImage alloc];
     * This instance can be released using [img release]
     * [img initWithData:nil];
     * The following would lead to a crash because the initializer above fails
     * [img release];
     * <p/>
     * NSObjectInvocationHandler therefore must make sure to not release an
     * object on finalization when there was an error in initialization and the receiver
     * is already freed according to the recommendation in the documentation.
     * <p/>
     * Summary: If nil is returned from a initializer, one must assume the object is already released
     *
     * @see org.rococoa.internal.NSObjectInvocationHandler#invokeCocoa(java.lang.reflect.Method, Object[])
     * @see "http://developer.apple.com/mac/library/documentation/Cocoa/Conceptual/ObjectiveC/Articles/ocAllocInit.html#//apple_ref/doc/uid/TP30001163-CH22-105952"
     */
    @Test
    public void testInitReturnsNil() {
        final NSImage image = NSImage.alloc();
        assertNotNull("Allocation must return valid reference", image);
        assertNull("Expected init to fail and return nil", image.initWithData(null));
        // We shall not crash after garbage collection when the NSObjectInvocationHandler is finalized
        gc();
    }

    public static abstract class NSImage implements NSObject {
        private static final _Class CLASS = org.rococoa.Rococoa.createClass("NSImage", _Class.class);

        public static NSImage alloc() {
            return CLASS.alloc();
        }

        public static NSImage imageNamed(String name) {
            return CLASS.imageNamed(name);
        }

        public interface _Class extends org.rococoa.NSClass {
            NSImage alloc();

            NSImage imageNamed(String name);
        }

        public abstract NSImage initWithData(NSData data);
    }
}
