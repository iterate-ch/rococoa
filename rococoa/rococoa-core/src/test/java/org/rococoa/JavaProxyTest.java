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

import com.sun.jna.NativeLong;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.rococoa.cocoa.foundation.*;
import org.rococoa.test.RococoaTestCase;

import static org.junit.Assert.*;


public class JavaProxyTest extends RococoaTestCase {

    public static class JavaImplementor {
        public Object arg;

        public void nothing() {
            arg = null;
        }

        public ID sayHello() {
            return Foundation.cfString("Hello");
        }

        public ID testTakesIDReturnsID(ID id) {
            arg = id;
            return Foundation.cfString("Hello");
        }

        public NSObject takesNSObjectReturnsNSObject(NSObject s) {
            arg = s;
            return s;
        }

        public void takesInt_AndInt(int a, int b) {
            arg = new Object[]{a, b};
        }

        public byte takesStringReturnsByte(NSString s) {
            arg = s;
            return 42;
        }

        public boolean takesBooleanReturnsBoolean(boolean b) {
            arg = b;
            return !b;
        }

        public String takesJavaStringReturnsJavaString(String s) {
            arg = s;
            return s.toLowerCase();
        }

        public TestStruct takesStructureReturnsStructure(TestStruct s) {
            arg = s;
            return new TestStruct(s.anInt, s.aDouble);
        }

        public TestStruct.ByValue takesStructureByValueReturnsStructureByValue(TestStruct.ByValue s) {
            arg = s;
            return new TestStruct.ByValue(s.anInt, s.aDouble);
        }

        public NativeLong takesNativeLongReturnsNativeLong(NativeLong l) {
            arg = l;
            return l;
        }

        public long takesLongReturnsLong(long l) {
            arg = l;
            return l;
        }

        public double takesDoubleReturnsDouble(double d) {
            arg = d;
            return d;
        }

        public void notify(NSNotification notification) {
            this.arg = notification;
        }
    }

    private JavaImplementor implementor;
    private NSObject proxy;

    @Before
    public void setUp() throws Exception {
        implementor = new JavaImplementor();
        proxy = Rococoa.proxy(implementor, NSObject.class); // hang onto this to prevent GC issues
    }

    @Test
    public void testRepondsToSelector() {
        // respond to selector is required for delegates
        assertEquals(0, (byte) Foundation.send(proxy.id(), "respondsToSelector:",
                byte.class, new Selector[]{Foundation.selector("Bo")}));
        assertEquals(1, (byte) Foundation.send(proxy.id(), "respondsToSelector:",
                byte.class, new Selector[]{Foundation.selector("sayHello")}));
    }

    @Test
    public void testNoArgsReturnsVoid() {
        implementor.arg = "something";
        ID result = Foundation.sendReturnsID(proxy.id(), "nothing");
        assertTrue(result.isNull());
        assertNull(implementor.arg);
    }

    @Test
    public void testNoArgsReturnsID() {
        ID result = Foundation.sendReturnsID(proxy.id(), "sayHello");
        assertEquals("Hello", Foundation.toString(result));
        assertNull(implementor.arg);
    }

    @Test
    public void testTakesIDReturnsID() {
        ID result = Foundation.sendReturnsID(proxy.id(), "testTakesIDReturnsID:", ID.fromLong(42));
        assertEquals("Hello", Foundation.toString(result));
        assertEquals(ID.fromLong(42), implementor.arg);
    }

    @Test
    public void testTakesNSObjectReturnsNSObject() {
        ID result = Foundation.sendReturnsID(proxy.id(), "takesNSObjectReturnsNSObject:",
                Foundation.cfString("hello"));
        assertEquals("hello", Foundation.toString(result));
        assertEquals("hello",
                Rococoa.cast((NSObject) implementor.arg, NSString.class).toString());
        // as parameter was NSObject, it lost its string-ness
    }

    @Test
    public void testTakesStringReturnsByte() {
        byte result = Foundation.send(proxy.id(), "takesStringReturnsByte:",
                byte.class, new Object[]{Foundation.cfString("hello")});
        assertEquals(42, result);
        assertEquals("hello", ((NSString) implementor.arg).toString());
    }

    @Test
    public void testTakesBooleanReturnsBoolean() {
        assertTrue(Foundation.send(proxy.id(), "takesBooleanReturnsBoolean:",
                boolean.class, new Object[]{false}));
        assertFalse(Foundation.send(proxy.id(), "takesBooleanReturnsBoolean:",
                boolean.class, new Object[]{true}));
    }

    @Test
    public void testTakesIntAndInt() {
        ID result = Foundation.sendReturnsID(proxy.id(), "takesInt:AndInt:",
                42, -1);
        assertTrue(result.isNull());
        Object[] arg = (Object[]) implementor.arg;
        assertEquals(42, arg[0]);
        assertEquals(-1, arg[1]);
    }

    @Test
    public void testTakesJavaStringReturnsJavaString() {
        assertEquals("lower", Foundation.send(proxy.id(), "takesJavaStringReturnsJavaString:",
                String.class, new String[]{"LoWeR"}));
    }

    @Test
    public void testSendAndReceiveStructByReference() {
        TestStruct struct = new TestStruct(42, Math.PI);
        TestStruct result = Foundation.send(proxy.id(), "takesStructureReturnsStructure:",
                TestStruct.class, new TestStruct[]{struct});
        assertEquals("passing to java", 42, ((TestStruct) implementor.arg).anInt);
        assertEquals("passing to java", Math.PI, ((TestStruct) implementor.arg).aDouble, 0.00001);
        assertEquals("returning to OC", 42, result.anInt);
        assertEquals("returning to OC", Math.PI, result.aDouble, 0.00001);
    }

    @Test
    public void testSendAndReceiveStructByValue() {
        // Hmmm, difficult to prove this is passed by value
        TestStruct.ByValue struct = new TestStruct.ByValue(42, Math.PI);
        TestStruct result = Foundation.send(proxy.id(), "takesStructureByValueReturnsStructureByValue:",
                TestStruct.ByValue.class, new TestStruct.ByValue[]{struct});
        assertEquals("passing to java", 42, ((TestStruct) implementor.arg).anInt);
        assertEquals("passing to java", Math.PI, ((TestStruct) implementor.arg).aDouble, 0.00001);
        assertEquals("returning to OC", 42, result.anInt);
        assertEquals("returning to OC", Math.PI, result.aDouble, 0.00001);
    }

    @Test
    public void testSendAndReceiveNativeLong() {
        NativeLong result = Foundation.send(proxy.id(), "takesNativeLongReturnsNativeLong:",
                NativeLong.class, new NativeLong[]{new NativeLong(42)});
        assertEquals(42, result.longValue());
    }

    @Test
    public void testSendAndReceiveLong() {
        long result = Foundation.send(proxy.id(), "takesLongReturnsLong:",
                long.class, new Integer[]{42});
        assertEquals(42, result);

        result = Foundation.send(proxy.id(), "takesLongReturnsLong:",
                long.class, new Long[]{Long.MAX_VALUE});
        assertEquals(Long.MAX_VALUE, result);
    }

    @Test
    public void testSendAndReceiveDouble() {
        double result = Foundation.send(proxy.id(), "takesDoubleReturnsDouble:",
                double.class, new Double[]{Math.PI});
        assertEquals(Double.doubleToLongBits(Math.PI), Double.doubleToLongBits(result));

        result = Foundation.send(proxy.id(), "takesDoubleReturnsDouble:",
                double.class, new Double[]{Double.MAX_VALUE});
        assertEquals(Double.doubleToLongBits(Double.MAX_VALUE), Double.doubleToLongBits(result));
    }

    @Test
    public void testMultipleCallbacks() {
        // We managed to have static callback data, so that the last callback
        // registered was always the one called!
        // @see https://rococoa.dev.java.net/issues/show_bug.cgi?id=9
        JavaImplementor implementor2 = new JavaImplementor();
        ObjCObject proxy2 = Rococoa.proxy(implementor2);

        Foundation.sendReturnsVoid(proxy.id(), "testTakesIDReturnsID:", ID.fromLong(42));
        assertEquals(ID.fromLong(42), implementor.arg);

        Foundation.sendReturnsVoid(proxy2.id(), "testTakesIDReturnsID:", ID.fromLong(43));
        assertEquals(ID.fromLong(43), implementor2.arg);
    }

    @Test
    public void testNotifications() {
        NSNotificationCenter notificationCentre = NSNotificationCenter.CLASS.defaultCenter();
        final ID observer = proxy.id();
        notificationCentre.addObserver_selector_name_object(
                observer,
                Foundation.selector("notify:"),
                "MyNotification",
                null);

        NSNotification notification = NSNotification.CLASS.notificationWithName_object("MyNotification", null);

        assertNull(implementor.arg);
        notificationCentre.postNotification(notification);
        assertEquals(notification, implementor.arg);
        notificationCentre.removeObserver(observer);
    }

    @Test
    public void testMemoryManagement() {
        // we were autorelease'ing the proxy - so that this failed
        NSNotificationCenter notificationCentre = NSNotificationCenter.CLASS.defaultCenter();
        final ID observer = proxy.id();
        notificationCentre.addObserver_selector_name_object(
                observer,
                Foundation.selector("notify:"),
                "MyNotification",
                null);
        pool.drain();
        pool = NSAutoreleasePool.new_();

        NSNotification notification = NSNotification.CLASS.notificationWithName_object("MyNotification", null);
        notificationCentre.postNotification(notification);
        notificationCentre.removeObserver(observer);
    }

}
