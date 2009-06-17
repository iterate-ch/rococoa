/**
 * 
 */
package org.rococoa;

import com.sun.jna.Structure;

public class TestStruct extends Structure {
    public final int anInt;
    public final double aDouble;

    public TestStruct() {
        this(0, 0);
    };
    
    public TestStruct(int anInt, double aDouble) {
        this.anInt = anInt; this.aDouble = aDouble;
    }

    public static class ByValue extends TestStruct implements Structure.ByValue {
        public ByValue() {
            this(0, 0);
        };

        public ByValue(int anInt, double aDouble) {
            super(anInt, aDouble);
        }
    }
    
}