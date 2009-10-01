package org.rococoa.cocoa.foundation;

import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;

public abstract class NSLocale implements NSObject {
	public static final _Class CLASS = Rococoa.createClass("NSLocale", _Class.class);  //$NON-NLS-1$
    public interface _Class extends NSClass {
        NSLocale autoupdatingCurrentLocale();
    }
}
