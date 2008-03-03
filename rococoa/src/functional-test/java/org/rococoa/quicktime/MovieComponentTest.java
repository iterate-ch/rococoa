/*
 * Copyright 2007, 2008 Duncan McGregor
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
 
package org.rococoa.quicktime;

import java.awt.event.*;
import java.io.File;
import java.util.concurrent.CyclicBarrier;

import javax.swing.JFrame;

import org.rococoa.*;

public class MovieComponentTest extends RococoaTestCase {
    
    static final File FILE = new File("testdata/DrWho.mov");

    static {
    	// load library
        @SuppressWarnings("unused")
        QTKit instance = QTKit.instance;
    }
    
    public void testShow() throws Exception {
        assertTrue(FILE.exists());

        QTMovieView movieView = QTMovieView.CLASS.create();
        movieView.setControllerVisible(true);
        movieView.setPreservesAspectRatio(true);

        MovieComponent component = new MovieComponent(movieView);
        JFrame frame = new JFrame();
        frame.getContentPane().add(component);

        QTMovie movie = QTMovie.movieWithFile_error(FILE.getPath(), null);
        movieView.setMovie(movie);                
        movie.gotoEnd();
        showAndWaitUntilClosed(frame);
    }

    private void showAndWaitUntilClosed(JFrame frame)
            throws Exception {
        frame.pack();
        frame.setVisible(true);
        
        final CyclicBarrier done = new CyclicBarrier(2); 
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	try {
					done.await();
				} catch (Exception x) {
					throw new RuntimeException(x);
				}
            }});
        
        done.await();
    }

}
