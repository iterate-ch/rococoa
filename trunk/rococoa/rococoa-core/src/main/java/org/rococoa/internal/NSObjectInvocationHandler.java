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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.IDByReference;
import org.rococoa.NSObject;
import org.rococoa.NSObjectByReference;
import org.rococoa.ReturnType;
import org.rococoa.Rococoa;
import org.rococoa.RunOnMainThread;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Pointer;

/**
 * Listens to invocations of methods on a Java NSObject, and forwards them to
 * its Objective-C counterpart.
 * 
 * @author duncan
 *
 */
@SuppressWarnings("nls")
public class NSObjectInvocationHandler implements InvocationHandler, MethodInterceptor {
    
    private static Logger logging = LoggerFactory.getLogger("org.rococoa.proxy");
    
    static final Method OBJECT_TOSTRING;
    static final Method OBJECT_HASHCODE;
    static final Method OBJECT_EQUALS;
    static final Method OCOBJECT_ID;
    
    static {
        try {
            OBJECT_TOSTRING = Object.class.getMethod("toString");
            OBJECT_HASHCODE = Object.class.getMethod("hashCode");
            OBJECT_EQUALS = Object.class.getMethod("equals", Object.class);
            OCOBJECT_ID = NSObject.class.getMethod("id");
        }
        catch (Exception x) {
            throw new RuntimeException("Error retrieving method", x);
        }
    }

    private final ID ocInstance;
    private final String javaClassName;
    private final boolean invokeOnMainThread;
    private final boolean dontFinalize;
    
    private volatile boolean finalized;

    public NSObjectInvocationHandler(final ID ocInstance, Class<? extends NSObject> javaClass, boolean retain) {
        this.ocInstance = ocInstance;
        this.javaClassName = javaClass.getSimpleName();
        invokeOnMainThread = shouldInvokeOnMainThread(javaClass);
        dontFinalize = shouldNotFinalize(javaClass);

        if (logging.isTraceEnabled()) {
            int retainCount = Foundation.cfGetRetainCount(ocInstance);
            logging.trace("Creating NSObjectInvocationHandler for id {}, javaclass {}. retain = {}, retainCount = {}", 
                    new Object[] {ocInstance, javaClass, retain, retainCount});
        }

        if (ocInstance.isNull()) {
            throw new NullPointerException();
        }

        if (retain) {
            if (invokeOnMainThread) {
                Foundation.runOnMainThread(new Runnable() {
                    public void run() {
                        Foundation.cfRetain(ocInstance);                    
                    }}); 
            } else {
                Foundation.cfRetain(ocInstance);                                
            }
        }
    }
    
    private boolean shouldInvokeOnMainThread(Class<? extends NSObject> javaClass) {
        return javaClass.getAnnotation(RunOnMainThread.class) != null;
    }

    private boolean shouldNotFinalize(Class<? extends NSObject> javaClass) {
        return javaClass == NSAutoreleasePool.class; // see NSAutoreleasePoolThreadTest
    }

    @Override
    protected void finalize() throws Throwable {
        if (finalized || dontFinalize)
            return;
        try {
            if (invokeOnMainThread) {
                Foundation.runOnMainThread(new Runnable() {
                    public void run() {
                        release();                   
                    }}); 
            } else {
                release();                    
            }
            super.finalize();
        } finally {
            finalized = true;
        }
    }

    // must be run on appropriate thread
    private void release() {
        if (ocInstance.isNull())
            return;
        if (logging.isTraceEnabled()) {
            int retainCount = Foundation.cfGetRetainCount(ocInstance);
            logging.trace("finalizing [{} {}], releasing with retain count = {}", 
                    new Object[] {javaClassName, ocInstance, retainCount});
        }
        Foundation.cfRelease(ocInstance);                    
    }
    
    /**
     * Callback from java.lang.reflect proxy
     */    
    public Object invoke(Object proxy, final Method method, final Object[] args)  throws Throwable {
        if (logging.isTraceEnabled()) {
            logging.trace("invoking [{} {}].{}({})", 
                    new Object[] {javaClassName, ocInstance, method.getName(), new VarArgsUnpacker(args)});
        }
        if (isSpecialMethod(method))
            return invokeSpecialMethod(method, args);        
        return invokeCococaOnThisOrMainThread(method, args);
    }

    /**
     * Callback from cglib proxy
     */
    public Object intercept(Object proxy, final Method method, final Object[] args, MethodProxy methodProxy) throws Throwable {
        if (logging.isTraceEnabled()) {
            logging.trace("invoking [{} {}].{}({})", 
                    new Object[] {javaClassName, ocInstance, method.getName(), new VarArgsUnpacker(args)});
        }
        if (isSpecialMethod(method))
            return invokeSpecialMethod(method, args);
        if (!Modifier.isAbstract(method.getModifiers())) {
            // method is not abstract, so a Java override has been provided, which we call
            return methodProxy.invokeSuper(proxy, args);
        }
        // normal case
        return invokeCococaOnThisOrMainThread(method, args);            
    }

    private boolean isSpecialMethod(Method method) {
        return (OBJECT_TOSTRING.equals(method) || 
                OBJECT_EQUALS.equals(method) ||
                OCOBJECT_ID.equals(method));        
    }

    private Object invokeSpecialMethod(final Method method, final Object[] args) {
        if (OBJECT_TOSTRING.equals(method))
            return invokeDescription();
        if (OBJECT_EQUALS.equals(method)) {
            if (args[0] == null)
                return false;
            if (args[0] instanceof NSObject)
                return invokeIsEqual(((NSObject) args[0]).id());
            return false;
        }
        if (OCOBJECT_ID.equals(method))
            return ocInstance;
        throw new IllegalArgumentException("Not a special method " + method);
    }    
    
    private Object invokeDescription() {
        if (invokeOnMainThread) {
            return Foundation.callOnMainThread(
                new Callable<Object>() {
                    public Object call() {
                        return Foundation.send(ocInstance, "description", String.class);
                    }});
        } else {
            return Foundation.send(ocInstance, "description", String.class);
        }
    }
    
    private Object invokeIsEqual(final ID another) {
        if (invokeOnMainThread) {
            return Foundation.callOnMainThread(
                new Callable<Object>() {
                    public Object call() {
                        return Foundation.send(ocInstance, "isEqual:", Boolean.class, another);
                    }});
        } else {
            return Foundation.send(ocInstance, "isEqual:", Boolean.class, another);
        }
    }

    private Object invokeCococaOnThisOrMainThread(final Method method, final Object[] args) {
        if (invokeOnMainThread) {
            return Foundation.callOnMainThread(
                new Callable<Object>() {
                    public Object call() {
                        return invokeCococoa(method, args);
                    }});
        } else {
            return invokeCococoa(method, args);
        }
    }

    private Object invokeCococoa(final Method method, final Object[] args) {
        final String selectorName = selectorNameFor(method);
        final Class<?> returnType = returnTypeFor(method);
        final Object[] marshalledArgs = marshallArgsFor(method, args);

        Object result = Foundation.send(ocInstance, selectorName, returnType, marshalledArgs);
        fillInReferences(args, marshalledArgs);
        
        if (result instanceof Pointer && method.getReturnType().equals(String.class))
            // special case for return char*
            return ((Pointer) result).getString(0);
        else
            return result;
    }

    /**
     * We need to make sure that we have filled in all NSObjectByReferences 
     * so that they are retained.
     */
    private void fillInReferences(Object[] args, Object[] marshalledArgs) {
        if (args == null)
            return;
        for (int i = 0; i < args.length; i++) {
            Object original = args[i];
            Object marshalled = marshalledArgs[i];
            if (marshalled instanceof IDByReference) {
                if (!(original instanceof NSObjectByReference)) {
                    logging.error("Bad marshalling");
                    continue;
                }
                ((NSObjectByReference) original).setObject(
                   Rococoa.wrap(((IDByReference) marshalled).getValue(), NSObject.class));
            }
        }
    }

    private Class<?> returnTypeFor(final Method method) {
        ReturnType annotation = method.getAnnotation(ReturnType.class);
        if (annotation == null)
            return method.getReturnType();
        else
            return annotation.value();
    }

    private Object[] marshallArgsFor(Method method, Object[] args) {
        if (args == null)
            return null;
        List<Object> result = new ArrayList<Object>();
        for (int i = 0; i < args.length; i++) {
            Object marshalled = marshall(args[i]);
            if (marshalled instanceof Object[])
                // flatten varags, it would never(?) make sense to pass Object[] to Cococoa
                result.addAll(Arrays.asList((Object[]) marshalled));
            else
                result.add(marshalled);
        }
        return result.toArray(new Object[result.size()]);
    }

    private Object marshall(Object arg) {
        // Note that this is not the only marshalling that is done. 
        // RococoaTypeMapper also gets involved.
        if (arg == null)
            return null;
        if (arg instanceof NSObjectByReference)
            return new IDByReference(); // this will be filled in with an id by
                // the called code, and then marshalled to an NSObject by fillInReferences
            // TODO - passing existing inout, not just out
        return arg;
    }

    private String selectorNameFor(Method method) {
        String methodName = method.getName();
        if (methodName.endsWith("_")) // lets us append _ to allow Java keywords as method names
            methodName = methodName.substring(0, methodName.length() - 1);
        if (method.getParameterTypes().length == 0)
            return methodName;
        String[] parts = methodName.split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            result.append(part).append(":");
        }
        return result.toString();
    }
    
}
