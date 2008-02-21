package org.rococoa;


/**
 * Represents an Objective-C ID.
 * 
 * Maybe this should be a Pointer, or PointerType, in order to be the right size 
 * for 32 and 64 bit platforms.
 * 
 * @author duncan
 *
 */
public class ID extends com.sun.jna.IntegerType {

    public ID() {
        this(0);
    };
    
    public ID(int value) {
        super(4, value);
    }
    
    @Override
    public String toString() {
        return String.format("[ID 0x%x]", intValue()); //$NON-NLS-1$
    }
    
    public boolean isNull() {
        return intValue() == 0;
    }

}
