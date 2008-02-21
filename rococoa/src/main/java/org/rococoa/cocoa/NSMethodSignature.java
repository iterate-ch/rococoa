package org.rococoa.cocoa;

import org.rococoa.NSObject;
import org.rococoa.ReturnType;

import com.sun.jna.Pointer;

public interface NSMethodSignature extends NSObject {
    
    int numberOfArguments();

    @ReturnType(Pointer.class) String getArgumentTypeAtIndex(int index);
    @ReturnType(Pointer.class) String methodReturnType();

}
