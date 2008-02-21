package org.rococoa;

import com.sun.jna.ptr.ByReference;

/**
 * Used to retrieve an NSObject as an out param.
 * 
 * TODO - recast as extends ByReference
 * @author duncan
 *
 */
public class NSObjectByReference extends ByReference {

    private NSObject object;

    public NSObjectByReference() {
        super(4);
    }
    
    public <T extends NSObject> T getValueAs(Class<T> javaClass) {
        return Rococoa.cast(object, javaClass);
    }
    
    public void setObject(NSObject object) {
        this.object = object;
    }
    
    

}
