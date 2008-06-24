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

import com.sun.jna.FromNativeContext;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public class IDByReference extends ByReference {

    public IDByReference() {
        this(new ID(0));
    }
    
    public IDByReference(ID value) {
        super(4);
        setValue(value);
    }
    
    public void setValue(ID value) {
        getPointer().setInt(0, value.intValue());
    }
    
    public ID getValue() {
        return new ID(getPointer().getInt(0));
    }
    
    public Object fromNative(Object nativeValue, FromNativeContext context) {
        return super.fromNative(nativeValue, context);
    }
    
    /** Convert this object to its native type (a {@link Pointer}). */
    public Object toNative() {
        return super.toNative();
    }
}

