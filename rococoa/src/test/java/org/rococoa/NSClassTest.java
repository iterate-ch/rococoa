package org.rococoa;

import org.rococoa.cocoa.NSNumber;

@SuppressWarnings("nls")
public class NSClassTest extends NSTestCase {
    
    public void test() {
        NSNumber fortyTwo = NSNumber.CLASS.numberWithInt(42);
        ID idOfNSNumber = Foundation.nsClass("NSNumber");
        ID idOfNSString = Foundation.nsClass("NSString");
        assertTrue(fortyTwo.isKindOfClass(idOfNSNumber));
        assertFalse(fortyTwo.isKindOfClass(idOfNSString));
        
        assertTrue(fortyTwo.isKindOfClass(NSClass.CLASS.classWithName("NSNumber")));
        assertFalse(fortyTwo.isKindOfClass(NSClass.CLASS.classWithName("NSString")));
    }

}
