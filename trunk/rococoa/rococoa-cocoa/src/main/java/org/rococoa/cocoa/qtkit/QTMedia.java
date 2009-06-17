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
import org.rococoa.cocoa.foundation.NSDictionary;

import com.sun.jna.Pointer;

@SuppressWarnings("nls")
public @RunOnMainThread interface QTMedia extends NSObject {

    public static final String QTMediaDurationAttribute = "QTMediaDurationAttribute";
    public static final String QTMediaTimeScaleAttribute = "QTMediaTimeScaleAttribute";
    public static final String QTMediaSampleCountAttribute = "QTMediaSampleCountAttribute";

    public static final String QTMediaTypeAttribute = "QTMediaTypeAttribute";
    public static final String QTMediaTypeVideo = "vide";
    public static final String QTMediaTypeSound = "soun";
    public static final String QTMediaTypeText = "text";
    public static final String QTMediaTypeBase = "gnrc";
    public static final String QTMediaTypeMPEG = "MPEG";
    public static final String QTMediaTypeMusic = "musi";
    public static final String QTMediaTypeTimeCode = "tmcd";
    //etc...

    QTTrack track();

    boolean hasCharacteristic(String characteristic);
    boolean hasCharacteristic(ID characteristic);

    NSObject attributeForKey(String key);
    NSObject attributeForKey(ID key);
    void setAttribute_forKey(NSObject attribute, String key);
    void setAttribute_forKey(NSObject attribute, ID key);
    
    NSDictionary mediaAttributes();
    void setMediaAttributes(NSDictionary attributes);
    
    Pointer quickTimeMedia();
}
