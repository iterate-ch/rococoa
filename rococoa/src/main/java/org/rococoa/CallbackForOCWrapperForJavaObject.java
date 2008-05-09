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

import java.lang.reflect.Method;

import org.rococoa.cocoa.NSInvocation;
import org.rococoa.cocoa.NSMethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Memory;

/**
 * Callback from Objective-C when a method has been invoked on our proxy.
 * 
 * @author duncan
 *
 */
@SuppressWarnings("nls")
public class CallbackForOCWrapperForJavaObject {

    private static Logger logging = LoggerFactory.getLogger("org.rococoa.callback");

    private final Object javaObject;
    
    /**
     * Called when method has been invoked on OC proxy and needs to be forwarded to javaObject
     */
    public final Foundation.SelectorInvokedCallback selectorInvokedCallback = 
        new  Foundation.SelectorInvokedCallback() {
            public void callback(String selectorName, ID nsInvocation) {
                logging.trace("callback invoking {} on {}", selectorName, javaObject);
                callMethod(javaObject, selectorName, 
                        Rococoa.wrap(nsInvocation, NSInvocation.class));
            };        
    };
    
    /**
     * Called when method is about to be invoked on OC proxy and needs a method signature as String
     * 
     * @see http://www.cocoadev.com/index.pl?NSMethodSignature
     */
    public final Foundation.MethodSignatureCallback methodSignatureCallback = 
        new Foundation.MethodSignatureCallback() {
            public String callback(String selectorName) {
                logging.trace("callback wanting methodSignature for selector {}", selectorName);
                return methodSignatureForSelector(selectorName);
            }
    };
    
    public CallbackForOCWrapperForJavaObject(Object javaObject) {
        this.javaObject = javaObject;
    }

    protected String methodSignatureForSelector(String selectorName) {
        Method method = methodForSelector(selectorName);
        return method != null ? 
                ocMethodSignatureAsString(method):
                null;
    }

    private void callMethod(Object o, String selectorName, NSInvocation invocation) {
        try {
            Method method = methodForSelector(selectorName);
            
            NSMethodSignature nsMethodSignature = invocation.methodSignature();
            String typeToReturnToObjC = nsMethodSignature.methodReturnType();

            if (nsMethodSignature.numberOfArguments() - method.getParameterTypes().length != 2) // self, _cmd
                throw new NoSuchMethodException(String.format(
                        "Number of arguments mismatch for invocation  of selector %s (%s arguments supplied), method %s expects %s",
                        selectorName, nsMethodSignature.numberOfArguments(), method.getName(), method.getParameterTypes().length));
            
            if (typeToReturnToObjC.equals("v") && method.getReturnType() != void.class)
                throw new NoSuchMethodException(String.format(
                        "Selector %s expects void return, but method %s returns %s",
                        selectorName, method.getName(), method.getReturnType()));
                
            if (method.getReturnType() == void.class && !(typeToReturnToObjC.equals("v")))
                throw new NoSuchMethodException(String.format(
                        "Method %s returns void, but  selector %s expects %s",
                        method.getName(), selectorName, typeToReturnToObjC));
                
            Object[] marshalledArgs = argsForFrom(method, invocation, nsMethodSignature);
            Object result  = method.invoke(o, marshalledArgs);
            putResultIntoInvocation(invocation, typeToReturnToObjC, result);
        } catch (Exception e) {
            logging.error("Exception calling method", e);
            throw new RuntimeException(e);
        }
    }

    private void putResultIntoInvocation(NSInvocation invocation, String typeToReturnToObjC, Object result) {
        if (typeToReturnToObjC.equals("v")) // void
            return;
        
        Memory buffer;
    
        // TODO - just this one conversion for now
        if (result instanceof ID && typeToReturnToObjC.equals("@")) {
            buffer = new Memory(4);
            buffer.setInt(0, ((ID) result).intValue());
        } else {
            throw new IllegalStateException(
                    String.format("Don't (yet) know how to marshall %s as Objective-C type %s", result, typeToReturnToObjC));
        }
            
        invocation.setReturnValue(buffer);
    }

    private Object[] argsForFrom(Method method, NSInvocation invocation, NSMethodSignature nsMethodSignature) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] result = new Object[parameterTypes.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = javaObjectForObjCArgument(parameterTypes, 
                    invocation,
                    nsMethodSignature,
                    i);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Object javaObjectForObjCArgument(Class<?>[] parameterTypes,  NSInvocation invocation, NSMethodSignature nsMethodSignature, int index) {
        int indexAccountingForSelfAndCmd = 2 + index;
        String objCTypeString = nsMethodSignature.getArgumentTypeAtIndex(indexAccountingForSelfAndCmd); // self and _cmd
        Class<?> parameterType = parameterTypes[index];
        
        if (objCTypeString.equals("@")) {
            Memory buffer = new Memory(4);
            invocation.getArgument_atIndex(buffer, indexAccountingForSelfAndCmd);
            ID id = new ID(buffer.getInt(0));
            if (parameterType == ID.class)
                return id;
            if (NSObject.class.isAssignableFrom(parameterType))
                return Rococoa.wrap(id, (Class<? extends NSObject>)parameterType);
        }
        if (objCTypeString.equals("i")) {
            Memory buffer = new Memory(4);
            invocation.getArgument_atIndex(buffer, indexAccountingForSelfAndCmd);
            return buffer.getInt(0);
        }
        throw new IllegalStateException(
                String.format("Don't (yet) know how to marshall Objective-C type %s as %s", objCTypeString, parameterType));
    }

    protected Method methodForSelector(String selectorName) {
        int parameterCount = countColons(selectorName);
        String methodName = methodNameForSelector(selectorName);
        try {
            Method[] methods = javaObject.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName) && method.getParameterTypes().length == parameterCount)
                    return method;
            }
            return null;
        } catch (Exception e) {
            logging.error("Exception finding methodForSelector", e);
            return null;
        }
    }

    private int countColons(String selectorName) {
        int result = 0;
        for (int i = 0; i < selectorName.length(); i++) {
            if (selectorName.charAt(i) == ':')
                result++;
        }
        return result;
    }
    
    private String methodNameForSelector(String selectorName) {
        String candidate =  selectorName.replaceAll(":", "_");
        return candidate.endsWith("_") ? 
                candidate.substring(0, candidate.length() - 1) :
                candidate;
    }

    protected String ocMethodSignatureAsString(Method method) {
        StringBuilder result = new StringBuilder();
        result.append(stringForType(method.getReturnType()));
        result.append("@:"); // self and cmd - id and selector
        for (Class<?> parameterType : method.getParameterTypes()) {
            result.append(stringForType(parameterType));
        }
        return result.toString();
    }

    private String stringForType(Class<?> clas) {
        if (clas == void.class)
            return "v";
        if (clas == int.class)
            return "i";
        if (clas == ID.class)
            return "@";
        if (NSObject.class.isAssignableFrom(clas))
            return "@";
        logging.error("Unable to give Objective-C type string for {}", clas);
        return null;
    }




}
