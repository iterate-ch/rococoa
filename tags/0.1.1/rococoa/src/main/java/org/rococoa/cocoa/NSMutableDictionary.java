package org.rococoa.cocoa;

import java.util.concurrent.Callable;

import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;


public interface NSMutableDictionary extends NSDictionary {
    public static final _Class CLASS = Rococoa.wrap(
            Foundation.callOnMainThread(new Callable<ID>() {
                public ID call() throws Exception {
                    return Foundation.nsClass("NSMutableDictionary"); //$NON-NLS-1$
                }}),  _Class.class); 
    public interface _Class extends NSClass {
        NSMutableDictionary dictionaryWithCapacity(int numitems);
    }
    void setValue_forKey(NSObject object, String key);
    
}
