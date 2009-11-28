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

package org.rococoa;

import java.lang.ref.WeakReference;
import java.util.concurrent.CyclicBarrier;

import org.junit.Ignore;
import org.junit.Test;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.test.RococoaTestCase;

/**
 * NSAutoreleasePool's behaviour wrt threads is interesting. It seems that if 
 * the thread that the pool was created on has finished, then draining the pool
 * crashes. The tests here document the observed behaviour.
 * 
 * Our default policy is to release the pool's id in its finalizer - this is equivalent
 * to draining it, and will fail if the finalizer is only run after the thread has
 * exited. So we need a special case (currently in NSObjectInvocationHandler) to
 * not release pools in their finalizer.
 * 
 * NB - not a RococoaTestCase so we know the state of the pools
 * @author duncan
 *
 */

public class NSAutoreleasePoolThreadTest {

    static {
        RococoaTestCase.initializeLogging();        
    }
    
    // This is the test that is made pass by marking NSAutoreleasePool as ReleaseInFinalize(false)
    @Test public void garbageCollectDrainedPool() throws InterruptedException {
        Thread thread = new Thread("test") {
            public void run() {
                NSAutoreleasePool pool = NSAutoreleasePool.new_();
                pool.drain();
            }};
        thread.start();
        thread.join();
        RococoaTestCase.gc();        
    }


    @Test public void drainPoolAndFinalize() {        
        NSAutoreleasePool pool = NSAutoreleasePool.new_();
        pool.drain();
        WeakReference<Object> reference = new WeakReference<Object>(pool);
        pool = null;
        while (reference.get() != null) {
            RococoaTestCase.gc();
        }
        RococoaTestCase.gc();
    }
    
    @Test public void drainPoolAndFinalizeOnAnotherThread() throws InterruptedException {
        Thread thread = new Thread("test") {
            public void run() {
                drainPoolAndFinalize();
            }};
        thread.start();
        thread.join();
        RococoaTestCase.gc();        
    }
    
    @Ignore("crashes") @Test public void cantDrainPoolCreatedOnAFinishedThread() throws InterruptedException {
        final NSAutoreleasePool[] poolHolder = new NSAutoreleasePool[1];
        Thread thread = new Thread("test") {
            public void run() {
                poolHolder[0] = NSAutoreleasePool.new_();
            }};
        thread.start();
        thread.join();
        poolHolder[0].drain();
    }
    
    @Test public void drainPoolCreatedOnANotFinishedThread() throws InterruptedException {
        final NSAutoreleasePool[] poolHolder = new NSAutoreleasePool[1];
        final CyclicBarrier beforeDrain = new CyclicBarrier(2);
        final CyclicBarrier afterDrain = new CyclicBarrier(2);
        Thread thread = new Thread("test") {
            public void run() {
                poolHolder[0] = NSAutoreleasePool.new_();
                await(beforeDrain);
                await(afterDrain);
            }};
        thread.start();
        await(beforeDrain);
        poolHolder[0].drain();
        
        await(afterDrain);
        thread.join();
    }

    @Test public void drainAlreadyDrainedPoolCreatedOnANotFinishedThread() throws InterruptedException {
        final NSAutoreleasePool[] poolHolder = new NSAutoreleasePool[1];
        final CyclicBarrier beforeDrain = new CyclicBarrier(2);
        final CyclicBarrier afterDrain = new CyclicBarrier(2);
        Thread thread = new Thread("test") {
            public void run() {
                poolHolder[0] = NSAutoreleasePool.new_();
                poolHolder[0].drain();
                await(beforeDrain);
                await(afterDrain);
            }};
        thread.start();
        await(beforeDrain);
        poolHolder[0].drain();
        
        await(afterDrain);
        thread.join();
    }
    
    private void await(CyclicBarrier barrier) {
        try {
            barrier.await();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
}