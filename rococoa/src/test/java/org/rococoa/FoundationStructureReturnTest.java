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

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;

@SuppressWarnings("nls")
public class FoundationStructureReturnTest extends RococoaTestCase {     
    private interface StructLibrary extends Library {
        MyStruct.MyStructByValue  returnStructByValue(int a, double b);
        double addFieldsOfStructByValue(MyStruct.MyStructByValue s);
        double addFieldsOfStructByValueVARARGS(int count, MyStruct.MyStructByValue ...objects);
    }
    
    private StructLibrary instance = (StructLibrary) Native.loadLibrary("rococoa", StructLibrary.class);    
    
    public static class MyStructOfStruct extends Structure {
        public double aDouble;
        public MyStruct.MyStructByValue aStruct;
    }

    public static class MyStructOfStructByValue extends MyStructOfStruct implements Structure.ByValue {
        public MyStructOfStructByValue() {}
        public MyStructOfStructByValue(int anInt, double aDouble) {
            this.aDouble = aDouble;
            this.aStruct = new MyStruct.MyStructByValue(anInt, aDouble);
        }        
    }

    public void testStaticReceiveStructure() {
        MyStruct result = instance.returnStructByValue(42, Math.E);
        assertEquals(42, result.anInt);
        assertEquals(Math.E, result.aDouble);        
    }
    
    public void testStaticPassStructure() {
        MyStruct.MyStructByValue arg = new MyStruct.MyStructByValue(42, Math.PI);
        double result = instance.addFieldsOfStructByValue(arg);
        assertEquals(42 + Math.PI, result);
    }

    public void testStaticPassStructureVARARGS() {
        // demonstrate bug in JNA 3.0.3
        MyStruct.MyStructByValue arg = new MyStruct.MyStructByValue(42, Math.PI);
        double result = instance.addFieldsOfStructByValueVARARGS(1, arg);
        assertEquals(42 + Math.PI, result);
    }
    
    public void testCallMethod() {
        ID testID = Foundation.createInstance(Foundation.nsClass("TestShunt"));
        Object[] args = { 42, Math.E };
        MyStruct result = Foundation.send(testID, 
                Foundation.selector("testReturnStructByValue:and:"), 
                MyStruct.MyStructByValue.class, args);
        assertEquals(42, result.anInt);
        assertEquals(Math.E, result.aDouble);        
    }
    
    public void testAsPassStructAsArgument() {
        ID testID = Foundation.createInstance(Foundation.nsClass("TestShunt"));
        MyStruct.MyStructByValue arg = new MyStruct.MyStructByValue(42, Math.PI);
        Object[] args = { arg };
        double result = Foundation.send(testID, 
                Foundation.selector("testAddFieldsOfStructByValue:"), 
                double.class, args);
        assertEquals(42 + Math.PI, result);        
    }
    
    public void testStructOfStruct() {
        ID testID = Foundation.createInstance(Foundation.nsClass("TestShunt"));
        Object[] args1 = { 42, Math.E };
        MyStructOfStruct result = Foundation.send(testID, 
                Foundation.selector("testReturnStructOfStructByValue:and:"), 
                MyStructOfStructByValue.class, args1);
        assertEquals(Math.E, result.aDouble);
        assertEquals(42, result.aStruct.anInt);        
        assertEquals(Math.E, result.aStruct.aDouble);
        Object[] args = { result };
        
        double result2 = Foundation.send(testID, 
                Foundation.selector("testPassStructOfStructByValue:"), 
                double.class, args);
        assertEquals(Math.E, result2);        
    }        
}
