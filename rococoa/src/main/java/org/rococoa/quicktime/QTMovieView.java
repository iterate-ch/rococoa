package org.rococoa.quicktime;

import java.util.concurrent.Callable;

import org.rococoa.Foundation;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.RunOnMainThread;


public @RunOnMainThread interface QTMovieView extends NSObject {
    
    public static final _Class CLASS = new _Class();
    
    public static class _Class {
        public QTMovieView create() {
            return Foundation.callOnMainThread(new Callable<QTMovieView>() {
                public QTMovieView call() throws Exception {
                    return Rococoa.create("QTMovieView", QTMovieView.class); //$NON-NLS-1$
                }});                
        }
    }
    
    public void setMovie(QTMovie movie);
    
    public void setControllerVisible(boolean isVisible);

    public void setPreservesAspectRatio(boolean b);

    public void play(NSObject sender);

    public QTMovie movie();

}
