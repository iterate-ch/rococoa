package org.rococoa.cocoa;

import java.io.File;

import javax.swing.JFrame;

import static org.junit.Assert .*;
import org.junit.Test;
import org.rococoa.ID;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.appkit.NSOpenPanel;
import org.rococoa.cocoa.foundation.NSString;
import org.rococoa.test.RococoaTestCase;

public class NSOpenPanelTest extends RococoaTestCase {

    // Requires user to select a text file somewhere downtree from ~
    @Test
    public void testShow() {
        new JFrame().setVisible(true); // otherwise no panel
        NSOpenPanel panel = NSOpenPanel.CLASS.openPanel();
        
        // Keep this reference!
        NSObject ocProxy = Rococoa.proxy(new Object() {
            @SuppressWarnings("unused")
            public boolean panel_shouldShowFilename(ID panel, String filename) {
                char initialChar = new File(filename).getName().toLowerCase().charAt(0);
                return initialChar % 2 == 0;
            }
        });
        
        panel.setDelegate(ocProxy.id());
        int button = panel.runModalForTypes(null);
//              or, eg        
//                NSArray.CLASS.arrayWithObjects(
//                    NSString.stringWithString("txt"), null));
        NSString filenameAsNSString = panel.filename();
        if (button == NSOpenPanel.NSOKButton) {
            assertTrue(filenameAsNSString.toString().startsWith("/Users"));
        } else {
            assertEquals(NSOpenPanel.NSCancelButton, button);
            assertNull(filenameAsNSString);
        }
    }
}
