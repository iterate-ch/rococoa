package org.rococoa;

import com.sun.jna.Callback;
import com.sun.jna.Library;

/**
 * JNA Library for special operations provided by our own native code
 */
interface RococoaLibrary extends Library {
    
    /** Callback from Obj-C to get method signature string for Java method matching selectorName */
    public interface MethodSignatureCallback extends Callback {
        String callback(String selectorName);
    }
    
    /** Callback from Obj-C to invoke method on Java object */
    public interface SelectorInvokedCallback extends Callback {
        void callback(String selectorName, ID nsInvocation);    
    }
    
    public interface VoidCallback extends Callback {
        void callback();        
    }
    
    public void callOnMainThread(RococoaLibrary.VoidCallback callback);
    
    public ID proxyForJavaObject(SelectorInvokedCallback selectorInvokedCallback, 
            MethodSignatureCallback methodSignatureCallback);
}