package org.rococoa.cocoa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.rococoa.Rococoa;
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
