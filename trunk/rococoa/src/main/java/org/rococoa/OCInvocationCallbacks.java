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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.rococoa.cocoa.NSInvocation;
import org.rococoa.cocoa.NSMethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * Holds the callbacks called when a method is invoked on an Objective-C proxy
 * for a Java object.
 * 
 * When a message is sent to an OC object first it is sent respondsToSelector: 
 * then methodSignatureForSelector: Our Obj-C proxy forwards these to
 * methodSignatureCallback; we build a method signature string in Java 
 * corresponding to the Java method and return it.
 * 
 * The object is then sent forwardInvocation: passing an NSInvocation. It
 * forwards this to selectorInvokedCallback, which we use to invoke the method
 * on the Java Object.
 * 
 * @author duncan
 *
 */
@SuppressWarnings("nls")
class OCInvocationCallbacks {

    private static Logger logging = LoggerFactory.getLogger("org.rococoa.callback");

    private final Object javaObject;
    
    /**
     * Called when method is about to be invoked on OC proxy and needs a method signature as String
     * 
     * @see http://www.cocoadev.com/index.pl?NSMethodSignature
     */
    public final RococoaLibrary.MethodSignatureCallback methodSignatureCallback = 
        new RococoaLibrary.MethodSignatureCallback() {
            public String callback(String selectorName) {
                logging.trace("callback wanting methodSignature for selector {}", selectorName);
                return methodSignatureForSelector(selectorName);
            }
    };
    
    /**
     * Called when method has been invoked on OC proxy and needs to be forwarded to javaObject
     */
    public final RococoaLibrary.SelectorInvokedCallback selectorInvokedCallback = 
        new RococoaLibrary.SelectorInvokedCallback() {
            public void callback(String selectorName, ID nsInvocation) {
                logging.trace("callback invoking {} on {}", selectorName, javaObject);
                callMethod(javaObject, selectorName, 
                        Rococoa.wrap(nsInvocation, NSInvocation.class));
            };        
    };
    
    public OCInvocationCallbacks(Object javaObject) {
        this.javaObject = javaObject;
    }

    protected String methodSignatureForSelector(String selectorName) {
        Method method = methodForSelector(selectorName);
        return method == null ? 
                null :
                ocMethodSignatureAsString(method);
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

    /**
     * @see http://developer.apple.com/documentation/Cocoa/Conceptual/ObjectiveC/Articles/chapter_13_section_9.html
     */
    protected String ocMethodSignatureAsString(Method method) {
        StringBuilder result = new StringBuilder();
        result.append(stringForType(method.getReturnType()));
        result.append("@:"); // self and cmd - id and selector
        for (Class<?> parameterType : method.getParameterTypes()) {
            result.append(stringForType(parameterType));
        }
        return result.toString();
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
            method.setAccessible(true); // needed if implementation is an anonymous subclass of Object
            Object result  = method.invoke(o, marshalledArgs);
            putResultIntoInvocation(invocation, typeToReturnToObjC, result);
        } catch (Exception e) {
            logging.error("Exception calling method", e);
            throw new RuntimeException(e);
        }
    }

    private String methodNameForSelector(String selectorName) {
        String candidate =  selectorName.replaceAll(":", "_");
        return candidate.endsWith("_") ? 
                candidate.substring(0, candidate.length() - 1) :
                candidate;
    }

    private Object[] argsForFrom(Method method, NSInvocation invocation, NSMethodSignature nsMethodSignature) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] result = new Object[parameterTypes.length];
        for (int i = 0; i < result.length; i++) {
            int indexAccountingForSelfAndCmd = 2 + i;
            result[i] = javaObjectForOCArgument(invocation, 
                    indexAccountingForSelfAndCmd,
                    nsMethodSignature.getArgumentTypeAtIndex(indexAccountingForSelfAndCmd), 
                    parameterTypes[i]);
        }
        return result;
    }

    /**
     * At this point we have an NSInvocation, which has the arguments to the
     * call in it. We know the type of the argument, and the type of the
     * parameter expected by the Java method. [1]
     * Our mission is to get the value of the argument from the NSInvocation
     * and return a Java object of the desired type.
     */
    @SuppressWarnings("unchecked")
    private Object javaObjectForOCArgument(NSInvocation invocation,
            int indexInInvocation, String objCArgumentTypeAsString, Class<?> javaParameterType) {
        
        // TODO - I can't help feeling that JNA's marshalling code must come in handy here.
        // also Native.toNativeSize(Class)
        Memory buffer = new Memory(4);
        invocation.getArgument_atIndex(buffer, indexInInvocation);
        
        // TODO - more conversions
        if (objCArgumentTypeAsString.equals("@")) {
            ID id = new ID(buffer.getInt(0)); // TODO - NativeLong
            if (javaParameterType == ID.class)
                return id;
            if (NSObject.class.isAssignableFrom(javaParameterType))
                return Rococoa.wrap(id, (Class<? extends NSObject>)javaParameterType);
            if (javaParameterType == String.class) {
                return Foundation.toString(id);
            }
        }
        if (objCArgumentTypeAsString.equals("i")) {
            return buffer.getInt(0);
        }
        if (objCArgumentTypeAsString.equals("c")) {
            byte character = buffer.getByte(0);
            if (javaParameterType == boolean.class)
                return character == 0 ? Boolean.FALSE : Boolean.TRUE;
            else
                return character;            
        }
        if (Structure.class.isAssignableFrom(javaParameterType)) {
            if (Structure.ByValue.class.isAssignableFrom(javaParameterType))
                return readStructureByValue(invocation, indexInInvocation, 
                        objCArgumentTypeAsString, (Class<? extends Structure>) javaParameterType);
            else
                return readStructureByReference(invocation, indexInInvocation, 
                        objCArgumentTypeAsString, (Class<? extends Structure>) javaParameterType);
                
        }
        throw new IllegalStateException(
                String.format("Don't (yet) know how to marshall parameter Objective-C type %s as %s", objCArgumentTypeAsString, javaParameterType));
    }

    private Structure readStructureByValue(NSInvocation invocation, int indexInInvocation, 
            String objCArgumentTypeAsString, Class<? extends Structure> javaParameterType)
    {
        Structure result = newInstance(javaParameterType);
        Memory buffer = new Memory(result.size());
        invocation.getArgument_atIndex(buffer, indexInInvocation);
        return copyBufferToStructure(buffer, result);
    }
    
    private Structure readStructureByReference(NSInvocation invocation, int indexInInvocation, 
            String objCArgumentTypeAsString, Class<? extends Structure> javaParameterType)
    {
        Memory buffer = new Memory(Native.POINTER_SIZE);
        invocation.getArgument_atIndex(buffer, indexInInvocation);
        Pointer pointerToResult = buffer.getPointer(0);
        Structure result = newInstance(javaParameterType);        
        return copyBufferToStructure(pointerToResult, result);
    }

    @SuppressWarnings("unchecked")
    private <T> T newInstance(Class<?> clas) {
        try {
            return (T) clas.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate " + clas,  e);
        }
    }

    private Structure copyBufferToStructure(Pointer buffer, Structure structure) {
        int byteCount = structure.size();
        memcpy(structure.getPointer(), buffer, byteCount);
        structure.read();
        return structure;
    }

    private void memcpy(Pointer dest, Pointer src, int byteCount) {
        memcpyViaByteBuffer(dest, src, byteCount);
    }

    @SuppressWarnings("unused") // kept as naive implementation
    private void memcpyViaArray(Pointer dest, Pointer src, int byteCount) {
        byte[] structBytes = new byte[byteCount];
        src.read(0, structBytes, 0, byteCount);
        dest.write(0, structBytes, 0, byteCount);
    }

    private void memcpyViaByteBuffer(Pointer dest, Pointer src, int byteCount) {
        ByteBuffer destBuffer = dest.getByteBuffer(0, byteCount);
        ByteBuffer srcBuffer = src.getByteBuffer(0, byteCount);
        destBuffer.put(srcBuffer);
    }
    
    private void putResultIntoInvocation(NSInvocation invocation, String typeToReturnToObjC, Object result) {
        if (typeToReturnToObjC.equals("v")) {
            if (result != null)
                throw new IllegalStateException("Java method returned a result, but expected void");// void
            return;
        }
        
        Memory buffer = bufferForReturn(typeToReturnToObjC, result);
        if (buffer == null)
            throw new IllegalStateException(
                    String.format("Don't (yet) know how to marshall result %s as Objective-C type %s", result, typeToReturnToObjC));
            
        invocation.setReturnValue(buffer);
    }

    private Memory bufferForReturn(String typeToReturnToObjC, Object methodCallResult) {
    
        // TODO - more conversions
        if (typeToReturnToObjC.equals("@")) {
            Memory buffer = new Memory(4);
            if (methodCallResult instanceof ID)
                buffer.setInt(0, ((ID) methodCallResult).intValue());
            else if (methodCallResult instanceof String)
                buffer.setInt(0, Foundation.cfString((String) methodCallResult).intValue());
            return buffer;
        }
        if (typeToReturnToObjC.equals("c")) {
            Memory buffer = new Memory(1);
            if (methodCallResult instanceof Boolean)
                buffer.setByte(0, ((Boolean) methodCallResult) ? (byte) 1 : (byte) 0);
            else if (methodCallResult instanceof Byte)
                buffer.setByte(0, ((Byte) methodCallResult).byteValue());
            else 
                return null;
            return buffer;
        }
        if (methodCallResult instanceof Structure) {
            if (methodCallResult instanceof Structure.ByValue)
                return bufferForStructureByValue((Structure) methodCallResult);
            else
                return bufferForStructureByReference((Structure) methodCallResult);
        }
        return null;
    }

    private Memory bufferForStructureByValue(Structure methodCallResult) {
        methodCallResult.write();
        int byteCount = methodCallResult.size();
        Memory buffer = new Memory(byteCount);
        memcpy(buffer, methodCallResult.getPointer(), byteCount);
        return buffer;
    }

    private Memory bufferForStructureByReference(Structure methodCallResult) {
        methodCallResult.write();
        Memory buffer = new Memory(Native.POINTER_SIZE);
        buffer.setPointer(0, methodCallResult.getPointer());
        return buffer;
    }

    private int countColons(String selectorName) {
        int result = 0;
        for (int i = 0; i < selectorName.length(); i++) {
            if (selectorName.charAt(i) == ':')
                result++;
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private String stringForType(Class<?> clas) {
        if (clas == void.class)
            return "v";
        if (clas == int.class)
            return "i";
        if (clas == ID.class)
            return "@";
        if (clas == byte.class)
            return "c";
        if (NSObject.class.isAssignableFrom(clas))
            return "@";
        if (clas == boolean.class)
            return "c"; // Cocoa BOOL is defined as signed char
        if (clas == String.class)
            return "@";
        if (clas == double.class)
            return "d";
        if (clas == float.class)
            return "f";
        if (Structure.class.isAssignableFrom(clas))
            return encodeStruct((Class<? extends Structure>) clas);
        logging.error("Unable to give Objective-C type string for {}", clas);
        return null;
    }

    private String encodeStruct(Class<? extends Structure> clas) {
        StringBuilder result = new StringBuilder();
        if (!(Structure.ByValue.class.isAssignableFrom(clas)))
            result.append('^'); // pointer to
            
        result.append('{').append(clas.getSimpleName()).append('=');
        for (Field f : collectStructFields(clas, new ArrayList<Field>())) {
            result.append(stringForType(f.getType()));
        }
        return result.append('}').toString();
    }

    @SuppressWarnings("unchecked")
    private List<Field> collectStructFields(Class<? extends Structure> clas, List<Field> list) {
        if (clas == Structure.class)
            return list;
        for (Field f : clas.getDeclaredFields()) {
            list.add(f);
        }
        return collectStructFields((Class<? extends Structure>) clas.getSuperclass(), list);
    }
    
    /*
     * [1] - http://en.wikipedia.org/wiki/Parameter_(computer_science)
     * Although parameters are also commonly referred to as arguments,
     * arguments are more properly thought of as the actual values or references
     * assigned to the parameter variables when the subroutine is called at
     * runtime. When discussing code that is calling into a subroutine, any
     * values or references passed into the subroutine are the arguments, and
     * the place in the code where these values or references are given is the
     * parameter list. When discussing the code inside the subroutine
     * definition, the variables in the subroutine's parameter list are the
     * parameters, while the values of the parameters at runtime are the
     * arguments.
     */

}
