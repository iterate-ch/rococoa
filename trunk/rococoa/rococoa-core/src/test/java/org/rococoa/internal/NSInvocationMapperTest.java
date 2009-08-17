package org.rococoa.internal;

import static org.junit.Assert.*;
import org.junit.Test;
import org.rococoa.cocoa.CGFloat;
import org.rococoa.cocoa.foundation.NSInteger;
import org.rococoa.cocoa.foundation.NSUInteger;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;

public class NSInvocationMapperTest {

    @Test public void testEncoding32() throws Exception {
        if (Native.LONG_SIZE != 4)
            return;
        
        /* These from calling @encode
        bool: B
        BOOL: c
        char: c
        signed char: c
        unsigned char: C
        signed short: s
        unsigned short: S
        signed int: i
        unsigned int: I
        signed long: l
        unsigned long: L
        signed long long: q
        unsigned long long: Q
        NSInteger: i
        NSUInteger: I
        CGFloat: f
        float: f
        double: d
        long double: d
         */
        check("c", boolean.class);
        check("c", byte.class);
        check("s", char.class);
        check("s", short.class);
        check("i", int.class);
        check("q", long.class);
        check("i", NSInteger.class);
        check("I", NSUInteger.class);
        check("l", NativeLong.class);
        check("f", CGFloat.class);
        check("f", float.class);
        check("d", double.class);        
    }

    @Test public void testEncoding64() throws Exception {
        if (Native.LONG_SIZE == 4)
            return;
        
        /* These from calling @encode
        bool: B
        BOOL: c
        char: c
        signed char: c
        unsigned char: C
        signed short: s
        unsigned short: S
        signed int: i
        unsigned int: I
        signed long: q
        unsigned long: Q
        signed long long: q
        unsigned long long: Q
        NSInteger: q
        NSUInteger: Q
        CGFloat: d
        float: f
        double: d
        long double: d
         */
        check("c", boolean.class);
        check("c", byte.class);
        check("s", char.class);
        check("s", short.class);
        check("i", int.class);
        check("q", long.class);
        check("q", NSInteger.class);
        check("Q", NSUInteger.class);
        check("q", NativeLong.class);
        check("d", CGFloat.class);
        check("f", float.class);
        check("d", double.class);
    }

    private void check(String expected, Class<?> javaType) {
        assertEquals(javaType.toString(), expected,  NSInvocationMapperLookup.stringForType(javaType));
    }
}
