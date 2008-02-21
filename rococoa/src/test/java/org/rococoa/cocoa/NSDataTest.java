package org.rococoa.cocoa;

import org.rococoa.NSTestCase;

@SuppressWarnings("nls")
public class NSDataTest extends NSTestCase {

    public void testInitWithBytes() throws Exception {
       byte[] bytes = "Hello".getBytes();
       
       NSData data = NSData.CLASS.dataWithBytes_length(bytes, bytes.length);
       assertEquals(bytes.length, data.length());

       byte[] resultBytes = new byte[bytes.length];
       data.getBytes(resultBytes);
       assertEquals("Hello", new String(resultBytes));
   }
}
