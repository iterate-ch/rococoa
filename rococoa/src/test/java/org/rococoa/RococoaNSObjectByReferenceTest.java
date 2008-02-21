package org.rococoa;

import org.rococoa.cocoa.NSNumber;

@SuppressWarnings("nls")
public class RococoaNSObjectByReferenceTest extends NSTestCase {
    
    private interface TestShunt extends NSObject {
        void testNSNumberByReference_with(NSObjectByReference reference, int value);
    };
    
    public void test() {
        TestShunt shunt = Rococoa.create("TestShunt", TestShunt.class);
        NSObjectByReference reference = new NSObjectByReference();
        shunt.testNSNumberByReference_with(reference, 42);
        NSNumber value = reference.getValueAs(NSNumber.class);
        assertEquals(42, value.intValue());
                
        // we better have retained the result by the time it gets back
        assertEquals(3, value.retainCount());
        Foundation.releasePool(pool);
        assertEquals(2, value.retainCount());
    }

}
