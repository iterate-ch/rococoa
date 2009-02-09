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
        TestStruct.ByValue returnStructByValue(int a, double b);
        double addFieldsOfStructByValue(TestStruct.ByValue s);
        double addFieldsOfStructByValueVARARGS(int count, TestStruct.ByValue ...objects);
    }
    
    private StructLibrary instance = (StructLibrary) Native.loadLibrary("rococoa", StructLibrary.class);    
    
    public static class MyStructOfStruct extends Structure {
        public double aDouble;
        public TestStruct.ByValue aStruct;
    }

    public static class MyStructOfStructByValue extends MyStructOfStruct implements Structure.ByValue {
        public MyStructOfStructByValue() {}
        public MyStructOfStructByValue(int anInt, double aDouble) {
            this.aDouble = aDouble;
            this.aStruct = new TestStruct.ByValue(anInt, aDouble);
        }        
    }
    
    public void testStaticReceiveStructure() {
        TestStruct result = instance.returnStructByValue(42, Math.E);
        assertEquals(42, result.anInt);
        assertEquals(Math.E, result.aDouble);        
    }
    
    public void testStaticPassStructure() {
        TestStruct.ByValue arg = new TestStruct.ByValue(42, Math.PI);
        double result = instance.addFieldsOfStructByValue(arg);
        assertEquals(42 + Math.PI, result);
    }

    public void testStaticPassStructureVARARGS() {
        // demonstrate bug in JNA 3.0.3
        TestStruct.ByValue arg = new TestStruct.ByValue(42, Math.PI);
        double result = instance.addFieldsOfStructByValueVARARGS(1, arg);
        assertEquals(42 + Math.PI, result);
    }
    
    public void testCallMethod() {
        ID testID = Foundation.sendReturnsID(Foundation.getClass("TestShunt"), "new");
        Foundation.sendReturnsID(testID, "autorelease");
        Object[] args = { 42, Math.E };
        TestStruct result = Foundation.send(testID, 
                Foundation.selector("testReturnStructByValue:and:"), 
                TestStruct.ByValue.class, args);
        assertEquals(42, result.anInt);
        assertEquals(Math.E, result.aDouble);        
    }
    
    public void testAsPassStructAsArgument() {
        ID testID = Foundation.sendReturnsID(Foundation.getClass("TestShunt"), "new");
        Foundation.sendReturnsID(testID, "autorelease");
        TestStruct.ByValue arg = new TestStruct.ByValue(42, Math.PI);
        Object[] args = { arg };
        double result = Foundation.send(testID, 
                Foundation.selector("testAddFieldsOfStructByValue:"), 
                double.class, args);
        assertEquals(42 + Math.PI, result);        
    }
    
    public void testStructOfStruct() {
        ID testID = Foundation.sendReturnsID(Foundation.getClass("TestShunt"), "new");
        Foundation.sendReturnsID(testID, "autorelease");
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
