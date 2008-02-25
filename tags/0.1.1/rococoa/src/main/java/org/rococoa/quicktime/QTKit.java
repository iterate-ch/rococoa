package org.rococoa.quicktime;

import com.sun.jna.Library;
import com.sun.jna.Native;

@SuppressWarnings("nls")
public interface QTKit extends Library {
    
    public static QTKit instance = (QTKit) Native.loadLibrary("QTKit", QTKit.class);

}
