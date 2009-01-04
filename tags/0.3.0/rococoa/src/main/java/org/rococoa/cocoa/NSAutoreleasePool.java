package org.rococoa.cocoa;

import org.rococoa.NSObject;
import org.rococoa.Rococoa;

public abstract class NSAutoreleasePool implements NSObject {

    public static NSAutoreleasePool new_() {
        return Rococoa.create("NSAutoreleasePool", NSAutoreleasePool.class);
    }
    
    public abstract void addObject(NSObject object);

}
