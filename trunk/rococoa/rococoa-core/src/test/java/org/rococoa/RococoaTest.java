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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.rococoa.Foundation;
import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.NSArray;
import org.rococoa.cocoa.NSDate;
import org.rococoa.cocoa.NSNumber;
import org.rococoa.cocoa.NSString;
import org.rococoa.test.RococoaTestCase;

@SuppressWarnings("nls")
public class RococoaTest extends RococoaTestCase {
        
    @Test public void testCreate() {
        NSNumber fortyTwo = NSNumber.CLASS.numberWithInt(42);
        assertEquals(42, fortyTwo.intValue());        
    }
    
    @Test public void testEqualsWithAliases() {
        NSNumber fortyTwo = NSNumber.CLASS.numberWithInt(42);
        NSNumber fortyTwoAlias = Rococoa.wrap(fortyTwo.id(), NSNumber.class);
        NSNumber fortyThree = NSNumber.CLASS.numberWithInt(43);
        assertTrue(fortyTwo.equals(fortyTwoAlias));
        assertTrue(fortyTwoAlias.equals(fortyTwo));
        assertFalse(fortyTwo.equals(fortyThree));
        assertFalse(fortyTwo.equals(null));
    }
        
    @Test public void testEqualsMapsToIsEqual() {
        NSString s1 = NSString.stringWithString("string");
        NSString s2 = NSString.stringWithString("STRING").lowercaseString();
        assertNotSame(s1, s2);
        assertFalse(s1.id().equals(s2.id()));
        assertEquals(s1, s2);
    }
   
    @Test public void testReturnTypes() {
        NSNumber e = NSNumber.CLASS.numberWithDouble(Math.E);
        assertEquals(2, e.intValue());
        assertEquals(2, e.longValue());
        assertEquals((float) Math.E, e.floatValue(), 0.001);
        assertEquals(Math.E, e.doubleValue(), 0.001);
    }
    
    @Test public void testPassOCObject() {
        NSNumber fortyTwo = NSNumber.CLASS.numberWithInt(42);    
        NSNumber e = NSNumber.CLASS.numberWithDouble(Math.E);
        
        assertEquals(-1, e.compare(fortyTwo));
        assertEquals(0, e.compare(e));
        assertEquals(1, fortyTwo.compare(e));
    }
    
    @Test public void testStringMarshalling() {
        NSString string = NSString.CLASS.stringWithString("Hello world");
        assertTrue(string.isEqualToString("Hello world"));
        assertFalse(string.isEqualToString("Hello worldy"));
    }
    
    @Test public void testKeywordMethod() {
        // TODO - this method doesn't actually test keyword methods any more
        NSDate epoc = NSDate.CLASS.dateWithTimeIntervalSince1970(0);
        assertEquals(0, epoc.timeIntervalSince1970(), 0.000001f);
        NSDate anotherDate = NSDate.CLASS.dateWithTimeIntervalSince1970(40d);
        assertEquals(40, anotherDate.timeIntervalSince1970(), 0.000001f);        
    }
        
    @Test public void testVarags() {
        NSArray array = NSArray.CLASS.arrayWithObjects(
                NSNumber.CLASS.numberWithBool(true),
                NSNumber.CLASS.numberWithInt(42),
                NSDate.CLASS.dateWithTimeIntervalSince1970(666),
                null); // required by NSArray
        assertNotNull(array);
        assertFalse(array.id().isNull());
        assertEquals(3, array.count());
    }
    
    @Test public void testFactory() {
        NSNumber._Class nsNumberClass = Rococoa.createClass("NSNumber",  NSNumber._Class.class); //$NON-NLS-1$
        assertEquals(nsNumberClass.id(), Foundation.getClass("NSNumber"));
    }
    
    public interface OddClass extends NSClass {
        public NSObject numberWithInt(int value);
    }

    @Test public void testDownCast() {
        // this is OK
        NSObject numberAsObject = NSNumber.CLASS.numberWithInt(42);
        assertEquals(42, ((NSNumber) numberAsObject).intValue());
        
        // but when defined return type is NSObject, we can't cast Java objects
        OddClass nsClass = Rococoa.createClass("NSNumber", OddClass.class);
        NSObject returnAsObject = nsClass.numberWithInt(42);
        try {
            ((NSNumber) returnAsObject).intValue();
            fail();
        } catch (ClassCastException expected) {}
        
        // we need to do this
        assertEquals(42, Rococoa.cast(returnAsObject, NSNumber.class).intValue());
    }
    
    @Test public void testToString() {
        NSNumber fortyTwo = NSNumber.CLASS.numberWithInt(42);
        assertEquals("42", fortyTwo.toString());        
    }
    
    @Test public void testGeneratedClassName() {
        NSString string = NSString.stringWithString("Hello World");
        Class<? extends NSString> stringClass = string.getClass();
        assertEquals(NSString.class.getPackage(), stringClass.getPackage());
        assertEquals("NSString$$ByRococoa", stringClass.getSimpleName());
    }

}
