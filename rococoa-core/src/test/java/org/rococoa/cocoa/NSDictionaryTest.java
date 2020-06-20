/*
 * Copyright 2007, 2008, 2009 Duncan McGregor
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

package org.rococoa.cocoa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSDictionary;
import org.rococoa.cocoa.foundation.NSMutableDictionary;
import org.rococoa.cocoa.foundation.NSNumber;
import org.rococoa.cocoa.foundation.NSString;
import org.rococoa.test.RococoaTestCase;

public class NSDictionaryTest extends RococoaTestCase {
    
    @Test public void testDictionaryWithObjects_forKeys() {
        NSArray objects = NSArray.CLASS.arrayWithObjects(
                NSString.stringWithString("string value"),
                NSNumber.CLASS.numberWithInt(42));
        NSArray keys = NSArray.CLASS.arrayWithObjects(
                NSString.stringWithString("string key"),
                NSString.stringWithString("int key"));
        NSDictionary dictionary = NSDictionary.dictionaryWithObjects_forKeys(objects, keys);
        
        check(dictionary);
    }
    
    @Test public void testDictionaryWithObjectsAndKeys() {
        NSDictionary dictionary = NSDictionary.dictionaryWithObjectsAndKeys(
                NSString.stringWithString("string value"), NSString.stringWithString("string key"),
                NSNumber.CLASS.numberWithInt(42), NSString.stringWithString("int key"));
        
        check(dictionary);
    }

    @Test public void testMutableDictionary() {
        NSMutableDictionary dictionary = NSMutableDictionary.dictionaryWithCapacity(5);
        assertEquals(0, dictionary.count());
        
        dictionary.setValue_forKey(
                NSString.stringWithString("string value"), NSString.stringWithString("string key"));
        dictionary.setValue_forKey(
                NSNumber.CLASS.numberWithInt(42), "int key");

        check(dictionary);
    }

    private void check(NSDictionary dictionary) {
        assertEquals(2, dictionary.count());
                
        NSString value = Rococoa.cast(
                dictionary.objectForKey(NSString.stringWithString("string key")),
                NSString.class);
        assertEquals("string value", value.toString());
    
        NSNumber value2 = Rococoa.cast(
                dictionary.objectForKey("int key"),
                NSNumber.class);
        assertEquals(42, value2.intValue());
    }

}
