package org.rococoa.internal;

import java.util.concurrent.Callable;

import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.RococoaException;
import org.rococoa.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exists just to tidy up Foundation.
 * 
 * @author duncan
 *
 */
public abstract class MainThreadUtils {
    private static Logger logging = LoggerFactory.getLogger("org.rococoa.foundation");
    
    private static final ID idNSThreadClass = Foundation.getClass("NSThread");
    private static final Selector isMainThreadSelector = Foundation.selector("isMainThread");
    
    private static final ThreadLocal<Boolean> isMainThreadThreadLocal = new ThreadLocal<Boolean>() {
        protected Boolean initialValue() {
            return nsThreadSaysIsMainThread();
        }
    };
    
    /**
     * Return the result of calling callable on the main Cococoa thread.
     */
    @SuppressWarnings("unchecked")
    public static <T> T callOnMainThread(RococoaLibrary rococoaLibrary, final Callable<T> callable) {
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
            
        rococoaLibrary.callOnMainThread(callback, true);
        rethrow(thrown[0]);
        return (T) result[0];        
    }
    
    /**
     * Run runnable on the main Cococoa thread.
     * @param wait 
     */
    public static void runOnMainThread(RococoaLibrary rococoaLibrary, final Runnable runnable, final boolean waitUntilDone) {
        final Throwable[] thrown = new Throwable[1];
        RococoaLibrary.VoidCallback callback = new RococoaLibrary.VoidCallback() {
            public void callback() {
                try {
                    runnable.run();
                } catch (Throwable t) {
                    if (waitUntilDone)
                        thrown[0] = t;
                    else
                        logging.error("Lost exception on main thread", t);
                }
            }};
            
        rococoaLibrary.callOnMainThread(callback, waitUntilDone);
        rethrow(thrown[0]);
    }

    public static boolean isMainThread() {
        return isMainThreadThreadLocal.get();
    }
    
    private static boolean nsThreadSaysIsMainThread() {
        return Foundation.send(idNSThreadClass, isMainThreadSelector, boolean.class);
    }
    
    private static void rethrow(Throwable t) {
        if (t == null)
            return;
        if (t instanceof Error)
            throw (Error) t;
        if (t instanceof RuntimeException)
            throw (RuntimeException) t;
        throw new RococoaException(t);
    }

}
