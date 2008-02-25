package org.rococoa.cocoa;

import org.rococoa.NSObject;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

public interface  NSInvocation extends NSObject {

    NSMethodSignature methodSignature();
    void getArgument_atIndex(Pointer receiver, int index);
    void setReturnValue(Memory buffer);
}
