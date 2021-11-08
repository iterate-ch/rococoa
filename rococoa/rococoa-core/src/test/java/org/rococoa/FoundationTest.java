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

import org.junit.Ignore;
import org.junit.Test;
import org.rococoa.test.RococoaTestCase;

import static org.junit.Assert.*;

public class FoundationTest extends RococoaTestCase {

    @Test
    public void testCFString() {
        ID string = Foundation.cfString("Hello World");
        assertNotNull(string);
        assertEquals("Hello World", Foundation.toString(string));
    }

    @Test
    public void testCFStringWithDifferentEncoding() throws Exception {
        String stringWithOddChar = "Hello \u2648"; // Aries
        ID string = Foundation.cfString(stringWithOddChar);
        assertEquals(stringWithOddChar, Foundation.toString(string));
    }

    @SuppressWarnings("unused")
    @Ignore("slow")
    @Test
    public void testStringPerformance() {
        String stringWithOddChar = "Hello \u2648";
        StringBuilder longStringBuilder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longStringBuilder.append(stringWithOddChar);
        }
        String longString = longStringBuilder.toString();
        ID string = Foundation.cfString(longString);

        for (int i = 0; i < 10000; i++) {
            String s = Foundation.toStringViaUTF16(string);
        }
    }

    @Test
    public void testInt() {
        ID clas = Foundation.getClass("NSNumber");
        ID anInt = Foundation.sendReturnsID(clas, "numberWithInt:", 42);
        int anIntValue = Foundation.send(anInt, "intValue", int.class);
        assertEquals(42, anIntValue);
    }

    @Test
    public void testDouble() {
        ID clas = Foundation.getClass("NSNumber");
        ID aDouble = Foundation.sendReturnsID(clas, "numberWithDouble:", Math.E);
        Object[] args = {};
        double aDoubleValue = Foundation.send(aDouble, Foundation.selector("doubleValue"), double.class, args);
        assertEquals(Math.E, aDoubleValue, 0.001);
    }

    @Test
    @Ignore
    public void testFloat() {
        ID clas = Foundation.getClass("NSNumber");
        ID aFloat = Foundation.sendReturnsID(clas, "numberWithFloat:", 5.485f);
        Object[] args = {};
        float aFloatValue = Foundation.send(aFloat, Foundation.selector("floatValue"), float.class, args);
        assertEquals(5.485f, aFloatValue, 0f);
    }

    @Test
    public void testSendNoArgs() {
        ID clas = Foundation.getClass("NSDate");
        ID instance = Foundation.sendReturnsID(clas, "date");
        ID result = Foundation.sendReturnsID(instance, "description");
        assertTrue(Foundation.toString(result).startsWith("2")); // 2007-11-15 16:01:50 +0000
    }

    @Test
    public void testSelector() {
        Selector selector = Foundation.selector("selectorName:");
        assertTrue(selector.longValue() != 0); // selectors always exist
        assertSame("selectorName:", selector.getName());

        Selector noSuchSelector = Foundation.selector("noSelector:NamedThis:OrribleThing:");
        assertTrue(noSuchSelector.longValue() != 0);
        assertSame("noSelector:NamedThis:OrribleThing:", noSuchSelector.getName());
    }

    @Test
    public void sendMessageToNilIsOK() {
        assertEquals(new ID(0), Foundation.sendReturnsID(new ID(0), "description"));
    }

    // TODO - make work by wrapping call with native try- catch
    @Ignore("to make work")
    @Test
    public void testInvokeUnknownSelector() {
        Selector noSuchSelector = Foundation.selector("noSelector:NamedThis:OrribleThing:");
        assertTrue(noSuchSelector.longValue() != 0);
        ID clas = Foundation.getClass("NSNumber");
        try {
            Foundation.send(clas, noSuchSelector, int.class);
            fail();
        } catch (NoSuchMethodError xpected) {
        }
    }
}
