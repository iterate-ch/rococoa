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

import net.sf.cglib.proxy.Enhancer;

public abstract class Rococoa  {

    public static <T extends NSObject> T create(String ocClassName, Class<T> javaClass, String ocFactoryName, Object... args) {
        ProxyForOC invocationHandler = new ProxyForOC(ocClassName, javaClass, ocFactoryName, args);
        return createProxy(javaClass, invocationHandler);
    }

    @SuppressWarnings("unchecked")
    private static <T> T createProxy(Class<T> type, ProxyForOC invocationHandler) {
        if (type.isInterface()) {
            return (T) Proxy.newProxyInstance(
                invocationHandler.getClass().getClassLoader(), 
                new Class[] {type}, invocationHandler);
        } else {
            Enhancer e = new Enhancer();
            e.setUseCache(true); // make sure that we reuse if we've already defined
            e.setSuperclass(type);
            e.setCallback(invocationHandler);
            return (T) e.create();            
        }
    }
    
    public static <T extends NSObject> T create(String ocClassName, Class<T> javaClass) {
        ProxyForOC invocationHandler = new ProxyForOC(ocClassName, javaClass);
        return createProxy(javaClass, invocationHandler);
    }
        
    public static <T extends NSObject> T  wrap(ID id, Class<T> javaClass) {
        ProxyForOC invocationHandler = new ProxyForOC(id, javaClass);
        return createProxy(javaClass, invocationHandler);
    }

    public static <T extends NSObject> T cast(NSObject object, Class<T> desiredType) {
        return wrap(object.id(), desiredType);
    }

    public static <T extends NSClass> T createClass(String ocClassName, Class<T> type) {
        return wrap(Foundation.nsClass(ocClassName), type);
    }


}
