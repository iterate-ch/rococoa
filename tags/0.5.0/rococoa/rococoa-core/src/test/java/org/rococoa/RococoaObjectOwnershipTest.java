package org.rococoa;

import static org.junit.Assert.assertSame;

import java.lang.ref.WeakReference;

import org.junit.Test;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.cocoa.foundation.NSDate;
import org.rococoa.test.RococoaTestCase;

public class RococoaObjectOwnershipTest extends RococoaTestCase {
    
    public static boolean shouldBeInPool = true;
    public static boolean shouldNotBeInPool = false;
	
    @Test public void directFactoryMethodsReturnsYieldsPooledObject() {
	// TODO - I've seen this fail with a retain count of 3. I wonder whether
	// there is some aggressive instance sharing going on with NSDate
        check(shouldBeInPool, 
            new Factory() {
                public NSDate create() {
                    return Rococoa.create("NSDate", NSDate.class, "dateWithTimeIntervalSince1970:", 0.0);
                }});
    }
    
    @Test public void factoryMethodOnClassYieldsPooledObject() {
	// TODO - see above
        check(shouldBeInPool, 
            new Factory() {
                public NSDate create() {
                    return NSDate.CLASS.dateWithTimeIntervalSince1970(0.0);
                }});
    }
    
    @Test public void createYieldsNonPooledObject() {
        check(shouldNotBeInPool, 
            new Factory() {
                public NSDate create() {
                    return Rococoa.create("NSDate", NSDate.class);
                }});
    }
    
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
