/*
 * Copyright 2007, 2008, 2009 Duncan McGregor
 *
 * This file is part of Rococoa, a library to allow Java to talk to Cocoa.
 *
 * Rococoa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rococoa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Rococoa.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.rococoa.cocoa;

import org.rococoa.ID;
import org.rococoa.ObjCClass;
import org.rococoa.Rococoa;
import org.rococoa.RunOnMainThread;
import org.rococoa.cocoa.foundation.NSImage;
import org.rococoa.cocoa.foundation.NSInteger;
import org.rococoa.cocoa.foundation.NSObject;

/**
 * NSApplication
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSApplication.java,v 1.0 Mar 21, 2008 11:12:34 PM haraldk Exp$
 */
@RunOnMainThread
public abstract class NSApplication extends NSObject {

    private static final _Class CLASS = Rococoa.createClass("NSApplication", _Class.class);  //$NON-NLS-1$

    // NOTE: This class should not run on main thread (deadlocks?)
    private interface _Class extends ObjCClass {
        NSApplication sharedApplication();
    }

    static public final NSApplication NSApp = NSApplication.CLASS.sharedApplication();

    /*
    Tasks
Getting the Application

    * + sharedApplication
*/
    static public NSApplication sharedApplication() {
        return NSApp;
    }

    public abstract void run();

    public abstract void stop(ID sender);

    /*

Configuring Applications

    * ? applicationIconImage
    * ? setApplicationIconImage:
    * ? delegate
    * ? setDelegate:
    * */

    public abstract NSImage applicationIconImage();

    public abstract void setApplicationIconImage(NSImage image);

    public abstract NSDockTile dockTile();


    public abstract ID delegate();

    public abstract void setDelegate(ID delegate);

    public abstract NSWindow mainWindow();
    
    /*
    typedef enum {
       NSCriticalRequest = 0,
       NSInformationalRequest = 10
    } NSRequestUserAttentionType;    
     */

    public static final int NSCriticalRequest = 0;
    public static final int NSInformationalRequest = 10;

    /**
     * Activating the application cancels the user attention request.
     * A spoken notification will occur if spoken notifications are enabled.
     * Sending requestUserAttention: to an application that is already active has no effect.
     *
     * If the inactive application presents a modal panel, this method will be invoked with
     * NSCriticalRequest automatically. The modal panel is not brought to the front for an
     * inactive application.
     *
     * @param requestType {@link #NSCriticalRequest} or {@link #NSInformationalRequest}
     * @return The identifier for the request.
     * You can use this value to cancel the request later using the {@link #cancelUserAttentionRequest} method.
     */
    public abstract NSInteger requestUserAttention(int requestType);
    
    public abstract void cancelUserAttentionRequest(NSInteger request);
    /*

Launching Applications

    * ? finishLaunching
    * ? applicationWillFinishLaunching:  delegate method
    * ? applicationDidFinishLaunching:  delegate method

Terminating Applications

    * ? terminate:
    * ? applicationShouldTerminate:  delegate method
    * ? applicationShouldTerminateAfterLastWindowClosed:  delegate method
    * ? replyToApplicationShouldTerminate:
    * ? applicationWillTerminate:  delegate method

Managing Active Status

    * ? isActive
    * ? activateIgnoringOtherApps:
    * ? applicationWillBecomeActive:  delegate method
    * ? applicationDidBecomeActive:  delegate method
    * ? deactivate
    * ? applicationWillResignActive:  delegate method
    * ? applicationDidResignActive:  delegate method

Hiding Applications

    * ? hideOtherApplications:
    * ? unhideAllApplications:
    * ? applicationWillHide:  delegate method
    * ? applicationDidHide:  delegate method
    * ? applicationWillUnhide:  delegate method
    * ? applicationDidUnhide:  delegate method

Managing the Event Loop

    * ? isRunning
    * ? run
    * ? stop:
    * ? runModalForWindow:
    */
    public abstract int runModalForWindow(NSWindow window);
    /*
    * ? stopModal
    * ? stopModalWithCode:
    * ? abortModal
    * ? beginModalSessionForWindow:
    * ? runModalSession:
    * ? modalWindow
    * ? endModalSession:
    * ? sendEvent:

Handling Events

    * ? currentEvent
    * ? nextEventMatchingMask:untilDate:inMode:dequeue:
    * ? discardEventsMatchingMask:beforeEvent:

Posting Events

    * ? postEvent:atStart:

Managing Sheets

    * ? beginSheet:modalForWindow:modalDelegate:didEndSelector:contextInfo:
    * ? endSheet:
    * ? endSheet:returnCode:

Managing Windows

    * ? keyWindow
    * ? mainWindow
    * ? windowWithWindowNumber:
    * ? windows
    * ? makeWindowsPerform:inOrder:
    * ? applicationWillUpdate:  delegate method
    * ? applicationDidUpdate:  delegate method
    * ? applicationShouldHandleReopen:hasVisibleWindows:  delegate method

Minimizing Windows

    * ? miniaturizeAll:

Hiding Windows

    * ? isHidden
    * ? hide:
    * ? unhide:
    * ? unhideWithoutActivation

Updating Windows

    * ? updateWindows
    * ? setWindowsNeedUpdate:

Managing Window Layers

    * ? preventWindowOrdering
    * ? arrangeInFront:

Accessing the Main Menu

    * ? mainMenu
    * */

    public abstract NSMenu mainMenu();

    /*
    * ? setMainMenu:

Managing the Window Menu

    * ? windowsMenu
    * ? setWindowsMenu:
    * ? addWindowsItem:title:filename:
    * ? changeWindowsItem:title:filename:
    * ? removeWindowsItem:
    * ? updateWindowsItem:

Managing the Dock Menu

    * ? applicationDockMenu:  delegate method

Managing the Services Menu

    * ? registerServicesMenuSendTypes:returnTypes:
    * ? servicesMenu
    * ? setServicesMenu:

Providing Services

    * ? validRequestorForSendType:returnType:
    * ? servicesProvider
    * ? setServicesProvider:

Managing Panels

    * ? orderFrontColorPanel:
    * ? orderFrontStandardAboutPanel:
    * ? orderFrontStandardAboutPanelWithOptions:
    * ? orderFrontCharacterPalette:
    * ? runPageLayout:

Displaying Help

    * ? showHelp:
    * ? activateContextHelpMode:

Displaying Errors

    * ? application:willPresentError:  delegate method

Managing Threads

    * + detachDrawingThread:toTarget:withObject:

Posting Actions

    * ? tryToPerform:with:
    * ? sendAction:to:from:
    * ? targetForAction:
    * ? targetForAction:to:from:

Drawing Windows

    * ? context

Logging Exceptions

    * ? reportException:

Scripting

    * ? orderedDocuments
    * ? orderedWindows
    * ? application:delegateHandlesKey:  delegate method

Managing User Attention Requests

    * ? requestUserAttention:
    * ? cancelUserAttentionRequest:
    * ? replyToOpenOrPrint:

Managing the Screen

    * ? applicationDidChangeScreenParameters:  delegate method

Opening Files

    * ? application:openFile:  delegate method
    * ? application:openFileWithoutUI:  delegate method
    * ? application:openTempFile:  delegate method
    * ? application:openFiles:  delegate method
    * ? applicationOpenUntitledFile:  delegate method
    * ? applicationShouldOpenUntitledFile:  delegate method

Printing

    * ? application:printFile:  delegate method
    * ? application:printFiles:withSettings:showPrintPanels:  delegate method

Deprecated

    * ? runModalForWindow:relativeToWindow:
    * ? beginModalSessionForWindow:relativeToWindow:
    * ? application:printFiles:  delegate method Deprecated in Mac OS X v10.4 
    */
}
