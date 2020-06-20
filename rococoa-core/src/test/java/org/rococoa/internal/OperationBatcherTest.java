/*
 * Copyright 2007, 2008, 2009 Duncan McGregor
 *
 * This file is part of Rococoa, a library to allow Java to talk to Cocoa.
 *
 * Rococoa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rococoa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Rococoa.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.rococoa.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
