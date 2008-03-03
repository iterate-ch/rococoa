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


/**
 * Represents an Objective-C ID.
 * 
 * Maybe this should be a Pointer, or PointerType, in order to be the right size 
 * for 32 and 64 bit platforms.
 * 
 * @author duncan
 *
 */
public class ID extends com.sun.jna.IntegerType {

    public ID() {
        this(0);
    };
    
    public ID(int value) {
        super(4, value);
    }
    
    @Override
    public String toString() {
        return String.format("[ID 0x%x]", intValue()); //$NON-NLS-1$
    }
    
    public boolean isNull() {
        return intValue() == 0;
    }

}
