package org.rococoa.quicktime;

import java.awt.Component;
import java.awt.Dimension;

import com.apple.eawt.CocoaComponent;

/**
 * A component which lets you put a {@link QTMovieView} into a {@link Component}.
 * 
 * @author duncan
 *
 */
public class MovieComponent extends CocoaComponent {

    private QTMovieView movieView;

    public MovieComponent(QTMovieView movieView) {
        this.movieView =  movieView;
    }

    @Override
    public int createNSView() {
        return movieView.id().intValue();
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(1024, 768);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(10, 7);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    public QTMovieView view() {
        return movieView;
    }

}
