package org.rococoa.internal;

import static org.junit.Assert.*;

import org.junit.Test;


public class OperationBatcherTest {
    
    private TestBatcher batcher;
    private int operationCount;
    private int resetCount;

private class TestBatcher extends OperationBatcher {

    public TestBatcher(int batchSize) {
        super(batchSize);
    }
    
    protected void operation() {
        ++operationCount;
    }
    
    protected void reset() {
        ++resetCount;
    }
}
    
    @Test public void batchSizeOfOneAlwaysPerformsOperation() {
        batcher = new TestBatcher(1);
        assertEquals(0, operationCount);
        assertEquals(1, resetCount);

        batcher.operate();
        assertEquals(1, operationCount);
        assertEquals(2, resetCount);
        
        batcher.operate();
        assertEquals(2, operationCount);
        assertEquals(3, resetCount);
    }

    @Test public void batchSizeThree() {
        batcher = new TestBatcher(3);
        assertEquals(0, operationCount);
        assertEquals(1, resetCount);
        
        batcher.operate();
        assertEquals(0, operationCount);
        assertEquals(1, resetCount);
        batcher.operate();
        assertEquals(0, operationCount);
        assertEquals(1, resetCount);
        batcher.operate();
        assertEquals(1, operationCount);
        assertEquals(2, resetCount);
        
        batcher.operate();
        assertEquals(1, operationCount);
        assertEquals(2, resetCount);
    }
    
    @Test public void closeRunsButDoesntReset() {
        batcher = new TestBatcher(3);
        assertEquals(0, operationCount);
        assertEquals(1, resetCount);
        
        batcher.operate();
        assertEquals(0, operationCount);
        assertEquals(1, resetCount);

        batcher.close();
        assertEquals(1, operationCount);
        assertEquals(1, resetCount);
        
        try {
            batcher.operate();
            fail();
        } catch (IllegalStateException xpected) {}
    }
}
