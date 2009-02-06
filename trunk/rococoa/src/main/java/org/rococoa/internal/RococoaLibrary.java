package org.rococoa.internal;


import org.rococoa.ID;

import com.sun.jna.Callback;
import com.sun.jna.Library;

/**
 * JNA Library for special operations provided by our own native code
 */
public interface RococoaLibrary extends Library {
    
    /** 
     * Callback from Obj-C to get method signature string for Java method matching selectorName 
     */
    public interface MethodSignatureCallback extends Callback {
        String callback(String selectorName);
    }
    
    /**
     *  Callback from Obj-C to invoke method on Java object 
     */
    public interface SelectorInvokedCallback extends Callback {
        void callback(String selectorName, ID nsInvocation);    
    }

    /**
     * Return an Obj-C object that will callback on methodSignature required and
     * selector invoked, so that we can use a Java object to implement.
     */
    public ID proxyForJavaObject(SelectorInvokedCallback selectorInvokedCallback, 
            MethodSignatureCallback methodSignatureCallback);

    /**
     * Generic callback from Obj-C
     */
    public interface VoidCallback extends Callback {
        void callback();        
    }
    
    /**
     * Call callback on the main Cococa event thread
     */
    public void callOnMainThread(RococoaLibrary.VoidCallback callback);
    
}