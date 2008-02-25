package org.rococoa;

public interface NSObject {
    
    ID id();
    
    int retainCount();
    
    boolean isKindOfClass(NSClass nsClass);
    boolean isKindOfClass(ID nsClass);
    
    public String description();
    
}
