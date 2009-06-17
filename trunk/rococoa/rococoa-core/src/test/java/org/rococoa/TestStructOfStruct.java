/**
 * 
 */
package org.rococoa;

import com.sun.jna.Structure;

public class TestStructOfStruct extends Structure {
    public double aDouble;
    public TestStruct.ByValue aStruct;
    
    public static class ByValue extends TestStructOfStruct implements Structure.ByValue {
    public ByValue() {}
    public ByValue(int anInt, double aDouble) {
        this.aDouble = aDouble;
        this.aStruct = new TestStruct.ByValue(anInt, aDouble);
    }        
}
}