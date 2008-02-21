package org.rococoa.cocoa;

import com.sun.jna.Structure;

public class NSSize extends Structure implements Structure.ByValue {
    public float width;
    public float height;
    
    public NSSize() {}
    
    public NSSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
