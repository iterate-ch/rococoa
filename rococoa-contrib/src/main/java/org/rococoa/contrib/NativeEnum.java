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
package org.rococoa.contrib;

import org.rococoa.cocoa.foundation.NSObject;

/** An interface for Enumerations that derive their actual values from native code.
 *  e.g. many header files contain groups of related String or Integer
 *  constants that have may be passed into the APIs defined in the header.
 *  @param <N> the type of native values associated with the enum
 */
public interface NativeEnum<N extends NSObject> {
    /** Get the native value associated with this enum value
     *  @return the native value associated with this enum value
     */
    public N getNativeValue();

    /** Given a native value, resolve the corresponding enum entry.
     */
    public static final class Resolver {
        private Resolver() {}
        /** Given an enum class that implements NativeEnum, resolve the Java enum
         *  value that corresponds to the native value <code>value</value>
         *  @param <E> the type of the Java enum
         *  @param nativeEnum the class of the Java enum
         *  @param value the native value to resolve
         *  @return the corresponding Java enum value
         *  @throws IllegalArgumentException if <code>value</code> does not correspond to any
         *  value in the Java enum
         */
        public static <E extends Enum<E> & NativeEnum<?>> E fromNative(Class<E> nativeEnum, NSObject value) {
            try {
                @SuppressWarnings("unchecked")
                E[] enumValues = (E[]) nativeEnum.getDeclaredMethod("values").invoke(null);
                for (E e : enumValues) {
                    if ( e.getNativeValue().equals(value) ) {
                        return e;
                    }
                }
            } catch (Exception ex) {
                throw new IllegalArgumentException("Unknown value " + value + " for " + nativeEnum.getSimpleName(), ex);
            }
            throw new IllegalArgumentException("Unknown value " + value + " for " + nativeEnum.getSimpleName());
        }
    };
}

