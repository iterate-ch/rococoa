package org.rococoa.cocoa.appkit;

import org.rococoa.cocoa.foundation.NSRect;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * @author paulloy
 */
public interface AppKitLibrary extends Library {
	
	public static final AppKitLibrary INSTANCE = (AppKitLibrary) Native.loadLibrary("AppKit", AppKitLibrary.class);

	void NSRectFill (NSRect aRect);

}
