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

import junit.framework.TestCase;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;

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
        public void returnsVoidTakesMyStruct(MyStruct s) {}
        public void returnsVoidTakesMyStructByValue(MyStruct.MyStructByValue s) {}
        public MyStruct returnsMyStructTakesVoid() {
            return null;
        }
        public MyStruct.MyStructByValue returnsMyStructByValueTakesVoid() {
            return null;
        }
        public NativeLong returnsNativeLongTakesNativeLong(NativeLong l) {
            return null;
        }
    }

    private OCInvocationCallbacks callbacks;
    
    @Override
    protected void setUp() throws Exception {
        callbacks = new OCInvocationCallbacks(new JavaImplementor());
    }
    
    public void testMethodForSelector() throws SecurityException, NoSuchMethodException {
        assertNull(callbacks.methodForSelector("nosuch"));
        assertEquals(
            JavaImplementor.class.getDeclaredMethod("returnsIDTakesVoid"),                
            callbacks.methodForSelector("returnsIDTakesVoid"));
        assertEquals(
            JavaImplementor.class.getDeclaredMethod("returnsVoidTakesInt", int.class),                
            callbacks.methodForSelector("returnsVoidTakesInt:"));
        assertEquals(
            JavaImplementor.class.getDeclaredMethod("returnsVoidTakesInt_andInt", int.class, int.class),
            callbacks.methodForSelector("returnsVoidTakesInt:andInt:"));
        // wrong number of args
        assertNull(callbacks.methodForSelector("returnsVoidTakesVoid:")); 
        assertNull(callbacks.methodForSelector("returnsVoidTakesInt"));
    }
    
    public void testMethodSignatureForSelector() {
        assertNull(callbacks.methodSignatureForSelector("nosuch"));

        assertEquals("v@:", callbacks.methodSignatureForSelector("returnsVoidTakesVoid"));
        assertEquals("v@:i", callbacks.methodSignatureForSelector("returnsVoidTakesInt:"));
        assertEquals("@@:", callbacks.methodSignatureForSelector("returnsIDTakesVoid"));
        assertEquals("v@:ii", callbacks.methodSignatureForSelector("returnsVoidTakesInt:andInt:"));
        assertEquals("v@:@", callbacks.methodSignatureForSelector("returnsVoidTakesOCObject:"));
        assertEquals("c@:@", callbacks.methodSignatureForSelector("returnsByteTakesOCObject:"));
        if (Native.LONG_SIZE == 4)
            assertEquals("i@:i", callbacks.methodSignatureForSelector("returnsNativeLongTakesNativeLong:"));
        else 
            assertEquals("l@:l", callbacks.methodSignatureForSelector("returnsNativeLongTakesNativeLong:"));
    }
    
    
    public void testMethodSignatureForSelectorForStructures() {
        assertEquals("v@:^{MyStruct=id}", callbacks.methodSignatureForSelector("returnsVoidTakesMyStruct:"));
        assertEquals("v@:{MyStructByValue=id}", callbacks.methodSignatureForSelector("returnsVoidTakesMyStructByValue:"));
        assertEquals("^{MyStruct=id}@:", callbacks.methodSignatureForSelector("returnsMyStructTakesVoid"));
        assertEquals("{MyStructByValue=id}@:", callbacks.methodSignatureForSelector("returnsMyStructByValueTakesVoid"));
    }
}
