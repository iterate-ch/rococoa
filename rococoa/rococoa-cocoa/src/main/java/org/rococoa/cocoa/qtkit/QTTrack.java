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
import org.rococoa.NSObject;
import org.rococoa.RunOnMainThread;
import org.rococoa.cocoa.NSDictionary;

import com.sun.jna.Pointer;

@SuppressWarnings("nls")
public @RunOnMainThread interface QTTrack extends NSObject {

    public static final String QTTrackTimeScaleAttribute = "QTTrackTimeScaleAttribute";
    public static final String QTTrackBoundsAttribute = "QTTrackBoundsAttribute";
    public static final String QTTrackDimensionsAttribute = "QTTrackDimensionsAttribute";
    
    QTMovie movie();
    
    QTMedia media();
    
    boolean isEnabled();
    void setEnabled(boolean enabled);
    
    float volume();
    void setVolume(float level);

    NSObject attributeForKey(String key);
    NSObject attributeForKey(ID key);
    void setAttribute_forKey(NSObject attribute, String key);
    
    NSDictionary trackAttributes();
    void setTrackAttributes(NSDictionary trackAttributes);
    
    Pointer quickTimeTrack();
    
}
