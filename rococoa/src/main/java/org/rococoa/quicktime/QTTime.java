package org.rococoa.quicktime;

import com.sun.jna.Structure;

//long   long         timeValue;   long            timeScale;   long            flags;
public class QTTime extends QTTimeByReference implements Structure.ByValue {

    public QTTime() {
        super();
    }

    public QTTime(long timeValue, int timeScale, int flags) {
        super(timeValue, timeScale, flags);
    }

    public QTTime(long timeValue, int timeScale) {
        super(timeValue, timeScale);
    }
    
}
