package org.rococoa.quicktime;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;

import org.rococoa.CallbackForOCWrapperForJavaObject;
import org.rococoa.Foundation;
import org.rococoa.ID;
import org.rococoa.NSTestCase;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.NSNotificationCenter;


@SuppressWarnings("nls")
public class MovieComponentTest extends NSTestCase {
    
    static final File FILE = new File("testdata/DrWho.mov");

    static {
        @SuppressWarnings("unused")
        QTKit instance = QTKit.instance;
    }
    
    public final class Handler {
        @SuppressWarnings("unused")
        public void notification(ID notification) {
            System.out.println("callback");
        }
    }
    

    
    public void testShow() throws InterruptedException {
        assertTrue(FILE.exists());

        final QTMovie movie = QTMovie.CLASS.movieWithFile_error(FILE.getPath(), null);
        final QTMovieView movieView = QTMovieView.CLASS.create();
        movieView.setMovie(movie);                
        movie.gotoEnd();
        movieView.setControllerVisible(true);
        movieView.setPreservesAspectRatio(true);
        movie.setCurrentTime(new QTTime(1000, 1000, 0));
//        assertEquals(new QTTime(684, 1000, 0),
//                Foundation.sendReturnsQTTime(movie.id(), "currentTime"));
        
//        send listener retain message
        final CallbackForOCWrapperForJavaObject wrapper = new CallbackForOCWrapperForJavaObject(new Handler());
        final ID createOCProxy = Foundation.createOCProxy(wrapper.selectorInvokedCallback, wrapper.methodSignatureCallback);
        NSNotificationCenter notificationCentre = Rococoa.create("NSNotificationCenter", NSNotificationCenter.class, "defaultCenter");
        notificationCentre.addObserver_selector_name_object(createOCProxy, 
                Foundation.selector("notification:"),
                "QTMovieTimeDidChangeNotification",
                movieView.movie());
        
        MovieComponent component = new MovieComponent(movieView);
        JFrame frame = new JFrame();
        frame.getContentPane().add(component);
        showAndWaitUntilClosed(frame);
    }

    private void showAndWaitUntilClosed(JFrame frame)
            throws InterruptedException {
        frame.pack();
        frame.setVisible(true);
        
        final Object semaphore = new Object();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                synchronized(semaphore) {
                    semaphore.notify();
                }
            }});
        
        synchronized(semaphore) {
            semaphore.wait();
        }
    }

}
