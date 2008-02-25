package org.rococoa.quicktime;

import org.rococoa.ID;
import org.rococoa.NSObject;
import org.rococoa.RunOnMainThread;
import org.rococoa.cocoa.NSDictionary;

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
