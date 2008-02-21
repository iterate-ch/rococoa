/**
 * 
 */
package org.rococoa.cocoa;

import org.rococoa.Foundation;
import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;

public abstract class NSString implements NSObject {
    public static _Class CLASS = Rococoa.createClass("NSString",  _Class.class);         //$NON-NLS-1$
    public interface _Class extends NSClass {
        NSString stringWithString(String string);
    }
    
    public static NSString stringWithString(String string) {
        return CLASS.stringWithString(string);
    }
    
    public abstract boolean isEqualToString(String string);
    
    public abstract NSString substringFromIndex(int anIndex);
    
    public abstract NSString lowercaseString();

    public String toString() {
        return Foundation.toString(id());
    }
}