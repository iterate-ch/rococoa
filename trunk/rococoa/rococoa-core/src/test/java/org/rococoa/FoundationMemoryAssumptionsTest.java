package org.rococoa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Showing some fundamentals of Cocoa memory management
 *
 * Not a RococoaTestCase so that we know the state of autorelease pools.
 * 
 * TODO - don't run these when in ObjC GC
 * 
 * @author duncan
 *
 */
public class FoundationMemoryAssumptionsTest {
	
	@Test public void classHasInfiniteRetainCount() {
        checkRetainCount(-1, Foundation.getClass("NSString"));
	}
	
	@Test public void cFStringHasRetainCountOfOne() {
		checkRetainCount(1, Foundation.cfString("Hello World"));
	}
	
	@Test public void initGivesRetainCountOfOne() {
        ID idNSObject = Foundation.sendReturnsID(Foundation.getClass("NSObject"), "alloc");
        checkRetainCount(1, idNSObject);		
	}
	
	@Test public void newGivesRetainCountOfOne() {
        ID idNSObject = Foundation.sendReturnsID(Foundation.getClass("NSObject"), "new");
        checkRetainCount(1, idNSObject);				
	}
	
	@Test public void testAutoreleasePool() {
		ID idPool = Foundation.sendReturnsID(Foundation.getClass("NSAutoreleasePool"), "new");
        checkRetainCount(1, idPool);				

        ID idNSObject = Foundation.sendReturnsID(Foundation.getClass("NSObject"), "new");
        checkRetainCount(1, idNSObject);
		
        assertEquals(idNSObject, Foundation.sendReturnsID(idNSObject, "autorelease"));
        checkRetainCount(1, idNSObject); // pool doesn't retain, but does assume ownership. 
        	// Effectively autorelease gives the object to the pool, rather than sharing        

        // retain so that draining the pool doesn't free
        assertEquals(idNSObject, Foundation.cfRetain(idNSObject));
        checkRetainCount(2, idNSObject);

        Foundation.sendReturnsVoid(idPool, "drain");
        checkRetainCount(1, idNSObject);
        
        Foundation.cfRelease(idNSObject);
	}

	@Test public void testAutoreleaseFactoryMethod() {
		ID idPool = Foundation.sendReturnsID(Foundation.getClass("NSAutoreleasePool"), "new");
        checkRetainCount(1, idPool);				

        ID idNSString = Foundation.sendReturnsID(Foundation.getClass("NSString"), "stringWithCString:", "kowabunga");
        checkRetainCount(1, idNSString);

        // retain so that draining the pool doesn't free
        assertEquals(idNSString, Foundation.cfRetain(idNSString));
        checkRetainCount(2, idNSString);

        Foundation.sendReturnsVoid(idPool, "drain");
        checkRetainCount(1, idNSString);
        
        Foundation.cfRelease(idNSString);
	}

	@Ignore @Test public void crashDoubleFreeing() {
        ID idNSObject = Foundation.sendReturnsID(Foundation.getClass("NSObject"), "new");
        checkRetainCount(1, idNSObject);				

        Foundation.cfRelease(idNSObject);        
        Foundation.cfRelease(idNSObject); // crash
	}

	@Ignore @Test public void zombies() {
		assertEquals("YES", System.getenv("NSZombiesEnabled"));
		assertEquals("16", System.getenv("CFZombieLevel"));
		
        ID idNSObject = Foundation.sendReturnsID(Foundation.getClass("NSObject"), "new");
        checkRetainCount(1, idNSObject);				

        Foundation.cfRelease(idNSObject);
        Foundation.cfRelease(idNSObject); // crash, but with stderr logging
	}

	@Test public void nSStringSpecialCases() {
        ID idEmptyNSString = Foundation.sendReturnsID(Foundation.getClass("NSString"), "alloc");
        checkRetainCount(-1, idEmptyNSString); // I guess that there is single empty string with infinite count
        
        ID idInitedNSString = Foundation.sendReturnsID(idEmptyNSString, "initWithCString:", "bananarama");
        assertThat(idInitedNSString, not(equalTo(idEmptyNSString)));
        checkRetainCount(1, idInitedNSString);
	}

	private void checkRetainCount(int expectedCount, ID id) {
        assertEquals("Retain count", expectedCount, Foundation.send(id, "retainCount", int.class).intValue());		
        assertEquals("Retain count", expectedCount, Foundation.cfGetRetainCount(id));		
	}
}
