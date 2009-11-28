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

import org.junit.After;
import org.junit.Test;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.test.RococoaTestCase;

/**
 * NSAutoreleasePool's behaviour wrt retain counts is interesting. 
 *
 * Mike Swingler - "NSAutoreleasePools are magical, and are not actually 
 * allocated or released objects (though you get a point(er) to an id that does 
 * respond to selectors...but that's no different than an @"some string" constant."
 *
 * This test documents the observed behaviour.
 */
public class NSAutoreleasePoolTest {

    private NSAutoreleasePool pool;
    private final ID idString;
    
    
    @Test public void nothingAffectsReferenceCount() {
        RococoaTestCase.assertRetainCount(1, pool);
        pool.drain();
        RococoaTestCase.assertRetainCount(1, pool);
        pool.release();
        RococoaTestCase.assertRetainCount(1, pool);
        Foundation.cfRelease(pool.id());
        RococoaTestCase.assertRetainCount(1, pool);
    }
    
    public NSAutoreleasePoolTest() {
        idString = Foundation.cfString("test");
        pool = NSAutoreleasePool.new_();
	Foundation.cfRetain(idString); // keep from being dealloc'd
        Foundation.sendReturnsID(idString, "autorelease");
        RococoaTestCase.assertRetainCount(2, idString); 
    }
    
    @After public void tearDown() {
	Foundation.cfRelease(idString);
    }

    @Test public void drainReleasesContents() {	
	pool.drain();
	RococoaTestCase.assertRetainCount(1, idString);
    }

    @Test public void releaseReleasesContents() {	
	pool.release();
	RococoaTestCase.assertRetainCount(1, idString);
    }
    
    @Test public void cfReleaseReleasesContents() {	
	Foundation.cfRelease(pool.id());
	RococoaTestCase.assertRetainCount(1, idString);
    }
    
    @Test public void drainOnAnotherThreadDoesntRelease() throws InterruptedException {
        Thread thread = new Thread("test") {
            public void run() {
                pool.drain();
            }};
        thread.start();
        thread.join();
        RococoaTestCase.assertRetainCount(2, idString);
    }
}
