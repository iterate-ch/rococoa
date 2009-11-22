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

import java.util.concurrent.Callable;

import org.rococoa.Foundation;
import org.rococoa.cocoa.foundation.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.RunOnMainThread;


public @RunOnMainThread abstract class QTMovieView extends NSObject {
    
    public static final _Class CLASS = new _Class();
    
    public static class _Class {
        public QTMovieView create() {
            return Foundation.callOnMainThread(new Callable<QTMovieView>() {
                public QTMovieView call() throws Exception {
                    return Rococoa.create("QTMovieView", QTMovieView.class); //$NON-NLS-1$
                }});                
        }
    }
    
    public abstract void setMovie(QTMovie movie);
    
	public abstract void setControllerVisible(boolean isVisible);
    
	public abstract void setPreservesAspectRatio(boolean b);
    
	public abstract void play(NSObject sender);
    
	public abstract QTMovie movie();

}
