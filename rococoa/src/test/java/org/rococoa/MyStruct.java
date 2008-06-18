/**
 * 
 */
package org.rococoa;

import com.sun.jna.Structure;

public class MyStruct extends Structure {
    public final int anInt;
    public final double aDouble;

    public MyStruct() {
        this(0, 0);
    };
    
    public MyStruct(int anInt, double aDouble) {
        this.anInt = anInt; this.aDouble = aDouble;
    }

public static class MyStructByValue extends MyStruct implements Structure.ByValue {
    public MyStructByValue() {};

    public MyStructByValue(int anInt, double aDouble) {
        super(anInt, aDouble);
    }
}

}