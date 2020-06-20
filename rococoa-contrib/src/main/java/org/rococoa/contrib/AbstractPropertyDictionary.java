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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSDictionary;
import org.rococoa.cocoa.foundation.NSMutableDictionary;
import org.rococoa.cocoa.foundation.NSNumber;
import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.cocoa.foundation.NSString;

/** Wraps an <code>NSMutableDictionary</code> with a set of keys defined by
 *  a Java enumeration. Also provides convenience methods for converting between
 *  Java and Cocoa types for the values.
 *  @param <E> the enumerated type of the keys used with this dictionary
 */
public abstract class AbstractPropertyDictionary<E extends Enum<E> & NativeEnum<?>> {
    /**Used to convert dates between Cocoa and Java*/
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS Z";
    /** The data dictionary we are providing convenient access to*/
    private final NSMutableDictionary data;

    /** Construct a new empty dictionary with the given initial capacity.
     *  @param initialCapacity the initial capacity to use
     */
    protected AbstractPropertyDictionary(int initialCapacity) {
        this.data = NSMutableDictionary.dictionaryWithCapacity(initialCapacity);
    }
    /** Construct a new dictionary with the given dictionary.
     *  The dictionary is <strong>not</strong> copied.
     *  @param data the dictionary to use
     */
    protected AbstractPropertyDictionary(final NSMutableDictionary data) {
        this.data = data;
    }
    /** Construct a new dictionary with the given dictionary.
     *  The dictionary <strong>is</strong> copied into a mutable dictionary.
     *  @param data the dictionary to use
     */
    protected AbstractPropertyDictionary(final NSDictionary data) {
        this.data = NSMutableDictionary.dictionaryWithDictionary(data);
    }
    /** Get the underlying dictionary
     *  @return the dictionary being wrapped
     */
    public NSMutableDictionary getData() {
        return data;
    }    

    /** Get the value associated with the given key as a String
     *  @param key the key whose associated value will be returned
     *  @return the value associated with the key as a Java String
     */
    public String getString(E key) {
        return data.objectForKey(key.getNativeValue()).toString();
    }

    /** Set the value associated with the given key.
     *  @param key the key whose value will be set
     *  @param value the value to set
     */
    public void setString(E key, String value) {
        data.setValue_forKey(NSString.stringWithString(value), key.getNativeValue());
    }
    
    /** Get the value associated with the given key as an int
     *  @param key the key whose associated value will be returned
     *  @return the value associated with the key as a Java int
     */
    public int getInt(E key) {
        return getValueAsType(key, NSNumber.class).intValue();
    }    

    /** Get the value associated with the given key as a short
     *  @param key the key whose associated value will be returned
     *  @return the value associated with the key as a Java short
     */
    public short getShort(E key) {
        return getValueAsType(key, NSNumber.class).shortValue();
    }    
    
    /** Get the value associated with the given key as a boolean
     *  @param key the key whose associated value will be returned
     *  @return the value associated with the key as a Java boolean
     */
    public boolean getBoolean(E key) {
        return getInt(key) != 0;
    }
    
    /** Get the value associated with the given key as a Date
     *  @param key the key whose associated value will be returned
     *  @return the value associated with the key as a Java Date
     */
    public Date getDate(E key) {
        String date = getString(key);
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(date);
        } catch (ParseException ex) {
            throw new IllegalStateException("Bad date found in property dictionary: " + date, ex);
        }
    }

    /** Set the value associated with the given key.
     *  @param key the key whose value will be set
     *  @param date the value to set
     */
    public void setDate(E key, Date date) {
        setString(key, new SimpleDateFormat(DATE_FORMAT).format(date));
    }

    /** Get the value associated with the given key as an enum
     *  @param key the key whose associated value will be returned
     *  @param nativeEnum the class of the enum, used to resolve native values to the corresponding Java enum values
     *  @return the value associated with the key as a Java Enum
     *  @param <EN> the type of enum to return
     */
    public <EN extends Enum<EN> & NativeEnum<?>> EN getEnum(Class<EN> nativeEnum, E key) {
        return NativeEnum.Resolver.fromNative(nativeEnum, data.objectForKey(key.getNativeValue()));
    }

    
    /** Get the value associated with the given key as coercing it to the given type.
     *  @param key the key whose associated value will be returned
     *  @param type the subclass of <code>NSObject</code> that the value associated with <code>key</code> will be coerced to
     *  @return the value associated with the key coerced as into <code>type</code>
     *  @param <R> the type to return
     */
    public <R extends NSObject> R getValueAsType(E key, Class<R> type) {
        NSObject result = data.objectForKey(key.getNativeValue());
        return result == null ? null : Rococoa.cast(result, type);
    }    


    /** Set the value associated with the given key.
     *  @param key the key whose value will be set
     *  @param value the value to set
     */
    public void setValue(E key, NSObject value) {
        data.setValue_forKey(value, key.getNativeValue());
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder('[' + getClass().getSimpleName());
        result.append(": ").append(data).append(']');
        return result.toString();
    }
}
