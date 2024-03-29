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

package org.rococoa;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class TestStruct extends Structure {
    public int anInt;
    public double aDouble;

    public TestStruct() {
        this(0, 0);
    }

    public TestStruct(int anInt, double aDouble) {
        this.anInt = anInt;
        this.aDouble = aDouble;
    }

    public static class ByValue extends TestStruct implements Structure.ByValue {
        public ByValue() {
            this(0, 0);
        }

        public ByValue(int anInt, double aDouble) {
            super(anInt, aDouble);
        }
    }


    @Override
    protected List getFieldOrder() {
        return Arrays.asList("anInt", "aDouble");
    }
}