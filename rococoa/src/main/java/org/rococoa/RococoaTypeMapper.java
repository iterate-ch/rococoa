package org.rococoa;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.FromNativeContext;
import com.sun.jna.FromNativeConverter;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;

/**
 * A TypeMapper that knows how to convert NSObject to and from ID.
 * 
 * @author duncan
 *
 */
public class RococoaTypeMapper extends DefaultTypeMapper {
    
    private class NSObjectTypeConverter implements TypeConverter {

        private final Class<?> javaType;

        public NSObjectTypeConverter(Class<?> javaType) {
            this.javaType = javaType;
        }

        @SuppressWarnings("unchecked")
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (nativeValue == null)
                return null;            
            Integer nativeAsInteger = (Integer) nativeValue;
            return Rococoa.wrap(new ID(nativeAsInteger.intValue()), (Class<? extends NSObject>) javaType);
        }

        public Class<?> nativeType() {
            return Integer.class;
        }

        public Object toNative(Object value, ToNativeContext context) {
            if (value == null)
                return null;
            return new Integer(((NSObject) value).id().intValue());
        }
    }
    
    private class StringTypeConverter implements TypeConverter {

        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (nativeValue == null)
                return null;            
            Integer nativeAsInteger = (Integer) nativeValue;
            return Foundation.toString(new ID(nativeAsInteger.intValue()));
        }

        public Class<?> nativeType() {
            return Integer.class;
        }

        public Object toNative(Object value, ToNativeContext context) {
            if (value == null)
                return null;
            return new Integer(Foundation.cfString((String) value).intValue());
        }
    }

    // work in progress
    private class ObjectByReferenceConverter implements TypeConverter {
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            throw new UnsupportedOperationException();
        }

        public Class<?> nativeType() {
            return IDByReference.class;
        }

        public Object toNative(Object value, ToNativeContext context) {
            if (value == null)
                return null;
            return new IDByReference();
        }
        
    }
    
    public RococoaTypeMapper() {        
        addToNativeConverter(NSObject.class, new NSObjectTypeConverter(NSObject.class));
        addTypeConverter(String.class, new StringTypeConverter());
        addToNativeConverter(NSObjectByReference.class, new ObjectByReferenceConverter());
            // not actually used at present because ProxyForOC does marshalling
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public FromNativeConverter getFromNativeConverter(Class javaType) {
        // return a new converter that knows the subtype it is going to create
        if (NSObject.class.isAssignableFrom(javaType))
            return new NSObjectTypeConverter(javaType);
        return super.getFromNativeConverter(javaType);
    }

}
