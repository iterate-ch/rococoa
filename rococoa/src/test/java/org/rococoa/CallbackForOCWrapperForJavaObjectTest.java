package org.rococoa;

import org.rococoa.CallbackForOCWrapperForJavaObject;
import org.rococoa.ID;
import org.rococoa.NSObject;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class CallbackForOCWrapperForJavaObjectTest extends TestCase {
    
    public static class JavaImplementor {
    
        public void returnsVoidTakesVoid() {}
        public void returnsVoidTakesInt(int i) {}      
        public ID returnsIDTakesVoid() {
            return null;
        }
        public void returnsVoidTakesInt_andInt(int arg1, int arg2) {}
        public void returnsVoidTakesOCObject(NSObject o) {}
        
     }
    
    public void testMethodForSelector() throws SecurityException, NoSuchMethodException {
        JavaImplementor implementor = new JavaImplementor();
        CallbackForOCWrapperForJavaObject callback = new CallbackForOCWrapperForJavaObject(implementor);
        
        assertEquals(
            JavaImplementor.class.getDeclaredMethod("returnsIDTakesVoid"),                
            callback.methodForSelector("returnsIDTakesVoid"));
        
        assertEquals(
            JavaImplementor.class.getDeclaredMethod("returnsVoidTakesInt", int.class),                
            callback.methodForSelector("returnsVoidTakesInt:"));
        
        assertEquals(
            JavaImplementor.class.getDeclaredMethod("returnsVoidTakesInt_andInt", int.class, int.class),
            callback.methodForSelector("returnsVoidTakesInt:andInt:"));

        assertNull(callback.methodForSelector("nosuch"));
        
        // wrong number of args
        assertNull(callback.methodForSelector("returnsVoidTakesVoid:")); 
        assertNull(callback.methodForSelector("returnsVoidTakesInt"));
    }
    
    public void testMethodSignatureForSelector() {
        JavaImplementor implementor = new JavaImplementor();
        CallbackForOCWrapperForJavaObject callback = new CallbackForOCWrapperForJavaObject(implementor);
        
        assertEquals("v@:", callback.methodSignatureForSelector("returnsVoidTakesVoid"));
        assertEquals("v@:i", callback.methodSignatureForSelector("returnsVoidTakesInt:"));
        assertEquals("@@:", callback.methodSignatureForSelector("returnsIDTakesVoid"));
        assertEquals("v@:ii", callback.methodSignatureForSelector("returnsVoidTakesInt:andInt:"));
        assertEquals("v@:@", callback.methodSignatureForSelector("returnsVoidTakesOCObject:"));
        
        assertNull(callback.methodSignatureForSelector("nosuch"));
        
    }
}
