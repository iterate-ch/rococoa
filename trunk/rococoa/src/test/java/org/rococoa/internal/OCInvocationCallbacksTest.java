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
 
package org.rococoa.internal;

import junit.framework.TestCase;

import org.junit.Test;
import org.rococoa.ID;
import org.rococoa.NSObject;
import org.rococoa.TestStruct;
import org.rococoa.cocoa.NSString;

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
        public void returnsVoidTakesStruct(TestStruct s) {}
        public void returnsVoidTakesStructByValue(TestStruct.ByValue s) {}
        public TestStruct returnsStructTakesVoid() {
            return null;
        }
        public TestStruct.ByValue returnsStructByValueTakesVoid() {
            return null;
        }
        public NativeLong returnsNativeLongTakesNativeLong(NativeLong l) {
            return null;
        }
        public String returnsStringTakesString(String s) {
            return null;
        }
        public NSString returnsNSStringTakesNSString(NSString s) {
            return null;
        }
    }

    private OCInvocationCallbacks callbacks;
    
    @Override
    protected void setUp() throws Exception {
        callbacks = new OCInvocationCallbacks(new JavaImplementor());
    }
    
    @Test public void testMethodForSelector() throws SecurityException, NoSuchMethodException {
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
    
    @Test public void testMethodSignatureForSelector() {
        assertNull(callbacks.methodSignatureForSelector("nosuch"));

        assertEquals("v@:", callbacks.methodSignatureForSelector("returnsVoidTakesVoid"));
        assertEquals("v@:i", callbacks.methodSignatureForSelector("returnsVoidTakesInt:"));
        assertEquals("@@:", callbacks.methodSignatureForSelector("returnsIDTakesVoid"));
        assertEquals("v@:ii", callbacks.methodSignatureForSelector("returnsVoidTakesInt:andInt:"));
        assertEquals("v@:@", callbacks.methodSignatureForSelector("returnsVoidTakesOCObject:"));
        assertEquals("c@:@", callbacks.methodSignatureForSelector("returnsByteTakesOCObject:"));
        if (Native.LONG_SIZE == 4)
            assertEquals("l@:l", callbacks.methodSignatureForSelector("returnsNativeLongTakesNativeLong:"));
        else 
            assertEquals("q@:q", callbacks.methodSignatureForSelector("returnsNativeLongTakesNativeLong:"));
        assertEquals("@@:@", callbacks.methodSignatureForSelector("returnsStringTakesString:"));
        assertEquals("@@:@", callbacks.methodSignatureForSelector("returnsNSStringTakesNSString:"));
    }
    
    
    @Test public void testMethodSignatureForSelectorForStructures() {
        assertEquals("v@:^{TestStruct=id}", callbacks.methodSignatureForSelector("returnsVoidTakesStruct:"));
        assertEquals("v@:{ByValue=id}", callbacks.methodSignatureForSelector("returnsVoidTakesStructByValue:"));
        // TODO - better would be
            // assertEquals("v@:{TestStruct_ByValue=id}", callbacks.methodSignatureForSelector("returnsVoidTakesStructByValue:"));
        assertEquals("^{TestStruct=id}@:", callbacks.methodSignatureForSelector("returnsStructTakesVoid"));
        assertEquals("{ByValue=id}@:", callbacks.methodSignatureForSelector("returnsStructByValueTakesVoid"));
    }
}
