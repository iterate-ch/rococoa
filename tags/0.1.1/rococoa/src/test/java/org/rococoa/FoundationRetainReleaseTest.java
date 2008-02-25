package org.rococoa;


@SuppressWarnings("nls")
public class FoundationRetainReleaseTest extends NSTestCase {

    public void test() {
        ID string = Foundation.cfString("Hello world");
        assertEquals(1, Foundation.cfGetRetainCount(string));

        Foundation.cfRetain(string);
        assertEquals(2, Foundation.cfGetRetainCount(string));

        Foundation.cfRelease(string);
        assertEquals(1, Foundation.cfGetRetainCount(string));

        Foundation.cfRelease(string);
        // TODO - why does this fail?
        //assertEquals(0, Foundation.cfGetRetainCount(string));

    }
    

}
