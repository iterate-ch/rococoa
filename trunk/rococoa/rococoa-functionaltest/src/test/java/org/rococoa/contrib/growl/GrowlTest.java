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

package org.rococoa.contrib.growl;

import org.rococoa.cocoa.foundation.NSAutoreleasePool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * TestGrowl
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestGrowl.java,v 1.0 Mar 26, 2009 12:21:41 PM haraldk Exp$
 */
public class GrowlTest {
    private static final String MESSAGE = "Message";
    private static final String STARTUP = "Startup";

    public static void main(final String[] pArgs) {

        System.setProperty("apple.laf.useScreenMenuBar", "true");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NSAutoreleasePool.new_(); // Hmmm.. This is needed only if I create Growl beofre Swing is initialized...

                final Growl growl = new Growl(
                		GrowlTest.class.getSimpleName(),
                        null,
                        Arrays.asList(MESSAGE, STARTUP),
                        Arrays.asList(MESSAGE, STARTUP),
                        true
                );

                JMenu menu = new JMenu("Growl");
                menu.add(new JMenuItem(new AbstractAction("message") {
                    {
                        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                        putValue(Action.NAME, "Message");
                    }

                    public void actionPerformed(ActionEvent e) {
                        growl.postNotification(MESSAGE, "Message", "Growl says hello!");
                    }
                }));

                JMenuBar menubar = new JMenuBar();
                menubar.add(menu);

                JFrame frame = new JFrame("The frame...");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setPreferredSize(new Dimension(300, 200));
                frame.add(new JLabel("Pure Java. Almost...", JLabel.CENTER));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setJMenuBar(menubar);
                frame.setVisible(true);

                growl.postNotification(STARTUP, "Started", "GrowlTest started.");
            }
        });
        
    }
}
