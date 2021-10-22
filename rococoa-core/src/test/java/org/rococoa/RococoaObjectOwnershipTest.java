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

package org.rococoa;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.ref.WeakReference;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.cocoa.foundation.NSDate;
import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.test.RococoaTestCase;

public class RococoaObjectOwnershipTest extends RococoaTestCase {

    public static boolean shouldBeInPool = true;
    public static boolean shouldNotBeInPool = false;
	
    @Disabled("by vavi because of error")
    @Test public void directFactoryMethodsReturnsYieldsPooledObject() {
	// TODO - I've seen this fail with a retain count of 3. I wonder whether
	// there is some aggressive instance sharing going on with NSDate
        check(shouldBeInPool, 
            new Factory() {
                public NSDate create() {
                    return Rococoa.create("NSDate", NSDate.class, "dateWithTimeIntervalSince1970:", 0.0);
                }});
    }

    @Disabled("by vavi")
    @Test public void factoryMethodOnClassYieldsPooledObject() {
	// TODO - see above
        check(shouldBeInPool, 
            new Factory() {
                public NSDate create() {
                    return NSDate.CLASS.dateWithTimeIntervalSince1970(0.0);
                }});
    }

    @Disabled("by vavi")
    @Test public void createYieldsNonPooledObject() {
        check(shouldNotBeInPool, 
            new Factory() {
                public NSDate create() {
                    return Rococoa.create("NSDate", NSDate.class);
                }});
    }

    @Disabled("by vavi")
    @Test public void newYieldsNonPooledObject() {
        // calling new on an NSClass results in a NOT autorelease'd object
        check(shouldNotBeInPool, 
            new Factory() {
                public NSDate create() {
                    return Rococoa.create("NSDate", NSDate.class, "new");
                }});
    }

    @Test public void allocYieldsNonPooledObject() {
        // calling alloc on an NSClass results in a NOT autorelease'd object
        check(shouldNotBeInPool, 
            new Factory() {
                public NSObject create() {
                    // NSDate.alloc fails as it is an Umbrella class
                    return Rococoa.create("NSObject", NSObject.class, "alloc");
                }});
    }

    private static interface Factory {
        NSObject create();
    }

    private void check(boolean expectedAutorelease, Factory factory) {
        int expectedInitialRetainCount = expectedAutorelease ? 2 : 1;
        // that will decrease the count IF it was pooled
        int expectedFinalRetainCount = expectedAutorelease ?
            expectedInitialRetainCount - 1 : expectedInitialRetainCount;

        NSAutoreleasePool pool = NSAutoreleasePool.new_();

        NSObject object = factory.create();        
        assertRetainCount(expectedInitialRetainCount, object);

        // aliasing should increase the retain count, as the alias also owns it
        NSObject alias = Rococoa.cast(object, NSObject.class);
        assertSame(object.id(), alias.id());
        assertRetainCount(expectedInitialRetainCount + 1, object);
        assertRetainCount(expectedInitialRetainCount + 1, alias);

        // wait until object has been GC'd
        WeakReference<Object> reference = new WeakReference<>(object);
        object = null;
        while (reference.get() != null) {
            gc();
        }
        gc();

        // it should now have been release'd
        assertRetainCount(expectedInitialRetainCount, alias);

        // now let the pool go
        pool.drain();

        assertRetainCount(expectedFinalRetainCount, alias);
    }

}
