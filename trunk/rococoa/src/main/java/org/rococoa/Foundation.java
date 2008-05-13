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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Callback;
import com.sun.jna.InvocationMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Structure;


@SuppressWarnings("nls")
public abstract class Foundation {
    
    private static Logger logging = LoggerFactory.getLogger("org.rococoa.foundation");

    private static final Method SEND_MSG;
    private static final StringEncoding STRING_ENCODING_MAC;
   
    /**
     * JNA Library for plain C calls
     */
    public interface FoundationLibrary extends Library {
        // standard JNA marshalling applies to these
        void NSLog(ID pString, Object thing);
        
        ID CFStringCreateWithCString(ID allocator, String string, int encoding);        
        String CFStringGetCStringPtr(ID string, int encoding);
        byte CFStringGetCString(ID theString, byte[] buffer, int bufferSize, int encoding);
        int CFStringGetLength(ID theString);
        
        void CFRetain(ID cfTypeRef);
        void CFRelease(ID cfTypeRef);
        int CFGetRetainCount (ID cfTypeRef);
            
        ID objc_getClass(String className);
        ID class_createInstance(ID pClass, int extraBytes);        
        Selector sel_registerName(String selectorName);
    }

    /**
     * JNA Library for special message send calls, called and marshalled specially.
     */
    public interface MsgSendLibrary extends Library {        
        // This doesn't exist in the library, but is synthesised by msgSendHandler
        Object syntheticSendMessage(Class<?> returnType, ID receiver, Selector selector,  Object... args);
        
        // We don't call these directly, but through send_msg
        Object objc_msgSend(ID receiver, Selector selector, Object... args);        
        Structure objc_msgSend_stret(ID receiver, Selector selector, Object... args);         
        
    }
    
    /** Callback from Obj-C to get method signature string for Java method matching selectorName */
    public  interface MethodSignatureCallback extends Callback {
        String callback(String selectorName);
    }
    
    /** Callback from Obj-C to invoke method on Java object */
    public  interface SelectorInvokedCallback extends Callback {
        void callback(String selectorName, ID nsInvocation);    
    }
    
    /**
     * JNA Library for special operations provided by our own native code
     */
    public interface RococoaLibrary extends Library {
        public interface VoidCallback extends Callback {
            void callback();        
        }
        void callOnMainThread(VoidCallback callback);
        
        public ID createProxyForJavaObject(SelectorInvokedCallback selectorInvokedCallback, 
                MethodSignatureCallback methodSignatureCallback);
    }

    private static final FoundationLibrary foundationLibrary;
    private static final MsgSendLibrary messageSendLibrary;
    private static final RococoaLibrary rococoaLibrary;
    
    static {
        logging.trace("Initializing Foundation");
        
        // Set JNA to convert java.lang.String to char* using UTF-8, and match that with
        // the way we tell CF to interpret our char*
        System.setProperty("jna.encoding", "UTF8");
        STRING_ENCODING_MAC = StringEncoding.kCFStringEncodingUTF8;

        try {
            SEND_MSG = MsgSendLibrary.class.getDeclaredMethod("syntheticSendMessage", 
                    Class.class, ID.class, Selector.class, Object[].class);
        }
        catch (Exception e) {
            throw new Error("Error retrieving method");
        }
        
        Map<String, Object> optionMap = new HashMap<String, Object>();
        optionMap.put(Library.OPTION_INVOCATION_MAPPER, createInvocationMapper(optionMap));
        optionMap.put(Library.OPTION_TYPE_MAPPER, new RococoaTypeMapper());
                
        foundationLibrary = (FoundationLibrary) Native.loadLibrary("Foundation", FoundationLibrary.class);
        messageSendLibrary = (MsgSendLibrary) Native.loadLibrary("Foundation", MsgSendLibrary.class, optionMap);
        rococoaLibrary = (RococoaLibrary) Native.loadLibrary("rococoa", RococoaLibrary.class);        
        logging.trace("exit initializing Foundation");
    }

    private static InvocationMapper createInvocationMapper(final Map<String, Object> optionMap) {
        return new InvocationMapper() {
            public InvocationHandler getInvocationHandler(NativeLibrary lib, Method m) {
                if (!m.equals(SEND_MSG))
                    return null; // default handler
                return new MsgSendHandler(
                        lib.getFunction("objc_msgSend"),
                        lib.getFunction("objc_msgSend_stret"),
                        optionMap);
            }};
    }
       
    public static void nsLog(String format, Object thing) {
        foundationLibrary.NSLog(cfString(format), thing);
    }    
    
    /**
     * Return a CFString as an ID, toll-free bridged to NSString.
     * 
     * Note that the returned string must be freed with {@link #cfRelease(ID)}.
     */
    public static ID cfString(String s) {
        return foundationLibrary.CFStringCreateWithCString(null, s, 
                STRING_ENCODING_MAC.value);
    }
    
    public static void cfRetain(ID cfTypeRef) {
        logging.trace("calling cfRetain({})", cfTypeRef);
        foundationLibrary.CFRetain(cfTypeRef);        
    }
    
    public static void cfRelease(ID cfTypeRef) {
        logging.trace("calling cfRelease({})", cfTypeRef);
        foundationLibrary.CFRelease(cfTypeRef);
    }

    public static int cfGetRetainCount(ID cfTypeRef) {
        return foundationLibrary.CFGetRetainCount(cfTypeRef);
    }
    
    public static String toString(ID cfString) {
        // We try to just copy the chars out of the CFString, and if that fails, 
        // ask for the chars to be copied in the specified encoding
        String result = foundationLibrary.CFStringGetCStringPtr(cfString, STRING_ENCODING_MAC.value);
        if (result != null)
            return result;
        else 
            return toStringWithBuffer(cfString);
    }

    private static String toStringWithBuffer(ID cfString) {
        int lengthInChars = foundationLibrary.CFStringGetLength(cfString);
        int potentialLengthInBytes = 3 * lengthInChars + 1; // UTF8 fully escaped 16 bit chars, plus nul
        
        byte[] buffer = new byte[potentialLengthInBytes];
        byte ok = foundationLibrary.CFStringGetCString(cfString, buffer, buffer.length, STRING_ENCODING_MAC.value);
        if (ok == 0)
            throw new RuntimeException("Could not convert string");
        return Native.toString(buffer);
    }
    
    public static ID nsClass(String className) {
        logging.trace("calling objc_getClass({})", className);
        return foundationLibrary.objc_getClass(className);
    }
    
    public static ID createInstance(ID pClass) {
        logging.trace("calling class_createInstance({})", pClass);
        return foundationLibrary.class_createInstance(pClass, 0);
    }
    
    public static Selector selector(String selectorName) {
        return foundationLibrary.sel_registerName(selectorName).initName(selectorName);
    }

    public static <T> T send(ID receiver, String selectorName, Class<T> returnType, Object... args) {
        return send(receiver, selector(selectorName), returnType, args);
    }

    @SuppressWarnings("unchecked")
    public static <T> T send(ID receiver, Selector selector, Class<T> returnType, Object... args) {
        if (logging.isTraceEnabled())
            logging.trace("sending ({}) {}.{}({})", 
                    new Object[] {returnType.getSimpleName(), receiver, selector.getName(), new VarArgsUnpacker(args)});
        return (T) messageSendLibrary.syntheticSendMessage(returnType, receiver, selector, args);        
    }
        
    /**
     * Convenience as this happens a lot in tests.
     */
    public static ID sendReturnsID(ID receiver, String selectorName, Object... args) {
        return send(receiver, selector(selectorName), ID.class, args);
    }
        
    public static ID createPool() {
        ID ncClass = nsClass("NSAutoreleasePool");
        return sendReturnsID(sendReturnsID(ncClass, "alloc"), "init");
    }
    
    public static void releasePool(ID pool) {
        sendReturnsID( pool, "release");
    }
    
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

    public static ID createOCProxy(SelectorInvokedCallback selectorInvokedCallback, 
            MethodSignatureCallback methodSignatureCallback) {
        return rococoaLibrary.createProxyForJavaObject(selectorInvokedCallback, methodSignatureCallback);
    }


}
