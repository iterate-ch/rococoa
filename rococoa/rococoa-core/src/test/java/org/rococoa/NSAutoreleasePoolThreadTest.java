package org.rococoa;

import java.lang.ref.WeakReference;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.junit.Ignore;
import org.junit.Test;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.test.RococoaTestCase;

// Not a RococoaTestCase so we know the state of the pools
public class NSAutoreleasePoolThreadTest {

    static {
        RococoaTestCase.initializeLogging();        
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

    @Ignore("crashes") @Test public void cantDrainPoolOnAnotherThreadAndFinalize() throws InterruptedException {
        Thread thread = new Thread("test") {
            public void run() {
                NSAutoreleasePool pool = NSAutoreleasePool.new_();
                pool.drain();
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