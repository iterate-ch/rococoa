package org.rococoa.quicktime;

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
