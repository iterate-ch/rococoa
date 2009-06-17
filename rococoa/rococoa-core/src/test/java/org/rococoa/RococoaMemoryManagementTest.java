package org.rococoa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.lang.ref.WeakReference;

import org.junit.Test;
import org.rococoa.cocoa.NSAutoreleasePool;
import org.rococoa.cocoa.NSDate;

public class RococoaMemoryManagementTest extends RococoaTestCase {
    
    @Test public void testClassFactoryMethod() {
        // calling a factory method directly results in an autorelease'd object
        check(true, 
            new Factory() {
                public NSDate create() {
                    return Rococoa.create("NSDate", NSDate.class, "dateWithTimeIntervalSince1970:", 0.0);
                }});
    }
    
    @Test public void testMethodOnClassObject() {
        // calling a factory method on an NSClass results in an autorelease'd object
        check(true, 
            new Factory() {
                public NSDate create() {
                    return NSDate.CLASS.dateWithTimeIntervalSince1970(0.0);
                }});
    }
    
    @Test public void testSpecialCaseForNew() {
        // calling new on an NSClass results in a NOT autorelease'd object
        check(false, 
            new Factory() {
                public NSDate create() {
                    return Rococoa.create("NSDate", NSDate.class);
                }});
    }
    
    @Test public void testSpecialCaseForNewByName() {
        // calling new on an NSClass results in a NOT autorelease'd object
        check(false, 
            new Factory() {
                public NSDate create() {
                    return Rococoa.create("NSDate", NSDate.class, "new");
                }});
    }
    
    @Test public void testSpecialCaseForAlloc() {
        // calling alloc on an NSClass results in a NOT autorelease'd object
        check(false, 
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
        int initialRetainCount = expectedAutorelease ? 2 : 1;
        
        NSAutoreleasePool pool = NSAutoreleasePool.new_();
        
        NSObject object = factory.create();        
        assertEquals(initialRetainCount, object.retainCount());
        
        // aliasing should increase the retain count, as the alias also owns it
        NSObject alias = Rococoa.cast(object, NSObject.class);
        assertSame(object.id(), alias.id());
        assertEquals(initialRetainCount + 1, object.retainCount());
        assertEquals(initialRetainCount + 1, alias.retainCount());
        
        // wait until number has been GC'd
        WeakReference<NSObject> reference = new WeakReference<NSObject>(object);
        object = null;
        while (reference.get() != null) {
            gc();
        }
        gc();
        
        // it should now have been release'd
        assertEquals(initialRetainCount, alias.retainCount());
        
        // now let the pool go
        pool.release();

        // that will decrease the count IF it was pooled
        if (expectedAutorelease)
            assertEquals(initialRetainCount - 1, alias.retainCount());
        else
            assertEquals(initialRetainCount, alias.retainCount());
    }

    private void gc() {
        System.gc();
        System.gc();
        System.runFinalization();
    }

    
}
