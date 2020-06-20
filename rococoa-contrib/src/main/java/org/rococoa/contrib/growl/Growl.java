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

package org.rococoa.contrib.growl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rococoa.cocoa.NSApplication;
import org.rococoa.cocoa.NSDistributedNotificationCenter;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSDictionary;
import org.rococoa.cocoa.foundation.NSImage;
import org.rococoa.cocoa.foundation.NSMutableDictionary;
import org.rococoa.cocoa.foundation.NSNumber;
import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.cocoa.foundation.NSString;

/**
 * A class that encapsulates the work of talking to Growl.
 *
 * @author Karl Adam (original code using CocoaJavaBridge)
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a> (port to Rococoa).
 * @author last modified by $Author: haraldk$
 * @version $Id: Growl.java,v 1.0 Mar 26, 2009 12:24:28 PM haraldk Exp$
 */
public final class Growl {
    // TODO: Consider allowing Icon/(Buffered)Image to be passed instead of NSImage
    // TODO: Consider using Map instead of NSDictionary

    // defines
    /** The name of the growl registration notification for DNC. */
    public static final String GROWL_APP_REGISTRATION = "GrowlApplicationRegistrationNotification";

    //  Ticket Defines
    /** Ticket key for the application name. */
    public static final String GROWL_APP_NAME = "ApplicationName";
    /** Ticket key for the application icon. */
    public static final String GROWL_APP_ICON = "ApplicationIcon";
    /** Ticket key for the default notifactions. */
    public static final String GROWL_NOTIFICATIONS_DEFAULT = "DefaultNotifications";
    /** Ticket key for all notifactions. */
    public static final String GROWL_NOTIFICATIONS_ALL = "AllNotifications";

    //  Notification Defines
    /** The name of the growl notification for DNC. */
    public static final String GROWL_NOTIFICATION = "GrowlNotification";
    /** Notification key for the name. */
    public static final String GROWL_NOTIFICATION_NAME = "NotificationName";
    /** Notification key for the title. */
    public static final String GROWL_NOTIFICATION_TITLE = "NotificationTitle";
    /** Notification key for the description. */
    public static final String GROWL_NOTIFICATION_DESCRIPTION = "NotificationDescription";
    /** Notification key for the icon. */
    public static final String GROWL_NOTIFICATION_ICON = "NotificationIcon";
    /** Notification key for the application icon. */
    public static final String GROWL_NOTIFICATION_APP_ICON = "NotificationAppIcon";
    /** Notification key for the sticky flag. */
    public static final String GROWL_NOTIFICATION_STICKY = "NotificationSticky";
    /** Notification key for the identifier. */
    public static final String GROWL_NOTIFICATION_IDENTIFIER = "GrowlNotificationIdentifier";

    // Actual instance data
    // We should only register once
    private boolean registered;
    // "Application" Name
    private String appName;
    // "application" Icon
    private NSImage appImage;
    // All notifications
    private List<String> allNotes;
    // Default enabled notifications
    private List<String> defNotes;

    // The notification center
    private final NSDistributedNotificationCenter theCenter;

    private static NSArray toNSArray(final List<String> strings) {
        if (strings == null) {
            return null;
        }

        return toNSArray(strings.toArray(new String[strings.size()]));
    }

    private static NSArray toNSArray(final String... strings) {
        if (strings == null) {
            return null;
        }

        NSObject[] types = new NSObject[strings.length];
        for (int i = 0; i < strings.length; i++) {
            types[i] = NSString.stringWithString(strings[i]);

        }

        return NSArray.CLASS.arrayWithObjects(types);
    }

//    private static NSDictionary toNSDictionary(final Map<?, ?> map) {
//        NSDictionary dictionary = NSDictionary.dictionaryWithObjects_forKeys(objects, keys);
//        return dictionary;
//    }
//
//    private static NSImage toNSImage(final Image image) {
//        return null;
//    }

    //************  Constructors **************//
    /**
     * Convenience method to contruct a growl instance, defers to Growl(String
     * inAppName, NSData inImageData, NSArray inAllNotes, NSArray inDefNotes,
     * boolean registerNow) with empty arrays for your notifications.
     *
     * @param inAppName The Name of your "application"
     * @param inImage   The NSImage Icon for your Application
     */
    public Growl(String inAppName, NSImage inImage) {
        this(inAppName, inImage, null, null, false);
    }

    /**
     * Convenience method to contruct a growl instance, defers to Growl(String
     * inAppName, NSData inImageData, NSArray inAllNotes, NSArray inDefNotes,
     * boolean registerNow) with the arrays passed here and empty Data for the icon.
     *
     * @param inAppName  The Name of your "Application"
     * @param inAllNotes A String Array with the name of all your notifications
     * @param inDefNotes A String Array with the na,es of the Notifications on
     *                   by default
     */
    public Growl(String inAppName, List<String> inAllNotes, List<String> inDefNotes) {
        this(inAppName, null, inAllNotes, inDefNotes, false);
    }

    /**
     * Convenience method to contruct a growl instance, defers to Growl(String
     * inAppName, NSData inImageData, NSArray inAllNotes, NSArray inDefNotes,
     * boolean registerNow) with empty arrays for your notifications.
     *
     * @param inAppName   The Name of your "Application"
     * @param inImage     Your "Application"'s icon, or {@code null} to use your application's default application icon.
     * @param inAllNotes  The NSArray of Strings of all your Notifications
     * @param inDefNotes  The NSArray of Strings of your default Notifications
     * @param registerNow Since we have all the necessary info we can go ahead
     *                    and register
     */
    public Growl(String inAppName, NSImage inImage, List<String> inAllNotes, List<String> inDefNotes, boolean registerNow) {
        if (inAppName == null) {
            throw new IllegalArgumentException("Application name may not be null");
        }

        appName = inAppName;
        appImage = inImage != null ? inImage : NSApplication.NSApp.applicationIconImage();

        setAllowedNotifications(inAllNotes);
        setDefaultNotifications(inDefNotes);

        theCenter = NSDistributedNotificationCenter.defaultCenter();

        if (registerNow) {
            register();
        }
    }

    //************  Commonly Used Methods **************//

    // TODO: What's the point of this return value? It's always true...

    /**
     * Register all our notifications with Growl, this should only be called once.
     *
     * @return <code>true</code>.
     */
    public final boolean register() {
        if (!registered) {
            // Construct our dictionary
            // Make the arrays of objects then keys
            NSArray objects = NSArray.CLASS.arrayWithObjects(
                    NSString.stringWithString(appName),
                    toNSArray(allNotes),
                    toNSArray(defNotes),
                    appImage != null ? appImage.TIFFRepresentation() : null
            );

            NSArray keys = NSArray.CLASS.arrayWithObjects(
                    NSString.stringWithString(GROWL_APP_NAME),
                    NSString.stringWithString(GROWL_NOTIFICATIONS_ALL),
                    NSString.stringWithString(GROWL_NOTIFICATIONS_DEFAULT),
                    appImage != null ? NSString.stringWithString(GROWL_APP_ICON) : null
            );

            // Make the Dictionary
            NSDictionary regDict = NSDictionary.dictionaryWithObjects_forKeys(objects, keys);

            theCenter.postNotification(
                    GROWL_APP_REGISTRATION, // notificationName
                    null,                   // anObject
                    regDict,                // userInfoDictionary
                    true                    // deliverImmediately
            );

            registered = true;
        }

        return true;
    }

    /**
     * The fun part is actually sending those notifications we worked so hard for
     * so here we let growl know about things we think the user would like, and growl
     * decides if that is the case.
     *
     * @param inNotificationName The name of one of the notifications we told growl
     *                           about.
     * @param inIcon             The NSImage for the icon for this notification, can be null
     * @param inTitle            The Title of our Notification as Growl will show it
     * @param inDescription      The Description of our Notification as Growl will
     *                           display it
     * @param inExtraInfo        Growl is flexible and allows Display Plugins to do as they
     *                           please with thier own special keys and values, you may use
     *                           them here. These may be ignored by either the user's
     *                           preferences or the current Display Plugin. This can be null
     * @param inSticky           Whether the Growl notification should be sticky
     * @param inIdentifier       Notification identifier for coalescing. This can be null.
     * @throws IllegalArgumentException When a notification is not known
     */
    public void postNotification(String inNotificationName, NSImage inIcon, String inTitle, String inDescription,
                                 NSDictionary inExtraInfo, boolean inSticky, String inIdentifier) {
        NSMutableDictionary noteDict = NSMutableDictionary.dictionaryWithCapacity(0);

        if (!allNotes.contains(inNotificationName)) {
            throw new IllegalArgumentException("Undefined Notification attempted: " + inNotificationName);
        }

        noteDict.setValue_forKey(NSString.stringWithString(inNotificationName), GROWL_NOTIFICATION_NAME);
        noteDict.setValue_forKey(inTitle != null ? NSString.stringWithString(inTitle) : null, GROWL_NOTIFICATION_TITLE);
        noteDict.setValue_forKey(inDescription != null ? NSString.stringWithString(inDescription) : null, GROWL_NOTIFICATION_DESCRIPTION);
        noteDict.setValue_forKey(NSString.stringWithString(appName), GROWL_APP_NAME);
        if (inIcon != null) {
            noteDict.setValue_forKey(inIcon.TIFFRepresentation(), GROWL_NOTIFICATION_ICON);
        }

        if (inSticky) {
            noteDict.setValue_forKey(NSNumber.CLASS.numberWithInt(1), GROWL_NOTIFICATION_STICKY);
        }

        if (inIdentifier != null) {
            noteDict.setValue_forKey(NSString.stringWithString(inIdentifier), GROWL_NOTIFICATION_IDENTIFIER);
        }

        if (inExtraInfo != null) {
            noteDict.addEntriesFromDictionary(inExtraInfo);
        }

        theCenter.postNotification(GROWL_NOTIFICATION, null, noteDict, true);
    }

    /**
     * Convenience method that defers to postNotificationGrowlOf(String inNotificationName,
     * NSData inIconData, String inTitle, String inDescription,
     * NSDictionary inExtraInfo, boolean inSticky, String inIdentifier).
     * This is primarily for compatibility with older code
     *
     * @param inNotificationName The name of one of the notifications we told growl
     *                           about.
     * @param inIcon             The NSData for the icon for this notification, can be null
     * @param inTitle            The Title of our Notification as Growl will show it
     * @param inDescription      The Description of our Notification as Growl will
     *                           display it
     * @param inExtraInfo        Growl is flexible and allows Display Plugins to do as
     *                           they please with their own special keys and values, you
     *                           may use them here. These may be ignored by either the
     *                           user's  preferences or the current Display Plugin. This
     *                           can be null.
     * @param inSticky           Whether the Growl notification should be sticky.
     * @throws IllegalArgumentException When a notification is not known
     */
    public void postNotification(String inNotificationName, NSImage inIcon, String inTitle, String inDescription,
                                 NSDictionary inExtraInfo, boolean inSticky) {
        postNotification(inNotificationName, inIcon, inTitle, inDescription, inExtraInfo, inSticky, null);
    }


    /**
     * Convenience method that defers to postNotificationGrowlOf(String inNotificationName,
     * NSData inIconData, String inTitle, String inDescription,
     * NSDictionary inExtraInfo, boolean inSticky, String inIdentifier).
     * This is primarily for compatibility with older code
     *
     * @param inNotificationName The name of one of the notifications we told growl
     *                           about.
     * @param inIcon             The NSData for the icon for this notification, can be null
     * @param inTitle            The Title of our Notification as Growl will show it
     * @param inDescription      The Description of our Notification as Growl will
     *                           display it
     * @param inExtraInfo        Growl is flexible and allows Display Plugins to do as
     *                           they please with their own special keys and values, you
     *                           may use them here. These may be ignored by either the
     *                           user's  preferences or the current Display Plugin. This
     *                           can be null.
     * @throws IllegalArgumentException When a notification is not known
     */
    public void postNotification(String inNotificationName, NSImage inIcon, String inTitle, String inDescription,
                                 NSDictionary inExtraInfo) {
        postNotification(inNotificationName, inIcon, inTitle, inDescription, inExtraInfo, false, null);
    }

    /**
     * Convenienve method that defers to postNotificationGrowlOf(String inNotificationName,
     * NSData inIconData, String inTitle, String inDescription,
     * NSDictionary inExtraInfo, boolean inSticky, String inIdentifier) with
     * <code>null</code> passed for icon, extraInfo and identifier arguments
     *
     * @param inNotificationName The name of one of the notifications we told growl
     *                           about.
     * @param inTitle            The Title of our Notification as Growl will show it
     * @param inDescription      The Description of our Notification as Growl will
     *                           display it
     * @throws IllegalArgumentException When a notification is not known
     */
    public void postNotification(String inNotificationName, String inTitle, String inDescription) {
        postNotification(inNotificationName, null, inTitle, inDescription, null, false, null);
    }

    /**
     * Convenience method that defers to postNotificationGrowlOf(String inNotificationName,
     * NSData inIconData, String inTitle, String inDescription,
     * NSDictionary inExtraInfo, boolean inSticky)
     * with <code>null</code> passed for icon and extraInfo arguments.
     *
     * @param inNotificationName The name of one of the notifications we told growl
     *                           about.
     * @param inTitle            The Title of our Notification as Growl will show it
     * @param inDescription      The Description of our Notification as Growl will
     *                           display it
     * @param inSticky           Whether our notification should be sticky
     * @throws IllegalArgumentException When a notification is not known
     */
    public void postNotification(String inNotificationName, String inTitle, String inDescription, boolean inSticky) {
        postNotification(inNotificationName, null, inTitle, inDescription, null, inSticky, null);
    }


    //************  Accessors **************//

    /**
     * Accessor for The currently set "Application" Name
     *
     * @return String Application Name
     */
    public String applicationName() {
        return appName;
    }

    /**
     * Accessor for the Array of allowed Notifications returned an NSArray
     *
     * @return the array of allowed notifications.
     */
    public List<String> allowedNotifications() {
        return allNotes;
    }

    /**
     * Accessor for the Array of default Notifications returned as an NSArray
     *
     * @return the array of default notifications.
     */
    public List<String> defaultNotifications() {
        return defNotes;
    }

    //************  Mutators **************//

    /**
     * Sets The name of the Application talking to growl
     *
     * @param inAppName The Application Name
     * @throws IllegalStateException if already registered
     */
    public void setApplicationName(final String inAppName) {
        if (registered) {
            throw new IllegalStateException("Already registered");
        }

        appName = inAppName;
    }

    /**
     * Set the list of allowed Notifications
     *
     * @param inAllNotes The array of allowed Notifications
     * @throws IllegalStateException if already registered
     */
    public void setAllowedNotifications(final List<String> inAllNotes) {
        if (registered) {
            throw new IllegalStateException("Already registered");
        }

        allNotes = Collections.unmodifiableList(new ArrayList<String>(inAllNotes));
    }


    /**
     * Set the list of Default Notfiications
     *
     * @param inDefNotes The default Notifications
     * @throws IllegalArgumentException when an element of the array is not in the
     *                                  allowedNotifications
     * @throws IllegalStateException    if already registered
     */
    public void setDefaultNotifications(final List<String> inDefNotes) {
        if (registered) {
            throw new IllegalStateException("Already registered");
        }

        for (String inDefNote : inDefNotes) {
            if (!allNotes.contains(inDefNote)) {
                // TODO: This check is not done in the constructor
                throw new IllegalArgumentException("Array Element not in Allowed Notifications");
            }
        }

        defNotes = Collections.unmodifiableList(new ArrayList<String>(inDefNotes));
    }
}
