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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;

/**
 * Diagnosing a JNA bug (introduced in JNA 3.0.6) when returning small structures.
 * 
 */
public class StaticStructureReturnTest {
      
    public static class IntIntStruct extends Structure implements Structure.ByValue {
        public int a;
        public int b;
        
        public IntIntStruct() {}
        
        public IntIntStruct(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    public static class FloatFloatStruct extends Structure implements Structure.ByValue {
        public float a;
        public float b;
        
        public FloatFloatStruct() {}
        
        public FloatFloatStruct(float a, float b) {
            this.a = a;
            this.b = b;
        }
    }

    public static class IntFloatStruct extends Structure implements Structure.ByValue {
        public int a;
        public float b;
        
        public IntFloatStruct() {}
        
        public IntFloatStruct(int a, float b) {
            this.a = a;
            this.b = b;
        }
    }

    public static class IntLongStruct extends Structure implements Structure.ByValue {
        public int a;
        public long b;
        
        public IntLongStruct() {}
        
        public IntLongStruct(int a, long b) {
            this.a = a;
            this.b = b;
        }
    }

    public static class IntDoubleStruct extends Structure implements Structure.ByValue {
        public int a;
        public double b;
        
        public IntDoubleStruct() {}
        
        public IntDoubleStruct(int a, double b) {
            this.a = a;
            this.b = b;
        }
    }

    
    public interface TestLibrary extends Library {
        FloatFloatStruct createFloatFloatStruct(float a, float b);
        IntIntStruct createIntIntStruct(int a, int b);
        IntFloatStruct createIntFloatStruct(int a, float b);
        IntLongStruct createIntLongStruct(int a, long b);
        IntDoubleStruct createIntDoubleStruct(int a, double b);
    }

    private TestLibrary library3 = (TestLibrary) Native.loadLibrary("rococoa", TestLibrary.class);

    @Test public void testIntInt() {
        IntIntStruct struct = library3.createIntIntStruct(42, -99);
        assertEquals(-99, struct.b);
        assertEquals(42, struct.a);
    }

    @Test public void testFloatFloat() {
        FloatFloatStruct struct = library3.createFloatFloatStruct((float) Math.E, (float) Math.PI);
        assertEquals(Math.PI, struct.b, 0.001);
        assertEquals(Math.E, struct.a, 0.001);
    }

    @Test public void testIntFloat() {
        IntFloatStruct struct = library3.createIntFloatStruct(42, (float) Math.PI);
        assertEquals(Math.PI, struct.b, 0.001);
        assertEquals(42, struct.a);
    }
    
    @Test public void testIntLong() {
        IntLongStruct struct = library3.createIntLongStruct(42, -99);
        assertEquals(-99, struct.b);
        assertEquals(42, struct.a);
    }

    @Test public void testIntDouble() {
        IntDoubleStruct struct = library3.createIntDoubleStruct(42, Math.PI);
        assertEquals(Math.PI, struct.b, 0.001);
        assertEquals(42, struct.a);
    }

}
