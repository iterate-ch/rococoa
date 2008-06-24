/**
 * 
 */
package org.rococoa;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.sun.jna.InvocationMapper;
import com.sun.jna.NativeLibrary;

/**
 * A JNA InvocationMapper that maps calls to syntheticSendMessage to a MsgSendHandler.
 * 
 * This allows us to dispatch all calls to syntheticSendMessage and have MsgSendHandler
 * call objc_msgSend or objc_msgSend_stret as appropriate, casting the return
 * type appropriately.
 * 
 * @author duncan
 */
class MsgSendInvocationMapper implements InvocationMapper {

    private final static Method SYNTHETIC_SEND_MSG;

    static {
        try {
            SYNTHETIC_SEND_MSG = MsgSendLibrary.class.getDeclaredMethod("syntheticSendMessage", 
                    Class.class, ID.class, Selector.class, Object[].class);
        }
        catch (Exception e) {
            throw new Error("Error retrieving method");
        }
    }
    
    public InvocationHandler getInvocationHandler(NativeLibrary lib, Method m) {
        if (!m.equals(SYNTHETIC_SEND_MSG))
            return null; // default handler
        
        // Have to late bind this, as it's the only time we get to see lib.
        // Not too bad as the results are cached.
        return new MsgSendHandler(
                lib.getFunction("objc_msgSend"),
                lib.getFunction("objc_msgSend_stret"));
    }
    
}