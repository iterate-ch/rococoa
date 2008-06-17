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


@SuppressWarnings("nls")
public class CFStringTest extends NSTestCase {
    
    public void testCFStringImpersonatesNSString() throws Exception {
        CFString s = new CFString("Hello World");        
        ID clas = Foundation.nsClass("NSString");
        ID instance = Foundation.sendReturnsID(clas, "stringWithString:", s);
        assertEquals("Hello World",
                Foundation.toString(Foundation.sendReturnsID(instance, "description")));
    }
    
    public void xtestFinalize() {
        for (int i = 0; i <  50; i ++) {
            CFString s = new CFString("Hello world " + i);
// TODO - without this we get random crashes, but we should own the string?            
            Foundation.cfRetain(s);
            s = null;
            System.gc();
            System.gc();
            System.runFinalization();
        }
    }

}
