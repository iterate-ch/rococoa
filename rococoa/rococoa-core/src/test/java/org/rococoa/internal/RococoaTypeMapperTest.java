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

package org.rococoa.internal;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.rococoa.ObjCObject;
import org.rococoa.cocoa.foundation.NSNumber;
import org.rococoa.test.RococoaTestCase;

import com.sun.jna.TypeMapper;

@SuppressWarnings({"unchecked", "cast"})
public class RococoaTypeMapperTest extends RococoaTestCase {

    private final TypeMapper typeMapper = new RococoaTypeMapper();

    @Test
    public void testString() {
        assertTrue(typeMapper.getToNativeConverter(String.class) instanceof StringTypeConverter);
        assertTrue(typeMapper.getFromNativeConverter(String.class) instanceof StringTypeConverter);
    }

    @Test
    public void testObjCObject() {
        // ToNative only has to get the ID, so it only has to know about ObjCObject
        ObjCObjectTypeConverter<NSNumber> toNativeConverter = (ObjCObjectTypeConverter<NSNumber>) typeMapper.getToNativeConverter(NSNumber.class);
        assertTrue(toNativeConverter.convertsJavaType(ObjCObject.class));

        // FromNative needs to know the actual type so that it can create the right Java subclass of ObjCObject
        ObjCObjectTypeConverter<NSNumber> fromNativeConverter = (ObjCObjectTypeConverter<NSNumber>) typeMapper.getFromNativeConverter(NSNumber.class);
        assertTrue(fromNativeConverter.convertsJavaType(NSNumber.class));
    }
}
