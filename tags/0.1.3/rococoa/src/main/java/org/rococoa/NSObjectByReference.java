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

import com.sun.jna.ptr.ByReference;

/**
 * Used to retrieve an NSObject as an out param.
 * 
 * TODO - recast as extends ByReference
 * @author duncan
 *
 */
public class NSObjectByReference extends ByReference {

    private NSObject object;

    public NSObjectByReference() {
        super(4);
    }
    
    public <T extends NSObject> T getValueAs(Class<T> javaClass) {
        return Rococoa.cast(object, javaClass);
    }
    
    public void setObject(NSObject object) {
        this.object = object;
    }
    
    

}
