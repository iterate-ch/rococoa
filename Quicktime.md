# Quicktime and Rococoa #

Rococoa was written to allow the use of the [QTKit framework](http://developer.apple.com/quicktime/qtkit.html) from Java. It's by no means finished, but it is functional.

The package `org.rococoa.quicktime` contains the work so far on wrapping QTKit.

An example of using the code to play a Quicktime Movie can be found in `PlayMovieExample`.
```
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
```
`QTMovieTest` gives other examples.

One of the complications involved in using QTKit from Java is that all access to QTKit must be on the Cocoa event thread, which is not the same as the AWT event thread.

The `QTMovieView` and `QTMovie` classes are annotated with `@RunOnMainThread` - Rococoa dispatches all method calls to such classes on the Cocoa event thread.

If you plan to extend the QTKit coverage your classes may need to be similarly annotated.

You may notice the absence of calls to `Foundation.createPool()` in the code above.

I'm still trying to get my head around this.

Because the method calls are all dispatched on the Cocoa event thread, and Cocoa conveniently creates an autorelease pool for event handling code, the calls themselves are in the context of a pool.

I don't believe that this is the case for parameters that you pass in though, so bear with my while I think it through.