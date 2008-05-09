package org.rococoa.cocoa;

import org.rococoa.*;

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
}
