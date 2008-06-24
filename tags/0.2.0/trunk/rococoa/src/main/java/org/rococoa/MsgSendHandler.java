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

import org.rococoa.cocoa.NSSize;

import com.sun.jna.Function;
import com.sun.jna.Library;
import com.sun.jna.Structure;

/**
 * Very special case InvocationHandler that invokes the correct message dispatch
 * function for different return types.
 * 
 * Either objc_msgSend or objc_msgSend_stret should be called, depending on the
 * return type. The latter is usually for struct by value, but the former is
 * used for small structs on Intel! Oh and the call has to be mangled in all
 * cases as the result is returned on the stack, but is different sizes
 * depending on its type. Luckily jna and libffi take care of the details -
 * provided they know what the return type is.
 * 
 * Here we pass the return type in as the first arg to the method call that this
 * intercepts, and remove it before calling the appropriate fn.
 * 
 * @see http://www.cocoabuilder.com/archive/message/cocoa/2006/6/25/166236
 * 
 * @author duncan
 * 
 */
class MsgSendHandler implements InvocationHandler {

    private final String OPTION_INVOKING_METHOD = "invoking-method";
    	// TODO - use JNA string when made public
    private final static Method OBJC_MSGSEND;
    private final static Method OBJC_MSGSEND_STRET;
    
    private final Function objc_msgSend_stret_Function;
    private final Function objc_msgSend_Function;
    
    static {
        try {
            OBJC_MSGSEND = MsgSendLibrary.class.getDeclaredMethod("objc_msgSend", 
                ID.class, Selector.class, Object[].class);
            OBJC_MSGSEND_STRET = MsgSendLibrary.class.getDeclaredMethod("objc_msgSend_stret", 
                    ID.class, Selector.class, Object[].class);
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }

    public MsgSendHandler(Function objc_msgSend_Function, Function objc_msgSend_stret_Function) {
        this.objc_msgSend_Function = objc_msgSend_Function;
        this.objc_msgSend_stret_Function = objc_msgSend_stret_Function;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnTypeForThisCall = (Class<?>) args[0];
        Object[] argsWithoutReturnType = removeReturnTypeFrom(args);
        
        Map<String, Object> options = new HashMap<String, Object>(1);    
        options.put(Library.OPTION_TYPE_MAPPER, new RococoaTypeMapper());
        
        if (shouldCallStretFor(returnTypeForThisCall)) {
            options.put(OPTION_INVOKING_METHOD, OBJC_MSGSEND_STRET);
            return objc_msgSend_stret_Function.invoke(returnTypeForThisCall, argsWithoutReturnType, options);
        } else {
            options.put(OPTION_INVOKING_METHOD, OBJC_MSGSEND);
            return objc_msgSend_Function.invoke(returnTypeForThisCall, argsWithoutReturnType, options);
        }
    }
    
    private Object[] removeReturnTypeFrom(Object[] args) {
        Object[] result = new Object[args.length - 1];
        System.arraycopy(args, 1, result, 0, args.length - 2);
        return result;
    }

    private boolean shouldCallStretFor(Class<?> returnTypeForThisCall) {
        boolean isStructByValue = Structure.class.isAssignableFrom(returnTypeForThisCall) &&
            Structure.ByValue.class.isAssignableFrom(returnTypeForThisCall);
        if (!isStructByValue)
            return false;
        return returnTypeForThisCall != NSSize.class; // TODO - better strategy than this!
    }
}
