package org.rococoa.internal;

import static org.junit.Assert.assertNull;
import static org.rococoa.test.RococoaTestCase.assertRetainCount;

import org.junit.Test;
import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.test.RococoaTestCase;


public class AutoreleaseBatcherTest {
    static {
        RococoaTestCase.initializeLogging();
    }

    private Throwable thrown;
    
    @Test public void drains() {
        AutoreleaseBatcher batcher = new AutoreleaseBatcher(1);
        ID idNSObject = Foundation.cfRetain(autoreleasedObject());
        assertRetainCount(2, idNSObject);
        
        batcher.operate();
        assertRetainCount(1, idNSObject);
    }
    
    @Test public void batches() {
        AutoreleaseBatcher batcher = new AutoreleaseBatcher(2);
        ID idNSObject = Foundation.cfRetain(autoreleasedObject());
        assertRetainCount(2, idNSObject);

        batcher.operate();
        assertRetainCount(2, idNSObject);        

        batcher.operate();
        assertRetainCount(1, idNSObject);        
    }
    
    @Test public void resets() {
        AutoreleaseBatcher batcher = new AutoreleaseBatcher(1);
        
        ID idNSObject1 = Foundation.cfRetain(autoreleasedObject());
        assertRetainCount(2, idNSObject1);
        batcher.operate();
        assertRetainCount(1, idNSObject1);        

        ID idNSObject2 = Foundation.cfRetain(autoreleasedObject());
        assertRetainCount(2, idNSObject2);
        batcher.operate();
        assertRetainCount(1, idNSObject2);        
    }
    
    @Test public void threadLocal() {
        AutoreleaseBatcher.forThread(1);
        ID idNSObject = Foundation.cfRetain(autoreleasedObject());
        assertRetainCount(2, idNSObject);
        
        AutoreleaseBatcher.forThread(1).operate();
        assertRetainCount(1, idNSObject);
    }

    @Test public void threadLocal2Threads() throws InterruptedException {
        AutoreleaseBatcher.forThread(1);
        ID idNSObject = Foundation.cfRetain(autoreleasedObject());
        assertRetainCount(2, idNSObject);
        
        Thread thread = new Thread() {
            public void run() {
                try {
                    AutoreleaseBatcher.forThread(1);
                    ID idNSObject = Foundation.cfRetain(autoreleasedObject());
                    assertRetainCount(2, idNSObject);
    
                    AutoreleaseBatcher.forThread(1).operate();
                    assertRetainCount(1, idNSObject);
                } catch (Throwable t) {
                    thrown = t;
                }
            }};
        thread.run();
        thread.join();
        
        AutoreleaseBatcher.forThread(1).operate();
        assertRetainCount(1, idNSObject);
        assertNull(thrown);
    }
    
    private ID autoreleasedObject() {
        ID idNSObject = Foundation.sendReturnsID(Foundation.getClass("NSObject"), "new");
        return Foundation.sendReturnsID(idNSObject, "autorelease");
        
    }
}
