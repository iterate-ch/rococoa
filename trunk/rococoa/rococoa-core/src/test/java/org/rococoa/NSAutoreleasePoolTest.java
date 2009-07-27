package org.rococoa;

import java.lang.ref.WeakReference;

import org.junit.Test;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.test.RococoaTestCase;


public class NSAutoreleasePoolTest {

    @Test public void drainDoesntLeadToCrashInFinalize() {
	NSAutoreleasePool pool = NSAutoreleasePool.new_();
	RococoaTestCase.assertRetainCount(1, pool);
	pool.drain();
	pool.release();
	Foundation.cfRelease(pool.id());
	RococoaTestCase.assertRetainCount(1, pool);
	
        // wait until object has been GC'd
        WeakReference<Object> reference = new WeakReference<Object>(pool);
        pool = null;
        while (reference.get() != null) {
            RococoaTestCase.gc();
        }
        
        for (int i = 0; i < 20; i++) {
            RococoaTestCase.gc();
        }
    }
    
}
