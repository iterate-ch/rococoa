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
