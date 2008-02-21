package org.rococoa;

import java.util.concurrent.Callable;

import org.rococoa.Foundation;
import org.rococoa.ID;

@SuppressWarnings("nls")
public class FoundationMainThreadTest extends NSTestCase {

    public void testCallOnMainThread() {
        final Thread testThread = Thread.currentThread();
        Callable<Double> callable = new Callable<Double>() {
            public Double call() throws Exception {
                assertNotSame(testThread, Thread.currentThread());
                ID clas = Foundation.nsClass("NSNumber");
                ID aDouble = Foundation.sendReturnsID(clas, "numberWithDouble:", Math.E);
                Object[] args = {};
                return Foundation.send(aDouble, Foundation.selector("doubleValue"), double.class, args);
            }};
            
        assertEquals(Math.E, Foundation.callOnMainThread(callable), 0.001);        
    }

    public void testCallOnMainThreadThrows() {
        Callable<Double> callable = new Callable<Double>() {
            public Double call() throws Exception {
                throw new Error("deliberate");
            }};

        try {
            Foundation.callOnMainThread(callable);
            fail();
        } catch (Error expected) {
            assertEquals("deliberate", expected.getMessage());
        }
    }
    
    public void testRunOnMainThread() {
        final Thread testThread = Thread.currentThread();
        final double[] result = new double[1];
        Runnable runnable = new Runnable() {
            public void run() {
                assertNotSame(testThread, Thread.currentThread());
                ID clas = Foundation.nsClass("NSNumber");
                ID aDouble = Foundation.sendReturnsID(clas, "numberWithDouble:", Math.E);
                Object[] args = {};
                result[0] =  Foundation.send(aDouble, Foundation.selector("doubleValue"), double.class, args);
            }};
        Foundation.runOnMainThread(runnable);    
        assertEquals(Math.E, result[0], 0.001);        
    }
    

}
