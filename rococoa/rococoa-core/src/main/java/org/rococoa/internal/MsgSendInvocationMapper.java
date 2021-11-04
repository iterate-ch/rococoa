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

/**
 * 
 */
package org.rococoa.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.rococoa.ID;
import org.rococoa.Selector;

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
public class MsgSendInvocationMapper implements InvocationMapper {

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
                lib.getFunction("objc_msgSend_fpret"),
                lib.getFunction("objc_msgSend_stret"));
    }
    
}