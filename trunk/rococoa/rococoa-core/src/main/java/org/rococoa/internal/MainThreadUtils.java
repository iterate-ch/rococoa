package org.rococoa.internal;

import java.util.concurrent.Callable;

import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.Selector;

import com.sun.jna.Native;

public abstract class MainThreadUtils {
    
    private static final RococoaLibrary rococoaLibrary;
    static {
        rococoaLibrary = (RococoaLibrary) Native.loadLibrary("rococoa", RococoaLibrary.class);        
    }

    private static final ID idNSThreadClass = Foundation.getClass("NSThread");
    private static final Selector isMainThreadSelector = Foundation.selector("isMainThread");
    
    private static final ThreadLocal<Boolean> isMainThreadThreadLocal = new ThreadLocal<Boolean>();
    
    /**
     * Return the result of calling callable on the main Cococoa thread.
     */
    @SuppressWarnings("unchecked")
    public static <T> T callOnMainThread(final Callable<T> callable) {
        final Object[] result = new Object[1];
        final Throwable[] thrown = new Throwable[1];
        RococoaLibrary.VoidCallback callback = new RococoaLibrary.VoidCallback() {
            public void callback() {
                try {
                    result[0] = callable.call();
                } catch (Throwable t) {
                    thrown[0] = t;
                }
            }};
            
        rococoaLibrary.callOnMainThread(callback);
        if (thrown[0] instanceof Error)
            throw (Error) thrown[0];
        if (thrown[0] != null)
            throw new RuntimeException(thrown[0]);
        return (T) result[0];        
    }
    
    /**
     * Run runnable on the main Cococoa thread.
     */
    public static void runOnMainThread(final Runnable runnable) {
        final Throwable[] thrown = new Throwable[1];
        RococoaLibrary.VoidCallback callback = new RococoaLibrary.VoidCallback() {
            public void callback() {
                try {
                    runnable.run();
                } catch (Throwable t) {
                    thrown[0] = t;
                }
            }};
            
        rococoaLibrary.callOnMainThread(callback);
        if (thrown[0] instanceof Error)
            throw (Error) thrown[0];
        if (thrown[0] != null)
            throw new RuntimeException(thrown[0]);
    }

    public static boolean isMainThread() {
        Boolean cached = isMainThreadThreadLocal.get();
        if (cached == null) {
            cached = nsThreadSaysIsMainThread();
            isMainThreadThreadLocal.set(cached);
        }
        return cached;
    }
    
    private static boolean nsThreadSaysIsMainThread() {
        return Foundation.send(idNSThreadClass, isMainThreadSelector, boolean.class);
    }

}
