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

package org.rococoa.cocoa;

import org.junit.Test;
import org.rococoa.cocoa.foundation.NSMutableIndexSet;
import org.rococoa.cocoa.foundation.NSUInteger;
import org.rococoa.test.RococoaTestCase;

import static org.junit.Assert.*;

/**
 * @author David Kocher
 * @version $Id:$
 */
public class NSIndexSetTest extends RococoaTestCase {

    @Test
    public void testIndexWithRange() {
        NSMutableIndexSet index = NSMutableIndexSet.new_();
        index.addIndex(new NSUInteger(1));
        index.addIndex(new NSUInteger(2));
        assertEquals(new NSUInteger(2), index.count());
        assertFalse(index.containsIndex(new NSUInteger(0)));
        assertTrue(index.containsIndex(new NSUInteger(1)));
        assertTrue(index.containsIndex(new NSUInteger(2)));
        assertFalse(index.containsIndex(new NSUInteger(3)));
    }

    @Test
    public void testIndexWithDoubleRange() {
        NSMutableIndexSet index = NSMutableIndexSet.new_();
        index.addIndex(new NSUInteger(1));
        index.addIndex(new NSUInteger(2));
        index.addIndex(new NSUInteger(4));
        index.addIndex(new NSUInteger(5));
        assertEquals(new NSUInteger(4), index.count());
        assertFalse(index.containsIndex(new NSUInteger(0)));
        assertTrue(index.containsIndex(new NSUInteger(1)));
        assertTrue(index.containsIndex(new NSUInteger(2)));
        assertFalse(index.containsIndex(new NSUInteger(3)));
        assertTrue(index.containsIndex(new NSUInteger(4)));
        assertTrue(index.containsIndex(new NSUInteger(5)));
    }
}
