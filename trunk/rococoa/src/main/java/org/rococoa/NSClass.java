package org.rococoa;


/**
 * Marker interface that an OCObject represents a Class.
 * 
 * Note that in Objective-C Class is a struct, so there are no methods to call.
 * 
 * @author duncan
 *
 */
public interface NSClass extends NSObject {

    public static final _Class CLASS = new _Class();
    
    public static class _Class {
        public NSClass classWithName(String className) {
            return Rococoa.createClass(className, NSClass.class);
        }
    }
    
}
