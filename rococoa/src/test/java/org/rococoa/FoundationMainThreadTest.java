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
