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

package org.rococoa.cocoa;

import java.io.File;

import javax.swing.JFrame;

import static org.junit.Assert .*;
import org.junit.Ignore;
import org.junit.Test;
import org.rococoa.ID;
import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.ObjCObject;
import org.rococoa.cocoa.appkit.NSOpenPanel;
import org.rococoa.cocoa.foundation.NSString;
import org.rococoa.test.RococoaTestCase;

public class NSOpenPanelTest extends RococoaTestCase {

    // Requires user to select a text file somewhere downtree from ~
    @Test
    @Ignore
    public void testShow() {
        new JFrame().setVisible(true); // otherwise no panel
        NSOpenPanel panel = NSOpenPanel.CLASS.openPanel();
        
        // Keep this reference!
        ObjCObject ocProxy = Rococoa.proxy(new Object() {
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
