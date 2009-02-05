package org.rococoa.cocoa;

import com.sun.jna.FromNativeContext;
import com.sun.jna.Native;
import com.sun.jna.NativeMapped;

/**
 * CGFloat
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author duncan
 */
public class CGFloat extends Number implements NativeMapped {
    // Inspired by JNA NativeLong and IntegerType
    public static final int SIZE = Native.LONG_SIZE;

    private final double value;

    public CGFloat() {
        value = 0;
    }

    public CGFloat(double d) {
        value = d;
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public int hashCode() {
        // From Double.hashCode
        long bits = Double.doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));
    }

    @Override
    public boolean equals(Object other) {
        // Modified Double.equals
        return (other instanceof CGFloat) && (Double.doubleToLongBits(((CGFloat) other).value) == Double.doubleToLongBits(value));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    // Native mapping
    public Object fromNative(Object o, FromNativeContext fromNativeContext) {
        switch (SIZE) {
            case 4:
                return new CGFloat((Float) o);
            case 8:
                return new CGFloat((Double) o);
            default:
                throw new Error("Unknown Native.LONG_SIZE: " + SIZE);
        }
    }

    public Object toNative() {
        switch (SIZE) {
            case 4:
                return floatValue();
            case 8:
                return doubleValue();
            default:
                throw new Error("Unknown Native.LONG_SIZE: " + SIZE);
        }
    }

    public Class<?> nativeType() {
        switch (SIZE) {
            case 4:
                return Float.class;
            case 8:
                return Double.class;
            default:
                throw new Error("Unknown Native.LONG_SIZE: " + SIZE);
        }
    }
}
