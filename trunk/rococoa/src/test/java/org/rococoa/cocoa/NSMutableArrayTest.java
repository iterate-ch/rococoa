package org.rococoa.cocoa;

import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.RococoaTestCase;

public class NSMutableArrayTest extends RococoaTestCase {

    public void test() {
        NSMutableArray array = NSMutableArray.CLASS.arrayWithCapacity(3);
        assertEquals(0, array.count());
        array.addObject(NSString.stringWithString("Hello"));
        array.addObject("Goodbye");
        assertEquals(2, array.count());
        assertEquals("(\n    Hello,\n    Goodbye\n)", array.description());
        
        NSObject first = array.objectAtIndex(0);
        assertEquals(NSString.stringWithString("Hello"), first);
        
        NSString firstAsString = Rococoa.cast(first, NSString.class);
        assertEquals("Hello", firstAsString.toString());
        assertEquals("Goodbye", 
                Rococoa.cast(array.objectAtIndex(1), NSString.class).toString());
    }   
}
