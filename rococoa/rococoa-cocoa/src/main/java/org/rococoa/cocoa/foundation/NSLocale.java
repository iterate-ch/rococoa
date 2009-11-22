package org.rococoa.cocoa.foundation;


import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.Rococoa;

public abstract class NSLocale extends NSObject {
	public static final _Class CLASS = Rococoa.createClass("NSLocale", _Class.class);  //$NON-NLS-1$
    public abstract class _Class extends NSObject._class_ {
        public abstract NSLocale autoupdatingCurrentLocale();
    }
}
