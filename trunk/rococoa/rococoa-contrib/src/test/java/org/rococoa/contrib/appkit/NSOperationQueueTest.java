/*
 * Copyright 2009 Duncan McGregor
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

package org.rococoa.contrib.appkit;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.rococoa.Foundation;
import static org.junit.Assert.*;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSInteger;
import org.rococoa.test.RococoaTestCase;

/** Test case for mapping of NSOperationQueue.
 *
 */
public class NSOperationQueueTest extends RococoaTestCase {
    NSOperationQueue fixture;

    public NSOperationQueueTest() {
    }
    

    @Before
    public void setUp() {
        fixture = NSOperationQueue.CLASS.alloc().init();
    }

    private static class RunnableHolder {
        final boolean[] results;
        final NSObject[] runnables;
        final NSInvocationOperation[] ops;
        public RunnableHolder(int numItems) {
            this.results = new boolean[numItems];
            runnables = new NSObject[numItems];
            ops = new NSInvocationOperation[numItems];
            for(int i=0; i < ops.length; i++) {
                final int j = i;
                Runnable r = new Runnable() {
                    public void run() {
                        synchronized(results) {
                            results[j] = true;
                        }
                    }
                };
                ops[i] = NSInvocationOperation.CLASS.alloc();
                runnables[i] = Rococoa.proxy(r);
                ops[i].initWithTarget_selector_object(runnables[i].id(), Foundation.selector("run"), null);
            }
        }
        public void addOperations(NSOperationQueue queue) {
            for(NSOperation op : ops) {
                queue.addOperation(op);
            }
        }
        public void addOperationsAndWait(NSOperationQueue queue, boolean wait) {
            queue.addOperations_waitUntilFinished(NSArray.CLASS.arrayWithObjects(ops), wait);
        }
        public void checkResults() {
            List<Integer> incomplete = new ArrayList<Integer>(results.length);
            synchronized(results) {
                for(int i=0; i < results.length; i++) {
                    if (!results[i]) incomplete.add(i);
                }
            }
            assertEquals("Failed for items: " + incomplete, 0, incomplete.size());
        }
        public int firstFailure() {
            synchronized(results) {
                for(int i=0; i < results.length; i++) {
                    if (!results[i]) return i;
                }
                return results.length -1;
            }
        }
    }

    /**
     * Test of addOperation method, of class NSOperationQueue.
     */
    @Test
    public void testAddOperation() throws InterruptedException {
        RunnableHolder runnables = new RunnableHolder(250);
        runnables.addOperations(fixture);
        fixture.waitUntilAllOperationsAreFinished();
        assertEquals(0, fixture.operationCount().intValue());
        runnables.checkResults();
    }

    /**
     * Test of addOperations_waitUntilFinished method, of class NSOperationQueue.
     */
    @Test
    public void testAddOperations_waitUntilFinished() {
        RunnableHolder runnables = new RunnableHolder(250);
        runnables.addOperationsAndWait(fixture, true);
        assertEquals(0, fixture.operationCount().intValue());
        runnables.checkResults();

        //without waiting
        runnables = new RunnableHolder(250);
        runnables.addOperationsAndWait(fixture, false);
        fixture.waitUntilAllOperationsAreFinished();
        assertEquals(0, fixture.operationCount().intValue());
        runnables.checkResults();
    }
    /**
     * Test of cancelAllOperations method, of class NSOperationQueue.
     */
    @Test
    public void testCancelAllOperations() {
        int numItems = 250;
        RunnableHolder runnables = new RunnableHolder(numItems);
        runnables.addOperations(fixture);
        fixture.cancelAllOperations();
        fixture.waitUntilAllOperationsAreFinished();
        assertEquals(0, fixture.operationCount().intValue());
        int firstFailure = runnables.firstFailure();
        assertTrue("Not all should pass: " + firstFailure, 0 < firstFailure && firstFailure < numItems );
        assertTrue(runnables.ops[firstFailure].isCancelled()||runnables.ops[firstFailure].isReady());
    }

    /**
     * Test of isSuspended method, of class NSOperationQueue.
     */
    @Test
    public void testIsSuspended() { 
        assertFalse(fixture.isSuspended());
        fixture.setSuspended(true);
        assertTrue(fixture.isSuspended());
        fixture.setSuspended(false);
        assertFalse(fixture.isSuspended());
    }

    /**
     * Test of maxConcurrentOperationCount method, of class NSOperationQueue.
     */
    @Test
    public void testMaxConcurrentOperationCount() {
        assertEquals(NSOperationQueue.NSOperationQueueDefaultMaxConcurrentOperationCount, fixture.maxConcurrentOperationCount().intValue());
        fixture.setMaxConcurrentOperationCount(new NSInteger(5));
        assertEquals(new NSInteger(5), fixture.maxConcurrentOperationCount());
    }

    /**
     * Test of name method, of class NSOperationQueue.
     */
    @Test
    public void testName() {
        assertNotSame("foo", fixture.name());
        fixture.setName("foo");
        assertEquals("foo", fixture.name());
    }

    /**
     * Test of operationCount method, of class NSOperationQueue.
     */
    @Test
    public void testOperationCount() {
        int numItems = 250;
        RunnableHolder runnables = new RunnableHolder(numItems);
        runnables.addOperations(fixture);
        assertTrue(fixture.operationCount().intValue() > 0);
        fixture.waitUntilAllOperationsAreFinished();
        assertEquals(0, fixture.operationCount().intValue());
    }

    /**
     * Test of operations method, of class NSOperationQueue.
     */
    @Test
    public void testOperations() {
        int numItems = 250;
        RunnableHolder runnables = new RunnableHolder(numItems);
        runnables.addOperations(fixture);
        assertTrue("Should have some operations", fixture.operations().count() > 0);
        fixture.waitUntilAllOperationsAreFinished();
        assertTrue("Should have completed all operations", fixture.operations().count() == 0);
    }
}