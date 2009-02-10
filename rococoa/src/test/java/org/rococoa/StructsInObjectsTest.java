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

import org.rococoa.cocoa.NSSize;
import org.rococoa.cocoa.NSValue;

import com.sun.jna.Library;

/**
 * Checks that we can embed a struct by value in a object.
 * 
 */
public class StructsInObjectsTest extends RococoaTestCase {
    
    public interface MyLibrary extends Library {
        NSSize objc_msgSend(ID receiver, Selector selector, Object... args);
    }

    public void test() throws Exception {
        NSSize aSize = new NSSize(1f, 3f);
        NSValue value = NSValue.CLASS.valueWithSize(aSize);
        
        
        Foundation.send(value.id(), Foundation.selector("sizeValue"), NSSize.class);
        NSSize size = value.sizeValue(); 
            // fails here with jna 3.0.6+ in Java 1.5, see StaticStructureReturnTest

        assertEquals(1.0, size.width.doubleValue(), 0.0001);
        assertEquals(3.0, size.height.doubleValue(), 0.0001);        
    }
}
