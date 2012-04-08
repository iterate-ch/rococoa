/*
 * Copyright 2007, 2008 Duncan McGregor
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

package org.rococoa;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.rococoa.internal.FoundationLibrary;
import org.rococoa.internal.MainThreadUtils;
import org.rococoa.internal.MsgSendInvocationMapper;
import org.rococoa.internal.MsgSendLibrary;
import org.rococoa.internal.OCInvocationCallbacks;
import org.rococoa.internal.RococoaLibrary;
import org.rococoa.internal.VarArgsUnpacker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Library;
import com.sun.jna.Native;



/**
 * The core of Rococoa - statics to handle selectors and messaging at a function call level.
 *
 * Marshalling of Java types to C types is handled by JNA. Marshalling of Java
 * type to Objective-C types is handled by RococoaTypeMapper.
 *
 * Not to be confused with the Mac Foundation or Core Foundation frameworks, most
 * users shouldn't need to access this class directly.
 *
 * @author duncan
 */
@SuppressWarnings("nls")
public abstract class Foundation {

    private static Logger logging = LoggerFactory.getLogger("org.rococoa.foundation");

    private static final FoundationLibrary foundationLibrary;
    private static final MsgSendLibrary messageSendLibrary;
    private static final RococoaLibrary rococoaLibrary;

    private static final Map<String, Selector> selectorCache = new HashMap<String, Selector>();

    static {
        logging.trace("Initializing Foundation");

        // Set JNA to convert java.lang.String to char* using UTF-8, and match that with
        // the way we tell CF to interpret our char*
        // May be removed if we use toStringViaUTF16
        System.setProperty("jna.encoding", "UTF8");

        Map<String, Object> messageSendLibraryOptions = new HashMap<String, Object>(1);
        messageSendLibraryOptions.put(Library.OPTION_INVOCATION_MAPPER, new MsgSendInvocationMapper());
        messageSendLibrary = (MsgSendLibrary) Native.loadLibrary("Foundation", MsgSendLibrary.class, messageSendLibraryOptions);

        foundationLibrary = (FoundationLibrary) Native.loadLibrary("Foundation", FoundationLibrary.class);
        rococoaLibrary = (RococoaLibrary) Native.loadLibrary("rococoa", RococoaLibrary.class);
        logging.trace("exit initializing Foundation");
    }

    private Foundation() {
        //
    }

    public static void nsLog(String format, Object thing) {
        ID formatAsCFString = cfString(format);
        try {
            foundationLibrary.NSLog(formatAsCFString, thing);
        } finally {
            cfRelease(formatAsCFString);
        }
    }

    /**
     * Return a CFString as an ID, toll-free bridged to NSString.
     *
     * Note that the returned string must be freed with {@link #cfRelease(ID)}.
     */
    public static ID cfString(String s) {
        // Use a byte[] rather than letting jna do the String -> char* marshalling itself.
        // Turns out about 10% quicker for long strings.
        try {
            byte[] utf16Bytes = s.getBytes("UTF-16LE");
            return foundationLibrary.CFStringCreateWithBytes(null, utf16Bytes,
                    utf16Bytes.length,
                    StringEncoding.kCFStringEncodingUTF16LE.value, (byte) 0);
        } catch (UnsupportedEncodingException x) {
            throw new RococoaException(x);
        }
    }

    /**
     * Retain the NSObject with id
     */
    public static ID cfRetain(ID id) {
        if (logging.isTraceEnabled()) {
            logging.trace("calling cfRetain({})", id);
        }
        return foundationLibrary.CFRetain(id);
    }

    /**
     * Release the NSObject with id
     */
    public static void cfRelease(ID id) {
        if (logging.isTraceEnabled()) {
            logging.trace("calling cfRelease({})", id);
        }
        foundationLibrary.CFRelease(id);
    }

    public static int cfGetRetainCount(ID cfTypeRef) {
        return foundationLibrary.CFGetRetainCount(cfTypeRef);
    }

    public static String toString(ID cfString) {
        return toStringViaUTF8(cfString);
    }

    /* Experimental */
    static String toStringViaUTF16(ID cfString) {
        try {
            int lengthInChars = foundationLibrary.CFStringGetLength(cfString);
            int potentialLengthInBytes = 3 * lengthInChars + 1; // UTF16 fully escaped 16 bit chars, plus nul

            byte[] buffer = new byte[potentialLengthInBytes];
            byte ok = foundationLibrary.CFStringGetCString(cfString, buffer, buffer.length, StringEncoding.kCFStringEncodingUTF16LE.value);
            if (ok == 0) {
                throw new RococoaException("Could not convert string");
            }
            return new String(buffer, "UTF-16LE").substring(0, lengthInChars);
        } catch (UnsupportedEncodingException e) {
            throw new RococoaException(e);
        }
    }

    static String toStringViaUTF8(ID cfString) {
        int lengthInChars = foundationLibrary.CFStringGetLength(cfString);
        int potentialLengthInBytes = 3 * lengthInChars + 1; // UTF8 fully escaped 16 bit chars, plus nul

        byte[] buffer = new byte[potentialLengthInBytes];
        byte ok = foundationLibrary.CFStringGetCString(cfString, buffer, buffer.length, StringEncoding.kCFStringEncodingUTF8.value);
        if (ok == 0) {
            throw new RococoaException("Could not convert string");
        }
        return Native.toString(buffer);
    }

    /**
     * Get the ID of the NSClass with className
     */
    public static ID getClass(String className) {
        if (logging.isTraceEnabled()) {
            logging.trace("calling objc_getClass({})", className);
        }
        return foundationLibrary.objc_getClass(className);
    }

    public static Selector selector(String selectorName) {
        Selector cached = selectorCache.get(selectorName);
        if (cached != null) {
            return cached;
        }
        Selector result = foundationLibrary.sel_registerName(selectorName).initName(selectorName);
        selectorCache.put(selectorName, result);
        return result;
    }

    /**
     * Send message with selectorName to receiver, passing args, expecting returnType.
     *
     * Note that you are responsible for memory management if returnType is ID.
     */
    public static <T> T send(ID receiver, String selectorName, Class<T> returnType, Object... args) {
        return send(receiver, selector(selectorName), returnType, args);
    }

    /**
     * Send message with selector to receiver, passing args, expecting returnType.
     *
     * Note that you are responsible for memory management if returnType is ID.
     */
    @SuppressWarnings("unchecked")
    public static <T> T send(ID receiver, Selector selector, Class<T> returnType, Object... args) {
        if (logging.isTraceEnabled()) {
            logging.trace("sending ({}) {}.{}({})",
                    new Object[] {returnType.getSimpleName(), receiver, selector.getName(), new VarArgsUnpacker(args)});
        }
        return (T) messageSendLibrary.syntheticSendMessage(returnType, receiver, selector, args);
    }

    /**
     * Convenience as this happens a lot in tests.
     *
     * Note that you are responsible for memory management for the returned ID
     */
    public static ID sendReturnsID(ID receiver, String selectorName, Object... args) {
        return send(receiver, selector(selectorName), ID.class, args);
    }

    /**
     * Convenience as this happens a lot in tests.
     */
    public static void sendReturnsVoid(ID receiver, String selectorName, Object... args) {
        send(receiver, selector(selectorName), void.class, args);
    }

    public static boolean isMainThread() {
        return MainThreadUtils.isMainThread();
    }

    /**
     * Return the result of calling callable on the main Cococoa thread.
     */
    public static <T> T callOnMainThread(final Callable<T> callable) {
        return MainThreadUtils.callOnMainThread(rococoaLibrary, callable);
    }

    /**
     * Run runnable on the main Cococoa thread, waiting for completion.
     */
    public static void runOnMainThread(final Runnable runnable) {
        MainThreadUtils.runOnMainThread(rococoaLibrary, runnable, true);
    }

    /**
     * Run runnable on the main Cococoa thread, optionally waiting for completion.
     */
    public static void runOnMainThread(Runnable runnable, boolean waitUntilDone) {
        MainThreadUtils.runOnMainThread(rococoaLibrary, runnable, waitUntilDone);
    }
    /**
     * Create an Objective-C object which delegates to callbacks when methods
     * are invoked on it.
     *
     * Object is created with alloc, so is owned by the caller.
     */
    public static ID newOCProxy(OCInvocationCallbacks callbacks) {
        return rococoaLibrary.proxyForJavaObject(
                callbacks.selectorInvokedCallback,
                callbacks.methodSignatureCallback);
    }

    public static boolean selectorNameMeansWeOwnReturnedObject(String selectorName) {
        // From Memory Management Programming Guide for Cocoa
        // This is the fundamental rule:
        // You take ownership of an object if you create it using a method whose
        // name begins with 'alloc' or 'new' or contains 'copy' (for example,
        // alloc, newObject, or mutableCopy), or if you send it a retain
        // message. You are responsible for relinquishing ownership of objects
        // you own using release or autorelease. Any other time you receive an
        // object, you must not release it.

	// Note that this does not appear to be an infallible rule - see
	// https://rococoa.dev.java.net/servlets/ReadMsg?list=dev&msgNo=71
        return selectorName.startsWith("alloc") ||
        	selectorName.startsWith("new") ||
        	selectorName.toLowerCase().contains("copy");
    }

}
