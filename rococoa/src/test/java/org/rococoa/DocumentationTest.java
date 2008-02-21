package org.rococoa;


public class DocumentationTest extends NSTestCase {

/*
To map an Objective-C class to a Java class
 
Create a Java interface extending NSObject, with the methods you want to call. 
*/    
    public interface NSNumber_ extends NSObject {
        public int intValue();
        public double doubleValue();
        public int compare(NSNumber_ another);
    }
    
/*
Add a nested interface and static field describing the static methods of the Objective-C class.
*/
    public interface NSNumber extends NSObject {
        public static final _Class CLASS = Rococoa.createClass("NSNumber", _Class.class); //$NON-NLS-1$
        public interface _Class extends NSClass {
            public NSNumber numberWithInt(int value);
            public NSNumber numberWithDouble(double e);
        }
    
        public int intValue();
        public double doubleValue();
        public int compare(NSNumber another);
    }
    
    public void test() {        
        /*
        Create by calling the factory methods on CLASS
        */    
        NSNumber fortyTwo = NSNumber.CLASS.numberWithInt(42);        

        /*
        Call methods by calling methods on the Java object
         */
        assertEquals(42, fortyTwo.intValue());
        assertEquals("42", fortyTwo.description());
    }

}
