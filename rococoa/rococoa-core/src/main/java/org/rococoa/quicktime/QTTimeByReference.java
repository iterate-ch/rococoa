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
 
package org.rococoa.quicktime;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

public class QTTimeByReference extends Structure {

    public long timeValue;
    public NativeLong timeScale;
    public NativeLong flags;

    public QTTimeByReference() {
    }

    public QTTimeByReference(long timeValue, int timeScale) {
        this(timeValue, timeScale, 0);
    }

    public QTTimeByReference(long timeValue, int timeScale, int flags) {
        this.timeValue = timeValue;
        this.timeScale = new NativeLong(timeScale);
        this.flags = new NativeLong();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + flags.intValue();
        result = prime * result + timeScale.intValue();
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
        if (!flags.equals(other.flags))
            return false;
        if (!timeScale.equals(other.timeScale))
            return false;
        if (timeValue != other.timeValue)
            return false;
        return true;
    }
    
}
