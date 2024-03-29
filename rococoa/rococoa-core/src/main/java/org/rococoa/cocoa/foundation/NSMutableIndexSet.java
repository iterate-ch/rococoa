package org.rococoa.cocoa.foundation;

/**
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.free.fr/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a>, <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="https://github.com/java-native-access/jna">JNA</a>.
 */
public abstract class NSMutableIndexSet extends org.rococoa.cocoa.foundation.NSIndexSet {
    /**
     * Original signature : <code>-(void)addIndexes:(NSIndexSet*)</code><br>
     * <i>native declaration : NSIndexSet.h:109</i>
     */
    public abstract void addIndexes(org.rococoa.cocoa.foundation.NSIndexSet indexSet);

    /**
     * Original signature : <code>-(void)removeIndexes:(NSIndexSet*)</code><br>
     * <i>native declaration : NSIndexSet.h:110</i>
     */
    public abstract void removeIndexes(org.rococoa.cocoa.foundation.NSIndexSet indexSet);

    /**
     * Original signature : <code>-(void)removeAllIndexes</code><br>
     * <i>native declaration : NSIndexSet.h:111</i>
     */
    public abstract void removeAllIndexes();

    /**
     * Original signature : <code>-(void)addIndex:(NSUInteger)</code><br>
     * <i>native declaration : NSIndexSet.h:112</i>
     */
    public abstract void addIndex(org.rococoa.cocoa.foundation.NSUInteger value);

    /**
     * Original signature : <code>-(void)removeIndex:(NSUInteger)</code><br>
     * <i>native declaration : NSIndexSet.h:113</i>
     */
    public abstract void removeIndex(org.rococoa.cocoa.foundation.NSUInteger value);
    /**
     * <i>native declaration : NSIndexSet.h:114</i><br>
     * Conversion Error : /// Original signature : <code>-(void)addIndexesInRange:()</code><br>
     * - (void)addIndexesInRange:(null)range; (Argument range cannot be converted)
     */
    /**
     * <i>native declaration : NSIndexSet.h:115</i><br>
     * Conversion Error : /// Original signature : <code>-(void)removeIndexesInRange:()</code><br>
     * - (void)removeIndexesInRange:(null)range; (Argument range cannot be converted)
     */
    /**
     * For a positive delta, shifts the indexes in [index, INT_MAX] to the right, thereby inserting an "empty space" [index, delta], for a negative delta, shifts the indexes in [index, INT_MAX] to the left, thereby deleting the indexes in the range [index - delta, delta].<br>
     * Original signature : <code>-(void)shiftIndexesStartingAtIndex:(NSUInteger) by:(NSInteger)</code><br>
     * <i>native declaration : NSIndexSet.h:119</i>
     */
    public abstract void shiftIndexesStartingAtIndex_by(org.rococoa.cocoa.foundation.NSUInteger index, org.rococoa.cocoa.foundation.NSInteger delta);

    /// <i>native declaration : NSIndexSet.h</i>
    public static org.rococoa.cocoa.foundation.NSMutableIndexSet alloc() {
        return getNSClass().alloc();
    }

    /// <i>native declaration : NSIndexSet.h</i>
    public static org.rococoa.cocoa.foundation.NSMutableIndexSet new_() {
        return getNSClass().new_();
    }

    public static abstract class _class_ extends org.rococoa.cocoa.foundation.NSIndexSet._class_ {
        /// <i>native declaration : NSIndexSet.h</i>
        public abstract org.rococoa.cocoa.foundation.NSMutableIndexSet alloc();

        /// <i>native declaration : NSIndexSet.h</i>
        public abstract org.rococoa.cocoa.foundation.NSMutableIndexSet new_();
    }

    public static _class_ getNSClass() {
        if (_NSCLASS_ == null)
            _NSCLASS_ = org.rococoa.Rococoa.createClass("NSMutableIndexSet", _class_.class);
        return _NSCLASS_;
    }

    private static _class_ _NSCLASS_;
}
