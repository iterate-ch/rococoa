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
