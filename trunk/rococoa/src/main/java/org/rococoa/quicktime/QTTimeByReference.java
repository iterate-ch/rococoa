package org.rococoa.quicktime;

import com.sun.jna.Structure;

public class QTTimeByReference extends Structure {

    public long timeValue;
    public int timeScale;
    public int flags;

    public QTTimeByReference() {
    }

    public QTTimeByReference(long timeValue, int timeScale) {
        this(timeValue, timeScale, 0);
    }

    public QTTimeByReference(long timeValue, int timeScale, int flags) {
        this.timeValue = timeValue;
        this.timeScale = timeScale;
        this.flags = flags;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + flags;
        result = prime * result + timeScale;
        result = prime * result + (int) (timeValue ^ (timeValue >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final QTTime other = (QTTime) obj;
        if (flags != other.flags)
            return false;
        if (timeScale != other.timeScale)
            return false;
        if (timeValue != other.timeValue)
            return false;
        return true;
    }
    
}
