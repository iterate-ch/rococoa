package org.rococoa;

import org.rococoa.cocoa.NSSize;
import org.rococoa.cocoa.NSValue;


public class StructsInObjectsTest extends NSTestCase {
    
    public void test() throws Exception {
                
        NSSize input = new NSSize(1, 3);
        NSValue value = NSValue.CLASS.valueWithSize(input);
        NSSize size = value.sizeValue();

        assertEquals(1.0, size.width, 0.0001);
        assertEquals(3.0, size.height, 0.0001);        
    }
}
