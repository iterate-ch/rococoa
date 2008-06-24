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

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.FromNativeContext;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

/**
 * A TypeMapper that knows how to convert NSObject to and from ID.
 * 
 * @author duncan
 *
 */
class RococoaTypeMapper extends DefaultTypeMapper {
    
    private class NSObjectTypeConverter implements TypeConverter {

        private final Class<?> javaType;

        public NSObjectTypeConverter(Class<?> javaType) {
            this.javaType = javaType;
        }


@SuppressWarnings("unchecked")
public Object fromNative(Object nativeValue, FromNativeContext context) {
    if (nativeValue == null)
        return null;            
    // TODO - NativeLong surely?
    Integer nativeAsInteger = (Integer) nativeValue;
    return Rococoa.wrap(new ID(nativeAsInteger.intValue()), (Class<? extends NSObject>) javaType);
}

        public Class<?> nativeType() {
            return Integer.class;
        }

        public Object toNative(Object value, ToNativeContext context) {
            if (value == null)
                return null;
            return new Integer(((NSObject) value).id().intValue());
        }
    }
    
    private class StringTypeConverter implements TypeConverter {

        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (nativeValue == null)
                return null;            
            Integer nativeAsInteger = (Integer) nativeValue;
            return Foundation.toString(new ID(nativeAsInteger.intValue()));
        }

        public Class<?> nativeType() {
            return Integer.class;
        }

        public Object toNative(Object value, ToNativeContext context) {
            if (value == null)
                return null;
            return new Integer(Foundation.cfString((String) value).intValue());
        }
    }

    // work in progress
    private class ObjectByReferenceConverter implements TypeConverter {
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            throw new UnsupportedOperationException();
        }

        public Class<?> nativeType() {
            return IDByReference.class;
        }

        public Object toNative(Object value, ToNativeContext context) {
            if (value == null)
                return null;
            return new IDByReference();
        }
        
    }
    
    public RococoaTypeMapper() {        
        addToNativeConverter(NSObject.class, new NSObjectTypeConverter(NSObject.class));
        addTypeConverter(String.class, new StringTypeConverter());
        addToNativeConverter(NSObjectByReference.class, new ObjectByReferenceConverter());
            // not actually used at present because NSObjectInvocationHandler does marshalling
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public FromNativeConverter getFromNativeConverter(Class javaType) {
        // return a new converter that knows the subtype it is going to create
        if (NSObject.class.isAssignableFrom(javaType))
            return new NSObjectTypeConverter(javaType);
        return super.getFromNativeConverter(javaType);
    }

}
