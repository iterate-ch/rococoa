package org.rococoa.cocoa.appkit;

import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;

/**
 * @author paulloy
 */
public abstract class NSColor implements NSObject {
	public static final _Class CLASS = Rococoa.createClass("NSColor", _Class.class);  //$NON-NLS-1$
    public interface _Class extends NSClass {
        NSColor clearColor();
    }
	
    public abstract void set();
}
