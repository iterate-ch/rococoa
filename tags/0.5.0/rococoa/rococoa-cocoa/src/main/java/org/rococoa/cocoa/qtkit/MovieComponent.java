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
 
package org.rococoa.cocoa.qtkit;

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
    public long createNSViewLong() {
        return movieView.id().longValue();
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
