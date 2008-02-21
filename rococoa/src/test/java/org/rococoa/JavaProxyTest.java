package org.rococoa;

import org.rococoa.CallbackForOCWrapperForJavaObject;
import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.cocoa.NSNotification;
import org.rococoa.cocoa.NSNotificationCenter;
import org.rococoa.cocoa.NSString;



@SuppressWarnings("nls")
public class JavaProxyTest extends NSTestCase {
    
    public static class JavaImplementor {
        public Object arg;
    
        public void nothing() {
            arg = null;
        }
        
        public ID sayHello() {
            return Foundation.cfString("Hello");
        }
        
        public ID testTakesIDReturnsID(ID id) {
            arg = id;
            return Foundation.cfString("Hello");
        }
        
        public void takesOCObject(NSString s) {
            arg = s;            
        }
        
        public void takesInt_AndInt(int a, int b) {
            arg = new Object[] {a, b};
        }
        
        public void notify(NSNotification notification) {
            this.arg = notification;
        }
     }

    private JavaImplementor implementor;
    private CallbackForOCWrapperForJavaObject wrapper;
    private ID ocProxy;
    
    @Override
    protected void setUp() throws Exception {
        implementor = new JavaImplementor();
        wrapper = new CallbackForOCWrapperForJavaObject(implementor);
            // TODO - if we don't hold a reference to this, OC won't!
        ocProxy  = Foundation.createOCProxy(wrapper.selectorInvokedCallback, wrapper.methodSignatureCallback);
    }
    
    public void testNoArgsReturnsVoid() {
        implementor.arg = "something";
        ID result = Foundation.sendReturnsID(ocProxy, "nothing");
        assertTrue(result.isNull());
        assertNull(implementor.arg);
    }

    public void testNoArgsReturnsID() {
        ID result =  Foundation.sendReturnsID(ocProxy, "sayHello");
        assertEquals("Hello", Foundation.toString(result));        
        assertNull(implementor.arg);
    }

    public void testTakesIDReturnsID() {
        ID result =  Foundation.sendReturnsID(ocProxy, "testTakesIDReturnsID:", new ID(42));
        assertEquals("Hello", Foundation.toString(result));        
        assertEquals(new ID(42), implementor.arg);
    }
    
    public void testTakesOCObject() {
        ID result =  Foundation.sendReturnsID(ocProxy, "takesOCObject:", Foundation.cfString("hello"));
        assertTrue(result.isNull());
        assertEquals("hello", Foundation.toString(((NSString) implementor.arg).id()));
    }

    public void testTakesIntAndInt() {
        ID result =  Foundation.sendReturnsID(ocProxy, "takesInt:AndInt:", 42, -1);
        assertTrue(result.isNull());
        Object[] arg = (Object[]) implementor.arg;
        assertEquals(42, arg[0]);
        assertEquals(-1, arg[1]);
    }
    
    public void testNotifications() {
        NSNotificationCenter notificationCentre = NSNotificationCenter.CLASS.defaultCenter();
        
        notificationCentre.addObserver_selector_name_object(
                ocProxy, 
                Foundation.selector("notify:"),
                "MyNotification",
                null);

        NSNotification notification = NSNotification.CLASS.notificationWithName_object("MyNotification", null);
        
        assertNull(implementor.arg);
        notificationCentre.postNotification(notification);             
        assertEquals(notification, implementor.arg);
    }

    
}
