package org.rococoa.cocoa.appkit;

import org.rococoa.ID;
import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.RunOnMainThread;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSString;

public @RunOnMainThread interface NSOpenPanel extends NSObject {
    
    public static final int NSOKButton = 1;
    public static final int NSCancelButton = 0;
    
    public static final _Class CLASS = Rococoa.createClass("NSOpenPanel",  _Class.class); //$NON-NLS-1$
    public @RunOnMainThread interface _Class extends NSClass {
        public NSOpenPanel openPanel();
    }
    
    int runModalForTypes(NSArray arrayOfTypeStrings);

    NSString filename();

    NSArray filenames();
    
    void setDelegate(ID ocProxy);
}
