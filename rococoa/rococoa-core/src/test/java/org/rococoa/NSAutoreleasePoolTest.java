package org.rococoa;

import java.lang.ref.WeakReference;

import org.junit.After;
import org.junit.Test;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.test.RococoaTestCase;


public class NSAutoreleasePoolTest {

    private NSAutoreleasePool pool;
    private final ID idString;
    
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

    @Test public void finalizeDoesntRelease() {	
        WeakReference<Object> reference = new WeakReference<Object>(pool);
        pool = null;
        while (reference.get() != null) {
            RococoaTestCase.gc();
        }
        for (int i = 0; i < 10; i++) {
            RococoaTestCase.gc();
            RococoaTestCase.assertRetainCount(2, idString);
        }
    }
    
    @Test public void nothingAffectsReferenceCount() {
        RococoaTestCase.assertRetainCount(1, pool);
        pool.drain();
        RococoaTestCase.assertRetainCount(1, pool);
        pool.release();
        RococoaTestCase.assertRetainCount(1, pool);
        Foundation.cfRelease(pool.id());
        RococoaTestCase.assertRetainCount(1, pool);
    }
    
}
