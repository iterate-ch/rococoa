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
import com.sun.jna.NativeMapped;


/**
 * Represents an Objective-C ID.
 * 
 * Maybe this should be a Pointer, or PointerType, in order to be the right size 
 * for 32 and 64 bit platforms.
 * 
 * @author duncan
 *
 */
public class ID implements NativeMapped {

    private int value;
    
    public ID() {
        this(0);
    };
    
    public ID(int value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return String.format("[ID 0x%x]", value); //$NON-NLS-1$
    }
    
    public boolean isNull() {
        return value == 0;
    }

    public Object fromNative(Object nativeValue, FromNativeContext context) {
        if (nativeValue == null)
            return 0;
        Integer nativeAsInteger = (Integer) nativeValue;
        return new ID(nativeAsInteger);
    }
    
    public Class<?> nativeType() {
        return Integer.class;
    }

    public Object toNative() {
        return value;
    }
    
    public int intValue() {
        return value;
    }
    
    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ID other = (ID) obj;
        if (value != other.value)
            return false;
        return true;
    }

}
