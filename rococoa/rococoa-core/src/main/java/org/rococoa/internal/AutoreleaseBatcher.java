package org.rococoa.internal;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used by NSObjectInvocationHandler to make sure that there is an NSAutoreleasePool
 * available when NSObject's are finalized, but not pay the price of creating one
 * per call.
 * 
 * Take care, this is unproven code.
 * 
 * @author duncan
 *
 */
public class AutoreleaseBatcher extends OperationBatcher {
    
    private static Logger logging = LoggerFactory.getLogger("org.rococoa");

    private static final ThreadLocal<AutoreleaseBatcher> threadLocal = new ThreadLocal<AutoreleaseBatcher>();

    private NSAutoreleasePool pool;

    public static AutoreleaseBatcher forThread(int batchSize) {
        if (threadLocal.get() == null)
            threadLocal.set(new AutoreleaseBatcher(batchSize));
        return threadLocal.get();
    }
    
    public AutoreleaseBatcher(int batchSize) {
        super(batchSize);
    }

    @Override
    protected void operation() {
        if (logging.isDebugEnabled()) {
            logging.debug("Draining autorelease pool");
        }
        pool.drain();        
    }

    @Override
    protected void reset() {
        pool = NSAutoreleasePool.new_();
    }

}
