package org.rococoa.cocoa.foundation;

import org.rococoa.ReleaseInFinalize;
import org.rococoa.Rococoa;

public @ReleaseInFinalize(false) abstract class NSAutoreleasePool extends NSObject {

    public static NSAutoreleasePool new_() {
        return Rococoa.create("NSAutoreleasePool", NSAutoreleasePool.class);
    }
    
    public abstract void addObject(NSObject object);
    
    public abstract void drain();

}
