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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Library;
import com.sun.jna.Native;


/**
 * The core of Rococoa - handles the selectors and messaging at a function call level.
 * 
 * Marshalling of Java types to C and Objective-C types is handled by JNA and 
 * RococoaTypeMapper respectively.
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
            throw new RuntimeException(x);
        }
    }
    
    /**
     * Retain the NSObject with id
     */
    public static void cfRetain(ID id) {
        logging.trace("calling cfRetain({})", id);
        foundationLibrary.CFRetain(id);        
    }
    
    /**
     * Release the NSObject with id
     */
    public static void cfRelease(ID id) {
        logging.trace("calling cfRelease({})", id);
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
            if (ok == 0)
                throw new RuntimeException("Could not convert string");
            return new String(buffer, "UTF-16LE").substring(0, lengthInChars); 
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    static String toStringViaUTF8(ID cfString) {
        int lengthInChars = foundationLibrary.CFStringGetLength(cfString);
        int potentialLengthInBytes = 3 * lengthInChars + 1; // UTF8 fully escaped 16 bit chars, plus nul
        
        byte[] buffer = new byte[potentialLengthInBytes];
        byte ok = foundationLibrary.CFStringGetCString(cfString, buffer, buffer.length, StringEncoding.kCFStringEncodingUTF8.value);
        if (ok == 0)
            throw new RuntimeException("Could not convert string");
        return Native.toString(buffer);
    }
    
    /**
     * Get the ID of the NSClass with className
     */
    public static ID getClass(String className) {
        logging.trace("calling objc_getClass({})", className);
        return foundationLibrary.objc_getClass(className);
    }
        
    public static Selector selector(String selectorName) {
        Selector cached = selectorCache.get(selectorName);
        if (cached != null)
            return cached;
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
        if (logging.isTraceEnabled())
            logging.trace("sending ({}) {}.{}({})", 
                    new Object[] {returnType.getSimpleName(), receiver, selector.getName(), new VarArgsUnpacker(args)});
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

    /**
     * Return the result of calling callable on the main Cococoa thread.
     */
    @SuppressWarnings("unchecked")
    public static <T> T callOnMainThread(final Callable<T> callable) {
        final Object[] result = new Object[1];
        final Throwable[] thrown = new Throwable[1];
        RococoaLibrary.VoidCallback callback = new RococoaLibrary.VoidCallback() {
            public void callback() {
                try {
                    result[0] = callable.call();
                } catch (Throwable t) {
                    thrown[0] = t;
                }
            }
        };
        rococoaLibrary.callOnMainThread(callback);
        if (thrown[0] instanceof Error)
            throw (Error) thrown[0];
        if (thrown[0] != null)
            throw new RuntimeException(thrown[0]);
        return (T) result[0];        
    }
    
    /**
     * Run runnable on the main Cococoa thread.
     */
    public static void runOnMainThread(final Runnable runnable) {
        final Throwable[] thrown = new Throwable[1];
        RococoaLibrary.VoidCallback callback = new RococoaLibrary.VoidCallback() {
            public void callback() {
                try {
                    runnable.run();
                } catch (Throwable t) {
                    thrown[0] = t;
                }
            }
        };
        rococoaLibrary.callOnMainThread(callback);
        if (thrown[0] instanceof Error)
            throw (Error) thrown[0];
        if (thrown[0] != null)
            throw new RuntimeException(thrown[0]);
    }

    public static ID createOCProxy(RococoaLibrary.SelectorInvokedCallback selectorInvokedCallback, 
            RococoaLibrary.MethodSignatureCallback methodSignatureCallback) {
        return rococoaLibrary.createProxyForJavaObject(selectorInvokedCallback, methodSignatureCallback);
    }

}
