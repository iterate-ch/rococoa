package org.rococoa.cocoa;

import org.rococoa.NSObject;


public interface NSImage extends NSObject {
    
    void setScalesWhenResized(boolean scaleWhenResizing);
    
    void setSize(NSSize size);
    
    NSData TIFFRepresentation();

}
