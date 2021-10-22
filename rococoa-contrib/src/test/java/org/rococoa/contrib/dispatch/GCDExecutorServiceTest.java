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

package org.rococoa.contrib.dispatch;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.*;

/** Test the API of the GCDExecutorService.
 *  @author Andrew Thompson (lordpixel@mac.com)
 */
@DisabledIfEnvironmentVariable(named = "GITHUB_WORKFLOW", matches = ".*")
public class GCDExecutorServiceTest {
    /**The GCD Executor to test*/
    ExecutorService fixture;
    public GCDExecutorServiceTest() {
    }

    @BeforeAll
    public static void setupLogging() {
    }

    @BeforeEach
    public void setUp() {
        fixture = new GCDExecutorService();
    }

    /**
     * Test of shutdown method, of class GCDExecutorService.
     */
    @Test
    public void testShutdown() {
    	assertThrows(RejectedExecutionException.class, () -> {
	        fixture.shutdown();
	        assertTrue(fixture.isShutdown());
	        assertTrue(fixture.isTerminated());
	        fixture.execute(new Runnable() {
	            public void run() {}
	        });
    	});
    }

    @Test
    public void testShutdown_TasksFinish() throws InterruptedException {
        final boolean[] finished = { false };
        fixture.execute(new Runnable() {
            public void run() {
                finished[0] = true;
            }
        });
        fixture.shutdown();
        assertTrue(fixture.isShutdown());
        Thread.sleep(100);
        List<Runnable> unrun = fixture.shutdownNow();
        assertTrue(finished[0] && unrun.size() == 0, finished[0] + ", " + unrun);
    }

    /**
     * Test of shutdownNow method, of class GCDExecutorService.
     */
    @Test
    public void testShutdownNow() throws InterruptedException {
        Object lock = new Object();
        int count = 100;
        queueUpSomeTasks(lock, count);
        List<Runnable> outstandingTasks = fixture.shutdownNow();
        assertEquals(count, outstandingTasks.size());
        assertTrue(fixture.isShutdown());
        synchronized(lock) {
            lock.notifyAll();
        }
        assertTrue(fixture.awaitTermination(10, TimeUnit.SECONDS));
    }

    private void queueUpSomeTasks(final Object lock, int count) {
        for (int i=0; i < count; i++) {
            fixture.execute(new Runnable() {
                public void run() {
                    try {
                        synchronized(lock) {
                            lock.wait();
                        }
                    } catch (InterruptedException ie) {

                    }
                }
            });
        }
    }

    /**
     * Test of isShutdown method, of class GCDExecutorService.
     */
    @Test
    public void testIsShutdown() {
        fixture.shutdown();
        assertTrue(fixture.isShutdown());
    }

    /**
     * Test of awaitTermination method, of class GCDExecutorService.
     */
    @Test
    public void testAwaitTermination() throws Exception {
        int count=100;
        Object lock = new Object();
        queueUpSomeTasks(lock, count);
        List<Runnable> outstandingTasks = fixture.shutdownNow();
        assertEquals(count, outstandingTasks.size());
        assertTrue(fixture.isShutdown());
        synchronized(lock) {
            lock.notifyAll();
        }
        assertTrue(fixture.awaitTermination(5, TimeUnit.SECONDS));
        assertTrue(fixture.isTerminated());
    }

    /**
     * Test of execute method, of class GCDExecutorService.
     */
    @Test
    public void testExecute() throws InterruptedException {
        final boolean[] done = { false };
        fixture.execute(new Runnable() {
            public void run() {
                done[0]=true;
            }
        });
        Thread.sleep(1000);
        assertTrue(done[0]);
    }

    @Test
    public void testSubmit_Callable() throws InterruptedException, ExecutionException {
        Future<Boolean> result = fixture.submit(new Callable<Boolean> () {
           public Boolean call() {
               return true;
           }
        });
        assertTrue(result.get());
    }
    @Test
    public void testSubmit_Runnable() throws InterruptedException, ExecutionException, TimeoutException {
        final boolean[] runCheck = { false };
        Future<?> result = fixture.submit(new Runnable () {
           public void run() {
               runCheck[0] = true;
           }
        });
        assertEquals(null, result.get());
        assertTrue(runCheck[0]);
    }
    @Test
    public void testSubmit_Runnable_WithResult() throws InterruptedException, ExecutionException, TimeoutException {
        final boolean[] runCheck = { false };
        Future<Integer> result = fixture.submit(new Runnable () {
           public void run() {
               runCheck[0] = true;
           }
        }, 42);
        assertEquals(Integer.valueOf(42), result.get());
        assertTrue(runCheck[0]);
    }
}
