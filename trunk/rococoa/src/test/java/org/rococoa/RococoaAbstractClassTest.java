package org.rococoa;



public class RococoaAbstractClassTest extends NSTestCase {
    
public static abstract class NSNumberAsClass implements NSObject {
    
    public static final _Class CLASS = Rococoa.createClass("NSNumber", _Class.class); //$NON-NLS-1$
    public interface _Class extends NSClass {
        public NSNumberAsClass numberWithInt(int value);
    }
    
    public static NSNumberAsClass numberWithInt(int value) {
        return CLASS.numberWithInt(value);
    }

    public abstract int intValue();
    
    public int twice() {
        return 2 * intValue();
    }

}
    public void test() {
        NSNumberAsClass number = NSNumberAsClass.numberWithInt(42);        
        assertEquals(42, number.intValue());
        assertEquals(84, number.twice());
    }
    
    public void testCGLibResusesClasses() {
        NSNumberAsClass number = NSNumberAsClass.numberWithInt(42);        
        NSNumberAsClass number2 = NSNumberAsClass.numberWithInt(42);
        assertSame(number.getClass(), number2.getClass());
    }

}
