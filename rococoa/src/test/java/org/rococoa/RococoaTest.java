package org.rococoa;

import org.rococoa.cocoa.NSArray;
import org.rococoa.cocoa.NSDate;
import org.rococoa.cocoa.NSNumber;
import org.rococoa.cocoa.NSString;


@SuppressWarnings("nls")
public class RococoaTest extends NSTestCase {
        
    public void testCreate() {
        NSNumber fortyTwo = NSNumber.CLASS.numberWithInt(42);
        assertEquals(42, fortyTwo.intValue());        
    }
    
    public void testRetain() {
        ID localPool = Foundation.createPool();
        NSNumber fortyTwo = NSNumber.CLASS.numberWithInt(42);
        assertEquals(2, fortyTwo.retainCount()); // autorelease has one, OCObject has one
        NSNumber fortyTwoAlias = Rococoa.wrap(fortyTwo.id(), NSNumber.class);
        assertEquals(3, fortyTwo.retainCount()); // second OCObject now has one
        Foundation.releasePool(localPool);
        assertEquals(2, fortyTwo.retainCount()); // just our two references
        assertEquals(2, fortyTwoAlias.retainCount()); // which point to the same thing
        
        // now let one reference go and wait for finalization to reduce the retain count
        fortyTwo = null;
        
        while (fortyTwoAlias.retainCount() == 2)  {
            System.gc();
            System.gc();
            System.runFinalization();
        }
        assertEquals(1, fortyTwoAlias.retainCount());        
    }
    
    public void testEqualsWithAliases() {
        NSNumber fortyTwo = NSNumber.CLASS.numberWithInt(42);
        NSNumber fortyTwoAlias = Rococoa.wrap(fortyTwo.id(), NSNumber.class);
        NSNumber fortyThree = NSNumber.CLASS.numberWithInt(43);
        assertTrue(fortyTwo.equals(fortyTwoAlias));
        assertTrue(fortyTwoAlias.equals(fortyTwo));
        assertFalse(fortyTwo.equals(fortyThree));
        assertFalse(fortyTwo.equals(null));
    }
        
    public void testEqualsMapsToIsEqual() {
        NSString s1 = NSString.stringWithString("string");
        NSString s2 = NSString.stringWithString("STRING").lowercaseString();
        assertNotSame(s1, s2);
        assertFalse(s1.id().equals(s2.id()));
        assertEquals(s1, s2);
    }
   
    public void testReturnTypes() {
        NSNumber e = NSNumber.CLASS.numberWithDouble(Math.E);
        assertEquals(2, e.intValue());
        assertEquals(2, e.longValue());
        assertEquals((float) Math.E, e.floatValue(), 0.001);
        assertEquals(Math.E, e.doubleValue(), 0.001);
    }
    
    public void testPassOCObject() {
        NSNumber fortyTwo = NSNumber.CLASS.numberWithInt(42);    
        NSNumber e = NSNumber.CLASS.numberWithDouble(Math.E);
        
        assertEquals(-1, e.compare(fortyTwo));
        assertEquals(0, e.compare(e));
        assertEquals(1, fortyTwo.compare(e));
    }
    
    public void testStringMarshalling() {
        NSString string = NSString.CLASS.stringWithString("Hello world");
        assertTrue(string.isEqualToString("Hello world"));
        assertFalse(string.isEqualToString("Hello worldy"));
    }
    
    public void testKeywordMethod() {
        // TODO - this method doesn't actually test keyword methods any more
        NSDate epoc = NSDate.CLASS.dateWithTimeIntervalSince1970(0);
        assertEquals(0, epoc.timeIntervalSince1970(), 0.000001f);
        NSDate anotherDate = NSDate.CLASS.dateWithTimeIntervalSince1970(40d);
        assertEquals(40, anotherDate.timeIntervalSince1970(), 0.000001f);        
    }
        
    public void testVarags() {
        NSArray array = NSArray.CLASS.arrayWithObjects(
                NSNumber.CLASS.numberWithBool(true),
                NSNumber.CLASS.numberWithInt(42),
                NSDate.CLASS.dateWithTimeIntervalSince1970(666),
                null); // required by NSArray
        assertNotNull(array);
        assertFalse(array.id().isNull());
        assertEquals(3, array.count());
    }
    
    public void testFactory() {
        NSNumber._Class nsNumberClass = Rococoa.createClass("NSNumber",  NSNumber._Class.class); //$NON-NLS-1$
        assertEquals(nsNumberClass.id(), Foundation.nsClass("NSNumber"));
    }
    
    public interface OddClass extends NSClass {
        public NSObject numberWithInt(int value);
    }

    public void testDownCast() {
        // this is OK
        NSObject numberAsObject = NSNumber.CLASS.numberWithInt(42);
        assertEquals(42, ((NSNumber) numberAsObject).intValue());
        
        // but when defined return type is NSObject, we can't cast Java objects
        OddClass nsClass = Rococoa.createClass("NSNumber", OddClass.class);
        NSObject returnAsObject = nsClass.numberWithInt(42);
        try {
            ((NSNumber) returnAsObject).intValue();
            fail();
        } catch (ClassCastException expected) {}
        
        // we need to do this
        assertEquals(42, Rococoa.cast(returnAsObject, NSNumber.class).intValue());
    }
    
    public void testToString() {
        NSDate epoc = NSDate.CLASS.dateWithTimeIntervalSince1970(0);
        assertEquals("1970-01-01 01:00:00 +0100", epoc.toString());        
    }

}
