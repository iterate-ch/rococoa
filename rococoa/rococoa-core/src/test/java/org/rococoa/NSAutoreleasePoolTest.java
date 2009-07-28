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
