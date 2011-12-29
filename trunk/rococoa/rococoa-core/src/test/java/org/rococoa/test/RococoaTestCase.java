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
 
package org.rococoa.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import org.junit.After;
import org.junit.Before;
import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;
import org.rococoa.cocoa.foundation.NSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Pointer;

/**
 * A TestCase which runs tests with an autorelease pool in place.
 * 
 * @author duncan
 */
@SuppressWarnings("nls")
public abstract class RococoaTestCase {
    
    // stress our memory management
    public static boolean gcAfterTest = true;
    protected final static Logger logging ;

    static {
    	initializeLogging();
    	logging = LoggerFactory.getLogger("org.rococoa.RococoaTestCase");
    	logVersions();
    };

    public static void initializeLogging() {
        try {
            InputStream is = null;
            try {
                is = RococoaTestCase.class.getResourceAsStream("/test-logging.properties");
                if (is == null)
                    throw new FileNotFoundException("Cannot find test-logging.properties");
                LogManager.getLogManager().readConfiguration(is);
            } finally {
                if (is != null)
                    is.close();
            }
        } catch (IOException x) {
            throw new RuntimeException("Could not initialize logging", x);
        }
    }
    
    private static void logVersions() {
        logging.info("Running with JAVA_HOME = {}, java.version = {}, sizeof(Pointer) = {}", 
                new Object[] { System.getenv("JAVA_HOME"),
                    System.getProperty("java.version"),
                    Pointer.SIZE});
    }

    protected NSAutoreleasePool pool;
    
    @Before
    public void preSetup() {
        pool = NSAutoreleasePool.new_();
		assertNotNull(pool);
    }

    @After
    public void postTeardown() {
        if (gcAfterTest)
            gc();
        pool.drain();
    }
    
    public static void assertRetainCount(int expected, NSObject object) {
	assertRetainCount(expected, object.id());
    }    

    public static void assertRetainCount(int expected, ID id) {
	assertEquals(expected, Foundation.cfGetRetainCount(id));
    }    


    public static void gc() {
        System.gc();
        System.gc();
        System.runFinalization();
    }
    
}
