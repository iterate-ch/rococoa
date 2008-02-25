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
