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

import java.lang.reflect.Proxy;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Enhancer;

/**
 * Static factory for creating Java wrappers for Objective-C instances, and Objective-C
 * wrappers for Java instances. <strong>START HERE</strong>.
 * 
 * @author duncan
 *
 */
public abstract class Rococoa  {

    /**
     * Create a Java NSClass representing the Objective-C class with ocClassName
     */
    public static <T extends NSClass> T createClass(String ocClassName, Class<T> type) {
        return wrap(Foundation.getClass(ocClassName), type, false);
    }
    
    /**
     * Create a Java NSObject representing an instance of the Objective-C class
     * ocClassName. The Objective-C instance is created by calling the static 
     * factory ocFactoryName, passing args.
     */
    public static <T extends NSObject> T create(String ocClassName, Class<T> javaClass, String ocFactoryName, Object... args) {
        boolean weOwnObject = weOwnObjectGivenFactoryName(ocFactoryName);
        
        // If we don't own the object we know that it has been autorelease'd
        // But we need to own these objects, so that they are not dealloc'd when
        // the pool is release'd. So we retain them.
        // Objects that we own (because they were created with 'alloc' or 'new')
        // have not been autorelease'd, so we don't retain them.
        boolean retain = !weOwnObject;
        return create(ocClassName, javaClass, ocFactoryName, retain, args);
    }
    
    /**
     * Create a Java NSObject representing an instance of the Objective-C class
     * ocClassName, created with the class method <code>+new</code>.
     */
    public static <T extends NSObject> T create(String ocClassName, Class<T> javaClass) {
        return create(ocClassName, javaClass, "new");
    }

    private static boolean weOwnObjectGivenFactoryName(String ocFactoryName) {
        // From Memory Management Programming Guide for Cocoa
        // This is the fundamental rule:
        // You take ownership of an object if you create it using a method whose
        // name begins with “alloc” or “new” or contains “copy” (for example,
        // alloc, newObject, or mutableCopy), or if you send it a retain
        // message. You are responsible for relinquishing ownership of objects
        // you own using release or autorelease. Any other time you receive an
        // object, you must not release it.
        return ocFactoryName.startsWith("alloc") || ocFactoryName.startsWith("new");
    }
    
    private static <T extends NSObject> T create(String ocClassName, Class<T> javaClass,
            String ocFactoryName, 
            boolean retain,
            Object... args) {
        ID ocClass = Foundation.getClass(ocClassName);
        ID ocInstance = Foundation.send(ocClass, ocFactoryName, ID.class, args);
        checkRetainCount(ocInstance, 1);
        T result = wrap(ocInstance, javaClass, retain);
        checkRetainCount(ocInstance, retain ? 2 : 1);
        return result;
    }
    
    /**
     * Create a Java NSObject wrapping an existing Objective-C instance, represented
     * by id.
     * 
     * The NSObject is retained, and released when the object is GC'd.
     */
    public static <T extends NSObject> T wrap(ID id, Class<T> javaClass) {
        return wrap(id, javaClass, true);
    }

    /**
     * Create a Java NSObject down-casting an existing NSObject to a more derived
     * type.
     */
    public static <T extends NSObject> T cast(NSObject object, Class<T> desiredType) {
        return wrap(object.id(), desiredType, true);
    }

    protected static <T extends NSObject> T wrap(ID id, Class<T> javaClass, boolean retain) {
        // Why would we not want to retain? Well if we are wrapping a Core Foundation
        // created object, or one created with new (allow init), it will not
        // have been autorelease'd. 
        NSObjectInvocationHandler invocationHandler = new NSObjectInvocationHandler(id, javaClass, retain);
        return createProxy(javaClass, invocationHandler);        
    }
    
    /**
     * Return the ID of a new Objective-C object which will forward messages to
     * javaObject.
     * 
     * Keep hold of the ID all the time that methods may be invoked on the Obj-C
     * object, otherwise the callbacks may be GC'd, with amusing consequences.
     */
    public static ID wrap(Object javaObject) {
        // TODO - could we set up some interesting weak-reference to javaObject, allowing the
        // callbacks to be GC'd once it has been let go?
        OCInvocationCallbacks callbacks = new OCInvocationCallbacks(javaObject);
        ID idOfOCProxy = Foundation.createOCProxy(callbacks.selectorInvokedCallback, callbacks.methodSignatureCallback);
        return new WrapperID(idOfOCProxy, callbacks);
    }
    
    /**
     * Create a java.lang.reflect.Proxy or cglib proxy of type, which forwards
     * invocations to invococationHandler.
     */
    @SuppressWarnings("unchecked")
    private static <T> T createProxy(final Class<T> type, NSObjectInvocationHandler invocationHandler) {
        if (type.isInterface()) {
            return (T) Proxy.newProxyInstance(
                invocationHandler.getClass().getClassLoader(), 
                new Class[] {type}, invocationHandler);
        } else {
            Enhancer e = new Enhancer();
            e.setUseCache(true); // make sure that we reuse if we've already defined
            e.setNamingPolicy(new DefaultNamingPolicy() {
                public String getClassName(String prefix, String source, Object key, Predicate names) {
                    if (source.equals(net.sf.cglib.proxy.Enhancer.class.getName()))
                        return type.getName() + "$$ByRococoa";
                    else 
                        return super.getClassName(prefix, source, key, names);
                }});
            e.setSuperclass(type);
            e.setCallback(invocationHandler);
            return (T) e.create();            
        }
    }
    
    // Public only because JNA doesn't call setAccessible to access ctor.
    public static class WrapperID extends ID {
        // used to prevent callbacks being GC'd as long as we hang onto this ID
        @SuppressWarnings("unused")
        private OCInvocationCallbacks callbacks;
        
        public WrapperID() {
            // required by jna
        }
        
        public WrapperID(ID anotherID, OCInvocationCallbacks callbacks) {
            super(anotherID.intValue());
            this.callbacks = callbacks;
        }
    }
    
    private static void checkRetainCount(ID ocInstance, int expected) {
        int retainCount = Foundation.cfGetRetainCount(ocInstance);
        if (retainCount != expected)
            throw new IllegalStateException("Created an object which had a retain count of " + retainCount + " not " + expected);
    }
    
    /**
     * Enforce static factory-ness.
     */
    private Rococoa() {
        
    }

}
