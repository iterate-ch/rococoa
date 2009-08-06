package org.rococoa.internal;

/**
 * Batches calls to operate(), calling operation() once per batchsize.
 * 
 * @author duncan
 */
public abstract class OperationBatcher {
    private final int batchSize;
    private int count;
    private boolean closed;

    public OperationBatcher(int batchSize) {
        this.batchSize = batchSize;
        reset();
    }

    public void operate() {
        if (closed)
            throw new IllegalStateException("Batcher closed");
        if (++count < batchSize) 
            return;
        operation();
        reset();
        count = 0;
    }

    public void close() {
        if (closed)
            throw new IllegalStateException("Batcher closed");
        operation();
        closed = true;
    }

    protected abstract void operation();
    
    protected abstract void reset();
}
