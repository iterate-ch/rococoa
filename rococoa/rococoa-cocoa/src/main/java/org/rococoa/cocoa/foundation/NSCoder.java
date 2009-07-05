package org.rococoa.cocoa.foundation;

import org.rococoa.NSObject;

/**
 * @author paulloy
 */
public interface NSCoder extends NSObject {
	
	void encodeObject_forKey (NSObject objv, NSString key);
	NSObject decodeObjectForKey(NSString mountainNameString);
    
}
