package org.rococoa.quicktime;

import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.NSTestCase;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * We had issues with jna crashing with nested structs.
 * 
 * @author duncan
 *
 */
@SuppressWarnings("nls")
public class QTTimeRangeTest extends NSTestCase {

    private interface StructLibrary extends Library {
        int passQTTimeRangeByValue(QTTimeRange r);
    }
    
    private StructLibrary instance = (StructLibrary) Native.loadLibrary("rococoa", StructLibrary.class);    
    
    public void testPassByValueAsMethodCall() {
        ID testID = Foundation.createInstance(Foundation.nsClass("TestShunt"));        
        QTTimeRange range = new QTTimeRange(new QTTime(25, 1000, 0), new QTTime(50, 1000,10));
        int result = Foundation.send(testID, "testPassQTTimeRangeByValue:", int.class, 
                range);
        assertEquals(75, result);
    }
    
    public void testPassByValueAsFunctionCall() {
        QTTimeRange range = new QTTimeRange(new QTTime(25, 1000, 0), new QTTime(50, 1000,10));
        assertEquals(75, instance.passQTTimeRangeByValue(range));
    }


    
}
