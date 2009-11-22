package org.rococoa.cocoa.foundation;

import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.Rococoa;

public abstract class NSDateFormatter extends NSFormatter {
	public static final _Class CLASS = Rococoa.createClass("NSDateFormatter", _Class.class);  //$NON-NLS-1$
    public abstract class _Class extends NSObject._class_ {
    }
    
    public static final int NSDateFormatterNoStyle = 0;
    public static final int NSDateFormatterShortStyle = 1;
    public static final int NSDateFormatterMediumStyle = 2;
    public static final int NSDateFormatterLongStyle = 3;
    public static final int NSDateFormatterFullStyle = 4;
    
	public abstract void setDateStyle(int nsDateFormatterStyle);
	public abstract void setTimeStyle(int nsDateFormatterStyle);
	public abstract void setLocale(NSLocale locale);
	public abstract NSString stringFromDate(NSDate date);

}
