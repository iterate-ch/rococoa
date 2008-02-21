package org.rococoa;


@SuppressWarnings("nls")
public class CFStringTest extends NSTestCase {
    
    public void testCFStringImpersonatesNSString() throws Exception {
        CFString s = new CFString("2001-03-24 10:45:32 +0000");        
        ID clas = Foundation.nsClass("NSDate");
        ID instance = (ID) Foundation.sendReturnsID(clas, "dateWithString:", s);
        assertEquals("2001-03-24 10:45:32 +0000",
                Foundation.toString(Foundation.sendReturnsID(instance, "description")));
    }
    
    public void xtestFinalize() {
        for (int i = 0; i <  50; i ++) {
            CFString s = new CFString("Hello world " + i);
// TODO - without this we get random crashes, but we should own the string?            
            Foundation.cfRetain(s);
            s = null;
            System.gc();
            System.gc();
            System.runFinalization();
        }
    }

}
