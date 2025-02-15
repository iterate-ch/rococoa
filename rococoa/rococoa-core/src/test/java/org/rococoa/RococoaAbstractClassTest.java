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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.test.RococoaTestCase;


public class RococoaAbstractClassTest extends RococoaTestCase {

    public static abstract class NSNumberAsClass extends NSObject {

        public static final _Class CLASS = Rococoa.createClass("NSNumber", _Class.class); //$NON-NLS-1$

        public interface _Class extends ObjCClass {
            NSNumberAsClass numberWithInt(int value);
        }

        public static NSNumberAsClass numberWithInt(int value) {
            return CLASS.numberWithInt(value);
        }

        public abstract int intValue();

        public int twice() {
            return 2 * intValue();
        }

    }

    @Test
    public void test() {
        NSNumberAsClass number = NSNumberAsClass.numberWithInt(42);
        assertEquals(42, number.intValue());
        assertEquals(84, number.twice());
    }

    @Test
    public void testByteBuddyReusesClasses() {
        NSNumberAsClass number = NSNumberAsClass.numberWithInt(42);
        NSNumberAsClass number2 = NSNumberAsClass.numberWithInt(42);
        assertSame(number.getClass(), number2.getClass());
    }

}
