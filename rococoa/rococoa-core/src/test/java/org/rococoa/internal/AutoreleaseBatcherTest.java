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

package org.rococoa.internal;

import org.junit.Test;
import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.test.RococoaTestCase;

import static org.junit.Assert.assertNull;
import static org.rococoa.test.RococoaTestCase.assertRetainCount;


public class AutoreleaseBatcherTest {
    static {
        RococoaTestCase.initializeLogging();
    }

    private Throwable thrown;

    @Test
    public void drains() {
        AutoreleaseBatcher batcher = new AutoreleaseBatcher(1);
        ID idNSObject = Foundation.cfRetain(autoreleasedObject());
        assertRetainCount(2, idNSObject);

        batcher.operate();
        assertRetainCount(1, idNSObject);
    }

    @Test
    public void batches() {
        AutoreleaseBatcher batcher = new AutoreleaseBatcher(2);
        ID idNSObject = Foundation.cfRetain(autoreleasedObject());
        assertRetainCount(2, idNSObject);

        batcher.operate();
        assertRetainCount(2, idNSObject);

        batcher.operate();
        assertRetainCount(1, idNSObject);
    }

    @Test
    public void resets() {
        AutoreleaseBatcher batcher = new AutoreleaseBatcher(1);

        ID idNSObject1 = Foundation.cfRetain(autoreleasedObject());
        assertRetainCount(2, idNSObject1);
        batcher.operate();
        assertRetainCount(1, idNSObject1);

        ID idNSObject2 = Foundation.cfRetain(autoreleasedObject());
        assertRetainCount(2, idNSObject2);
        batcher.operate();
        assertRetainCount(1, idNSObject2);
    }

    @Test
    public void threadLocal() {
        AutoreleaseBatcher.forThread(1);
        ID idNSObject = Foundation.cfRetain(autoreleasedObject());
        assertRetainCount(2, idNSObject);

        AutoreleaseBatcher.forThread(1).operate();
        assertRetainCount(1, idNSObject);
    }

    @Test
    public void threadLocal2Threads() throws InterruptedException {
        AutoreleaseBatcher.forThread(1);
        ID idNSObject = Foundation.cfRetain(autoreleasedObject());
        assertRetainCount(2, idNSObject);

        Thread thread = new Thread() {
            public void run() {
                try {
                    AutoreleaseBatcher.forThread(1);
                    ID idNSObject = Foundation.cfRetain(autoreleasedObject());
                    assertRetainCount(2, idNSObject);

                    AutoreleaseBatcher.forThread(1).operate();
                    assertRetainCount(1, idNSObject);
                } catch (Throwable t) {
                    thrown = t;
                }
            }
        };
        thread.run();
        thread.join();

        AutoreleaseBatcher.forThread(1).operate();
        assertRetainCount(1, idNSObject);
        assertNull(thrown);
    }

    private ID autoreleasedObject() {
        ID idNSObject = Foundation.sendReturnsID(Foundation.getClass("NSObject"), "new");
        return Foundation.sendReturnsID(idNSObject, "autorelease");

    }
}
