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

import org.rococoa.OCInvocationCallbacks;
import org.rococoa.ID;
import org.rococoa.NSObject;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class OCInvocationCallbacksTest extends TestCase {
    
    public static class JavaImplementor {
    
        public void returnsVoidTakesVoid() {}
        public void returnsVoidTakesInt(int i) {}      
        public ID returnsIDTakesVoid() {
            return null;
        }
        public void returnsVoidTakesInt_andInt(int arg1, int arg2) {}
        public void returnsVoidTakesOCObject(NSObject o) {}
        public byte returnsByteTakesOCObject(NSObject o) {
            return -1;
        }
        
     }
    
    public void testMethodForSelector() throws SecurityException, NoSuchMethodException {
        JavaImplementor implementor = new JavaImplementor();
        OCInvocationCallbacks callback = new OCInvocationCallbacks(implementor);
        
        assertEquals(
            JavaImplementor.class.getDeclaredMethod("returnsIDTakesVoid"),                
            callback.methodForSelector("returnsIDTakesVoid"));
        
        assertEquals(
            JavaImplementor.class.getDeclaredMethod("returnsVoidTakesInt", int.class),                
            callback.methodForSelector("returnsVoidTakesInt:"));
        
        assertEquals(
            JavaImplementor.class.getDeclaredMethod("returnsVoidTakesInt_andInt", int.class, int.class),
            callback.methodForSelector("returnsVoidTakesInt:andInt:"));

        assertNull(callback.methodForSelector("nosuch"));
        
        // wrong number of args
        assertNull(callback.methodForSelector("returnsVoidTakesVoid:")); 
        assertNull(callback.methodForSelector("returnsVoidTakesInt"));
    }
    
    public void testMethodSignatureForSelector() {
        JavaImplementor implementor = new JavaImplementor();
        OCInvocationCallbacks callback = new OCInvocationCallbacks(implementor);
        
        assertEquals("v@:", callback.methodSignatureForSelector("returnsVoidTakesVoid"));
        assertEquals("v@:i", callback.methodSignatureForSelector("returnsVoidTakesInt:"));
        assertEquals("@@:", callback.methodSignatureForSelector("returnsIDTakesVoid"));
        assertEquals("v@:ii", callback.methodSignatureForSelector("returnsVoidTakesInt:andInt:"));
        assertEquals("v@:@", callback.methodSignatureForSelector("returnsVoidTakesOCObject:"));
        assertEquals("c@:@", callback.methodSignatureForSelector("returnsByteTakesOCObject:"));
        
        assertNull(callback.methodSignatureForSelector("nosuch"));
        
    }
}
