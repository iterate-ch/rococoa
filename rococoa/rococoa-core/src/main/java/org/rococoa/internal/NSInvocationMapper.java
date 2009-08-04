package org.rococoa.internal;


import org.rococoa.cocoa.foundation.NSInvocation;

import com.sun.jna.Memory;

/**
 * Maps to and from bytes in an NSInvocation to Java types.
 * 
 * @author duncan
 *
 */
public abstract class NSInvocationMapper {

    protected final Class<?> type;
    protected final String typeString;

    protected NSInvocationMapper(String typeString, Class<?> type) {
        this.type = type;
        this.typeString = typeString;
    }

    public String typeString() {
        return typeString;
    }
    
    public Object readArgumentFrom(NSInvocation invocation, int index, Class<?> type) {
        Memory buffer = new Memory(8); // big enough for long or double
        invocation.getArgument_atIndex(buffer, index);
        return readFrom(buffer, type);
    }

    protected Object readFrom(Memory buffer, Class<?> type) {
        throw new RuntimeException("Should be overriden or bypassed");
    }
    
    public abstract Memory bufferForResult(Object methodCallResult);

}
