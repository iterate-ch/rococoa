package org.rococoa.cocoa;

import org.rococoa.NSTestCase;
import org.rococoa.Rococoa;

public class NSArrayTest extends NSTestCase {

    public void test() {
        NSArray array = NSArray.CLASS.arrayWithObjects(
                NSNumber.CLASS.numberWithInt(42),
                NSNumber.CLASS.numberWithInt(64)
        );
        assertEquals(2, array.count());
        NSNumber second = Rococoa.cast(array.objectAtIndex(1), NSNumber.class);
        assertEquals(64, second.intValue());
    }
    
}
