/*
 * Copyright 2007, 2008 Duncan McGregor
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
 
package org.rococoa;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;

import org.junit.jupiter.api.Test;
import org.rococoa.test.RococoaTestCase;

@SuppressWarnings("nls")
public class FoundationMainThreadTest extends RococoaTestCase {
    
    private ID idNSThreadClass = Foundation.getClass("NSThread");
    private Selector isMainThreadSelector = Foundation.selector("isMainThread");
    
    private boolean nsThreadSaysIsMainThread() {
        return Foundation.send(idNSThreadClass, isMainThreadSelector, boolean.class);
    }
    
    @Test public void mainThreadChanges() {
        // Not sure that I understand this result
        assertFalse(nsThreadSaysIsMainThread());
        Thread t1 = Foundation.callOnMainThread(new Callable<Thread>() {
            public Thread call() throws Exception {
                assertTrue(nsThreadSaysIsMainThread());
                return Thread.currentThread();
            }});
        Thread t2 = Foundation.callOnMainThread(new Callable<Thread>() {
            public Thread call() throws Exception {
                assertTrue(nsThreadSaysIsMainThread());
                return Thread.currentThread();
            }});
        assertNotSame(t1, Thread.currentThread());
        assertNotSame(t1, t2);
        assertFalse(t1.equals(t2));
        assertSame(t1.getThreadGroup(), t2.getThreadGroup());
        assertNotSame(Thread.currentThread().getThreadGroup(), t1.getThreadGroup());
    }
    
    @Test public void testCallOnMainThreadFromMainThread() {
        // Weird 
        Thread mainThread = Foundation.callOnMainThread(new Callable<Thread> (){
            public Thread call() throws Exception {
                assertTrue(nsThreadSaysIsMainThread());
                
                Thread insideThread = Foundation.callOnMainThread(new Callable<Thread> (){
                    public Thread call() throws Exception {
                        assertTrue(nsThreadSaysIsMainThread());
                        return Thread.currentThread();
                    }});

                assertSame(Thread.currentThread(), insideThread);
                return insideThread;
            }});
        assertNotSame(mainThread, Thread.currentThread());
    }
    
    @Test public void isMainThread() {
        assertFalse(Foundation.isMainThread());
        assertTrue(Foundation.callOnMainThread(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return Foundation.isMainThread();
            }}));
    }
    
    @Test public void testCallOnMainThread() {
        Callable<Double> callable = new Callable<Double>() {
            public Double call() throws Exception {
                assertTrue(nsThreadSaysIsMainThread());
                ID clas = Foundation.getClass("NSNumber");
                ID aDouble = Foundation.sendReturnsID(clas, "numberWithDouble:", Math.E);
                Object[] args = {};
                return Foundation.send(aDouble, Foundation.selector("doubleValue"), double.class, args);
            }};

        assertEquals(Math.E, Foundation.callOnMainThread(callable), 0.001);
    }

    @Test public void callOnMainThreadPropagatesError() {
        Throwable thrown = new Error("deliberate");
        try {
            throwOnMainThreadViaCallable(thrown);
            fail();
        } catch (Error e) {
            assertSame(thrown, e);
        }
    }
    
    @Test public void callOnMainThreadPropagatesRuntimeException() {
        Throwable thrown = new RuntimeException("deliberate");
        try {
            throwOnMainThreadViaCallable(thrown);
            fail();
        } catch (RuntimeException e) {
            assertSame(thrown, e);
        }
    }

    @Test public void callOnMainThreadWrapsException() {
        Throwable thrown = new Exception("deliberate");
        try {
            throwOnMainThreadViaCallable(thrown);
            fail();
        } catch (Exception e) {
            assertSame(thrown, e.getCause());
        }
    }
    
    @Test public void runOnMainThread() {
        final double[] result = new double[1];
        Runnable runnable = new Runnable() {
            public void run() {
                assertTrue(nsThreadSaysIsMainThread());
                ID clas = Foundation.getClass("NSNumber");
                ID aDouble = Foundation.sendReturnsID(clas, "numberWithDouble:", Math.E);
                Object[] args = {};
                result[0] =  Foundation.send(aDouble, Foundation.selector("doubleValue"), double.class, args);
            }};
        Foundation.runOnMainThread(runnable);    
        assertEquals(Math.E, result[0], 0.001);        
    }
    
    @Test public void runOnMainThreadPropagatesError() {
        Throwable thrown = new Error("deliberate");
        try {
            throwOnMainThreadViaRunnable(thrown);
            fail();
        } catch (Error e) {
            assertSame(thrown, e);
        }
    }
    
    @Test public void runOnMainThreadPropagatesRuntimeException() {
        Throwable thrown = new RuntimeException("deliberate");
        try {
            throwOnMainThreadViaRunnable(thrown);
            fail();
        } catch (RuntimeException e) {
            assertSame(thrown, e);
        }
    }

    @Test public void runOnMainThreadNoWait() throws Exception {
        final Throwable[] throwable = new Throwable[1];
        final CyclicBarrier barrier = new CyclicBarrier(2);
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    assertTrue(nsThreadSaysIsMainThread());
                    barrier.await();
                } catch (Throwable t) {
                    throwable[0] = t;
                }
            }};
        Foundation.runOnMainThread(runnable, false);
        barrier.await();
        assertNull(throwable[0]);
    }
    
    @Test public void runOnMainThreadNoWaitThrows() throws Exception {
        final Throwable[] throwable = new Throwable[1];
        final CyclicBarrier barrier = new CyclicBarrier(2);
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    barrier.await();
                } catch (Throwable t) {
                    throwable[0] = t;
                }
                throw new Error("deliberate");
            }};
        Foundation.runOnMainThread(runnable, false); // Exception is just logged
        barrier.await();
    }
    
    @Test public void runOnMainThreadNoWaitWithGC() throws Exception {
        // We had a problem where the callback that JNA uses to invoke Java code
        // could be gc'd before the call had happened, if waitUntilDone == false
        
        // First block the Cocoa main thread
        final CyclicBarrier barrier1 = new CyclicBarrier(2);
        final CyclicBarrier barrier2 = new CyclicBarrier(2);
        Runnable mainThreadBlocker = new Runnable() {
            public void run() {
                try {
                    barrier1.await();
                    barrier2.await();
                } catch (Throwable t) { System.out.println(t); }             
            }};
        Foundation.runOnMainThread(mainThreadBlocker, false);
        barrier1.await();
        // Now we know the main thread is stalled inside run

        // now set up another runnable
        final CyclicBarrier barrier3 = new CyclicBarrier(2);
        Runnable actualRunnable = new Runnable() {
            public void run() {
                try {
                    barrier3.await();
                } catch (Throwable t) { System.out.println(t); }             
            }};
        Foundation.runOnMainThread(actualRunnable, false);

        // Give the internal callback a chance to be gc'd
        gc();

        // release the main thread, so that the callback can happen
        barrier2.await();
        
        // and wait for it to happen - if the internal callback has been gc'd
        // it won't be invoked and this thread will hang here
        barrier3.await();
    }
    
    private void throwOnMainThreadViaCallable(final Throwable x) {
        Callable<Double> callable = new Callable<Double>() {
            public Double call() throws Exception {
                if (x instanceof Error) throw (Error) x;
                else throw (Exception) x;
            }};
        Foundation.callOnMainThread(callable);
    }

    private void throwOnMainThreadViaRunnable(final Throwable x) {
        Runnable runnable = new Runnable() {
            public void run() {
                if (x instanceof Error) throw (Error) x;
                else throw (RuntimeException) x;
            }};
        Foundation.runOnMainThread(runnable);
    }
    
}
