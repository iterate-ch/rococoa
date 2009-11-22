package org.rococoa.cocoa.foundation;

import org.rococoa.cocoa.foundation.NSObject;

/**
 * @author paulloy
 */
public abstract class NSCoder extends NSObject {
	
	public abstract void encodeObject_forKey (NSObject objv, NSString key);
	public abstract NSObject decodeObjectForKey(NSString mountainNameString);
    
}
