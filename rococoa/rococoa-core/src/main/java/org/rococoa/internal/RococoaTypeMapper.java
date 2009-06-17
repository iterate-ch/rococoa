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


import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.FromNativeContext;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.NativeMapped;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

/**
 * A TypeMapper that knows how to convert :
 * <ul>
 *   <li>{@link NSObject} to and from an integer type with the right size to be an id.</li>
 *   <li>{@link String} to and from an integer type with the right size to be an id.</li>
 * </ul>
 * 
 * Note that nativeType is never NativeLong, but the appropriate Java primitive
 * with the right size of NativeLong. 
 * 
 * @author duncan
 *
 */
public class RococoaTypeMapper extends DefaultTypeMapper {
    
    private static final NativeMapped nativeLongConverter = new ID();
    
    private class NSObjectTypeConverter<T extends NSObject> implements TypeConverter {

        private final Class<T> javaType;

        public NSObjectTypeConverter(Class<T> javaType) {
            this.javaType = javaType;
        }


        public Class<?> nativeType() {
            // we can't return NativeLong here - has to be a primitive type, so
            // delegate so that we are 32/64 correct
            return nativeLongConverter.nativeType();
        }

        // Takes an Integer or Long representing id (32 or 64 bit respectively)
        // and returns an NSObject of javaType with that id.
        public T fromNative(Object nativeValue, FromNativeContext context) {
            if (nativeValue == null)
                return null;
            Number nativeValueAsNumber = (Number) nativeValue;
            return Rococoa.wrap(ID.fromLong(nativeValueAsNumber.longValue()), javaType);
        }
        
        // Takes an NSObject its id as Integer or Long
        public Object toNative(Object value, ToNativeContext context) {
            if (value == null)
                return null;
            NSObject valueAsNSObject = (NSObject) value;
            ID idToReturn = valueAsNSObject.id();
            return idToReturn.toNative();
        }
    }
    
    /**
     * Converts between java.lang.String and Cocooa id, which it needs to return
     * as Integer or Long depending on platform
     */
    private class StringTypeConverter implements TypeConverter {

        public Class<?> nativeType() {
            // see NSObjectTypeConverter.nativeType
            return nativeLongConverter.nativeType();
        }

        // Takes an Integer or Long representing id (32 or 64 bit respectively)
        // and returns a java.lang.String
        public String fromNative(Object nativeValue, FromNativeContext context) {
            if (nativeValue == null)
                return null;       
            Number nativeValueAsNumber = (Number) nativeValue;
            return Foundation.toString(ID.fromLong(nativeValueAsNumber.longValue()));
        }

        // Takes java.lang.String and returns value of an id as Integer or Long
        public Object toNative(Object value, ToNativeContext context) {
            if (value == null)
                return null;
            String valueAsString = (String) value;
            ID valueAsID = Foundation.cfString(valueAsString);
            return valueAsID.toNative();
        }
    }

    // work in progress
//    private class ObjectByReferenceConverter implements TypeConverter {
//        public Object fromNative(Object nativeValue, FromNativeContext context) {
//            throw new UnsupportedOperationException();
//        }
//
//        public Class<?> nativeType() {
//            return IDByReference.class;
//        }
//
//        public Object toNative(Object value, ToNativeContext context) {
//            if (value == null)
//                return null;
//            return new IDByReference();
//        }
//        
//    }
    
    public RococoaTypeMapper() {        
        addToNativeConverter(NSObject.class, new NSObjectTypeConverter<NSObject>(NSObject.class));
        addTypeConverter(String.class, new StringTypeConverter());
//        addToNativeConverter(NSObjectByReference.class, new ObjectByReferenceConverter());
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
