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

import org.junit.Test;
import org.rococoa.cocoa.foundation.*;
import org.rococoa.test.RococoaTestCase;

import java.lang.ref.WeakReference;

import static org.junit.Assert.assertSame;

public class RococoaObjectOwnershipTest extends RococoaTestCase {

    public static boolean shouldBeInPool = true;
    public static boolean shouldNotBeInPool = false;

    @Test
    public void directFactoryMethodsReturnsYieldsPooledObject() throws Exception {
        check(shouldBeInPool,
                new Factory() {
                    public NSArray create() throws Exception {
                        return Rococoa.create("NSArray", NSArray.class,
                                NSArray._Class.class.getMethod("arrayWithObjects", NSObject[].class),
                                "arrayWithObjects:", NSNumber.CLASS.numberWithInt(0));
                    }
                });
        check(shouldBeInPool,
                new Factory() {
                    public NSArray create() throws Exception {
                        return Rococoa.create("NSArray", NSArray.class,
                                "arrayWithObject:", NSNumber.CLASS.numberWithInt(0));
                    }
                });
    }

    @Test
    public void factoryMethodOnClassYieldsPooledObject() throws Exception {
        check(shouldBeInPool,
                new Factory() {
                    public NSArray create() {
                        return NSArray.CLASS.arrayWithObjects(NSNumber.CLASS.numberWithInt(0));
                    }
                });
    }

    @Test
    public void createYieldsNonPooledObject() throws Exception {
        check(shouldNotBeInPool,
                new Factory() {
                    public NSDate create() {
                        return Rococoa.create("NSDate", NSDate.class);
                    }
                });
    }

    @Test
    public void newYieldsNonPooledObject() throws Exception {
        // calling new on an NSClass results in a NOT autorelease'd object
        check(shouldNotBeInPool,
                new Factory() {
                    public NSArray create() {
                        return Rococoa.create("NSArray", NSArray.class, "new");
                    }
                });
    }

    @Test
    public void allocYieldsNonPooledObject() throws Exception {
        // calling alloc on an NSClass results in a NOT autorelease'd object
        check(shouldNotBeInPool,
                new Factory() {
                    public NSObject create() {
                        // NSArray.alloc fails as it is an Umbrella class
                        return Rococoa.create("NSObject", NSObject.class, "alloc");
                    }
                });
    }

    private interface Factory {
        NSObject create() throws Exception;
    }

    private void check(boolean expectedAutorelease, Factory factory) throws Exception {
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
        WeakReference<Object> reference = new WeakReference<Object>(object);
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
