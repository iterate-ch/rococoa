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
import java.util.concurrent.Callable;

import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.Selector;

import com.sun.jna.Native;

@SuppressWarnings({"nls", "unused"})
public class FoundationTest extends NSTestCase {
             
    public void testString() {
        ID string = Foundation.cfString("Hello World");
        assertNotNull(string);
        assertEquals("Hello World", Foundation.toString(string));
    }
    
    public void testInt() {
        ID clas = Foundation.nsClass("NSNumber");
        ID anInt = Foundation.sendReturnsID(clas, "numberWithInt:", 42);
        int anIntValue = Foundation.send(anInt, "intValue", int.class);
        assertEquals(42, anIntValue);
    }
    
    public void testDouble() {
        ID clas = Foundation.nsClass("NSNumber");
        ID aDouble = Foundation.sendReturnsID(clas, "numberWithDouble:", Math.E);
        Object[] args = {};
        double aDoubleValue = Foundation.send(aDouble, Foundation.selector("doubleValue"), double.class, args);
        assertEquals(Math.E, aDoubleValue, 0.001);
    }

    public void testFloat() {
        ID clas = Foundation.nsClass("NSNumber");
        ID aFloat = Foundation.sendReturnsID(clas, "numberWithFloat:", 3.142f);
        Object[] args = {};
        float aFloatValue = Foundation.send(aFloat, Foundation.selector("floatValue"), float.class, args);
        assertEquals(3.142f, aFloatValue, 0.001);
    }

    public void testSendNoArgs() {
        ID clas = Foundation.nsClass("NSDate");
        ID instance = (ID) Foundation.sendReturnsID(clas, "date");
        ID result = (ID) Foundation.sendReturnsID(instance, "description");
        assertTrue(Foundation.toString(result).startsWith("20")); // 2007-11-15 16:01:50 +0000
    }
    
    public void testSelector() {
        Selector selector = Foundation.selector("selectorName:");
        assertTrue(selector.intValue() != 0); // selectors always exist
        assertSame("selectorName:", selector.getName());
        
        Selector noSuchSelector = Foundation.selector("noSelector:NamedThis:OrribleThing:");
        assertTrue(noSuchSelector.intValue() != 0);
        assertSame("noSelector:NamedThis:OrribleThing:", noSuchSelector.getName());
    }
    
    // TODO - make work by wrapping call with native try- catch
    public void xtestInvokeUnknownSelector() {
        Selector noSuchSelector = Foundation.selector("noSelector:NamedThis:OrribleThing:");
        assertTrue(noSuchSelector.intValue() != 0); 
        ID clas = Foundation.nsClass("NSNumber");
        try {
            Foundation.send(clas, noSuchSelector, int.class);
            fail();
        } catch (NoSuchMethodError xpected) {}
    }
    

}
