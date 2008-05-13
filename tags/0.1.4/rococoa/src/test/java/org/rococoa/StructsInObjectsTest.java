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


public class StructsInObjectsTest extends NSTestCase {
    
    public void test() throws Exception {
                
        NSSize input = new NSSize(1, 3);
        NSValue value = NSValue.CLASS.valueWithSize(input);
        NSSize size = value.sizeValue();

        assertEquals(1.0, size.width, 0.0001);
        assertEquals(3.0, size.height, 0.0001);        
    }
}
