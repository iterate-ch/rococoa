package org.rococoa.cocoa.appkit;


import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.Rococoa;

/**
 * @author paulloy
 */
public abstract class NSColor extends NSObject {
	public static final _Class CLASS = Rococoa.createClass("NSColor", _Class.class);  //$NON-NLS-1$
    public abstract class _Class extends NSObject._class_ {
        public abstract NSColor clearColor();
    }
	
    public abstract void set();
}
