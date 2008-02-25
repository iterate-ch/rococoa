package org.rococoa.quicktime;

import com.sun.jna.Structure;

public class QTTimeRange extends Structure implements Structure.ByValue {

    public QTTime time;     
    public QTTime duration;

    public QTTimeRange() {
    }

    public QTTimeRange(QTTime time, QTTime duration) {
        this.time = time;
        this.duration = duration;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((duration == null) ? 0 : duration.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final QTTimeRange other = (QTTimeRange) obj;
        if (duration == null) {
            if (other.duration != null)
                return false;
        } else if (!duration.equals(other.duration))
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        } else if (!time.equals(other.time))
            return false;
        return true;
    }
    
}
