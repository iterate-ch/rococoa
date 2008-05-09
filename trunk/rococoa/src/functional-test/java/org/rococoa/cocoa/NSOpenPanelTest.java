package org.rococoa.cocoa;

import javax.swing.JFrame;

import org.rococoa.*;

public class NSOpenPanelTest extends RococoaTestCase {

    // Requires user to select a text file somewhere downtree from ~
    public void testShow() {
        new JFrame().setVisible(true); // otherwise no panel
        NSOpenPanel panel = NSOpenPanel.CLASS.openPanel();
        int button = panel.runModalForTypes(NSArray.CLASS.arrayWithObjects(
                NSString.stringWithString("txt"), null));
        NSString filenameAsNSString = panel.filename();
        if (button == NSOpenPanel.NSOKButton) {
            assertTrue(filenameAsNSString.toString().startsWith("/Users"));
        } else {
            assertEquals(NSOpenPanel.NSCancelButton, button);
            assertTrue(filenameAsNSString.id().isNull());
        }

    }
}
