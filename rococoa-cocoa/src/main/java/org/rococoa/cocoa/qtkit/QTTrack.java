/*
 * Copyright 2007, 2008 Duncan McGregor
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
 
package org.rococoa.cocoa.qtkit;

import org.rococoa.ID;
import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.RunOnMainThread;
import org.rococoa.cocoa.foundation.NSDictionary;

import com.sun.jna.Pointer;

@SuppressWarnings("nls")
public @RunOnMainThread abstract class QTTrack extends NSObject {

    public static final String QTTrackTimeScaleAttribute = "QTTrackTimeScaleAttribute";
    public static final String QTTrackBoundsAttribute = "QTTrackBoundsAttribute";
    public static final String QTTrackDimensionsAttribute = "QTTrackDimensionsAttribute";
    
    public abstract QTMovie movie();
    
    public abstract QTMedia media();
    
    public abstract boolean isEnabled();
    public abstract void setEnabled(boolean enabled);
    
    public abstract float volume();
    public abstract void setVolume(float level);
    public abstract NSObject attributeForKey(String key);
    public abstract NSObject attributeForKey(ID key);
    public abstract void setAttribute_forKey(NSObject attribute, String key);
    public abstract NSDictionary trackAttributes();
    public abstract void setTrackAttributes(NSDictionary trackAttributes);
    public abstract Pointer quickTimeTrack();
    
}
