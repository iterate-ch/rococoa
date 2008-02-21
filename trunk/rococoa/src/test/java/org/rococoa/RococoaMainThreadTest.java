package org.rococoa;



@SuppressWarnings("nls")
public class RococoaMainThreadTest extends NSTestCase {
    
    private interface TestShunt extends NSObject {
        boolean isMainThread();
    };
    
    private @RunOnMainThread interface TestShuntOnMainThread extends TestShunt {};
    
    public void testNotMainThread() {
        TestShunt testShunt = Rococoa.create("TestShunt", TestShunt.class);
        assertFalse(testShunt.isMainThread());
    }

    public void testOnMainThread() {
        TestShunt testShunt = Rococoa.create("TestShunt", TestShuntOnMainThread.class);
        assertTrue(testShunt.isMainThread());

    }

}
