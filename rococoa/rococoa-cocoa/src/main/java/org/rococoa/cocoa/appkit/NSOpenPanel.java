package org.rococoa.cocoa.appkit;

import org.rococoa.ID;

import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.RunOnMainThread;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSString;

public @RunOnMainThread abstract class NSOpenPanel extends NSObject {
    
    public static final int NSOKButton = 1;
    public static final int NSCancelButton = 0;
    
    public static final _Class CLASS = Rococoa.createClass("NSOpenPanel",  _Class.class); //$NON-NLS-1$
    public @RunOnMainThread abstract class _Class extends NSObject._class_ {
        public abstract NSOpenPanel openPanel();
    }
    
    public abstract int runModalForTypes(NSArray arrayOfTypeStrings);
	
    public abstract NSString filename();
    public abstract NSArray filenames();
	
    public abstract void setDelegate(ID ocProxy);
}
