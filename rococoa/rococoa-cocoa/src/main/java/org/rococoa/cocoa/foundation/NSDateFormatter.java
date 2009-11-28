/*
 * Copyright 2007, 2008, 2009 Duncan McGregor
 *
 * This file is part of Rococoa, a library to allow Java to talk to Cocoa.
 *
 * Rococoa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rococoa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Rococoa.  If not, see <http://www.gnu.org/licenses/>.
 */

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
