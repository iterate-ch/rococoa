package org.rococoa.internal;

import java.util.HashMap;
import java.util.Map;

import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSInteger;
import org.rococoa.cocoa.foundation.NSUInteger;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

/**
 * Look up how to map from and from NSInvocation and Java objects.
 * 
 * @author duncan
 *
 */
public class NSInvocationMapperLookup {
    private static final int NATIVE_POINTER_SIZE = Native.POINTER_SIZE;
    private static final int NATIVE_LONG_SIZE = Native.LONG_SIZE;
    
    private static final String NATIVE_LONG_ENCODING = NATIVE_LONG_SIZE == 4 ? "l" : "q";
    private static final String NSINTEGER_ENCODING = NATIVE_LONG_SIZE == 4 ? "i" : "q";
    private static final String NSUINTEGER_ENCODING = NATIVE_LONG_SIZE == 4 ? "I" : "Q";

    private static final Map<Class<?>, NSInvocationMapper> classToMapperLookup = new HashMap<Class<?>, NSInvocationMapper>();
        
    public static NSInvocationMapper mapperForType(Class<?> type) {
        // first check if we have a direct hit in the classToMapperLookup
        NSInvocationMapper directMatch = classToMapperLookup.get(type);
        if (directMatch != null)
            return directMatch;
        
        // Now if it is any subclass of NSObject, then the generic mapper will do
        if (NSOBJECT.type.isAssignableFrom(type))
            return NSOBJECT;

        // Now if it is any subclass of NSObject, then the generic mapper will do
        if (NATIVE_LONG.type.isAssignableFrom(type))
            return NATIVE_LONG;

        // finally if it's a structure (that wasn't found in classToMapperLookup)
        // create a mapper for the actual type and add it for next time
        if (Structure.class.isAssignableFrom(type)) {
            NSInvocationStructureMapper result = new NSInvocationStructureMapper(type);
            addToLookup(result);
            return result;
        }
        
        return null;
    }
    
    public static String stringForType(Class<?> type) {
        return mapperForType(type).typeString();
    }

    static {
        addToLookup(new NSInvocationMapper("v", void.class) {
            @Override public Object readFrom(Memory buffer, Class<?> type) {
                throw new IllegalStateException("Should not have to read void");
            }
            @Override public Memory bufferForResult(Object methodCallResult) {
                return new Memory(0);
            }
        });
        addToLookup(new NSInvocationMapper("c", boolean.class) {
            // Cocoa BOOL is defined as signed char
            @Override public Object readFrom(Memory buffer, Class<?> type) {
                byte character = buffer.getByte(0);
                return character == 0 ? java.lang.Boolean.FALSE : java.lang.Boolean.TRUE;
            }
            @Override public Memory bufferForResult(Object methodCallResult) {
                Memory result = new Memory(1);
                result.setByte(0, ((Boolean) methodCallResult) ? (byte) 1 : (byte) 0);
                return result;
            }
        });
        addToLookup(new NSInvocationMapper("c", byte.class) {
            @Override public Object readFrom(Memory buffer, Class<?> type) {
                return buffer.getByte(0);
            }        
            @Override public Memory bufferForResult(Object methodCallResult) {
                Memory result = new Memory(1);
                result.setByte(0, ((Byte) methodCallResult).byteValue());
                return result;
            }
        });
        addToLookup(new NSInvocationMapper("s", char.class) {
            @Override public Object readFrom(Memory buffer, Class<?> type) {
                throw new UnsupportedOperationException("Don't yet support char, while I think what to do");
                // TODO - think what to do
            }
            @Override
            public Memory bufferForResult(Object methodCallResult) {
                throw new UnsupportedOperationException("Don't yet support char, while I think what to do");
                // TODO - think what to do
            }                
        });
        addToLookup(new NSInvocationMapper("s", short.class) {
            @Override public Object readFrom(Memory buffer, Class<?> type) {
                return buffer.getShort(0);
            }
            @Override public Memory bufferForResult(Object methodCallResult) {
                Memory result = new Memory(2);
                result.setShort(0, ((Short) methodCallResult));
                return result;
            }
        });
        addToLookup(new NSInvocationMapper("i", int.class) {
            @Override public Object readFrom(Memory buffer, Class<?> type) {
                return buffer.getInt(0);
            }
            @Override public Memory bufferForResult(Object methodCallResult) {
                Memory result = new Memory(4);
                result.setInt(0, ((Integer) methodCallResult));
                return result;
            }
        });
        addToLookup(new NSInvocationMapper("q", long.class) {
            @Override public Object readFrom(Memory buffer, Class<?> type) {
                return buffer.getLong(0);
            }
            @Override public Memory bufferForResult(Object methodCallResult) {
                Memory result = new Memory(8);
                result.setLong(0, (Long) methodCallResult);
                return result;
            };
        });
        addToLookup(new NSInvocationMapper("f", float.class) {
            @Override public Object readFrom(Memory buffer, Class<?> type) {
                return buffer.getFloat(0);
            }
            @Override public Memory bufferForResult(Object methodCallResult) {
                Memory result = new Memory(4);
                result.setFloat(0, ((Float) methodCallResult));
                return result;
            }
        });
        addToLookup(new NSInvocationMapper("d", double.class) {
            @Override public Object readFrom(Memory buffer, Class<?> type) {
                return buffer.getDouble(0);
            }
            @Override public Memory bufferForResult(Object methodCallResult) {
                Memory result = new Memory(8);
                result.setDouble(0, ((Double) methodCallResult));
                return result;
            }
        });
        addToLookup(new NSInvocationMapper("@", ID.class) {
            @Override public Object readFrom(Memory buffer, Class<?> type) {
                return ID.fromLong(buffer.getNativeLong(0).longValue());
            }
            @Override public Memory bufferForResult(Object methodCallResult) {
                Memory result = new Memory(NATIVE_POINTER_SIZE);
                result.setNativeLong(0, ((ID) methodCallResult));
                return result;
            };
        });
        addToLookup(new NSInvocationMapper("@", String.class) {
            @Override public Object readFrom(Memory buffer, Class<?> type) {
                ID id = ID.fromLong(buffer.getNativeLong(0).longValue());
                if (id.isNull())
                    return null;
                return Foundation.toString(id);
            }
            @Override public Memory bufferForResult(Object methodCallResult) {
                Memory buffer = new Memory(NATIVE_POINTER_SIZE);
                buffer.setNativeLong(0, Foundation.cfString((String) methodCallResult));
                return buffer;
            }
        });
        addToLookup(new NSInvocationMapper(NSINTEGER_ENCODING, NSInteger.class) {
            @Override public Object readFrom(Memory buffer, Class<?> type) {
                return buffer.getInt(0);
            }
            @Override public Memory bufferForResult(Object methodCallResult) {
                Memory result = new Memory(4);
                result.setInt(0, ((Integer) methodCallResult));
                return result;
            }
        });
        addToLookup(new NSInvocationMapper(NSUINTEGER_ENCODING, NSUInteger.class) {
            @Override public Object readFrom(Memory buffer, Class<?> type) {
                return buffer.getInt(0);
            }
            @Override public Memory bufferForResult(Object methodCallResult) {
                Memory result = new Memory(4);
                result.setInt(0, ((Integer) methodCallResult));
                return result;
            }
        });
    }
    
    static NSInvocationMapper NSOBJECT = new NSInvocationMapper("@", NSObject.class) {
        @SuppressWarnings("unchecked")
        @Override public Object readFrom(Memory buffer, Class<?> type) {
            ID id = ID.fromLong(buffer.getNativeLong(0).longValue());
            if (id.isNull())
                return null;
            return Rococoa.wrap(id, (Class<? extends NSObject>) type);
        }
        @Override public Memory bufferForResult(Object methodCallResult) {
            Memory buffer = new Memory(NATIVE_POINTER_SIZE);
            buffer.setNativeLong(0, ((NSObject) methodCallResult).id());
            return buffer;
        }};
    
    static NSInvocationMapper NATIVE_LONG = new NSInvocationMapper(NATIVE_LONG_ENCODING, NativeLong.class) {
        @Override public Object readFrom(Memory buffer, Class<?> type) {
            return buffer.getNativeLong(0);
        }
        @Override public Memory bufferForResult(Object methodCallResult) {
            Memory result = new Memory(NATIVE_LONG_SIZE);
            result.setNativeLong(0, ((NativeLong) methodCallResult));
            return result;
        }};
    
    private static void addToLookup(NSInvocationMapper mapper) {
        Class<?> type = mapper.type;
        classToMapperLookup.put(type, mapper);
        if (type.isPrimitive())
            classToMapperLookup.put(boxTypeFor(type), mapper);
    }
    
    private static Class<?> boxTypeFor(Class<?> type) {
        if (type == void.class)
            return null;
        if (type == boolean.class)
            return Boolean.class;
        if (type == byte.class)
            return Byte.class;
        if (type == short.class)
            return Short.class;
        if (type == char.class)
            return Character.class;
        if (type == int.class)
            return Integer.class;
        if (type == long.class)
            return Long.class;
        if (type == float.class)
            return Float.class;
        if (type == double.class)
            return Double.class;
        throw new IllegalArgumentException("Not a primitive class " + type);
    }
}
