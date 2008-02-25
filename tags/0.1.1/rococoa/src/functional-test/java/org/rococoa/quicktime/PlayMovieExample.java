package org.rococoa.quicktime;

import java.io.File;

import javax.swing.JFrame;

public class PlayMovieExample {

    static final File FILE = new File("testdata/DrWho.mov");

    static {
    	// load library
        @SuppressWarnings("unused")
        QTKit instance = QTKit.instance;
    }

	public static void main(String[] args) {
        QTMovieView movieView = QTMovieView.CLASS.create();
        movieView.setControllerVisible(true);
        movieView.setPreservesAspectRatio(true);

        MovieComponent component = new MovieComponent(movieView);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(component);

        QTMovie movie = QTMovie.movieWithFile_error(FILE.getPath(), null);
        movieView.setMovie(movie);                
        movie.gotoBeginning();
        frame.pack();
        frame.setVisible(true);
    }

}
